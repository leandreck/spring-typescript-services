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
package org.leandreck.endpoints.processor.config

import org.leandreck.endpoints.annotations.TypeScriptTemplatesConfiguration
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

import static org.hamcrest.Matchers.containsInAnyOrder
import static spock.util.matcher.HamcrestSupport.that

@Narrative('''Unit Test for TemplateConfiguration checking correct TemplateConfiguration creation and possible Exceptions.
''')
@Title("TemplateConfiguration Unit Test")
@Subject(TemplateConfiguration)
class TemplateConfigurationUnitSpec extends Specification {

    def "If no @TypeScriptTemplatesConfiguration is found a TemplateConfiguration with defaults is created"() {
        given: "a RoundEnvironment containing no Elements annotated with @TypeScriptTemplatesConfiguration"
        RoundEnvironment environment = Stub RoundEnvironment
        environment.getElementsAnnotatedWith(TypeScriptTemplatesConfiguration.class) >> Collections.emptyList()

        when:
        TemplateConfiguration actual = TemplateConfiguration.buildFromEnvironment(environment)

        then:
        notThrown MultipleConfigurationsFoundException

        and:
        actual.apiModuleTemplate == TypeScriptTemplatesConfiguration.DEFAULT_API_MODULE
        actual.endpointTemplate == TypeScriptTemplatesConfiguration.DEFAULT_ENDPOINT
        actual.enumTemplate == TypeScriptTemplatesConfiguration.DEFAULT_ENUMERATION
        actual.indexTemplate == TypeScriptTemplatesConfiguration.DEFAULT_INDEX
        actual.interfaceTemplate == TypeScriptTemplatesConfiguration.DEFAULT_INTERFACE
    }

    def "If one @TypeScriptTemplatesConfiguration is found a TemplateConfiguration is created"() {
        given: "a RoundEnvironment containing one Element annotated with a customized @TypeScriptTemplatesConfiguration"
        TypeScriptTemplatesConfiguration configuration = Stub TypeScriptTemplatesConfiguration
        configuration.apimodule() >> "api"
        configuration.endpoint() >> "endoint"
        configuration.enumeration() >> "enumeration"
        configuration.index() >> "index"
        configuration.interfaces() >> "interfaces"

        Element element = Stub Element
        element.getAnnotation(TypeScriptTemplatesConfiguration.class) >> configuration

        RoundEnvironment environment = Stub RoundEnvironment
        environment.getElementsAnnotatedWith(TypeScriptTemplatesConfiguration.class) >> Collections.singletonList(element)

        when:
        TemplateConfiguration actual = TemplateConfiguration.buildFromEnvironment(environment)

        then:
        notThrown MultipleConfigurationsFoundException

        and:
        actual.apiModuleTemplate == configuration.apimodule()
        actual.endpointTemplate == configuration.endpoint()
        actual.enumTemplate == configuration.enumeration()
        actual.indexTemplate == configuration.index()
        actual.interfaceTemplate == configuration.interfaces()
    }

    def "If multiple @TypeScriptTemplatesConfiguration are found throw MultipleConfigurationsFoundException"() {
        given: "a RoundEnvironment containing two Elements annotated with @TypeScriptTemplatesConfiguration"

        Element element1 = Stub(Element)
        Element element2 = Stub(Element)

        RoundEnvironment environment = Stub RoundEnvironment
        environment.getElementsAnnotatedWith(TypeScriptTemplatesConfiguration.class) >> [ element1, element2 ]

        when:
        TemplateConfiguration.buildFromEnvironment(environment)

        then:
        MultipleConfigurationsFoundException mcfe = thrown()

        and:
        that mcfe.elementsWithConfiguration, containsInAnyOrder(element1, element2)
    }

}
