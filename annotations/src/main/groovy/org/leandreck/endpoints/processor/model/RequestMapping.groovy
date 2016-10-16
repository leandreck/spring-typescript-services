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

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;

/**
 * Created by kowalzik on 16.10.2016.
 */
public class RequestMapping {

    private final RequestMethod[] method;
    private final String[] produces;
    private final String[] value;

    public RequestMapping(def method, def produces, def value) {
        this.method = method;
        this.produces = produces;
        this.value = value;
    }

    public RequestMethod[] method() {
        return Arrays.copyOf(method, method.length);
    }

    public String[] produces() {
        return Arrays.copyOf(produces, produces.length);
    }

    public String[] value() {
        return Arrays.copyOf(value, value.length);
    }

}
