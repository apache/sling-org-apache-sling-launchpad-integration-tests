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
package org.apache.sling.launchpad.webapp.jsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import org.apache.sling.commons.testing.integration.HttpTest;
import org.apache.sling.commons.testing.junit.Retry;
import org.apache.sling.commons.testing.junit.RetryRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Verifies that JSP Tag files function correctly
 *
 */
public class TagFileTest {

    private final HttpTest H = new HttpTest();

    private static final String TAGLIB_URI= "https://sling.apache.org/tags/test/1.0";
    
    private static String TAG_FILE_SCRIPT = 
            "<%@ taglib prefix=\"t\" uri=\"" + TAGLIB_URI + "\" %>\n" +
            "\n" + 
            "<t:test/>";

    @Rule
    public RetryRule retryRule = new RetryRule();

    @Before
    public void setup() throws Exception {
        H.setUp();
    }

    private void assertTaglibInstalled() throws IOException {
        final String content = H.getContent(H.HTTP_BASE_URL + "/system/console/status-jsptaglibs", H.CONTENT_TYPE_DONTCARE);
        assertTrue("Expecting taglib to be registered: " + TAGLIB_URI, content.contains(TAGLIB_URI));
    }
    
    /**
     * Tests a tag file packaged in a jar file is properly executed
     */
    @Test
    @Retry(intervalMsec = 250, timeoutMsec = 5000)
    public void testTagFileDeployedInBundle() throws IOException {
        
        assertTaglibInstalled();

        H.getTestClient().createNode(H.HTTP_BASE_URL + "/content/tagtest", Collections.singletonMap("sling:resourceType", "sling/test/tagfile"));
        H.getTestClient().mkdirs(H.HTTP_BASE_URL, "/apps/sling/test/tagfile");
        H.getTestClient().upload(H.HTTP_BASE_URL + "/apps/sling/test/tagfile/html.jsp", new ByteArrayInputStream(TAG_FILE_SCRIPT.getBytes(Charset.forName("UTF-8"))));
        
        String content = H.getContent(H.HTTP_BASE_URL + "/content/tagtest.html", H.CONTENT_TYPE_DONTCARE, null, 200);
        assertEquals("Incorrect output from rendering script", "TEST OUTPUT", content.trim());
    }
    
    @After
    public void tearDown() throws Exception {
        H.getTestClient().delete(H.HTTP_BASE_URL + "/content/tagtest");
        H.getTestClient().delete(H.HTTP_BASE_URL + "/apps/sling/test/tagfile");
        H.tearDown();
    }

}