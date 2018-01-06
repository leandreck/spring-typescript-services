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
package org.leandreck.endpoints.processor.printer;

import java.io.IOException;
import java.io.Writer;

import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.leandreck.endpoints.processor.model.EndpointNode;
import org.leandreck.endpoints.processor.model.TypeNode;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Handles Freemarker initialization and processing of the templates.
 */
public class Engine {

    private final Configuration freemarkerConfiguration;
    private final TemplateConfiguration templateConfiguration;

    public Engine(TemplateConfiguration configuration) {
        this.templateConfiguration = configuration;

        // Create your Configuration instance, and specify if up to what FreeMarker
        // version (here 2.3.25) do you want to apply the fixes that are not 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        this.freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_23);

        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        this.freemarkerConfiguration.setDefaultEncoding("UTF-8");

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:
        this.freemarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/");


        // Sets how errors will appear.
        this.freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        this.freemarkerConfiguration.setLogTemplateExceptions(false);
    }

    public void processEndpoint(final EndpointNode clazz, final Writer out) throws IOException, TemplateException {
        final Template service = this.freemarkerConfiguration.getTemplate(clazz.getTemplate());
        service.process(clazz, out);
        out.append("\n");
    }

    public void processIndexTs(final TypesPackage params, final Writer out) throws IOException, TemplateException {
        final Template service = this.freemarkerConfiguration.getTemplate(templateConfiguration.getIndexTemplate());
        service.process(params, out);
        out.append("\n");
    }

    public void processModuleTs(final TypesPackage params, final Writer out) throws IOException, TemplateException {
        final Template service = this.freemarkerConfiguration.getTemplate(templateConfiguration.getApiModuleTemplate());
        service.process(params, out);
        out.append("\n");
    }

    public void processTypeScriptTypeNode(final TypeNode node, final Writer out) throws IOException, TemplateException {
        final Template temp = this.freemarkerConfiguration.getTemplate(node.getTemplate());
        temp.process(node, out);
        out.append("\n");
    }

    public void processServiceConfig(final Writer out) throws IOException, TemplateException {
        final Template service = this.freemarkerConfiguration.getTemplate("/org/leandreck/endpoints/templates/typescript/serviceconfig.ftl");
        service.process(null, out);
        out.append("\n");
    }
}
