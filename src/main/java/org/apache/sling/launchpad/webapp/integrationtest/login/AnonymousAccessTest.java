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
package org.apache.sling.launchpad.webapp.integrationtest.login;

import java.net.URL;
import java.util.UUID;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.sling.commons.testing.integration.HttpTest;
import org.apache.sling.commons.testing.integration.NameValuePairList;
import org.apache.sling.servlets.post.SlingPostConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/** Verify that anonymous has read access via HTTP, but only
 *  under /content as per SLING-6130
 */
@RunWith(Parameterized.class)
public class AnonymousAccessTest {

    private final HttpTest H = new HttpTest();
    private String displayUrl;
    private String testText;
    
     @Parameterized.Parameters(name="{0}")
    public static Object[] data() {
        final Object [] result = new Object[] {
            new Object[] { "/ANON_CAN_READ", true },
            new Object[] { "", false }
        };
        return result;
    }

    private final String basePath;
    private final boolean anonymousAccessAllowed;

    public AnonymousAccessTest(String basePath, boolean anonymousAccessAllowed) {
        this.basePath = basePath;
        this.anonymousAccessAllowed = anonymousAccessAllowed;
    }

   @Before
    public void setUp() throws Exception {
        H.setUp();
        
        // create test node under a unique path
        final String id = UUID.randomUUID().toString();
        final String url = H.HTTP_BASE_URL + basePath + "/" + getClass().getSimpleName() + "/" + id + SlingPostConstants.DEFAULT_CREATE_SUFFIX;
        testText = "Test text " + id;
        final NameValuePairList list = new NameValuePairList();
        list.add("text", testText);
        displayUrl = H.getTestClient().createNode(url, list, null, true);
        assertTrue(
                "Expecting base path (" + basePath + ") in test node URL (" + displayUrl + ") for POST URL " + url,
                displayUrl.contains(basePath));
    }

    @After
    public void cleanup() throws Exception {
        H.getTestClient().delete(displayUrl);
        H.tearDown();
    }
    
    private void assertContent(String info) throws Exception {
        final String content = H.getContent(displayUrl + ".txt", H.CONTENT_TYPE_PLAIN);
        assertTrue(info, content.contains(testText));
    }
    
    @Test
    public void testAnonymousContent() throws Exception {
        // disable credentials -> anonymous session
        final URL url = new URL(H.HTTP_BASE_URL);
        final AuthScope scope = new AuthScope(url.getHost(), url.getPort(), AuthScope.ANY_REALM);
        H.getHttpClient().getParams().setAuthenticationPreemptive(false);
        H.getHttpClient().getState().setCredentials(scope, null);
        
        try {
            if(anonymousAccessAllowed) {
                assertContent("Expecting content when testing under anonymous access subtree");
            } else {
                assertEquals(
                    "Expecting status 404 when testing outside of anonymous access subtree",
                    404, H.getTestClient().get(displayUrl));
            }
        } finally {
            // re-enable credentials -> admin session
            H.getHttpClient().getParams().setAuthenticationPreemptive(true);
            Credentials defaultcreds = new UsernamePasswordCredentials("admin", "admin");
            H.getHttpClient().getState().setCredentials(scope, defaultcreds);
        }
    }
    
    @Test
    public void testAdminContent() throws Exception {
        // HTTP test client has credentials by default
        assertContent("Expecting content when testing with admin credentials");
    }
}
