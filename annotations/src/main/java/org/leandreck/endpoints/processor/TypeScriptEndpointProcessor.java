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
package org.leandreck.endpoints.processor;

import freemarker.template.TemplateException;
import org.leandreck.endpoints.annotations.TypeScriptEndpoint;
import org.leandreck.endpoints.annotations.TypeScriptIgnore;
import org.leandreck.endpoints.annotations.TypeScriptType;
import org.leandreck.endpoints.processor.model.EndpointNode;
import org.leandreck.endpoints.processor.model.EndpointNodeFactory;
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.printer.Engine;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 19.08.2016.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TypeScriptEndpointProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private EndpointNodeFactory factory;
    private Engine engine;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        this.engine = new Engine();
        factory = new EndpointNodeFactory(processingEnv.getTypeUtils());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(TypeScriptEndpoint.class.getCanonicalName());
        annotataions.add(TypeScriptIgnore.class.getCanonicalName());
        annotataions.add(TypeScriptType.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        final Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(TypeScriptEndpoint.class);

        annotated.stream().parallel()
                .filter(element -> ElementKind.CLASS.equals(element.getKind()))
                .map(element -> (TypeElement) element)
                .forEach(this::processEndpoint);

        return true;
    }

    private static Collection<TypeNode> flatten(TypeNode root) {
        final Set<TypeNode> typeSet = root.getChildren().stream().parallel()
                .map(TypeScriptEndpointProcessor::flatten)
                .flatMap(Collection::stream)
                .filter(c -> !c.isMappedType())
                .collect(toSet());
        typeSet.add(root);
        return typeSet;
    }

    private void processEndpoint(final TypeElement typeElement) {
        final EndpointNode endpointNode = factory.createEndpointNode(typeElement);
        Writer out = null;
        try {
            final FileObject file = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", endpointNode.getServiceName() + ".ts", typeElement);
            out = file.openWriter();

            final Set<TypeNode> types = endpointNode.getMethods().stream().parallel()
                    .map(m -> m.getReturnType())
                    .map(TypeScriptEndpointProcessor::flatten)
                    .flatMap(Collection::stream)
                    .filter(c -> !c.isMappedType())
                    .filter(c -> !c.getTypeName().contains("["))
                    .collect(toSet());
            engine.processEndpoint(endpointNode, types, out);
            out.close();

            for (TypeNode type : types) {
                if (type.getTypeName().contains("[")) {
                    continue;
                }
                final FileObject typeFile = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", type.getTypeName() + ".model.ts", typeElement);
                out = typeFile.openWriter();
                engine.processTypeScriptTypeNode(type, out);
                out.close();
            }
        } catch (IOException ioe) {
            printMessage(ERROR, typeElement, "Could not load template %s. Cause: %s", endpointNode.getTemplate(), ioe.getMessage());
        } catch (TemplateException tmex) {
            printMessage(ERROR, typeElement, "Could not process template %s. Cause: %s", endpointNode.getTemplate(), tmex.getMessage());
        } catch (Exception exc) {
            printMessage(ERROR, typeElement, "Something went wrong! Element: %s. Cause: %s", endpointNode.getTemplate(), exc.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException exc) {
                    printMessage(ERROR, typeElement, "Could not close writer to source file! Element: %s. Cause: %s", endpointNode.getTemplate(), exc.getMessage());
                }
            }
        }
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String msg, Object... args) {
        messager.printMessage(kind, String.format(msg, args), element);
    }

}