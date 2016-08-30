/**
 * Copyright © 2016 Mathias Kowalzik (Mathias.Kowalzik@leandreck.org)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 19.08.2016.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface TypeScriptType {

    /**
     * The name of the interface. Defaults to the name of the type of the Field annotated with {@link TypeScriptType}.
     * @return name.
     */
    String value() default "";

    /**
     * Template to use for generating TypeScript-files for this {@link TypeScriptType}.
     * If none is specified the default-template will be used.
     * Default template is located at "/org/leandreck/endpoints/templates/typescript/interface.ftl".
     *
     * @return classpath location of the template
     */
    String template() default "/org/leandreck/endpoints/templates/typescript/interface.ftl";

}
