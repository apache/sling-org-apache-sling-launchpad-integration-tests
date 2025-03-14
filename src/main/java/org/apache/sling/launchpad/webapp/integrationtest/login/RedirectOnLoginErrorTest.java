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
package org.apache.sling.launchpad.webapp.integrationtest.login;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.sling.commons.testing.integration.HttpTestBase;

/** Test SLING-2165 Verify that redirect to the referring login form after login error works */
public class RedirectOnLoginErrorTest extends HttpTestBase {

    /** Execute a POST request and check status
     * @return the HttpMethod executed
     * @throws IOException */
    private HttpMethod assertPostStatus(
            String url, int expectedStatusCode, List<NameValuePair> postParams, String assertMessage, String referer)
            throws IOException {
        final PostMethod post = new PostMethod(url);
        post.setFollowRedirects(false);
        post.setDoAuthentication(false);

        // set the referer to indicate where we came from
        post.setRequestHeader("Referer", referer);

        // set Accept header to trick sling into treating the request as from a browser
        post.setRequestHeader("User-Agent", "Mozilla/5.0 Sling Integration Test");

        if (postParams != null) {
            final NameValuePair[] nvp = {};
            post.setRequestBody(postParams.toArray(nvp));
        }

        if (postParams != null) {
            final NameValuePair[] nvp = {};
            post.setRequestBody(postParams.toArray(nvp));
        }

        final int status = httpClient.executeMethod(post);
        if (assertMessage == null) {
            assertEquals(expectedStatusCode, status);
        } else {
            assertEquals(assertMessage, expectedStatusCode, status);
        }
        return post;
    }

    /**
     * Test SLING-2165.  Login Error should redirect back to the referrer
     * login page.
     *
     * @throws Exception
     */
    public void testRedirectToLoginFormAfterLoginError() throws Exception {
        // login failure
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("j_username", "___bogus___"));
        params.add(new NameValuePair("j_password", "not_a_real_user"));
        final String loginPageUrl = String.format("%s/system/sling/form/login", HTTP_BASE_URL);
        PostMethod post = (PostMethod) assertPostStatus(
                HTTP_BASE_URL + "/j_security_check",
                HttpServletResponse.SC_MOVED_TEMPORARILY,
                params,
                null,
                loginPageUrl);

        final Header locationHeader = post.getResponseHeader("Location");
        String location = locationHeader.getValue();
        int queryStrStart = location.indexOf('?');
        if (queryStrStart != -1) {
            location = location.substring(0, queryStrStart);
        }
        assertEquals("Expected to remain on the form/login page", loginPageUrl, location);
    }

    /**
     * Test SLING-2165.  Verify that a GET request to the default login page does not
     * result in an error condition.
     *
     * @throws Exception
     */
    public void testGetDefaultLoginPage() throws Exception {
        final String loginPageUrl = String.format("%s/system/sling/login", HTTP_BASE_URL);
        final GetMethod get = new GetMethod(loginPageUrl);
        get.setFollowRedirects(false);
        get.setDoAuthentication(false);
        final int status = httpClient.executeMethod(get);
        final int family = status / 10;
        if (family != 20 && family != 30) {
            fail("Expected 20x or 30x status, got " + status + " at " + loginPageUrl);
        }
    }
}
