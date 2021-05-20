/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.launchpad.webapp.integrationtest.servlets.post;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.sling.commons.testing.integration.HttpTestBase;
import org.apache.sling.servlets.post.SlingPostConstants;
import org.apache.sling.servlets.post.impl.helper.MediaRangeList;

/** Test the various status options for POST, SLING-10006 */
public class PostSendErrorTest extends HttpTestBase {

    private String postPath = "PostSendErrorTest/" + System.currentTimeMillis();

    /**
     * expect html response, :sendError=true and the operation was not successful
     */
    public void testNotSuccessfulWithHtmlResponseAndSendError() throws IOException {
        // clear state to do the call as anonymous
        httpClient.getState().clear();

        final String resPath = "/" + postPath;
        final String postUrl = HTTP_BASE_URL + resPath;
        String content = getContent(postUrl, CONTENT_TYPE_HTML, 
                Arrays.asList(new NameValuePair(SlingPostConstants.RP_SEND_ERROR, "true"),
                        new NameValuePair(SlingPostConstants.RP_NODE_NAME_HINT, "test")),
                500, "POST");
        assertNotNull(content);

        // check the html content matches what would be sent from the DefaultErrorHandlingServlet
        Pattern regex = Pattern.compile("The requested URL \\/PostSendErrorTest\\/.* resulted in an error in org.apache.sling.servlets.post.impl.SlingPostServlet\\.", Pattern.MULTILINE);
        assertTrue("Expected error message", regex.matcher(content).find());
    }

    /**
     * expect html response, :sendError=true and the operation was successful
     */
    public void testSuccessfulWithHtmlResponseAndSendError() throws IOException {
        final String resPath = "/" + postPath;
        final String postUrl = HTTP_BASE_URL + resPath;
        String content = getContent(postUrl, CONTENT_TYPE_HTML, 
                Arrays.asList(new NameValuePair(SlingPostConstants.RP_SEND_ERROR, "true"),
                        new NameValuePair(SlingPostConstants.RP_NODE_NAME_HINT, "test")),
                201, "POST");
        assertNotNull(content);
        urlsToDelete.add(postUrl); // remove created nodes after test

        // check the html content matches what would be sent from the SlingPostServlet
        assertTrue("Expected status div", content.contains("<div id=\"Status\">201</div>"));
        assertTrue("Expected message div", content.contains("<div id=\"Message\">Created</div>"));
        Pattern regex = Pattern.compile("<div id=\"Path\">\\/PostSendErrorTest\\/.*<\\/div>", Pattern.MULTILINE);
        assertTrue("Expected created path div", regex.matcher(content).find());
    }

    /**
     * expect json response, :sendError=true and the operation was not successful
     */
    public void testNotSuccessfulWithJsonResponseAndSendError() throws IOException {
        // clear state to do the call as anonymous
        httpClient.getState().clear();

        final String resPath = "/" + postPath;
        final String postUrl = HTTP_BASE_URL + resPath;
        String content = getContent(postUrl, CONTENT_TYPE_JSON, 
                Arrays.asList(new NameValuePair(SlingPostConstants.RP_SEND_ERROR, "true"),
                        new NameValuePair(SlingPostConstants.RP_NODE_NAME_HINT, "test"),
                        new NameValuePair(MediaRangeList.PARAM_ACCEPT, "application/json,/;q=0.9")),
                500, "POST");
        assertNotNull(content);

        // check the json content matches what would be sent from the DefaultErrorHandlingServlet
        try (Reader reader = new StringReader(content);
                JsonReader jsonReader = Json.createReader(reader)) {
            JsonObject jsonObj = jsonReader.readObject();
            assertEquals(500, jsonObj.getInt("status"));
            assertEquals(resPath, jsonObj.getString("requestUri"));
            assertEquals("org.apache.sling.servlets.post.impl.SlingPostServlet", jsonObj.getString("servletName"));
            assertEquals("java.lang.IllegalArgumentException: Can't create child on a synthetic root", jsonObj.getString("message"));
        }
    }

    /**
     * expect json response, :sendError=true and the operation was successful
     **/
    public void testSuccessfulWithJsonResponseAndSendError() throws IOException {
        final String resPath = "/" + postPath;
        final String postUrl = HTTP_BASE_URL + resPath;
        String content = getContent(postUrl, CONTENT_TYPE_JSON, 
                Arrays.asList(new NameValuePair(SlingPostConstants.RP_SEND_ERROR, "true"),
                        new NameValuePair(SlingPostConstants.RP_NODE_NAME_HINT, "test"),
                        new NameValuePair(MediaRangeList.PARAM_ACCEPT, "application/json,/;q=0.9")),
                201, "POST");
        assertNotNull(content);
        urlsToDelete.add(postUrl); // remove created nodes after test

        // check the json content matches what would be sent from the SlingPostServlet
        try (Reader reader = new StringReader(content);
                JsonReader jsonReader = Json.createReader(reader)) {
            JsonObject jsonObj = jsonReader.readObject();
            assertEquals(201, jsonObj.getInt("status.code"));
            assertEquals(true, jsonObj.getBoolean("isCreate"));
            assertTrue(jsonObj.getString("path").startsWith(resPath));
        }
    }

}
