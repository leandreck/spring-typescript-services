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

import spock.lang.*

import javax.annotation.processing.Processor
import javax.tools.Diagnostic
import javax.tools.JavaFileObject
import java.nio.file.Files

/**
 * Created by kowalzik on 11.09.2016.
 */
@Narrative('''Integration Test for TypeScriptEndpointProcessor which compiles an
Endpoint.java with all possible combinations of possible Errors and checking Diagnostic afterwards.
''')
@Title("TypeScriptEndpointProcessor Error Integrations")
@Subject(TypeScriptEndpointProcessor)
class TypeScriptEndpointProcessorErrorSpec extends Specification {

    @Shared
    def defaultPathBase

    def setupSpec() {
        defaultPathBase = new File(".").getCanonicalPath()
        def annotationsTarget = new File("$defaultPathBase/target/generated-sources/annotations")
        Files.createDirectories(annotationsTarget.toPath())
    }

    def "if the template cannot be found an error should be printed"() {
        given: "an Endpoint with an invalid template in TypeScriptEndpoint-Annotation"
        def classFile = new File("$defaultPathBase/src/test/testcases/org/leandreck/endpoints/notemplate/Endpoint.java")

        when: "a simple Endpoint is compiled"
        List<Diagnostic<? extends JavaFileObject>> diagnostics = CompilerTestHelper.compileTestCase(Arrays.<Processor> asList(new TypeScriptEndpointProcessor()), classFile)

        then: "there should be one error on line 11"
        diagnostics.size() == 1
        diagnostics.every { d -> (Diagnostic.Kind.ERROR == d.kind) }
        diagnostics.get(0).getLineNumber() == 26
    }

}
