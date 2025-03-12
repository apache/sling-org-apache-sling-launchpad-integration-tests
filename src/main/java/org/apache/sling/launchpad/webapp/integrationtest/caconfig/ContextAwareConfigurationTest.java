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
package org.apache.sling.launchpad.webapp.integrationtest.caconfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.caconfig.management.ConfigurationManager;
import org.apache.sling.caconfig.spi.ConfigurationPersistData;
import org.apache.sling.junit.rules.TeleporterRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.apache.jackrabbit.JcrConstants.JCR_PRIMARYTYPE;
import static org.apache.jackrabbit.JcrConstants.NT_UNSTRUCTURED;

/** Various ResourceResolver API tests, converted to teleported tests from
 *  the previous resourceresolver-api.jsp script.
 */
public class ContextAwareConfigurationTest {

    @Rule
    public final TeleporterRule teleporter = TeleporterRule.forClass(getClass(), "Launchpad");

    private ResourceResolver resourceResolver;
    private ConfigurationManager configurationManager;

    private Resource contentResource;

    @Before
    public void setup() throws LoginException, PersistenceException {
        final ResourceResolverFactory resourceResolverFactory = teleporter.getService(ResourceResolverFactory.class);
        resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);

        // create test content resource
        Map<String, Object> props = new HashMap<>();
        props.put(JCR_PRIMARYTYPE, NT_UNSTRUCTURED);
        props.put("sling:configRef", "/conf/caconfig-test");
        contentResource =
                ResourceUtil.getOrCreateResource(resourceResolver, "/content/caconfig-test", props, null, true);

        configurationManager = teleporter.getService(ConfigurationManager.class);
    }

    @After
    public void tearDown() throws PersistenceException {
        Resource confContent = resourceResolver.getResource("/conf/caconfig-test");
        if (confContent != null) {
            resourceResolver.delete(confContent);
        }
        resourceResolver.delete(contentResource);
    }

    @Test
    public void testCreateAndReadConfiguration() {
        // write context-aware configuration
        configurationManager.persistConfiguration(
                contentResource,
                "my-config",
                new ConfigurationPersistData(Collections.singletonMap("prop1", "value1")));

        // read context-aware configuration
        ValueMap config = contentResource
                .adaptTo(ConfigurationBuilder.class)
                .name("my-config")
                .asValueMap();

        Assert.assertEquals("Context-Aware configuration value", "value1", config.get("prop1", String.class));
    }
}
