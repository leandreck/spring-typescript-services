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
import groovy.text.SimpleTemplateEngine
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.*

import javax.tools.Diagnostic
import java.nio.file.Files
import java.util.stream.Collectors

import static groovy.io.FileType.FILES
import static java.util.stream.Collectors.toList

/**
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
    @Shared
    def endpointsPath
    @Shared
    def annotationsTarget

    def setupSpec() {
        jsonSlurper = new JsonSlurper()
        defaultPathBase = new File(".").getCanonicalPath()
        endpointsPath = defaultPathBase + "/src/test/testcases/org/leandreck/endpoints"
        annotationsTarget = new File("$defaultPathBase/target/generated-sources/annotations")
        Files.createDirectories(annotationsTarget.toPath())
    }

    @Unroll
    def "simple Endpoint with one Method get() return type #returnType expecting #mappedType"() {
        given: "a simple Endpoint"
        def folder = "/case1"
        def sourceFile = getSourceFile("$folder/Endpoint.gstring", "$folder/Endpoint.java", [returnType: returnType, returnValue: returnValue])
        def destinationFolder = initFolder folder

        when: "a simple Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, sourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there must be no declared typescript interface file"
        def allTSFiles = getInterfaceFiles(destinationFolder)
        allTSFiles.size() == 0

        and: "the scanned model should be correct"
        def model = jsonSlurper.parse(new File("$annotationsTarget/$folder/endpoint.generated.ts"))
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
        sourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible simple values for case1 are"
        returnType                                 | returnValue                                               || mappedType
        "void"                                     | ""                                                        || "void"
        "byte"                                     | "return 1"                                                || "number"
        "Byte"                                     | "return 1"                                                || "number"
        "short"                                    | "return 1"                                                || "number"
        "Short"                                    | "return 1"                                                || "number"
        "int"                                      | "return 1"                                                || "number"
        "Integer"                                  | "return 1"                                                || "number"
        "long"                                     | "return 1"                                                || "number"
        "Long"                                     | "return 1"                                                || "number"
        "float"                                    | "return 1"                                                || "number"
        "Float"                                    | "return 1"                                                || "number"
        "double"                                   | "return 1"                                                || "number"
        "Double"                                   | "return 1"                                                || "number"
        "java.math.BigDecimal"                     | "return java.math.BigDecimal.ZERO"                        || "number"
        "java.math.BigInteger"                     | "return java.math.BigInteger.ZERO"                        || "number"
        "char"                                     | "return \"Some Value\""                                   || "string"
        "Character"                                | "return \"Some Value\""                                   || "string"
        "String"                                   | "return \"Some Value\""                                   || "string"
        "boolean"                                  | "return true"                                             || "boolean"
        "Boolean"                                  | "return true"                                             || "boolean"
        "java.util.Optional<Byte>"                 | "return java.util.Optional.of((byte)1)"                   || "number"
        "java.util.Optional<Short>"                | "return java.util.Optional.of((short)1)"                  || "number"
        "java.util.Optional<Integer>"              | "return java.util.Optional.of(1)"                         || "number"
        "java.util.Optional<Long>"                 | "return java.util.Optional.of(1L)"                        || "number"
        "java.util.Optional<Float>"                | "return java.util.Optional.of(1F)"                        || "number"
        "java.util.Optional<Double>"               | "return java.util.Optional.of(1D)"                        || "number"
        "java.util.Optional<java.math.BigDecimal>" | "return java.util.Optional.of(java.math.BigDecimal.ZERO)" || "number"
        "java.util.Optional<java.math.BigInteger>" | "return java.util.Optional.of(java.math.BigInteger.ZERO)" || "number"
        "java.util.Optional<Character>"            | "return java.util.Optional.of(\"Some Value\")"            || "string"
        "java.util.Optional<String>"               | "return java.util.Optional.of(\"Some Value\")"            || "string"
        "java.util.Optional<Boolean>"              | "return java.util.Optional.of(true)"                      || "boolean"

        and: "possible temporal types are"
        "java.util.Date"                          | "return new java.util.Date()"                             || "Date"
        "java.time.LocalDate"                     | "return java.time.LocalDate.now()"                        || "Date"
        "java.util.Optional<java.util.Date>"      | "return java.util.Optional.of(new java.util.Date())"      || "Date"
        "java.util.Optional<java.time.LocalDate>" | "return java.util.Optional.of(java.time.LocalDate.now())" || "Date"

        and: "possible array values for case1 are"
        "byte[]"                 | "return 1"                         || "number[]"
        "Byte[]"                 | "return 1"                         || "number[]"
        "short[]"                | "return 1"                         || "number[]"
        "Short[]"                | "return 1"                         || "number[]"
        "int[]"                  | "return 1"                         || "number[]"
        "Integer[]"              | "return 1"                         || "number[]"
        "long[]"                 | "return 1"                         || "number[]"
        "Long[]"                 | "return 1"                         || "number[]"
        "float[]"                | "return 1"                         || "number[]"
        "Float[]"                | "return 1"                         || "number[]"
        "double[]"               | "return 1"                         || "number[]"
        "Double[]"               | "return 1"                         || "number[]"
        "java.math.BigDecimal[]" | "return java.math.BigDecimal.ZERO" || "number[]"
        "java.math.BigInteger[]" | "return java.math.BigInteger.ZERO" || "number[]"
        "char[]"                 | "return \"Some Value\""            || "string[]"
        "Character[]"            | "return \"Some Value\""            || "string[]"
        "String[]"               | "return \"Some Value\""            || "string[]"
        "boolean[]"              | "return true"                      || "boolean[]"
        "Boolean[]"              | "return true"                      || "boolean[]"
        "Object[]"               | "return \"Some Value\""            || "any[]"

        "java.util.Optional<Byte[]>"                 | "return java.util.Optional.of(new Byte[0])"                      || "number[]"
        "java.util.Optional<Short[]>"                | "return java.util.Optional.of(new Short[0])"                     || "number[]"
        "java.util.Optional<Integer[]>"              | "return java.util.Optional.of(new Integer[0])"                   || "number[]"
        "java.util.Optional<Long[]>"                 | "return java.util.Optional.of(new Long[0])"                      || "number[]"
        "java.util.Optional<Float[]>"                | "return java.util.Optional.of(new Float[0])"                     || "number[]"
        "java.util.Optional<Double[]>"               | "return java.util.Optional.of(new Double[0])"                    || "number[]"
        "java.util.Optional<java.math.BigDecimal[]>" | "return java.util.Optional.of(new java.math.BigDecimal.ZERO[0])" || "number[]"
        "java.util.Optional<java.math.BigInteger[]>" | "return java.util.Optional.of(new java.math.BigInteger.ZERO[0])" || "number[]"
        "java.util.Optional<Character[]>"            | "return java.util.Optional.of(new Character[0])"                 || "string[]"
        "java.util.Optional<String[]>"               | "return java.util.Optional.of(new String[0])"                    || "string[]"
        "java.util.Optional<Boolean[]>"              | "return java.util.Optional.of(new Boolean[0])"                   || "boolean[]"
        "java.util.Optional<Object[]>"               | "return java.util.Optional.of(new Object[0])"                    || "any[]"

        //generic Arrays are Bullshit :-), maybe we should add a check in ArrayTypeNodeFactory
        "java.util.Optional<Byte>[]"                 | "return new java.util.Optional[0]" || "number[]"
        "java.util.Optional<Short>[]"                | "return new java.util.Optional[0]" || "number[]"
        "java.util.Optional<Integer>[]"              | "return new java.util.Optional[0]" || "number[]"
        "java.util.Optional<Long>[]"                 | "return new java.util.Optional[0]" || "number[]"
        "java.util.Optional<Float>[]"                | "return new java.util.Optional[0]" || "number[]"
        "java.util.Optional<Double>[]"               | "return new java.util.Optional[0]" || "number[]"
        "java.util.Optional<java.math.BigDecimal>[]" | "return new java.util.Optional[0]" || "number[]"
        "java.util.Optional<java.math.BigInteger>[]" | "return new java.util.Optional[0]" || "number[]"
        "java.util.Optional<Character>[]"            | "return new java.util.Optional[0]" || "string[]"
        "java.util.Optional<String>[]"               | "return new java.util.Optional[0]" || "string[]"
        "java.util.Optional<Boolean>[]"              | "return new java.util.Optional[0]" || "boolean[]"
        "java.util.Optional<Object>[]"               | "return new java.util.Optional[0]" || "any[]"

        and: "possible List values for case1 are"
        "java.util.List<Byte>"                 | "return java.util.Collections.emptyList()"                        || "number[]"
        "java.util.List<Short>"                | "return java.util.Collections.emptyList()"                        || "number[]"
        "java.util.List<Integer>"              | "return java.util.Collections.emptyList()"                        || "number[]"
        "java.util.List<Long>"                 | "return java.util.Collections.emptyList()"                        || "number[]"
        "java.util.List<Float>"                | "return java.util.Collections.emptyList()"                        || "number[]"
        "java.util.List<Double>"               | "return java.util.Collections.emptyList()"                        || "number[]"
        "java.util.List<java.math.BigDecimal>" | "return java.util.Collections.emptyList()"                        || "number[]"
        "java.util.List<java.math.BigInteger>" | "return java.util.Collections.emptyList()"                        || "number[]"
        "java.util.List<Character>"            | "return java.util.Collections.emptyList()"                        || "string[]"
        "java.util.List<String>"               | "return java.util.Collections.emptyList()"                        || "string[]"
        "java.util.List<Boolean>"              | "return java.util.Collections.emptyList()"                        || "boolean[]"
        "java.util.List<?>"                    | "return java.util.Collections.emptyList()"                        || "any[]"
        "java.util.List<Object>"               | "return java.util.Collections.emptyList()"                        || "any[]"
        "java.util.List"                       | "return java.util.Collections.emptyList()"                        || "any[]"
        "java.util.Optional<java.util.List>"   | "return java.util.Optional.of(java.util.Collections.emptyList())" || "any[]"

        and: "possible Set values for case1 are"
        "java.util.Set<Byte>"                 | "return java.util.Collections.emptySet()"                        || "number[]"
        "java.util.Set<Short>"                | "return java.util.Collections.emptySet()"                        || "number[]"
        "java.util.Set<Integer>"              | "return java.util.Collections.emptySet()"                        || "number[]"
        "java.util.Set<Long>"                 | "return java.util.Collections.emptySet()"                        || "number[]"
        "java.util.Set<Float>"                | "return java.util.Collections.emptySet()"                        || "number[]"
        "java.util.Set<Double>"               | "return java.util.Collections.emptySet()"                        || "number[]"
        "java.util.Set<java.math.BigDecimal>" | "return java.util.Collections.emptySet()"                        || "number[]"
        "java.util.Set<java.math.BigInteger>" | "return java.util.Collections.emptySet()"                        || "number[]"
        "java.util.Set<Character>"            | "return java.util.Collections.emptySet()"                        || "string[]"
        "java.util.Set<String>"               | "return java.util.Collections.emptySet()"                        || "string[]"
        "java.util.Set<Boolean>"              | "return java.util.Collections.emptySet()"                        || "boolean[]"
        "java.util.Set<?>"                    | "return java.util.Collections.emptySet()"                        || "any[]"
        "java.util.Set<Object>"               | "return java.util.Collections.emptySet()"                        || "any[]"
        "java.util.Set"                       | "return java.util.Collections.emptySet()"                        || "any[]"
        "java.util.Optional<java.util.Set>"   | "return java.util.Optional.of(java.util.Collections.emptySet())" || "any[]"

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
        "java.util.Map<Byte, ?>"                 | "return java.util.Collections.emptyMap()"                        || "{ [index: number]: any }"
        "java.util.Map<Short, ?>"                | "return java.util.Collections.emptyMap()"                        || "{ [index: number]: any }"
        "java.util.Map<Integer, ?>"              | "return java.util.Collections.emptyMap()"                        || "{ [index: number]: any }"
        "java.util.Map<Long, ?>"                 | "return java.util.Collections.emptyMap()"                        || "{ [index: number]: any }"
        "java.util.Map<Float, ?>"                | "return java.util.Collections.emptyMap()"                        || "{ [index: number]: any }"
        "java.util.Map<Double, ?>"               | "return java.util.Collections.emptyMap()"                        || "{ [index: number]: any }"
        "java.util.Map<java.math.BigDecimal, ?>" | "return java.util.Collections.emptyMap()"                        || "{ [index: number]: any }"
        "java.util.Map<java.math.BigInteger, ?>" | "return java.util.Collections.emptyMap()"                        || "{ [index: number]: any }"
        "java.util.Map<Character, ?>"            | "return java.util.Collections.emptyMap()"                        || "{ [index: string]: any }"
        "java.util.Map<String, ?>"               | "return java.util.Collections.emptyMap()"                        || "{ [index: string]: any }"
        "java.util.Map<Boolean, ?>"              | "return java.util.Collections.emptyMap()"                        || "{ [index: boolean]: any }"
        "java.util.Map<Object, ?>"               | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: any }"
        "java.util.Map<?, Byte>"                 | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: number }"
        "java.util.Map<?, Short>"                | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: number }"
        "java.util.Map<?, Integer>"              | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: number }"
        "java.util.Map<?, Long>"                 | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: number }"
        "java.util.Map<?, Float>"                | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: number }"
        "java.util.Map<?, Double>"               | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: number }"
        "java.util.Map<?, java.math.BigDecimal>" | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: number }"
        "java.util.Map<?, java.math.BigInteger>" | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: number }"
        "java.util.Map<?, Character>"            | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: string }"
        "java.util.Map<?, String>"               | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: string }"
        "java.util.Map<?, Boolean>"              | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: boolean }"
        "java.util.Map<?, Object>"               | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: any }"
        "java.util.Map<?, ?>"                    | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: any }"
        "java.util.Map"                          | "return java.util.Collections.emptyMap()"                        || "{ [index: any]: any }"
        "java.util.Optional<java.util.Map>"      | "return java.util.Optional.of(java.util.Collections.emptyMap())" || "{ [index: any]: any }"

        and: "possible other Map-Types values for case1 are"
        "java.util.HashMap<?, ?>"                      | "return  new java.util.HashMap()"                      || "{ [index: any]: any }"
        "java.util.Hashtable<?, ?>"                    | "return  new java.util.Hashtable()"                    || "{ [index: any]: any }"
        "java.util.TreeMap<?, ?>"                      | "return  new java.util.TreeMap()"                      || "{ [index: any]: any }"
        "java.util.concurrent.ConcurrentHashMap<?, ?>" | "return  new java.util.concurrent.ConcurrentHashMap()" || "{ [index: any]: any }"
        "java.util.WeakHashMap<?, ?>"                  | "return  new java.util.WeakHashMap()"                  || "{ [index: any]: any }"

    }

    @Unroll
    def "if a Method is annotated with TypeScriptIgnore it should be ignored"() {
        given: "an Endpoint with a TypeScriptIgnore annotated Method"
        def folder = "/ignored"
        def sourceFile = new File("$endpointsPath$folder/${ignoreClass}.java")
        def destinationFolder = initFolder folder

        when: "a simple Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, sourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there must be no declared typescript interface file"
        def allTSFiles = getInterfaceFiles(destinationFolder)
        allTSFiles.size() == 0

        and: "the scanned model should contain no method"
        def model = jsonSlurper.parse(new File("$annotationsTarget/$folder/${ignoreClass.toLowerCase()}.generated.ts"))
        with(model) {
            serviceName == ignoreClass
            serviceUrl == "/api"
            methodCount == 0
        }

        cleanup: "remove test java source file"
        // Do not delete the source files: sourceFile.delete(), because they are not generated in this testcase!
        destinationFolder.deleteDir()

        where: "possible class files with ignored Methods are"
        ignoreClass       || bogus
        "Annotated"       || ""
        "PackageMethod"   || ""
        "PrivateMethod"   || ""
        "ProtectedMethod" || ""
        "NoMapping"       || ""
        "NoJson"          || ""
        "NoJsonMultiple"  || ""
    }

    @Unroll
    def "each RequestMethod #httpMethod results in a specific httpMethod-Entry"() {
        given: "an Endpoint with a HttpMethod"
        def folder = "/httpmethods"
        def sourceFile = getSourceFile("$folder/Endpoint.gstring", "$folder/${httpMethod}.java", [httpMethod: httpMethod, sourceMethod: sourceMethod])
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, sourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there must be no declared typescript interface file"
        def allTSFiles = getInterfaceFiles(destinationFolder)
        allTSFiles.size() == 0

        and: "the scanned model should contain the httpmethod"
        def model = jsonSlurper.parse(new File("$annotationsTarget${folder}/${httpMethod.toLowerCase()}.generated.ts"))
        with(model) {
            serviceName == httpMethod
            serviceUrl == "/api"
            methodCount == 1
            getProperty("${httpMethod.toString().toLowerCase()}MethodCount") == 1
        }

        cleanup: "remove test java source file"
        sourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible http-Methods are all possible values from RequestMethod"
        httpMethod << methodsList()
        sourceMethod << sourceList()
    }

    def methodsList() {
        def methodsList = Arrays.stream(RequestMethod.values()).map({ m -> m.toString() }).collect(Collectors.toList())
        methodsList.add("GET")
        return methodsList
    }

    def sourceList() {
        def methodsList = Arrays.stream(RequestMethod.values()).map({ m -> "method = ${m.toString()}," }).collect(Collectors.toList())
        methodsList.add("")
        return methodsList
    }

    @Unroll
    def "simple Endpoint with declared return type with mapped type #type as field member"() {
        given: "an Endpoint with a HttpMethod returning a simple declared type"
        def folder = "/complex"
        def endpointSourceFile = new File(endpointsPath + folder + "/Endpoint.java")
        def simpleRootTypeSourceFile = getSourceFile("$folder/SimpleRootType.gstring", "$folder/SimpleRootType.java", [type: type])
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, endpointSourceFile, simpleRootTypeSourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there should only be one declared typescript interface file"
        def allTSFiles = new ArrayList<File>()
        destinationFolder.eachFileMatch FILES, ~/.*\.model\.generated\.ts/, { file -> allTSFiles << file }
        allTSFiles.size() == 1
        allTSFiles[0].name == "simpleroottype.model.generated.ts"

        and: "it should contain the mapped type for the declared field"
        def model = jsonSlurper.parse(new File("$annotationsTarget/$folder/simpleroottype.model.generated.ts"))
        with(model) {
            typeName == "SimpleRootType"
            children.size == 1
            children[0].fieldName == "field"
            children[0].type == mappedType
        }

        cleanup: "remove test java source file"
        simpleRootTypeSourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible simple values for type in SimpleRootType are"
        type                   || mappedType
        "byte"                 || "number"
        "Byte"                 || "number"
        "short"                || "number"
        "Short"                || "number"
        "int"                  || "number"
        "Integer"              || "number"
        "long"                 || "number"
        "Long"                 || "number"
        "float"                || "number"
        "Float"                || "number"
        "double"               || "number"
        "Double"               || "number"
        "java.math.BigDecimal" || "number"
        "java.math.BigInteger" || "number"
        "char"                 || "string"
        "Character"            || "string"
        "String"               || "string"
        "boolean"              || "boolean"
        "Boolean"              || "boolean"

        and: "possible temporal types are"
        "java.util.Date"      || "Date"
        "java.time.LocalDate" || "Date"

        and: "possible array values for case1 are"
        "byte[]"                 || "number[]"
        "Byte[]"                 || "number[]"
        "short[]"                || "number[]"
        "Short[]"                || "number[]"
        "int[]"                  || "number[]"
        "Integer[]"              || "number[]"
        "long[]"                 || "number[]"
        "Long[]"                 || "number[]"
        "float[]"                || "number[]"
        "Float[]"                || "number[]"
        "double[]"               || "number[]"
        "Double[]"               || "number[]"
        "java.math.BigDecimal[]" || "number[]"
        "java.math.BigInteger[]" || "number[]"
        "char[]"                 || "string[]"
        "Character[]"            || "string[]"
        "String[]"               || "string[]"
        "boolean[]"              || "boolean[]"
        "Boolean[]"              || "boolean[]"
        "Object[]"               || "any[]"

        and: "possible List values for case1 are"
        "java.util.List<Byte>"                 || "number[]"
        "java.util.List<Short>"                || "number[]"
        "java.util.List<Integer>"              || "number[]"
        "java.util.List<Long>"                 || "number[]"
        "java.util.List<Float>"                || "number[]"
        "java.util.List<Double>"               || "number[]"
        "java.util.List<java.math.BigDecimal>" || "number[]"
        "java.util.List<java.math.BigInteger>" || "number[]"
        "java.util.List<Character>"            || "string[]"
        "java.util.List<String>"               || "string[]"
        "java.util.List<Boolean>"              || "boolean[]"
        "java.util.List<?>"                    || "any[]"
        "java.util.List<Object>"               || "any[]"
        "java.util.List"                       || "any[]"

        and: "possible Set values for case1 are"
        "java.util.Set<Byte>"                 || "number[]"
        "java.util.Set<Short>"                || "number[]"
        "java.util.Set<Integer>"              || "number[]"
        "java.util.Set<Long>"                 || "number[]"
        "java.util.Set<Float>"                || "number[]"
        "java.util.Set<Double>"               || "number[]"
        "java.util.Set<java.math.BigDecimal>" || "number[]"
        "java.util.Set<java.math.BigInteger>" || "number[]"
        "java.util.Set<Character>"            || "string[]"
        "java.util.Set<String>"               || "string[]"
        "java.util.Set<Boolean>"              || "boolean[]"
        "java.util.Set<?>"                    || "any[]"
        "java.util.Set<Object>"               || "any[]"
        "java.util.Set"                       || "any[]"

        and: "possible other Collection-Types values for case1 are"
        "java.util.LinkedList<?>"               || "any[]"
        "java.util.ArrayList<?>"                || "any[]"
        "java.util.ArrayDeque<?>"               || "any[]"
        "java.util.Vector<?>"                   || "any[]"
        "java.util.Queue<?>"                    || "any[]"
        "java.util.Deque<?>"                    || "any[]"
        "java.util.concurrent.BlockingQueue<?>" || "any[]"
        "java.util.concurrent.BlockingDeque<?>" || "any[]"
        "java.util.TreeSet<?>"                  || "any[]"
        "java.util.HashSet<?>"                  || "any[]"

        and: "possible Map values for case1 are"
        "java.util.Map<Byte, ?>"                 || "{ [index: number]: any }"
        "java.util.Map<Short, ?>"                || "{ [index: number]: any }"
        "java.util.Map<Integer, ?>"              || "{ [index: number]: any }"
        "java.util.Map<Long, ?>"                 || "{ [index: number]: any }"
        "java.util.Map<Float, ?>"                || "{ [index: number]: any }"
        "java.util.Map<Double, ?>"               || "{ [index: number]: any }"
        "java.util.Map<java.math.BigDecimal, ?>" || "{ [index: number]: any }"
        "java.util.Map<java.math.BigInteger, ?>" || "{ [index: number]: any }"
        "java.util.Map<Character, ?>"            || "{ [index: string]: any }"
        "java.util.Map<String, ?>"               || "{ [index: string]: any }"
        "java.util.Map<Boolean, ?>"              || "{ [index: boolean]: any }"
        "java.util.Map<Object, ?>"               || "{ [index: any]: any }"
        "java.util.Map<?, Byte>"                 || "{ [index: any]: number }"
        "java.util.Map<?, Short>"                || "{ [index: any]: number }"
        "java.util.Map<?, Integer>"              || "{ [index: any]: number }"
        "java.util.Map<?, Long>"                 || "{ [index: any]: number }"
        "java.util.Map<?, Float>"                || "{ [index: any]: number }"
        "java.util.Map<?, Double>"               || "{ [index: any]: number }"
        "java.util.Map<?, java.math.BigDecimal>" || "{ [index: any]: number }"
        "java.util.Map<?, java.math.BigInteger>" || "{ [index: any]: number }"
        "java.util.Map<?, Character>"            || "{ [index: any]: string }"
        "java.util.Map<?, String>"               || "{ [index: any]: string }"
        "java.util.Map<?, Boolean>"              || "{ [index: any]: boolean }"
        "java.util.Map<?, Object>"               || "{ [index: any]: any }"
        "java.util.Map<?, ?>"                    || "{ [index: any]: any }"
        "java.util.Map"                          || "{ [index: any]: any }"

        and: "possible other Map-Types values for case1 are"
        "java.util.HashMap<?, ?>"                      || "{ [index: any]: any }"
        "java.util.Hashtable<?, ?>"                    || "{ [index: any]: any }"
        "java.util.TreeMap<?, ?>"                      || "{ [index: any]: any }"
        "java.util.concurrent.ConcurrentHashMap<?, ?>" || "{ [index: any]: any }"
        "java.util.WeakHashMap<?, ?>"                  || "{ [index: any]: any }"

    }

    @Unroll
    def "simple Endpoint with declared lombok param and return type with mapped type #type as field member"() {
        given: "an Endpoint with a HttpMethod accepting and returning a simple lombok type"
        def folder = "/lombok"
        def endpointSourceFile = new File(endpointsPath + folder + "/Endpoint.java")
        def dataTypeSourceFile = getSourceFile("$folder/DataType.gstring", "$folder/DataType.java", [type: type])
        def valueTypeSourceFile = getSourceFile("$folder/ValueType.gstring", "$folder/ValueType.java", [type: type])
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, endpointSourceFile, dataTypeSourceFile, valueTypeSourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there should only be one declared typescript interface file"
        def allTSFiles = new ArrayList<File>()
        destinationFolder.eachFileMatch FILES, ~/.*\.model\.generated\.ts/, { file -> allTSFiles << file }
        allTSFiles.size() == 2
        allTSFiles.stream().map({ e -> e.name }).collect(toList()).containsAll(["datatype.model.generated.ts", "valuetype.model.generated.ts"])

        and: "it should contain the mapped datatype for the declared field"
        def dataModel = jsonSlurper.parse(new File("$annotationsTarget/$folder/datatype.model.generated.ts"))
        with(dataModel) {
            typeName == "DataType"
            children.size == 1
            children[0].fieldName == "dataField"
            children[0].type == mappedType
        }

        and: "it should contain the mapped valuetype for the declared field"
        def valueModel = jsonSlurper.parse(new File("$annotationsTarget/$folder/valuetype.model.generated.ts"))
        with(valueModel) {
            typeName == "ValueType"
            children.size == 1
            children[0].fieldName == "valueField"
            children[0].type == mappedType
        }

        cleanup: "remove test java source file"
        dataTypeSourceFile.delete()
        valueTypeSourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible simple values for type in SimpleRootType are"
        type                   || mappedType
        "byte"                 || "number"
        "Byte"                 || "number"
        "short"                || "number"
        "Short"                || "number"
        "int"                  || "number"
        "Integer"              || "number"
        "long"                 || "number"
        "Long"                 || "number"
        "float"                || "number"
        "Float"                || "number"
        "double"               || "number"
        "Double"               || "number"
        "java.math.BigDecimal" || "number"
        "java.math.BigInteger" || "number"
        "char"                 || "string"
        "Character"            || "string"
        "String"               || "string"
        "boolean"              || "boolean"
        "Boolean"              || "boolean"

        and: "possible temporal types are"
        "java.util.Date"      || "Date"
        "java.time.LocalDate" || "Date"

        and: "possible array values for case1 are"
        "byte[]"                 || "number[]"
        "Byte[]"                 || "number[]"
        "short[]"                || "number[]"
        "Short[]"                || "number[]"
        "int[]"                  || "number[]"
        "Integer[]"              || "number[]"
        "long[]"                 || "number[]"
        "Long[]"                 || "number[]"
        "float[]"                || "number[]"
        "Float[]"                || "number[]"
        "double[]"               || "number[]"
        "Double[]"               || "number[]"
        "java.math.BigDecimal[]" || "number[]"
        "java.math.BigInteger[]" || "number[]"
        "char[]"                 || "string[]"
        "Character[]"            || "string[]"
        "String[]"               || "string[]"
        "boolean[]"              || "boolean[]"
        "Boolean[]"              || "boolean[]"
        "Object[]"               || "any[]"

        and: "possible List values for case1 are"
        "java.util.List<Byte>"                 || "number[]"
        "java.util.List<Short>"                || "number[]"
        "java.util.List<Integer>"              || "number[]"
        "java.util.List<Long>"                 || "number[]"
        "java.util.List<Float>"                || "number[]"
        "java.util.List<Double>"               || "number[]"
        "java.util.List<java.math.BigDecimal>" || "number[]"
        "java.util.List<java.math.BigInteger>" || "number[]"
        "java.util.List<Character>"            || "string[]"
        "java.util.List<String>"               || "string[]"
        "java.util.List<Boolean>"              || "boolean[]"
        "java.util.List<?>"                    || "any[]"
        "java.util.List<Object>"               || "any[]"
        "java.util.List"                       || "any[]"

        and: "possible Set values for case1 are"
        "java.util.Set<Byte>"                 || "number[]"
        "java.util.Set<Short>"                || "number[]"
        "java.util.Set<Integer>"              || "number[]"
        "java.util.Set<Long>"                 || "number[]"
        "java.util.Set<Float>"                || "number[]"
        "java.util.Set<Double>"               || "number[]"
        "java.util.Set<java.math.BigDecimal>" || "number[]"
        "java.util.Set<java.math.BigInteger>" || "number[]"
        "java.util.Set<Character>"            || "string[]"
        "java.util.Set<String>"               || "string[]"
        "java.util.Set<Boolean>"              || "boolean[]"
        "java.util.Set<?>"                    || "any[]"
        "java.util.Set<Object>"               || "any[]"
        "java.util.Set"                       || "any[]"

        and: "possible other Collection-Types values for case1 are"
        "java.util.LinkedList<?>"               || "any[]"
        "java.util.ArrayList<?>"                || "any[]"
        "java.util.ArrayDeque<?>"               || "any[]"
        "java.util.Vector<?>"                   || "any[]"
        "java.util.Queue<?>"                    || "any[]"
        "java.util.Deque<?>"                    || "any[]"
        "java.util.concurrent.BlockingQueue<?>" || "any[]"
        "java.util.concurrent.BlockingDeque<?>" || "any[]"
        "java.util.TreeSet<?>"                  || "any[]"
        "java.util.HashSet<?>"                  || "any[]"

        and: "possible Map values for case1 are"
        "java.util.Map<Byte, ?>"                 || "{ [index: number]: any }"
        "java.util.Map<Short, ?>"                || "{ [index: number]: any }"
        "java.util.Map<Integer, ?>"              || "{ [index: number]: any }"
        "java.util.Map<Long, ?>"                 || "{ [index: number]: any }"
        "java.util.Map<Float, ?>"                || "{ [index: number]: any }"
        "java.util.Map<Double, ?>"               || "{ [index: number]: any }"
        "java.util.Map<java.math.BigDecimal, ?>" || "{ [index: number]: any }"
        "java.util.Map<java.math.BigInteger, ?>" || "{ [index: number]: any }"
        "java.util.Map<Character, ?>"            || "{ [index: string]: any }"
        "java.util.Map<String, ?>"               || "{ [index: string]: any }"
        "java.util.Map<Boolean, ?>"              || "{ [index: boolean]: any }"
        "java.util.Map<Object, ?>"               || "{ [index: any]: any }"
        "java.util.Map<?, Byte>"                 || "{ [index: any]: number }"
        "java.util.Map<?, Short>"                || "{ [index: any]: number }"
        "java.util.Map<?, Integer>"              || "{ [index: any]: number }"
        "java.util.Map<?, Long>"                 || "{ [index: any]: number }"
        "java.util.Map<?, Float>"                || "{ [index: any]: number }"
        "java.util.Map<?, Double>"               || "{ [index: any]: number }"
        "java.util.Map<?, java.math.BigDecimal>" || "{ [index: any]: number }"
        "java.util.Map<?, java.math.BigInteger>" || "{ [index: any]: number }"
        "java.util.Map<?, Character>"            || "{ [index: any]: string }"
        "java.util.Map<?, String>"               || "{ [index: any]: string }"
        "java.util.Map<?, Boolean>"              || "{ [index: any]: boolean }"
        "java.util.Map<?, Object>"               || "{ [index: any]: any }"
        "java.util.Map<?, ?>"                    || "{ [index: any]: any }"
        "java.util.Map"                          || "{ [index: any]: any }"

        and: "possible other Map-Types values for case1 are"
        "java.util.HashMap<?, ?>"                      || "{ [index: any]: any }"
        "java.util.Hashtable<?, ?>"                    || "{ [index: any]: any }"
        "java.util.TreeMap<?, ?>"                      || "{ [index: any]: any }"
        "java.util.concurrent.ConcurrentHashMap<?, ?>" || "{ [index: any]: any }"
        "java.util.WeakHashMap<?, ?>"                  || "{ [index: any]: any }"

    }

    @Unroll
    def "simple Endpoint with declared return #returnType, #targetType only referenced in List/Array/Map-type"() {
        given: "an Endpoint with a HttpMethod returning a Collection or Map type"
        def folder = "/returnref"
        def endpointSourceFile = getSourceFile("$folder/Endpoint.gstring", "$folder/Endpoint.java", [returnType: returnType, returnValue: returnValue])
        def simpleRootTypeSourceFile = new File(endpointsPath + folder + "/SimpleRootType.java")
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, endpointSourceFile, simpleRootTypeSourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there should only be one declared typescript interface file"
        def allTSFiles = new ArrayList<File>()
        destinationFolder.eachFileMatch FILES, ~/.*\.model\.generated\.ts/, { file -> allTSFiles << file }
        allTSFiles.size() == 1
        allTSFiles[0].name == "simpleroottype.model.generated.ts"

        and: "it should contain the mapped type for the declared field"
        def model = jsonSlurper.parse(new File("$annotationsTarget/$folder/simpleroottype.model.generated.ts"))
        with(model) {
            typeName == "SimpleRootType"
            children.size == 2
            children[0].fieldName == "field1"
            children[0].type == "string"
            children[1].fieldName == "field2"
            children[1].type == "string"
        }

        cleanup: "remove test java source file"
        endpointSourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible return values for type in List are"
        returnType                                           | returnValue                                || targetType
        "java.util.List<SimpleRootType>"                     | "return java.util.Collections.emptyList()" || "SimpleRootType[]"
        "java.util.LinkedList<SimpleRootType>"               | "return java.util.Collections.emptyList()" || "SimpleRootType[]"
        "java.util.ArrayList<SimpleRootType>"                | "return java.util.Collections.emptyList()" || "SimpleRootType[]"
        "java.util.ArrayDeque<SimpleRootType>"               | "return java.util.Collections.emptyList()" || "SimpleRootType[]"
        "java.util.Vector<SimpleRootType>"                   | "return java.util.Collections.emptyList()" || "SimpleRootType[]"
        "java.util.Queue<SimpleRootType>"                    | "return java.util.Collections.emptyList()" || "SimpleRootType[]"
        "java.util.Deque<SimpleRootType>"                    | "return java.util.Collections.emptyList()" || "SimpleRootType[]"
        "java.util.concurrent.BlockingQueue<SimpleRootType>" | "return java.util.Collections.emptyList()" || "SimpleRootType[]"
        "java.util.concurrent.BlockingDeque<SimpleRootType>" | "return java.util.Collections.emptyList()" || "SimpleRootType[]"

        and: "possible array values for case1 are"
        "SimpleRootType[]" | "return new SimpleRootType[1]" || "SimpleRootType[]"

        and: "possible return values for type in Set are"
        "java.util.Set<SimpleRootType>"     | "return java.util.Collections.emptySet()" || "SimpleRootType[]"
        "java.util.TreeSet<SimpleRootType>" | "return java.util.Collections.emptySet()" || "SimpleRootType[]"
        "java.util.HashSet<SimpleRootType>" | "return java.util.Collections.emptySet()" || "SimpleRootType[]"

        and: "possible return values for type in Map are"
        "java.util.Map<SimpleRootType, ?>"                          | "return java.util.Collections.emptyMap()" || "{ [index: SimpleRootType]: any }"
        "java.util.Map<?, SimpleRootType>"                          | "return java.util.Collections.emptyMap()" || "{ [index: any]: SimpleRootType }"
        "java.util.HashMap<SimpleRootType, ?>"                      | "return java.util.Collections.emptyMap()" || "{ [index: SimpleRootType]: any }"
        "java.util.HashMap<?, SimpleRootType>"                      | "return java.util.Collections.emptyMap()" || "{ [index: any]: SimpleRootType }"
        "java.util.Hashtable<SimpleRootType, ?>"                    | "return java.util.Collections.emptyMap()" || "{ [index: SimpleRootType]: any }"
        "java.util.Hashtable<?, SimpleRootType>"                    | "return java.util.Collections.emptyMap()" || "{ [index: any]: SimpleRootType }"
        "java.util.TreeMap<SimpleRootType, ?>"                      | "return java.util.Collections.emptyMap()" || "{ [index: SimpleRootType]: any }"
        "java.util.TreeMap<?, SimpleRootType>"                      | "return java.util.Collections.emptyMap()" || "{ [index: any]: SimpleRootType }"
        "java.util.concurrent.ConcurrentHashMap<SimpleRootType, ?>" | "return java.util.Collections.emptyMap()" || "{ [index: SimpleRootType]: any }"
        "java.util.concurrent.ConcurrentHashMap<?, SimpleRootType>" | "return java.util.Collections.emptyMap()" || "{ [index: any]: SimpleRootType }"
        "java.util.WeakHashMap<SimpleRootType, ?>"                  | "return java.util.Collections.emptyMap()" || "{ [index: SimpleRootType]: any }"
        "java.util.WeakHashMap<?, SimpleRootType>"                  | "return java.util.Collections.emptyMap()" || "{ [index: any]: SimpleRootType }"

        and: "possible composite values are"
        "java.util.Optional<SimpleRootType>"                        | "return new java.util.Optional.empty()"   || "SimpleRootType"
    }

    @Unroll
    def "simple Endpoint with return type void and declard parameter #paramType, #targetType"() {
        given: "an Endpoint with a HttpMethod returning void"
        def folder = "/returnvoid"
        def endpointSourceFile = getSourceFile("$folder/Endpoint.gstring", "$folder/Endpoint.java", [paramType: paramType])
        def simpleRootTypeSourceFile = new File(endpointsPath + folder + "/SimpleRootType.java")
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, endpointSourceFile, simpleRootTypeSourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there should only be one declared typescript interface file"
        def allTSFiles = new ArrayList<File>()
        destinationFolder.eachFileMatch FILES, ~/.*\.model\.generated\.ts/, { file -> allTSFiles << file }
        allTSFiles.size() == 1
        allTSFiles[0].name == "simpleroottype.model.generated.ts"

        and: "it should contain the mapped type for the declared field"
        def model = jsonSlurper.parse(new File("$annotationsTarget/$folder/simpleroottype.model.generated.ts"))
        with(model) {
            typeName == "SimpleRootType"
            children.size == 2
            children[0].fieldName == "field1"
            children[0].type == "number"
            children[1].fieldName == "field2"
            children[1].type == "string[]"
        }

        cleanup: "remove test java source file"
        endpointSourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible requestBodyType values are"
        paramType          || targetType
        "SimpleRootType"   || "SimpleRootType"
        "SimpleRootType[]" || "SimpleRootType[]"

        and: "possible requestBodyType values for type in List are"
        "java.util.List<SimpleRootType>"                     || "SimpleRootType[]"
        "java.util.LinkedList<SimpleRootType>"               || "SimpleRootType[]"
        "java.util.ArrayList<SimpleRootType>"                || "SimpleRootType[]"
        "java.util.ArrayDeque<SimpleRootType>"               || "SimpleRootType[]"
        "java.util.Vector<SimpleRootType>"                   || "SimpleRootType[]"
        "java.util.Queue<SimpleRootType>"                    || "SimpleRootType[]"
        "java.util.Deque<SimpleRootType>"                    || "SimpleRootType[]"
        "java.util.concurrent.BlockingQueue<SimpleRootType>" || "SimpleRootType[]"
        "java.util.concurrent.BlockingDeque<SimpleRootType>" || "SimpleRootType[]"

        and: "possible requestBodyType values for type in Set are"
        "java.util.Set<SimpleRootType>"     || "SimpleRootType[]"
        "java.util.TreeSet<SimpleRootType>" || "SimpleRootType[]"
        "java.util.HashSet<SimpleRootType>" || "SimpleRootType[]"

        and: "possible requestBodyType values for type in Map are"
        "java.util.Map<SimpleRootType, ?>"                          || "{ [index: SimpleRootType]: any }"
        "java.util.Map<?, SimpleRootType>"                          || "{ [index: any]: SimpleRootType }"
        "java.util.HashMap<SimpleRootType, ?>"                      || "{ [index: SimpleRootType]: any }"
        "java.util.HashMap<?, SimpleRootType>"                      || "{ [index: any]: SimpleRootType }"
        "java.util.Hashtable<SimpleRootType, ?>"                    || "{ [index: SimpleRootType]: any }"
        "java.util.Hashtable<?, SimpleRootType>"                    || "{ [index: any]: SimpleRootType }"
        "java.util.TreeMap<SimpleRootType, ?>"                      || "{ [index: SimpleRootType]: any }"
        "java.util.TreeMap<?, SimpleRootType>"                      || "{ [index: any]: SimpleRootType }"
        "java.util.concurrent.ConcurrentHashMap<SimpleRootType, ?>" || "{ [index: SimpleRootType]: any }"
        "java.util.concurrent.ConcurrentHashMap<?, SimpleRootType>" || "{ [index: any]: SimpleRootType }"
        "java.util.WeakHashMap<SimpleRootType, ?>"                  || "{ [index: SimpleRootType]: any }"
        "java.util.WeakHashMap<?, SimpleRootType>"                  || "{ [index: any]: SimpleRootType }"
    }

    @Unroll
    def "simple Endpoint with generic type should bind #boundType"() {
        given: "an Endpoint with a HttpMethod which has a generic Type"
        def folder = "/generics"
        def endpointSourceFile = getSourceFile("$folder/Endpoint.gstring", "$folder/Endpoint.java", [boundType: boundType])
        def simpleRootTypeSourceFile = new File(endpointsPath + folder + "/SimpleRootType.java")
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, endpointSourceFile, simpleRootTypeSourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there should only be one declared typescript interface file"
        def allTSFiles = new ArrayList<File>()
        destinationFolder.eachFileMatch FILES, ~/.*\.model\.generated\.ts/, { file -> allTSFiles << file }
        allTSFiles.size() == 1
        allTSFiles[0].name == "simpleroottype.model.generated.ts"

        and: "it should contain the mapped type for the declared field"
        def model = jsonSlurper.parse(new File("$annotationsTarget/$folder/simpleroottype.model.generated.ts"))
        with(model) {
            typeName == "SimpleRootType"
            children.size == 1
            children[0].fieldName == "field"
            children[0].type == type
            children[0].typeNameVariable == "T"
            children[0].mappedType == mapped
            children[0].optional == innerOptional
        }

        and: "the scanned model should contain only the httpmethod"
        def endpoint = jsonSlurper.parse(new File("$annotationsTarget${folder}/endpoint.generated.ts"))
        with(endpoint) {
            serviceName == "Endpoint"
            serviceUrl == "/api"
            methodCount == 1
            getMethods[0].url == "/boundType/{value}"
            getMethods[0].returnType == returnType
            getMethods[0].requestBodyType == "any | null"
            getMethods[0].methodParameterTypes[0].optional == outerOptional
        }

        cleanup: "remove test java source file"
        endpointSourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible bound type values are"
        boundType                                    || returnType               | type     | declaredComplexType | mapped | innerOptional | outerOptional
        "SimpleRootType<Integer>"                    || "SimpleRootType<number>" | "number" | false               | true   | false         | false
        "SimpleRootType<java.util.Optional<String>>" || "SimpleRootType<string>" | "string" | false               | true   | false         | false
        "java.util.Optional<SimpleRootType<String>>" || "SimpleRootType<string>" | "string" | false               | true   | false         | true

    }

    @Unroll
    def "each PathVariable type #type should create an input parameter for each #httpMethod-Entry"() {
        given: "an Endpoint with a Method with @PatVariable"
        def folder = "/pathvariable"
        def endpointSourceFile = getSourceFile("$folder/Endpoint.gstring", "$folder/${httpMethod}.java", [type: type, httpMethod: httpMethod, mappedType: mappedType])
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, endpointSourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there must be no declared typescript interface file"
        def allTSFiles = getInterfaceFiles(destinationFolder)
        allTSFiles.size() == 0

        and: "the scanned model should contain only the httpmethod with pathvariable"
        def model = jsonSlurper.parse(new File("$annotationsTarget${folder}/${httpMethod.toLowerCase()}.generated.ts"))
        def method = httpMethod.toLowerCase()
        with(model) {
            serviceName == "${httpMethod}"
            serviceUrl == "/api"
            methodCount == 1
            getProperty("${method}MethodCount") == 1
            with(getProperty("${method}Methods")[0]) {
                name == "getInt"
                url == "/{value}"
                httpMethods == ["$method"]
                returnType == "$mappedType"
                pathVariableTypes[0].fieldName == "value"
                pathVariableTypes[0].type == "$mappedType"
            }
        }

        cleanup: "remove test java source file"
        endpointSourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible values for type and httpmethod are"
        type                         | httpMethod || mappedType
        "int"                        | "GET"      || "number"
        "int[]"                      | "GET"      || "number[]"
        "java.util.List<Integer>"    | "GET"      || "number[]"
        "String"                     | "GET"      || "string"
        "java.util.Date"             | "GET"      || "Date"
        "java.time.LocalDate"        | "GET"      || "Date"
        "java.util.Optional<String>" | "GET"      || "string"

        "int"                        | "HEAD"     || "number"
        "int[]"                      | "HEAD"     || "number[]"
        "java.util.List<Integer>"    | "HEAD"     || "number[]"
        "String"                     | "HEAD"     || "string"
        "java.util.Date"             | "HEAD"     || "Date"
        "java.time.LocalDate"        | "HEAD"     || "Date"
        "java.util.Optional<String>" | "HEAD"     || "string"

        "int"                        | "POST"     || "number"
        "int[]"                      | "POST"     || "number[]"
        "java.util.List<Integer>"    | "POST"     || "number[]"
        "String"                     | "POST"     || "string"
        "java.util.Date"             | "POST"     || "Date"
        "java.time.LocalDate"        | "POST"     || "Date"
        "java.util.Optional<String>" | "POST"     || "string"

        "int"                        | "PUT"      || "number"
        "int[]"                      | "PUT"      || "number[]"
        "java.util.List<Integer>"    | "PUT"      || "number[]"
        "String"                     | "PUT"      || "string"
        "java.util.Date"             | "PUT"      || "Date"
        "java.time.LocalDate"        | "PUT"      || "Date"
        "java.util.Optional<String>" | "PUT"      || "string"

        "int"                        | "PATCH"    || "number"
        "int[]"                      | "PATCH"    || "number[]"
        "java.util.List<Integer>"    | "PATCH"    || "number[]"
        "String"                     | "PATCH"    || "string"
        "java.util.Date"             | "PATCH"    || "Date"
        "java.time.LocalDate"        | "PATCH"    || "Date"
        "java.util.Optional<String>" | "PATCH"    || "string"

        "int"                        | "DELETE"   || "number"
        "int[]"                      | "DELETE"   || "number[]"
        "java.util.List<Integer>"    | "DELETE"   || "number[]"
        "String"                     | "DELETE"   || "string"
        "java.util.Date"             | "DELETE"   || "Date"
        "java.time.LocalDate"        | "DELETE"   || "Date"
        "java.util.Optional<String>" | "DELETE"   || "string"

        "int"                        | "OPTIONS"  || "number"
        "int[]"                      | "OPTIONS"  || "number[]"
        "java.util.List<Integer>"    | "OPTIONS"  || "number[]"
        "String"                     | "OPTIONS"  || "string"
        "java.util.Date"             | "OPTIONS"  || "Date"
        "java.time.LocalDate"        | "OPTIONS"  || "Date"
        "java.util.Optional<String>" | "OPTIONS"  || "string"

        "int"                        | "TRACE"    || "number"
        "int[]"                      | "TRACE"    || "number[]"
        "java.util.List<Integer>"    | "TRACE"    || "number[]"
        "String"                     | "TRACE"    || "string"
        "java.util.Date"             | "TRACE"    || "Date"
        "java.time.LocalDate"        | "TRACE"    || "Date"
        "java.util.Optional<String>" | "TRACE"    || "string"

    }

    @Unroll
    def "each composed #annotation results in a specific httpMethod-Entry"() {
        given: "an Endpoint with a composed Annotation"
        def folder = "/composed"
        def endpointSourceFile = getSourceFile("$folder/Endpoint.gstring", "$folder/Endpoint.java", [annotation: annotation])
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, endpointSourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there must be no declared typescript interface file"
        def allTSFiles = getInterfaceFiles(destinationFolder)
        allTSFiles.size() == 0

        and: "the scanned model should contain only the httpmethod"
        def model = jsonSlurper.parse(new File("$annotationsTarget${folder}/endpoint.generated.ts"))
        def method = annotation.toString().substring(1, annotation.indexOf("Mapping")).toLowerCase()
        with(model) {
            serviceName == "Endpoint"
            serviceUrl == "/api/method"
            methodCount == 1
            getProperty("${method}MethodCount") == 1
        }

        cleanup: "remove test java source file"
        endpointSourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible requestBodyType values are"
        annotation       || targetType
        "@GetMapping"    || "SimpleRootType"
        "@PutMapping"    || "SimpleRootType"
        "@PostMapping"   || "SimpleRootType"
        "@DeleteMapping" || "SimpleRootType"
        "@PatchMapping"  || "SimpleRootType"
    }

    def "the custom endpoint name is independent of the Classname "() {
        given: "an Endpoint with a custom name in @TypeScriptEndpoint"
        def folder = "/epname"
        def sourceFile = new File("$endpointsPath/$folder/Endpoint.java")
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, sourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there must be no declared typescript interface file"
        def allTSFiles = getInterfaceFiles(destinationFolder)
        allTSFiles.size() == 0

        and: "there must be a ts file with the custom name"
        destinationFolder.listFiles().length == 3
        destinationFolder.eachFile { f -> f.name == "CustomName.ts" || f.name == "index.ts" || f.name == "api.module.ts" }

        cleanup: "remove test java source file"
        // Do not delete the source files: sourceFile.delete(), because they are not generated in this testcase!
        destinationFolder.deleteDir()
    }

    @Unroll
    def "a type with enum member (values: #enumValues) must generate a typescript enum"() {
        given: "an Endpoint with a Type including an Enum"
        def folder = "/enums"
        def endPointFile = new File("$endpointsPath/$folder/Endpoint.java")
        def rootTypeSourceFile = new File("$endpointsPath/$folder/SimpleRootType.java")
        def enumSourceFile = getSourceFile("$folder/DeclaredEnum.gstring", "$folder/DeclaredEnum.java", [values: enumValues])
        def destinationFolder = initFolder folder

        when: "the Endpoint is compiled"
        def diagnostics = CompilerTestHelper.compileTestCase([new TypeScriptEndpointProcessor()], folder, endPointFile, rootTypeSourceFile, enumSourceFile)

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there must be two declared typescript interface file"
        def allTSFiles = getInterfaceFiles(destinationFolder)
        allTSFiles.size() == 2

        and: "one is the root type and one the enum"
        def rootTypeCount = 0
        def enumTypeCount = 0
        def enumFile, rootTypeFile
        allTSFiles.each {
            f ->
                if (f.name == "simpleroottype.model.generated.ts") {
                    rootTypeCount++
                    rootTypeFile = f
                } else if (f.name == "declaredenum.model.generated.ts") {
                    enumTypeCount++
                    enumFile = f
                }
        }
        rootTypeCount == 1
        enumTypeCount == 1

        and: "the rootType must not contain any enum values"
        def rootModel = jsonSlurper.parse(rootTypeFile)
        with(rootModel) {
            typeName == "SimpleRootType"
            values == []
        }

        and: "this enum file must include all enum values"
        def enumModel = jsonSlurper.parse(enumFile)
        def enumValuesArray = enumValues.split(",")
        with(enumModel) {
            typeName == "DeclaredEnum"
            children == []
            values.size() == enumValuesArray.length
            enumValuesArray.each { v -> values.contains(v) }
        }

        cleanup: "remove test java source file"
        // Do not delete the source files: sourceFile.delete(), because they are not generated in this testcase!
        enumSourceFile.delete()
        destinationFolder.deleteDir()

        where: "possible values are"
        enumValues << ["SOME, AND, NOT", "SOME(10), AND(4), NOT(100)"]
    }

    def getSourceFile(inputFilePath, outputFilePath, Map<?, ?> variables) {
        def text = new File("$endpointsPath/$inputFilePath").getText("utf-8")
        def sourceFile = new File("$endpointsPath/$outputFilePath")
        Files.createDirectories(sourceFile.getParentFile().toPath())
        sourceFile.createNewFile()
        sourceFile.write(new SimpleTemplateEngine().createTemplate(text).make(variables).toString(), 'UTF8')
        return sourceFile
    }

    def getInterfaceFiles(File destinationFolder) {
        def allTSFiles = new ArrayList<File>()
        destinationFolder.eachFileRecurse(FILES) {
            if (it.name.endsWith('.model.generated.ts')) {
                allTSFiles << it
            }
        }
        return allTSFiles
    }

    def initFolder(String folder) {
        def destinationFolder = new File("$annotationsTarget/$folder")
        Files.createDirectories(destinationFolder.toPath())
        return destinationFolder
    }
}