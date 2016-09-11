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
import spock.lang.*

import javax.annotation.processing.Processor
import javax.tools.Diagnostic
import javax.tools.JavaFileObject
import java.nio.file.Files

/**
 * Created by kowalzik on 31.08.2016.
 */
@Narrative('''Integration Test for TypeScriptEndpointProcessor which compiles an
Endpoint.java with all possible combinations of Java return type, Http-Methods, ...
using a service.ftl for test purposes to transform the Java processing model into a json model
and checking this model afterwards.
''')
@Title("TypeScriptEndpointProcessor Integrations")
@Subject(TypeScriptEndpointProcessor)
class TypeScriptEndpointProcessorSpec extends Specification {

    @Shared
    def jsonSlurper
    @Shared
    def defaultPathBase

    def classFile

    def setupSpec() {
        jsonSlurper = new JsonSlurper()
        defaultPathBase = new File(".").getCanonicalPath()
        def annotationsTarget = new File("$defaultPathBase/target/generated-sources/annotations")
        Files.createDirectories(annotationsTarget.toPath())
    }

    def setup() {
        classFile = new File("$defaultPathBase/src/test/testcases/org/leandreck/endpoints/case1/Endpoint.java")
        Files.createDirectories(classFile.getParentFile().toPath())
        classFile.createNewFile()
    }

    def cleanup() {
        classFile.delete()
    }

    @Unroll
    def "simple Endpoint with one Method get() return type #returnType expecting #mappedType"() {
        given: "a simple Endpoint"
        classFile.text = getSourceCase1(returnType, returnValue)

        when: "a simple Endpoint is compiled"
        List<Diagnostic<? extends JavaFileObject>> diagnostics = CompilerTestHelper.compileTestCase(Arrays.<Processor> asList(new TypeScriptEndpointProcessor()), classFile)
        def model = jsonSlurper.parse(new File("$defaultPathBase/target/generated-sources/annotations/Endpoint.ts"))

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "the scanned model should be correct"
        with(model) {
            serviceName == "Endpoint"
            serviceUrl == "/api"
            methodCount == 1
            methods[0].name == "getInt"
            methods[0].url == "/int"
            methods[0].httpMethods == ["get"]
            methods[0].returnType == mappedType
        }

        cleanup: "remove test java source file"

        where: "possible simple values for case1 are"
        returnType             | returnValue                        || mappedType
        "void"                 | ""                                 || "Void"
        "byte"                 | "return 1"                         || "Number"
        "Byte"                 | "return 1"                         || "Number"
        "short"                | "return 1"                         || "Number"
        "Short"                | "return 1"                         || "Number"
        "int"                  | "return 1"                         || "Number"
        "Integer"              | "return 1"                         || "Number"
        "long"                 | "return 1"                         || "Number"
        "Long"                 | "return 1"                         || "Number"
        "float"                | "return 1"                         || "Number"
        "Float"                | "return 1"                         || "Number"
        "double"               | "return 1"                         || "Number"
        "Double"               | "return 1"                         || "Number"
        "java.math.BigDecimal" | "return java.math.BigDecimal.ZERO" || "Number"
        "java.math.BigInteger" | "return java.math.BigInteger.ZERO" || "Number"
        "char"                 | "return \"Some Value\""            || "String"
        "Character"            | "return \"Some Value\""            || "String"
        "String"               | "return \"Some Value\""            || "String"
        "boolean"              | "return true"                      || "Boolean"
        "Boolean"              | "return true"                      || "Boolean"
        //Date
//        mappings.put("Date", "Date");

        and: "possible array values for case1 are"
        "byte[]"                 | "return 1"                         || "Number[]"
        "Byte[]"                 | "return 1"                         || "Number[]"
        "short[]"                | "return 1"                         || "Number[]"
        "Short[]"                | "return 1"                         || "Number[]"
        "int[]"                  | "return 1"                         || "Number[]"
        "Integer[]"              | "return 1"                         || "Number[]"
        "long[]"                 | "return 1"                         || "Number[]"
        "Long[]"                 | "return 1"                         || "Number[]"
        "float[]"                | "return 1"                         || "Number[]"
        "Float[]"                | "return 1"                         || "Number[]"
        "double[]"               | "return 1"                         || "Number[]"
        "Double[]"               | "return 1"                         || "Number[]"
        "java.math.BigDecimal[]" | "return java.math.BigDecimal.ZERO" || "Number[]"
        "java.math.BigInteger[]" | "return java.math.BigInteger.ZERO" || "Number[]"
        "char[]"                 | "return \"Some Value\""            || "String[]"
        "Character[]"            | "return \"Some Value\""            || "String[]"
        "String[]"               | "return \"Some Value\""            || "String[]"
        "boolean[]"              | "return true"                      || "Boolean[]"
        "Boolean[]"              | "return true"                      || "Boolean[]"
        "Object[]"               | "return \"Some Value\""            || "any[]"

        and: "possible List values for case1 are"
        "java.util.List<Byte>"                 | "return java.util.Collections.emptyList()" || "Number[]"
        "java.util.List<Short>"                | "return java.util.Collections.emptyList()" || "Number[]"
        "java.util.List<Integer>"              | "return java.util.Collections.emptyList()" || "Number[]"
        "java.util.List<Long>"                 | "return java.util.Collections.emptyList()" || "Number[]"
        "java.util.List<Float>"                | "return java.util.Collections.emptyList()" || "Number[]"
        "java.util.List<Double>"               | "return java.util.Collections.emptyList()" || "Number[]"
        "java.util.List<java.math.BigDecimal>" | "return java.util.Collections.emptyList()" || "Number[]"
        "java.util.List<java.math.BigInteger>" | "return java.util.Collections.emptyList()" || "Number[]"
        "java.util.List<Character>"            | "return java.util.Collections.emptyList()" || "String[]"
        "java.util.List<String>"               | "return java.util.Collections.emptyList()" || "String[]"
        "java.util.List<Boolean>"              | "return java.util.Collections.emptyList()" || "Boolean[]"
        "java.util.List<?>"                    | "return java.util.Collections.emptyList()" || "any[]"
        "java.util.List<Object>"               | "return java.util.Collections.emptyList()" || "any[]"
        "java.util.List"                       | "return java.util.Collections.emptyList()" || "any[]"

        and: "possible Set values for case1 are"
        "java.util.Set<Byte>"                 | "return java.util.Collections.emptySet()" || "Number[]"
        "java.util.Set<Short>"                | "return java.util.Collections.emptySet()" || "Number[]"
        "java.util.Set<Integer>"              | "return java.util.Collections.emptySet()" || "Number[]"
        "java.util.Set<Long>"                 | "return java.util.Collections.emptySet()" || "Number[]"
        "java.util.Set<Float>"                | "return java.util.Collections.emptySet()" || "Number[]"
        "java.util.Set<Double>"               | "return java.util.Collections.emptySet()" || "Number[]"
        "java.util.Set<java.math.BigDecimal>" | "return java.util.Collections.emptySet()" || "Number[]"
        "java.util.Set<java.math.BigInteger>" | "return java.util.Collections.emptySet()" || "Number[]"
        "java.util.Set<Character>"            | "return java.util.Collections.emptySet()" || "String[]"
        "java.util.Set<String>"               | "return java.util.Collections.emptySet()" || "String[]"
        "java.util.Set<Boolean>"              | "return java.util.Collections.emptySet()" || "Boolean[]"
        "java.util.Set<?>"                    | "return java.util.Collections.emptySet()" || "any[]"
        "java.util.Set<Object>"               | "return java.util.Collections.emptySet()" || "any[]"
        "java.util.Set"                       | "return java.util.Collections.emptySet()" || "any[]"

        and: "possible other Collection-Types values for case1 are"
        "java.util.LinkedList<?>"               | "return new java.util.LinkedList()"               || "any[]"
        "java.util.ArrayList<?>"                | "return new java.util.ArrayList()"                || "any[]"
        "java.util.ArrayDeque<?>"               | "return new java.util.ArrayDeque()"               || "any[]"
        "java.util.Vector<?>"                   | "return new java.util.Vector()"                   || "any[]"
        "java.util.Queue<?>"                    | "return new java.util.Queue()"                    || "any[]"
        "java.util.Deque<?>"                    | "return new java.util.Deque()"                    || "any[]"
        "java.util.concurrent.BlockingQueue<?>" | "return new java.util.concurrent.BlockingQueue()" || "any[]"
        "java.util.concurrent.BlockingDeque<?>" | "return new java.util.concurrent.BlockingDeque()" || "any[]"
        "java.util.TreeSet<?>"                  | "return new java.util.TreeSet()"                  || "any[]"
        "java.util.HashSet<?>"                  | "return new java.util.HashSet()"                  || "any[]"

        and: "possible Map values for case1 are"
        "java.util.Map<Byte, ?>"                 | "return java.util.Collections.emptyMap()" || "{ [index: Number]: any }"
        "java.util.Map<Short, ?>"                | "return java.util.Collections.emptyMap()" || "{ [index: Number]: any }"
        "java.util.Map<Integer, ?>"              | "return java.util.Collections.emptyMap()" || "{ [index: Number]: any }"
        "java.util.Map<Long, ?>"                 | "return java.util.Collections.emptyMap()" || "{ [index: Number]: any }"
        "java.util.Map<Float, ?>"                | "return java.util.Collections.emptyMap()" || "{ [index: Number]: any }"
        "java.util.Map<Double, ?>"               | "return java.util.Collections.emptyMap()" || "{ [index: Number]: any }"
        "java.util.Map<java.math.BigDecimal, ?>" | "return java.util.Collections.emptyMap()" || "{ [index: Number]: any }"
        "java.util.Map<java.math.BigInteger, ?>" | "return java.util.Collections.emptyMap()" || "{ [index: Number]: any }"
        "java.util.Map<Character, ?>"            | "return java.util.Collections.emptyMap()" || "{ [index: String]: any }"
        "java.util.Map<String, ?>"               | "return java.util.Collections.emptyMap()" || "{ [index: String]: any }"
        "java.util.Map<Boolean, ?>"              | "return java.util.Collections.emptyMap()" || "{ [index: Boolean]: any }"
        "java.util.Map<Object, ?>"               | "return java.util.Collections.emptyMap()" || "{ [index: any]: any }"
        "java.util.Map<?, Byte>"                 | "return java.util.Collections.emptyMap()" || "{ [index: any]: Number }"
        "java.util.Map<?, Short>"                | "return java.util.Collections.emptyMap()" || "{ [index: any]: Number }"
        "java.util.Map<?, Integer>"              | "return java.util.Collections.emptyMap()" || "{ [index: any]: Number }"
        "java.util.Map<?, Long>"                 | "return java.util.Collections.emptyMap()" || "{ [index: any]: Number }"
        "java.util.Map<?, Float>"                | "return java.util.Collections.emptyMap()" || "{ [index: any]: Number }"
        "java.util.Map<?, Double>"               | "return java.util.Collections.emptyMap()" || "{ [index: any]: Number }"
        "java.util.Map<?, java.math.BigDecimal>" | "return java.util.Collections.emptyMap()" || "{ [index: any]: Number }"
        "java.util.Map<?, java.math.BigInteger>" | "return java.util.Collections.emptyMap()" || "{ [index: any]: Number }"
        "java.util.Map<?, Character>"            | "return java.util.Collections.emptyMap()" || "{ [index: any]: String }"
        "java.util.Map<?, String>"               | "return java.util.Collections.emptyMap()" || "{ [index: any]: String }"
        "java.util.Map<?, Boolean>"              | "return java.util.Collections.emptyMap()" || "{ [index: any]: Boolean }"
        "java.util.Map<?, Object>"               | "return java.util.Collections.emptyMap()" || "{ [index: any]: any }"
        "java.util.Map<?, ?>"                    | "return java.util.Collections.emptyMap()" || "{ [index: any]: any }"
        "java.util.Map"                          | "return java.util.Collections.emptyMap()" || "{ [index: any]: any }"

        and: "possible other Map-Types values for case1 are"
        "java.util.HashMap<?, ?>"                      | "return  new java.util.HashMap()"                      || "{ [index: any]: any }"
        "java.util.Hashtable<?, ?>"                    | "return  new java.util.Hashtable()"                    || "{ [index: any]: any }"
        "java.util.TreeMap<?, ?>"                      | "return  new java.util.TreeMap()"                      || "{ [index: any]: any }"
        "java.util.concurrent.ConcurrentHashMap<?, ?>" | "return  new java.util.concurrent.ConcurrentHashMap()" || "{ [index: any]: any }"
        "java.util.WeakHashMap<?, ?>"                  | "return  new java.util.WeakHashMap()"                  || "{ [index: any]: any }"

    }

    private static def getSourceCase1(String returnType, String returnValue) {
        return """
package org.leandreck.endpoints.case1;

import org.leandreck.endpoints.annotations.TypeScriptEndpoint;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@TypeScriptEndpoint(template = "/org/leandreck/endpoints/templates/testing/service.ftl")
@RestController
@RequestMapping("/api")
public class Endpoint {

    @RequestMapping(value = "/int", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody $returnType getInt() {
        $returnValue;
    }
}"""
    }

}