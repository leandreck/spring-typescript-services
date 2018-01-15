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
package org.leandreck.endpoints.processor.config;

import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;

/**
 * Exception indicating that more than one configured {@link org.leandreck.endpoints.annotations.TypeScriptTemplatesConfiguration} exists inside the current compilation unit.
 */
public class MultipleConfigurationsFoundException extends Exception {

    private final transient Set<Element> elementsWithConfiguration;

    /**
     * @param elementsWithConfiguration all {@link Element}s annotated with {@link org.leandreck.endpoints.annotations.TypeScriptTemplatesConfiguration}
     */
    public MultipleConfigurationsFoundException(final Set<? extends Element> elementsWithConfiguration) {
        this.elementsWithConfiguration = new HashSet<>(elementsWithConfiguration);
    }

    /**
     * All {@link Element}s annotated with {@link org.leandreck.endpoints.annotations.TypeScriptTemplatesConfiguration}.
     * @return set of {@link Element}
     */
    public Set<Element> getElementsWithConfiguration() {
        return elementsWithConfiguration;
    }
}
