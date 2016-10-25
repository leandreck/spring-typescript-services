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

import org.leandreck.endpoints.annotations.TypeScriptIgnore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 28.08.2016.
 */
class MethodNodeFactory {

    private final TypeNodeFactory typeNodeFactory;
    private final RequestMappingFactory requestMappingFactory;

    public MethodNodeFactory(final Types typeUtils, Elements elementUtils) {
        typeNodeFactory = new TypeNodeFactory(typeUtils, elementUtils);
        requestMappingFactory = new RequestMappingFactory();
    }

    public MethodNode createMethodNode(final ExecutableElement methodElement) {
        final RequestMapping requestMapping = requestMappingFactory.createRequestMappging(methodElement);

        final String name = defineName(methodElement);
        final boolean ignored = defineIgnored(methodElement, requestMapping);
        if (ignored) {
            return new MethodNode(name, "", true, null, null);
        }
        final String url = defineUrl(requestMapping);
        final List<String> httpMethods = defineHttpMethods(requestMapping);
        final TypeNode returnType = defineReturnType(methodElement);

        final List<? extends VariableElement> parameters = methodElement.getParameters();
        final TypeNode requestBodyType = defineRequestBodyType(parameters);
        final List<TypeNode> pathVariables = definePathVariableTypes(parameters);

        return new MethodNode(name, url, false, httpMethods, returnType, requestBodyType, pathVariables);
    }

    private TypeNode defineReturnType(final ExecutableElement methodElement) {
        final TypeMirror returnMirror = methodElement.getReturnType();
        return typeNodeFactory.createTypeNode(returnMirror);
    }

    private List<TypeNode> definePathVariableTypes(final List<? extends VariableElement> parameters) {
        return parameters.stream()
                .filter(p -> p.getAnnotation(PathVariable.class) != null)
                .map(p -> typeNodeFactory.createTypeNode(p))
                .collect(toList());
    }

    private TypeNode defineRequestBodyType(final List<? extends VariableElement> parameters) {
        final Optional<? extends VariableElement> optional = parameters.stream()
                .filter(p -> p.getAnnotation(RequestBody.class) != null)
                .findFirst();
        final TypeNode requestBodyType;
        if (optional.isPresent()) {
            final VariableElement paramElement = optional.get();
            requestBodyType = typeNodeFactory.createTypeNode(paramElement);
        } else {
            requestBodyType = null;
        }

        return requestBodyType;
    }

    private static List<String> defineHttpMethods(final RequestMapping requestMapping) {

        final List<String> methods = new ArrayList<>();
        if (requestMapping != null) {
            return Arrays.stream(requestMapping.method())
                    .map(requestMethod -> requestMethod.toString().toLowerCase())
                    .collect(toList());
        }

        return methods;
    }

    private static boolean defineIgnored(final ExecutableElement methodElement, final RequestMapping requestMapping) {
        boolean hasIgnoreAnnotation = methodElement.getAnnotation(TypeScriptIgnore.class) != null;
        boolean hasRequestMappingAnnotation = requestMapping != null;
        boolean producesJson = hasRequestMappingAnnotation && Arrays.stream(requestMapping.produces())
                .map(value -> value.startsWith(MediaType.APPLICATION_JSON_VALUE))
                .reduce(false, (a, b) -> a || b);

        boolean isPublic = methodElement.getModifiers().contains(Modifier.PUBLIC);
        return hasIgnoreAnnotation || !isPublic || !hasRequestMappingAnnotation || !producesJson;
    }

    private static String defineUrl(final RequestMapping requestMapping) {
        if (requestMapping != null) {
            final String[] mappings = requestMapping.value();
            if (mappings.length > 0) {
                return mappings[0];
            }
        }
        return "";
    }

    private static String defineName(final ExecutableElement methodElement) {
        return methodElement.getSimpleName().toString();
    }
}
