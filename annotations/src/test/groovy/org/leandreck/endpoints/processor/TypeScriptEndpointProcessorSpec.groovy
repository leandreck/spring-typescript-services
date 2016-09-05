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

import groovy.json.JsonSlurper
import spock.lang.Specification

import javax.annotation.processing.Processor
import java.nio.file.Files

/**
 * Created by kowalzik on 31.08.2016.
 */
class TypeScriptEndpointProcessorSpec extends Specification {

    def "simple Endpoint with only one Method get()"() {
        given: "a simple Endpoint"
        def defaultPathBase = new File(".").getCanonicalPath()
        def classFile = new File("$defaultPathBase/src/test/testcases/org/leandreck/endpoints/case1/Endpoint.java")
        Files.createDirectories(classFile.getParentFile().toPath())
        classFile.createNewFile()
        classFile.text = getSourceCase1(returnType, returnValue)

        when: "a simple Endpoint is compiled"
        CompilerTestHelper.compileTestCase(Arrays.<Processor> asList(new TypeScriptEndpointProcessor()), classFile)
        def model = new JsonSlurper().parse(new File("$defaultPathBase/target/generated-sources/annotations/Endpoint.ts"))

        then: "the scanned model should be correct"
        model.serviceName == "Endpoint"
        model.serviceUrl == "/api"
        model.methodCount == 1
        model.methods[0].name == "getInt"
        model.methods[0].url == "/int"
        model.methods[0].httpMethods == ["get"]
        model.methods[0].returnType == mappedType

        cleanup: "remove test java source file"
        classFile.delete()

        where: "possible values for case1 are"
        returnType   | returnValue       || mappedType
//        mappings.put("VOID", "Void");

        "byte"       | "1"               || "Number"
        "Byte"       | "1"               || "Number"
        "short"      | "1"               || "Number"
        "Short"      | "1"               || "Number"
        "int"        | "1"               || "Number"
        "Integer"    | "1"               || "Number"
        "long"       | "1"               || "Number"
        "Long"       | "1"               || "Number"
        "float"      | "1"               || "Number"
        "Float"      | "1"               || "Number"
        "double"     | "1"               || "Number"
        "Double"     | "1"               || "Number"
        "BigDecimal" | "BigDecimal.ZERO" || "Number"
        "BigInteger" | "BigInteger.ZERO" || "Number"
        "char"       | "\"Some Value\""  || "String"
        "Character"  | "\"Some Value\""  || "String"
        "String"     | "\"Some Value\""  || "String"
        "boolean"    | "true"            || "Boolean"
        "Boolean"    | "true"            || "Boolean"
        //Date
//        mappings.put("Date", "Date");
    }

    private def getSourceCase1(String returnType, String returnValue) {
        return "package org.leandreck.endpoints.case1;\n" +
                "\n" +
                "import org.leandreck.endpoints.annotations.TypeScriptEndpoint;\n" +
                "import org.springframework.http.MediaType;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.ResponseBody;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "import java.math.BigDecimal;\n" +
                "import java.math.BigInteger;\n" +
                "\n" +
                "import static org.springframework.web.bind.annotation.RequestMethod.GET;\n" +
                "\n" +
                "@TypeScriptEndpoint(template = \"/org/leandreck/endpoints/templates/testing/service.ftl\")\n" +
                "@RestController\n" +
                "@RequestMapping(\"/api\")\n" +
                "public class Endpoint {\n" +
                "\n" +
                "    @RequestMapping(value = \"/int\", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)\n" +
                "    public @ResponseBody $returnType getInt() {\n" +
                "        return $returnValue;\n" +
                "    }\n" +
                "}"
    }
}