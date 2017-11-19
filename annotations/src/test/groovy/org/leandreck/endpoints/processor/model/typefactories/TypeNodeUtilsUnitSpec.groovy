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
package org.leandreck.endpoints.processor.model.typefactories

import org.leandreck.endpoints.annotations.TypeScriptType
import org.leandreck.endpoints.processor.config.TemplateConfiguration
import spock.lang.*

import javax.lang.model.element.TypeElement

@Title("RequestMappingFactory Unittests")
@Subject(TypeNodeUtils)
class TypeNodeUtilsUnitSpec extends Specification {

    @Shared
    TemplateConfiguration templateConfiguration = new TemplateConfiguration(
            "",
            "enumTemplate",
            "",
            "interfaceTemplate",
            ""
    )

    @Unroll
    def "If template() is #templateValue and TypeNodeKind is #typeNodeKind it will returned #expectedTemplate"() {
        given: "an Element annotated with TypeScriptEndpoint"
        TypeElement typeElement = Stub TypeElement
        TypeScriptType typeScriptType = Stub TypeScriptType
        typeScriptType.template() >> templateValue

        when:
        def actual = TypeNodeUtils.defineTemplate(expectedTemplate, typeScriptType, typeElement)

        then:
        notThrown MissingConfigurationTemplateException

        and:
        actual == expectedTemplate

        where: "possible values are"
        templateValue | typeNodeKind            || expectedTemplate
        ""            | TypeNodeKind.SIMPLE     || "interfaceTemplate"
        ""            | TypeNodeKind.ARRAY      || "interfaceTemplate"
        ""            | TypeNodeKind.COLLECTION || "interfaceTemplate"
        ""            | TypeNodeKind.MAP        || "interfaceTemplate"
        ""            | TypeNodeKind.ENUM       || "enumTemplate"

        "some value"  | TypeNodeKind.SIMPLE     || "some value"
        "some value"  | TypeNodeKind.ARRAY      || "some value"
        "some value"  | TypeNodeKind.COLLECTION || "some value"
        "some value"  | TypeNodeKind.MAP        || "some value"
        "some value"  | TypeNodeKind.ENUM       || "some value"
    }

    def "If templateConfiguration is null a MissingConfigurationTemplateException is thrown"() {
        given: "an Element annotated with TypeScriptEndpoint"
        TypeElement typeElement = Stub TypeElement

        when:
        def actual = TypeNodeUtils.defineTemplate(null, null, typeElement)

        then:
        MissingConfigurationTemplateException mcte = thrown()

        and:
        mcte.element == typeElement
    }
}
