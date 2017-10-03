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

import org.leandreck.endpoints.annotations.TypeScriptTemplatesConfiguration;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.util.Set;
import java.util.stream.Collectors;

import static org.leandreck.endpoints.processor.model.StringUtil.definedValue;

/**
 * Holds the configuration with the location of the templates to be used
 * when generating the TypeScript code.
 */
public class TemplateConfiguration {
	private final String apiModuleTemplate;
	private final String endpointTemplate;
	private final String enumerationTemplate;
	private final String indexTemplate;
	private final String interfaceTemplate;

	public TemplateConfiguration(String apiModuleTemplate,
								 String enumerationTemplate,
								 String indexTemplate,
								 String interfaceTemplate,
								 String endpointTemplate) {
		this.apiModuleTemplate = apiModuleTemplate;
		this.enumerationTemplate = enumerationTemplate;
		this.indexTemplate = indexTemplate;
		this.interfaceTemplate = interfaceTemplate;
		this.endpointTemplate = endpointTemplate;
	}

	/**
	 * Create a template paths configuration by scanning the classpath for the configuration
	 * annotation.
	 *
	 * @param roundEnv {@link RoundEnvironment}
	 * @return properly configured instance
	 */
	public static TemplateConfiguration buildFromEnvironment(RoundEnvironment roundEnv) {
		Set<? extends Element> configurationAnnotation =
				roundEnv.getElementsAnnotatedWith(TypeScriptTemplatesConfiguration.class);

		if (configurationAnnotation != null && configurationAnnotation.size() > 1) {
			throw new IllegalStateException(String.format(
					"Multiple configurations found for the template locations. " +
					"Classes that match: %s",
					configurationAnnotation.stream()
						.map(it -> it.getSimpleName().toString())
						.collect(Collectors.toList())
					));
		}

		// we don't have any configuration, just use the defaults.
		if (configurationAnnotation == null || configurationAnnotation.isEmpty()) {
			return new TemplateConfiguration(
					TypeScriptTemplatesConfiguration.DEFAULT_API_MODULE,
					TypeScriptTemplatesConfiguration.DEFAULT_ENUMERATION,
					TypeScriptTemplatesConfiguration.DEFAULT_INDEX,
					TypeScriptTemplatesConfiguration.DEFAULT_INTERFACE,
					TypeScriptTemplatesConfiguration.DEFAULT_ENDPOINT
			);
		}

		TypeScriptTemplatesConfiguration annotation = configurationAnnotation
				.iterator().next()
				.getAnnotation(TypeScriptTemplatesConfiguration.class);

		return new TemplateConfiguration(
				definedValue(annotation.apimodule(), TypeScriptTemplatesConfiguration.DEFAULT_API_MODULE),
				definedValue(annotation.enumeration(), TypeScriptTemplatesConfiguration.DEFAULT_ENUMERATION),
				definedValue(annotation.index(), TypeScriptTemplatesConfiguration.DEFAULT_INDEX),
				definedValue(annotation.interfaces(), TypeScriptTemplatesConfiguration.DEFAULT_INTERFACE),
				definedValue(annotation.endpoint(), TypeScriptTemplatesConfiguration.DEFAULT_ENDPOINT)
		);
	}

	public String getEnumTemplate() {
		return enumerationTemplate;
	}

	public String getInterfaceTemplate() {
		return interfaceTemplate;
	}

	public String getEndpointTemplate() {
		return endpointTemplate;
	}

	public String getIndexTemplate() {
		return indexTemplate;
	}

	public String getApiModuleTemplate() {
		return apiModuleTemplate;
	}
}
