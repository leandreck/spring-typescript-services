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
package org.leandreck.endpoints.annotations;

import java.lang.annotation.*;

/**
 * Annotate Fields with {@link TypeScriptType} to specify a custom template or override default mappings.<br>
 * <br>
 * {@code @TypeScriptType("any")}<br>
 * {@code private Foobar foo;}<br>
 * will result in<br>
 * {@code foo : any}<br>
 * <br>
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface TypeScriptType {

    /**
     * The name of the interface. Defaults to the name of the java type annotated with {@link TypeScriptType}.
     *
     * @return name.
     */
    String value() default "";

    /**
     * Template to use for generating TypeScript-files for this specific {@link TypeScriptType}, this overwrites any defaults.<br>
     * You can configure a default Template for all TypeScriptType in {@link TypeScriptTemplatesConfiguration}.<br>
     * If none is specified the default-template will be used.<br>
     * Default template is located at "/org/leandreck/endpoints/templates/typescript/interface.ftl".<br>
     *
     * @return classpath location of the template
     * @see TypeScriptTemplatesConfiguration
     */
    String template() default "";
}
