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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.leandreck.endpoints.processor.model.EndpointNode;
import org.leandreck.endpoints.processor.model.TypeNode;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 21.08.2016.
 */
public class Engine {

    private final Configuration cfg;

    public Engine() {
        // Create your Configuration instance, and specify if up to what FreeMarker
        // version (here 2.3.25) do you want to apply the fixes that are not 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        this.cfg = new Configuration(Configuration.VERSION_2_3_23);

        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.setDefaultEncoding("UTF-8");

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:
        cfg.setClassForTemplateLoading(this.getClass(), "/");


        // Sets how errors will appear.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.setLogTemplateExceptions(false);
    }

    public void processEndpoint(EndpointNode clazz, Set<TypeNode> types, Writer out) throws IOException, TemplateException {
        final Template imports = this.cfg.getTemplate("/org/leandreck/endpoints/templates/typescript/imports.ftl");
        final Map<String, Object> root = new HashMap<>();
        root.put("types", types);
        imports.process(root, out);

        final Template service = this.cfg.getTemplate(clazz.getTemplate());
        service.process(clazz, out);
        out.append("\n");
    }

    public void processTypeScriptTypeNode(TypeNode node, Writer out) throws IOException, TemplateException {
        final Template temp = this.cfg.getTemplate(node.getTemplate());
        temp.process(node, out);
        out.append("\n");
    }
}
