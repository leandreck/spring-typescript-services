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
package org.leandreck.endpoints.processor.model

import org.springframework.web.bind.annotation.*

import javax.lang.model.element.ExecutableElement

import static org.springframework.web.bind.annotation.RequestMethod.*

/**
 * Created by kowalzik on 16.10.2016.
 */
class RequestMappingFactory {

    def RequestMapping createRequestMappging(final ExecutableElement methodElement) {
        def methods = []
        def produces = []
        def value = []

        populate(methods, produces, value, methodElement.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class), null)
        populate(methods, produces, value, methodElement.getAnnotation(GetMapping.class), GET)
        populate(methods, produces, value, methodElement.getAnnotation(PostMapping.class), POST)
        populate(methods, produces, value, methodElement.getAnnotation(PutMapping.class), PUT)
        populate(methods, produces, value, methodElement.getAnnotation(DeleteMapping.class), DELETE)
        populate(methods, produces, value, methodElement.getAnnotation(PatchMapping.class), PATCH)

        final RequestMapping requestMapping = new RequestMapping(methods, produces, value);
        return requestMapping;
    }

    def populate(def methods, def produces, def value, def annotation, def requestMethod) {
        if (annotation != null) {
            try {
                def methods2Add = annotation.method()
                if (methods2Add != null) {
                    methods.addAll Arrays.<RequestMethod> asList(methods2Add)
                }
            } catch (MissingMethodException e) {
                //Ignored
            }

            if (requestMethod != null) {
                methods.add requestMethod
            }

            def produces2Add = annotation.produces()
            if (produces2Add != null) {
                produces.addAll Arrays.<RequestMethod> asList(produces2Add)
            }

            def value2Add = annotation.value()
            if (value2Add != null) {
                value.addAll Arrays.<RequestMethod> asList(value2Add)
            }
        }

    }

}
