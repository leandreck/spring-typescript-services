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
package org.leandreck.endpoints.processor

import org.leandreck.endpoints.annotations.TypeScriptTemplatesConfiguration
import org.leandreck.endpoints.processor.config.MultipleConfigurationsFoundException
import org.leandreck.endpoints.processor.config.TemplateConfiguration
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.tools.Diagnostic

import static org.hamcrest.Matchers.any
import static org.hamcrest.Matchers.containsInAnyOrder
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

@Narrative('''Unit Test for TypeScriptEndpointProcessor checking correct behaviour of utility Methods.''')
@Title("TypeScriptEndpointProcessor Unit Test")
@Subject(TypeScriptEndpointProcessor)
class TypeScriptEndpointProcessorUnitSpec extends Specification {

    def "printConfigurationErrors should print no ERROR when MultipleConfigurationsFoundException contains no Elements"() {
        given: "a TypeScriptEndpointProcessor and a MultipleConfigurationsFoundException containing no Elements annotated with @TypeScriptTemplatesConfiguration"

        ProcessingEnvironment environment = Stub ProcessingEnvironment
        Messager messager = Mock Messager
        environment.messager >> messager
        TypeScriptEndpointProcessor endpointProcessor = new TypeScriptEndpointProcessor()
        endpointProcessor.init(environment)

        MultipleConfigurationsFoundException mcfe = new MultipleConfigurationsFoundException(Collections.emptySet())

        when: "printConfigurationErrors is called"
        endpointProcessor.printConfigurationErrors(mcfe)

        then:
        0 * messager.printMessage(_, _,)
        0 * messager.printMessage(_, _, _)
        0 * messager.printMessage(_, _, _, _)
        0 * messager.printMessage(_, _, _, _, _)
    }

    def "printConfigurationErrors should print one ERROR for every Configuration-Element in MultipleConfigurationsFoundException"() {
        given: "a TypeScriptEndpointProcessor and a MultipleConfigurationsFoundException containing three Elements annotated with @TypeScriptTemplatesConfiguration"

        ProcessingEnvironment environment = Stub ProcessingEnvironment
        Messager messager = Mock Messager
        environment.messager >> messager
        TypeScriptEndpointProcessor endpointProcessor = new TypeScriptEndpointProcessor()
        endpointProcessor.init(environment)

        def setOfElements = [createElement(), createElement(), createElement()] as Set

        MultipleConfigurationsFoundException mcfe = new MultipleConfigurationsFoundException(setOfElements)

        when: "printConfigurationErrors is called"
        endpointProcessor.printConfigurationErrors(mcfe)

        then:
        setOfElements.size() * messager.printMessage(Diagnostic.Kind.ERROR, _, _, _)
    }

    Element createElement() {
        TypeScriptTemplatesConfiguration annotation = Stub TypeScriptTemplatesConfiguration
        Element element = Stub Element
        element.getAnnotation(TypeScriptTemplatesConfiguration.class) >> annotation
        element.getAnnotationMirrors() >> Collections.singletonList(Stub(AnnotationMirror))
        return element
    }
}
