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
package org.apache.sling.starter.webapp.integrationtest.servlets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.sling.commons.testing.integration.HttpTestBase;

public class StreamedUploadTest extends HttpTestBase {

    private static final String SERVLET_PATH = "/bin/streamed-upload.txt";
    private static final String UPLOAD_MODE_HEADER = "Sling-uploadmode";
    private static final String STREAM_MODE = "stream";

    public void testStreamedUploadWithJavaxPart() throws IOException {
        final String url = HTTP_BASE_URL + SERVLET_PATH;

        // Create a test file to upload
        File testFile = createTestFile("streamed-upload-test.txt", "Test content for streamed upload");

        try {
            PostMethod post = new PostMethod(url);

            // Set the streaming upload mode header
            post.setRequestHeader(UPLOAD_MODE_HEADER, STREAM_MODE);

            // Create multipart request with a file part
            Part[] parts = {new StringPart("field1", "value1"), new FilePart("file", testFile)};
            post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));

            int status = httpClient.executeMethod(post);
            String response = post.getResponseBodyAsString();

            // Check for success - the servlet returns "OK:" if casting worked
            assertEquals("Expected 200 OK response. Response body: " + response, 200, status);
            assertTrue(
                    "Response should indicate success with javax.servlet.http.Part. Response: " + response,
                    response.contains("OK:"));
            assertTrue(
                    "Response should show processed parts. Response: " + response,
                    response.contains("Part 1:") || response.contains("Processed"));

        } finally {
            testFile.delete();
        }
    }

    private File createTestFile(String name, String content) throws IOException {
        Path tempPath = Files.createTempFile("streamed-upload-", ".txt");
        Files.writeString(tempPath, content);
        return tempPath.toFile();
    }
}
