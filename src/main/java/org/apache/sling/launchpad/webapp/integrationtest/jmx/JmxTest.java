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
package org.apache.sling.launchpad.webapp.integrationtest.jmx;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.json.JsonValue;
import org.apache.sling.commons.testing.integration.HttpTestBase;
import org.apache.sling.launchpad.webapp.integrationtest.util.JsonUtil;

/**
 * The <tt>DomTest</tt> verifies that simple DOM executions are successful
 *
 */
public class JmxTest extends HttpTestBase {

    private static final String DEFAULT_THREADPOOL_OBJECTNAME =
            "\"org.apache.sling:type=threads,service=ThreadPool,name=default\"";

    public void testSlingJmxMBeansListing() throws IOException {

        String content = getContent(HTTP_BASE_URL + "/bin/jmx.json", CONTENT_TYPE_JSON);

        List<String> slingMBeanNames =
                JsonUtil.parseArray(content).stream().map(JsonValue::toString).collect(Collectors.toList());

        assertTrue("Expecting at least one Sling MBean", !slingMBeanNames.isEmpty());
        boolean found = slingMBeanNames.contains(DEFAULT_THREADPOOL_OBJECTNAME);
        assertTrue("Expecting " + DEFAULT_THREADPOOL_OBJECTNAME + " to be listed in " + slingMBeanNames, found);
    }
}
