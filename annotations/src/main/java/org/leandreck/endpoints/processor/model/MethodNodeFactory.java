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
/*
  Copyright © 2016 Mathias Kowalzik (Mathias.Kowalzik@leandreck.org)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package org.leandreck.endpoints.processor.model;

import org.leandreck.endpoints.annotations.TypeScriptIgnore;
import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
import static org.leandreck.endpoints.processor.model.StringUtil.definedValue;

/**
 */
class MethodNodeFactory {

    private final TypeNodeFactory typeNodeFactory;
    private final RequestMappingFactory requestMappingFactory;

    MethodNodeFactory(final TemplateConfiguration configuration,
                      final Types typeUtils,
                      final Elements elementUtils) {
        typeNodeFactory = new TypeNodeFactory(configuration, typeUtils, elementUtils);
        requestMappingFactory = new RequestMappingFactory();
    }

    MethodNode createMethodNode(final ExecutableElement methodElement) {
        final RequestMapping requestMapping = requestMappingFactory.createRequestMapping(methodElement);

        final String name = defineName(methodElement);
        final boolean ignored = defineIgnored(methodElement, requestMapping);
        if (ignored) {
            return new MethodNode(name, "", true, null, null);
        }
        final String url = defineUrl(requestMapping);
        final List<String> httpMethods = defineHttpMethods(requestMapping);
        final TypeNode returnType = defineReturnType(methodElement);

        final List<? extends VariableElement> parameters = methodElement.getParameters();
        final TypeMirror containingType = methodElement.asType();
        final TypeNode requestBodyType = defineRequestBodyType(parameters, containingType);
        final List<TypeNode> pathVariables = definePathVariableTypes(parameters, containingType);
        final List<TypeNode> queryParams = defineQueryParamsTypes(parameters, containingType);

        return new MethodNode(name, url, false, httpMethods, returnType, requestBodyType, pathVariables, queryParams);
    }

    private TypeNode defineReturnType(final ExecutableElement methodElement) {
        final TypeMirror returnMirror = methodElement.getReturnType();
        return typeNodeFactory.createTypeNode(returnMirror);
    }

    private List<TypeNode> definePathVariableTypes(final List<? extends VariableElement> parameters, final TypeMirror containingType) {
        return parameters.stream()
                .filter(p -> p.getAnnotation(PathVariable.class) != null)
                .map(it -> typeNodeFactory.createTypeNode(it, definedValue(
                        it.getAnnotation(PathVariable.class).name(),
                        it.getAnnotation(PathVariable.class).value()
                ), containingType))
                .collect(toList());
    }

    private List<TypeNode> defineQueryParamsTypes(final List<? extends VariableElement> parameters, final TypeMirror containingType) {
        return parameters.stream()
                .filter(p -> p.getAnnotation(RequestParam.class) != null)
                .filter(it -> !it.asType().toString().equals("org.springframework.web.multipart.MultipartFile"))
                .map(it -> typeNodeFactory.createTypeNode(it, definedValue(
                        it.getAnnotation(RequestParam.class).name(),
                        it.getAnnotation(RequestParam.class).value()
                ), containingType))
                .collect(toList());
    }

    private TypeNode defineRequestBodyType(final List<? extends VariableElement> parameters, final TypeMirror containingType) {
        final Optional<? extends VariableElement> optionalRequestBody = parameters.stream()
                .filter(it -> it.getAnnotation(RequestBody.class) != null
                    || it.asType().toString().equals("org.springframework.web.multipart.MultipartFile"))
                .findFirst();

        final TypeNode requestBodyType;
        if (optionalRequestBody.isPresent()) {
            final VariableElement paramElement = optionalRequestBody.get();
            requestBodyType = typeNodeFactory.createTypeNode(paramElement, null, containingType);
        } else {
            requestBodyType = typeNodeFactory.createTypeNode("body", null, null, null);
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
