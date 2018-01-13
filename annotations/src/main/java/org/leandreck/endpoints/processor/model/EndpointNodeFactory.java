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

import static java.util.stream.Collectors.toList;
import static javax.lang.model.type.TypeKind.DECLARED;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.leandreck.endpoints.annotations.TypeScriptEndpoint;
import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 */
public class EndpointNodeFactory {

    private final MethodNodeFactory methodNodeFactory;
    private final TemplateConfiguration configuration;

    public EndpointNodeFactory(final TemplateConfiguration configuration,
                               final Types typeUtils,
                               final Elements elementUtils) {
        this.configuration = configuration;
        this.methodNodeFactory = new MethodNodeFactory(configuration, typeUtils, elementUtils);
    }

    public EndpointNode createEndpointNode(final TypeElement typeElement) {

        final TypeScriptEndpoint annotation = typeElement.getAnnotation(TypeScriptEndpoint.class);

        final String name = defineName(typeElement, annotation);
        final String url = defineUrl(typeElement);
        final String template = defineTemplate(annotation);
        final List<MethodNode> methods = defineMethods(typeElement, (DeclaredType) typeElement.asType());

        return new EndpointNode(name, url, template, methods, configuration.getGlobalPrintConfiguration());
    }

    private List<MethodNode> defineMethods(final TypeElement typeElement, final DeclaredType containingType) {
        final TypeMirror superclass = typeElement.getSuperclass();
        final List<MethodNode> superclassMethods;
        if (DECLARED.equals(superclass.getKind()) && !"java.lang.Object".equals(superclass.toString())) {
            superclassMethods = defineMethods((TypeElement) ((DeclaredType)superclass).asElement(), containingType);
        } else {
            superclassMethods = new ArrayList<>(20);
        }

        //Implemented Interfaces:
        typeElement.getInterfaces().stream()
                .flatMap(it -> defineMethods((TypeElement) ((DeclaredType)it).asElement(), containingType).stream())
                .forEach(superclassMethods::add);

        //Own enclosed Methods
        ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
                    .map(methodElement -> methodNodeFactory.createMethodNode(methodElement, containingType))
                    .filter(method -> !method.isIgnored())
                    .forEach(superclassMethods::add);
        return superclassMethods;
    }

    private static String defineName(final TypeElement typeElement, final TypeScriptEndpoint annotation) {
        final String name;
        if (annotation.value().isEmpty()) {
            name = typeElement.getSimpleName().toString();
        } else {
            name = annotation.value();
        }
        return name;
    }

    private static String defineUrl(final TypeElement typeElement) {
        final RequestMapping requestMapping = typeElement.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            final String[] mappings = requestMapping.value();
            if (mappings.length > 0) {
                return mappings[0];
            }
        }
        return "";
    }

    private String defineTemplate(final TypeScriptEndpoint annotation) {
        final String template;
        if (annotation == null || annotation.template().isEmpty()) {
            template = configuration.getEndpointTemplate();
        } else {
            template = annotation.template();
        }

        return template;
    }
}
