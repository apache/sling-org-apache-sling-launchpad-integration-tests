<%@page import="javax.json.JsonWriter"%>
<%@page import="javax.json.JsonObject"%>
<%@page import="javax.json.JsonObjectBuilder"%>
<%@page import="javax.json.JsonBuilderFactory"%>
<%@page import="javax.json.Json"%>
<%@page import="java.util.Collections"%>
<%@page import="org.apache.sling.api.SlingConstants"%>
<%
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
 
int status = (Integer)request.getAttribute(SlingConstants.ERROR_STATUS);
response.setStatus(status);
response.setCharacterEncoding("UTF-8");
response.setContentType("application/json");

JsonBuilderFactory factory = Json.createBuilderFactory(Collections.emptyMap());
JsonObjectBuilder jsonObjBuilder = factory.createObjectBuilder();

jsonObjBuilder.add("status", status);

String msg = (String)request.getAttribute(SlingConstants.ERROR_MESSAGE);
if (msg != null && !msg.isEmpty()) {
	jsonObjBuilder.add("message", msg);
}

String requestUri = (String)request.getAttribute(SlingConstants.ERROR_REQUEST_URI);
if (requestUri != null && !requestUri.isEmpty()) {
	jsonObjBuilder.add("request_uri", requestUri);
}

JsonObject jsonObj = jsonObjBuilder.build();
JsonWriter jsonWriter = Json.createWriter(response.getWriter());
jsonWriter.writeObject(jsonObj);
jsonWriter.close();
%>