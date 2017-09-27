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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allow changing the templates that will be used for code generation, by specifying their
 * location in the classpath.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface TypeScriptTemplatesConfiguration {
	String DEFAULT_API_MODULE = "/org/leandreck/endpoints/templates/typescript/apimodule.ftl";
	String DEFAULT_ENUMERATION = "/org/leandreck/endpoints/templates/typescript/enum.ftl";
	String DEFAULT_INDEX = "/org/leandreck/endpoints/templates/typescript/index.ftl";
	String DEFAULT_INTERFACE = "/org/leandreck/endpoints/templates/typescript/interface.ftl";
	String DEFAULT_ENDPOINT = "/org/leandreck/endpoints/templates/typescript/service.ftl";

	/**
	 * Template used to generate the Angular API module.
	 * @return By default returns "/org/leandreck/endpoints/templates/typescript/apimodule.ftl"
	 */
	String apimodule() default DEFAULT_API_MODULE;

	/**
	 * Template that will be used to generate an enum.
	 * @return By default returns "/org/leandreck/endpoints/templates/typescript/enum.ftl"
	 */
	String enumeration() default DEFAULT_ENUMERATION;

	/**
	 * Entrypoint for all the the TypeScript generated classes.
	 * @return By default returns "/org/leandreck/endpoints/templates/typescript/index.ftl"
	 */
	String index() default DEFAULT_INDEX;

	/**
	 * Template used when generating a TypeScript interface.
	 * @return By default returns "/org/leandreck/endpoints/templates/typescript/interface.ftl"
	 */
	String interfaces() default DEFAULT_INTERFACE;

	/**
	 * Template used when generating the actual endpoint.
	 * @return By default returns "/org/leandreck/endpoints/templates/typescript/service.ftl"
	 */
	String endpoint() default DEFAULT_ENDPOINT;
}
