/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.sling.launchpad.webapp.integrationtest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonException;
import javax.json.JsonObject;

import org.apache.commons.httpclient.HttpException;
import org.apache.sling.commons.testing.integration.HttpTestBase;
import org.apache.sling.launchpad.webapp.integrationtest.util.JsonUtil;
import org.apache.sling.servlets.post.SlingPostConstants;

public class VersionParameterTest extends HttpTestBase {

    public static final String TEST_BASE_PATH = "/sling-tests";

    public static final String CONFIG_SERVLET = HTTP_BASE_URL + "/system/console/configMgr/org.apache.sling.servlets.get.impl.version.VersionInfoServlet";

    private String postUrl;

    private Map<String,String> params;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        postUrl = HTTP_BASE_URL + TEST_BASE_PATH + "/" + System.currentTimeMillis();
        params = new HashMap<String,String>();
        params.put("jcr:mixinTypes", "mix:versionable");
    }

    private String createVersionableNode() throws IOException, JsonException {
        params.put(":checkinNewVersionableNodes", "true");
        params.put("prop", "v1");
        final String location = testClient.createNode(postUrl + SlingPostConstants.DEFAULT_CREATE_SUFFIX, params);
        testClient.createNode(postUrl + SlingPostConstants.DEFAULT_CREATE_SUFFIX, params);

        params.put("prop", "v2");
        params.put(":autoCheckout", "true");
        testClient.post(location, params);

        params.put("prop", "v3");
        testClient.post(location, params);

        final String content = getContent(location + ".txt", CONTENT_TYPE_PLAIN);
        assertTrue("Node (" + location + ") should be checked in.", content.contains("jcr:isCheckedOut: false"));
        assertEquals("v3", getProp(location + ".json"));
        return location;
    }

    public void testVersionParameter() throws IOException, JsonException, InterruptedException {
        waitUntilSlingIsStable();
        final String location = createVersionableNode();
        assertEquals("v2", getProp(location + ".json;v=1.1"));
        assertEquals("v2", getProp(location + ";v='1.1'.json"));
    }

    public void testVersionParameterWithChildNode() throws IOException {
        Map<String, String> versionableNodeParams = new HashMap<>();
        versionableNodeParams.put("jcr:mixinTypes", "mix:versionable");

        final String testFileContent = "test-file-content";
        final String testFileContentUpdated = "test-file-content-v2";

        /* 
         * Step 1: Create a node
         */
        params.put(":checkinNewVersionableNodes", "false");
        params.put("prop", "parent");

        final String versionableNodeLocation = testClient.createNode(postUrl + SlingPostConstants.DEFAULT_CREATE_SUFFIX, params);
        final String childFileNodeLocation = versionableNodeLocation + "/childFile";

        /*
         * Step 2: Upload a file as a child of the node
         */
        InputStream inputStream = new ByteArrayInputStream(testFileContent.getBytes());
        testClient.upload(childFileNodeLocation, inputStream);

        /*
         * Validation: Check that the uploaded content can be received
         */
        String content = getContent(childFileNodeLocation, CONTENT_TYPE_DONTCARE);
        assertEquals(testFileContent, content);

        /*
         * Step 3: Create a new version of the node
         */
        Map<String, String> versionableNodeUpdatedParams = new HashMap<>();
        versionableNodeUpdatedParams.put(":operation", "checkin");
        testClient.post(versionableNodeLocation, versionableNodeUpdatedParams);

        versionableNodeUpdatedParams.put(":operation", "checkout");
        testClient.post(versionableNodeLocation, versionableNodeUpdatedParams);

        /* 
         * Step 4: Update the child node content
         */
        InputStream updatedInputStream = new ByteArrayInputStream(testFileContentUpdated.getBytes());
        testClient.upload(childFileNodeLocation, updatedInputStream);

        /*
         * Validation: Check that the current content can be received
         */
        String updatedContent = getContent(childFileNodeLocation, CONTENT_TYPE_DONTCARE);
        assertEquals(testFileContentUpdated, updatedContent);

        /*
         * Validation: Try to receive original version of the node (version 1.0)
         */
         assertEquals("parent", getProp(versionableNodeLocation + ".json;v='1.0'"));
       
        /*
         * Validation: Try to receive previous content of the child  (version 1.0)
         */

        String previousContent = getContent(childFileNodeLocation + ";v='1.0'", CONTENT_TYPE_DONTCARE);
        assertEquals(testFileContent, previousContent);
        
    }

    private void waitUntilSlingIsStable() throws HttpException, IOException,
            InterruptedException {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            if (testClient.get(HTTP_BASE_URL  + "/.json") == 200) {
                return;
            }
        }
        fail("Sling instance fails to respond with status 200");
    }

    private String getProp(String url) throws JsonException, IOException {
        final JsonObject content = JsonUtil.parseObject(getContent(url, CONTENT_TYPE_JSON));
        return content.getString("prop");
    }
}
