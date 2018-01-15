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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import java.util.Arrays;

public enum VariableAnnotations {

    PATH_VARIABLE("org.springframework.web.bind.annotation.PathVariable"),
    REQUEST_PARAM("org.springframework.web.bind.annotation.RequestParam"),
    REQUEST_BODY("org.springframework.web.bind.annotation.RequestBody");

    final String annotation;

    VariableAnnotations(final String annotation) {
        this.annotation = annotation;
    }

    public static boolean isOptionalByAnnotation(final VariableElement variableElement) {
        return variableElement.getAnnotationMirrors().stream()
                .map(VariableAnnotations::isOptional)
                .reduce((r, r2) -> (r || r2)).orElse(false);
    }

    public static boolean isOptional(final AnnotationMirror annotationMirror) {
        final boolean relevantAnnotation = Arrays.stream(VariableAnnotations.values())
                .map(it -> annotationMirror.getAnnotationType().toString().startsWith(it.annotation))
                .reduce((a, b) -> a || b).orElse(false);

        final boolean optional;
        if (relevantAnnotation) {
            final String required = annotationMirror.getElementValues().entrySet().stream().filter(e -> e.getKey().toString().equals("required()")).map(e -> e.getValue().toString()).findFirst().orElse("true");
            optional = !Boolean.valueOf(required);
        } else {
            optional = false;
        }

        return optional;
    }
}
