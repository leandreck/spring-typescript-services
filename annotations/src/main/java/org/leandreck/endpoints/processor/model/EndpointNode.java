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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 27.08.2016.
 */
public class EndpointNode {

    private final String serviceName;
    private final String serviceURL;
    private final String template;
    private final List<MethodNode> methods;
    private final Set<TypeNode> types;

    public EndpointNode(final String serviceName, final String serviceURL, String template, final List<MethodNode> methods) {
        this.serviceName = serviceName;
        this.serviceURL = serviceURL;
        this.template = template;
        this.methods = methods;
        this.types = this.getMethods().stream()
                .map(m -> m.getReturnType())
                .map(EndpointNode::flatten)
                .flatMap(Collection::stream)
                .filter(c -> !c.isMappedType())
                .filter(c -> !c.getTypeName().contains("["))
                .collect(toSet());
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

    public Set<TypeNode> getTypes() {
        return types;
    }

    private static Collection<TypeNode> flatten(TypeNode root) {
        final Set<TypeNode> typeSet = root.getChildren().stream()
                .map(EndpointNode::flatten)
                .flatMap(Collection::stream)
                .filter(c -> !c.isMappedType())
                .collect(toSet());
        typeSet.add(root);
        return typeSet;
    }
}
