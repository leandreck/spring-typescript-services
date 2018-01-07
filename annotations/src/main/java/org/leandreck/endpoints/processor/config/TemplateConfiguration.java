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
import org.leandreck.endpoints.processor.model.PrintConfiguration;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.util.Set;

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

	private final PrintConfiguration globalPrintConfiguration;

	private TemplateConfiguration(final String apiModuleTemplate,
								  final String enumerationTemplate,
								  final String indexTemplate,
								  final String interfaceTemplate,
								  final String endpointTemplate,
								  final PrintConfiguration globalPrintConfiguration) {
		this.apiModuleTemplate = apiModuleTemplate;
		this.enumerationTemplate = enumerationTemplate;
		this.indexTemplate = indexTemplate;
		this.interfaceTemplate = interfaceTemplate;
		this.endpointTemplate = endpointTemplate;
		this.globalPrintConfiguration = globalPrintConfiguration;
	}

	/**
	 * Create a template paths configuration by scanning the classpath for the configuration
	 * annotation {@link TypeScriptTemplatesConfiguration}.
	 *
	 * @param roundEnv {@link RoundEnvironment}
	 * @return properly configured instance
	 * @throws MultipleConfigurationsFoundException if more than one {@link TypeScriptTemplatesConfiguration} is found.
	 */
	public static TemplateConfiguration buildFromEnvironment(RoundEnvironment roundEnv) throws MultipleConfigurationsFoundException {
		Set<? extends Element> configurationAnnotation =
				roundEnv.getElementsAnnotatedWith(TypeScriptTemplatesConfiguration.class);

		if (configurationAnnotation != null && configurationAnnotation.size() > 1) {
			throw new MultipleConfigurationsFoundException(configurationAnnotation);
		}

		// we don't have any configuration, just use the defaults.
		if (configurationAnnotation == null || configurationAnnotation.isEmpty()) {
			return createDefaultTemplateConfiguration();
		}

		TypeScriptTemplatesConfiguration annotation = configurationAnnotation
				.iterator().next()
				.getAnnotation(TypeScriptTemplatesConfiguration.class);

		return new TemplateConfiguration(
				definedValue(annotation.apimodule(), TypeScriptTemplatesConfiguration.DEFAULT_API_MODULE),
				definedValue(annotation.enumeration(), TypeScriptTemplatesConfiguration.DEFAULT_ENUMERATION),
				definedValue(annotation.index(), TypeScriptTemplatesConfiguration.DEFAULT_INDEX),
				definedValue(annotation.interfaces(), TypeScriptTemplatesConfiguration.DEFAULT_INTERFACE),
				definedValue(annotation.endpoint(), TypeScriptTemplatesConfiguration.DEFAULT_ENDPOINT),
				new PrintConfiguration(
					annotation.useSuffixes(),
					definedValue(annotation.suffixGet(), TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_GET),
					definedValue(annotation.suffixHead(), TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_HEAD),
					definedValue(annotation.suffixDelete(), TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_DELETE),
					definedValue(annotation.suffixOptions(), TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_OPTIONS),
					definedValue(annotation.suffixPatch(), TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_PATCH),
					definedValue(annotation.suffixPost(), TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_POST),
					definedValue(annotation.suffixPut(), TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_PUT),
					definedValue(annotation.suffixTrace(), TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_TRACE)
				));
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

    public PrintConfiguration getGlobalPrintConfiguration() {
        return globalPrintConfiguration;
    }

	private static TemplateConfiguration createDefaultTemplateConfiguration() {
		return new TemplateConfiguration(
				TypeScriptTemplatesConfiguration.DEFAULT_API_MODULE,
				TypeScriptTemplatesConfiguration.DEFAULT_ENUMERATION,
				TypeScriptTemplatesConfiguration.DEFAULT_INDEX,
				TypeScriptTemplatesConfiguration.DEFAULT_INTERFACE,
				TypeScriptTemplatesConfiguration.DEFAULT_ENDPOINT,
				createDefaultPrintConfiguration());
	}

	private static PrintConfiguration createDefaultPrintConfiguration() {
		return new PrintConfiguration(
				TypeScriptTemplatesConfiguration.DEFAULT_USE_SUFFIXES,
				TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_GET,
				TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_HEAD,
				TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_DELETE,
				TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_OPTIONS,
				TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_PATCH,
				TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_POST,
				TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_PUT,
				TypeScriptTemplatesConfiguration.DEFAULT_SUFFIX_TRACE);
	}
}
