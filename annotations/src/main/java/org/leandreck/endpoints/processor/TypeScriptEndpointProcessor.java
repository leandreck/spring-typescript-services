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

import static java.util.stream.Collectors.toList;
import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.StandardLocation;

import org.leandreck.endpoints.annotations.TypeScriptEndpoint;
import org.leandreck.endpoints.annotations.TypeScriptIgnore;
import org.leandreck.endpoints.annotations.TypeScriptType;
import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.leandreck.endpoints.processor.model.EndpointNode;
import org.leandreck.endpoints.processor.model.EndpointNodeFactory;
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.printer.Engine;
import org.leandreck.endpoints.processor.printer.TypesPackage;

import freemarker.template.TemplateException;

/**
 * Annotation Processor for TypeScript-Annotations.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TypeScriptEndpointProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotations = new LinkedHashSet<>();
        annotations.add(TypeScriptEndpoint.class.getCanonicalName());
        annotations.add(TypeScriptIgnore.class.getCanonicalName());
        annotations.add(TypeScriptType.class.getCanonicalName());

        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(TypeScriptEndpoint.class);

        final List<TypeElement> endpoints = annotated.stream()
                .filter(element -> ElementKind.CLASS.equals(element.getKind()))
                .map(element -> (TypeElement) element)
//                .map(element -> factory.createEndpointNode(element))
                .collect(toList());

        if (!endpoints.isEmpty()) {
            TemplateConfiguration templateConfiguration = TemplateConfiguration.buildFromEnvironment(roundEnv);

            processEndpoints(templateConfiguration, endpoints);
        }

        return true;
    }

    private void processEndpoints(TemplateConfiguration templateConfiguration, final List<TypeElement> endpointElements) {
        final Types typeUtils = processingEnv.getTypeUtils();
        Engine engine = new Engine(templateConfiguration);
        EndpointNodeFactory factory = new EndpointNodeFactory(templateConfiguration, typeUtils, processingEnv.getElementUtils());

        final Set<EndpointNode> endpointNodes = new HashSet<>(endpointElements.size());
        //endpoint
        for (final TypeElement element : endpointElements) {
            final EndpointNode endpointNode = factory.createEndpointNode(element);
            try (final Writer out = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", toTSFilename(endpointNode.getServiceName(), ".generated.ts"), element).openWriter()) {
                engine.processEndpoint(endpointNode, out);
            } catch (TemplateException tex) {
                printMessage(element, element.getAnnotationMirrors().stream().findFirst().orElse(null), "Could not process template %s. Cause: %s", endpointNode.getTemplate(), tex.getMessage());
            } catch (IOException ioe) {
                printMessage(element, element.getAnnotationMirrors().stream().findFirst().orElse(null), "Could not load template %s. Cause: %s", endpointNode.getTemplate(), ioe.getMessage());
            }
            endpointNodes.add(endpointNode);
        }
        final TypeElement[] endpointArray = endpointElements.toArray(new TypeElement[endpointElements.size()]);
        final Set<TypeNode> typeNodes = endpointNodes.stream()
                .flatMap(endpointNode -> endpointNode.getTypes().stream())
                .collect(Collectors.toSet());

        final TypesPackage typesPackage = new TypesPackage(endpointNodes, typeNodes);

        //index.ts
        try (final Writer out = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "index.ts", endpointArray).openWriter()) {
            engine.processIndexTs(typesPackage, out);
        } catch (TemplateException tex) {
            printMessage("Could not process template index.ts. Cause: %s", tex.getMessage());
        } catch (IOException ioe) {
            printMessage("Could not load template index.ts. Cause: %s", ioe.getMessage());
        }

        //api.module.ts
        try (final Writer out = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "api.module.ts", endpointArray).openWriter()) {
            engine.processModuleTs(typesPackage, out);
        } catch (TemplateException tex) {
            printMessage("Could not process template api.module.ts. Cause: %s", tex.getMessage());
        } catch (IOException ioe) {
            printMessage("Could not load template api.module.ts. Cause: %s", ioe.getMessage());
        }

        //Types
        for (final TypeNode type : typeNodes) {
            try (final Writer out = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", toTSFilename(type.getTypeName(), ".model.generated.ts"), endpointArray).openWriter()) {
                engine.processTypeScriptTypeNode(type, out);
            } catch (TemplateException tex) {
                printMessage("Could not process template %s for TypeNode %s. Cause: %s", type.getTemplate(), type.getTypeName(), tex.getMessage());
            } catch (IOException ioe) {
                printMessage("Could not load template %s for TypeNode %s. Cause: %s", type.getTemplate(), type.getTypeName(), ioe.getMessage());
            }
        }
    }

    private void printMessage(String msg, Object... args) {
        messager.printMessage(ERROR, String.format(msg, args));
    }

    private void printMessage(Element element, AnnotationMirror annotationMirror, String msg, Object... args) {
        messager.printMessage(ERROR, String.format(msg, args), element, annotationMirror);
    }

    private String toTSFilename(final String typeName, final String suffix) {
        return typeName.toLowerCase() + suffix;
    }

}