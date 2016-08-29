/**
 * Copyright © 2016 Mathias Kowalzik (Mathias.Kowalzik@leandreck.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.leandreck.endpoints.processor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 27.08.2016.
 */
public class EndpointNode {

    private final String serviceName;
    private final String serviceURL;
    private final String template;
    private final List<MethodNode> methods;

    /**
     * For testing only.
     */
    public EndpointNode() {
        serviceName = "TestService";
        serviceURL = "/api/test/somewhere";
        methods = new ArrayList<>();
        methods.add(new MethodNode());
        template = "/org/leandreck/endpoints/templates/typescript/service.ftl";
    }

    public EndpointNode(final String serviceName, final String serviceURL, String template, final List<MethodNode> methods) {
        this.serviceName = serviceName;
        this.serviceURL = serviceURL;
        this.template = template;
        this.methods = methods;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public List<MethodNode> getMethods() {
        return methods;
    }

    public String getTemplate() {
        return template;
    }
}
