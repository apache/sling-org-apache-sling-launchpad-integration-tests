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
package org.apache.sling.launchpad.webapp.integrationtest.issues;

import javax.servlet.http.HttpServletResponse;

import org.apache.sling.commons.testing.integration.HttpTestBase;

public class SLING2617Test extends HttpTestBase {

    private final String TEST_PATH = "/" + getClass().getSimpleName();

    public void testDateBeanProperties() throws Exception {
        final String[] mustContain = {"New time (123456)", "All good!"};

        final TestNode tn = new TestNode(HTTP_BASE_URL + TEST_PATH, null);

        String toDelete = null;
        try {
            toDelete = uploadTestScript(tn.scriptPath, "issues/sling2617/bean-set-get.jsp", "html.jsp");
            final String content = getContent(tn.nodeUrl + ".html", CONTENT_TYPE_HTML, null, HttpServletResponse.SC_OK);

            for (String str : mustContain) {
                assertTrue("Content must contain " + str + " (" + content + ")", content.contains(str));
            }

        } finally {
            if (toDelete != null) {
                testClient.delete(toDelete);
            }
            tn.delete();
        }
    }
}
