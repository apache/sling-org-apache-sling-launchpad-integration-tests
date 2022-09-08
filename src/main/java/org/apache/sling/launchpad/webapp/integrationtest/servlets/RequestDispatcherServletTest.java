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
package org.apache.sling.launchpad.webapp.integrationtest.servlets;

import java.io.IOException;

import org.apache.sling.commons.testing.integration.HttpTestBase;
import org.junit.Test;

/**
 * Test servlets including other servlet responses via RequestDispatcher.
 */
public class RequestDispatcherServletTest extends HttpTestBase {

    @Test
    public void testOriginalResponse() throws IOException {
        String content = getContent(HTTP_BASE_URL + "/testing/requestDispatcher/originalResponse", CONTENT_TYPE_PLAIN);

        assertEquals("OriginalResponse", content);
    }

    /**
     * Test includes output of OriginalResponseServlet directly via RequestDispatcher.
     */
    @Test
    public void testIncludeDirect() throws IOException {
        String content = getContent(HTTP_BASE_URL + "/testing/requestDispatcher/includeDirect", CONTENT_TYPE_PLAIN);

        assertEquals("includeDirect(OriginalResponse)", content);
    }

    /**
     * Test includes output of OriginalResponseServlet indirectly via RequestDispatcher, using a "synthetic response"
     * created with Sling Builders API to buffer the response.
     */
    @Test
    public void testIncludeBuffered() throws IOException {
        String content = getContent(HTTP_BASE_URL + "/testing/requestDispatcher/includeBuffered", CONTENT_TYPE_PLAIN);

        assertEquals("includeBuffered(OriginalResponse)", content);
    }

}