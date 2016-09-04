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

import groovy.json.JsonSlurper;
import spock.lang.Specification

import javax.annotation.processing.Processor;

/**
 * Created by kowalzik on 31.08.2016.
 */
class TypeScriptEndpointProcessorSpec extends Specification {

    def "simple Endpoint with int get()"() {
        when: "a simple Endpoint is compiled"
        CompilerTestHelper.compileTestCase(Arrays.<Processor>asList(new TypeScriptEndpointProcessor()), "/org/leandreck/endpoints/case1/Endpoint.java")
        def defaultPathBase = new File( "." ).getCanonicalPath()
        println "Current dir: $defaultPathBase"
        def model = new JsonSlurper().parse(new File("$defaultPathBase/target/generated-sources/annotations/Endpoint.ts"))

        then: "the scanned model should be correct"
        model.serviceName == "Endpoint"
        model.serviceUrl == "/api"
        model.methodCount == 1
        model.methods[0].name == "getInt"
        model.methods[0].url == "/int"
        model.methods[0].httpMethods == ["get"]
    }
}