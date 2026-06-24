/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.starter.webapp.integrationtest.resourcehiding;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.sling.commons.testing.integration.HttpTestBase;
import org.junit.Test;

public class ServletsScriptsHidingTest extends HttpTestBase {

    private static final String SERVLET_URL = HTTP_BASE_URL + "/sling-test/hiding-tests/servlets.txt";
    private static final String SCRIPT_URL = HTTP_BASE_URL + "/sling-test/hiding-tests/scripts.txt";
    private static final String DEFAULT_SERVLET_TEXT = "PlainTextRenderer";

    private void assertResponse(String path, String expected) throws IOException {
        final String content = getContent(path, CONTENT_TYPE_PLAIN);
        assertTrue("Expected response to contain '" + expected + "'", content.contains(expected));
    }

    private void setHidden(boolean hide) throws IOException {
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("hide", String.valueOf(hide)));
        final String assertMessage = "POST with hide=" + hide;
        final HttpMethod post = assertPostStatus(SERVLET_URL, HttpServletResponse.SC_OK, params, assertMessage);
        assertTrue("Expecting " + assertMessage, post.getResponseBodyAsString().contains(assertMessage));
    }

    @Test
    public void testNotHidden() throws IOException {
        setHidden(false);
        assertResponse(SERVLET_URL, "HidingTestServlet");
        assertResponse(SCRIPT_URL, "This script might be hidden");
    }

    @Test
    public void testHidden() throws IOException {
        setHidden(true);
        assertResponse(SERVLET_URL, DEFAULT_SERVLET_TEXT);
        assertResponse(SCRIPT_URL, DEFAULT_SERVLET_TEXT);
    }
}
