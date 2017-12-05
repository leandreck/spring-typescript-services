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
 */
public class RequestMapping {

    private final RequestMethod[] method;
    private final String[] produces;
    private final String[] value;

    public RequestMapping(final RequestMethod[] method, final String[] produces, final String[] value) {
        this.method = method == null ? new RequestMethod[0] : method;
        this.produces = produces == null ? new String[0] : produces;
        this.value = value == null ? new String[0] : value;
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
