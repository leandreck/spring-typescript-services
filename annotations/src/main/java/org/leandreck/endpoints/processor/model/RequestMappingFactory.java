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

import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by kowalzik on 16.10.2016.
 */
public class RequestMappingFactory {

    public RequestMapping createRequestMappging(final ExecutableElement methodElement) {
        final ArrayList<RequestMethod> methods = new ArrayList<>();
        final ArrayList<String> produces = new ArrayList<>();
        final ArrayList<String> value = new ArrayList<>();

        populate(methods, produces, value, methodElement.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class), null);
        populate(methods, produces, value, methodElement.getAnnotation(GetMapping.class), GET);
        populate(methods, produces, value, methodElement.getAnnotation(PostMapping.class), POST);
        populate(methods, produces, value, methodElement.getAnnotation(PutMapping.class), PUT);
        populate(methods, produces, value, methodElement.getAnnotation(DeleteMapping.class), DELETE);
        populate(methods, produces, value, methodElement.getAnnotation(PatchMapping.class), PATCH);

        final RequestMapping requestMapping = new RequestMapping(methods.toArray(new RequestMethod[0]), produces.toArray(new String[0]), value.toArray(new String[0]));
        return requestMapping;
    }

    public void populate(final List<RequestMethod> methods, final List<String> produces, final List<String> value, final Annotation annotation, final RequestMethod requestMethod) {
        if (annotation != null) {
            if (annotation instanceof org.springframework.web.bind.annotation.RequestMapping) {
                org.springframework.web.bind.annotation.RequestMapping requestMapping = (org.springframework.web.bind.annotation.RequestMapping) annotation;
                final RequestMethod[] methods2Add = requestMapping.method();
                if (methods2Add != null) {
                    methods.addAll(Arrays.asList(methods2Add));
                }
            }

            if (requestMethod != null) {
                methods.add(requestMethod);
            }

            final String[] produces2Add = invokeMethod(annotation, "produces");
            if (produces2Add != null) {
                produces.addAll(Arrays.asList(produces2Add));
            }


            final String[] value2Add = invokeMethod(annotation, "value");
            if (value2Add != null) {
                value.addAll(Arrays.asList(value2Add));
            }
        }
    }

    private String[] invokeMethod(final Annotation annotation, final String methodName) {

        try {
            final Method method = annotation.getClass().getMethod(methodName);
            final String[] retVal = (String[]) method.invoke(annotation);
            return retVal;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return new String[0];
    }

}
