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

import groovy.io.FileType
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.*

import javax.annotation.processing.Processor
import javax.tools.Diagnostic
import javax.tools.JavaFileObject
import java.nio.file.Files
import java.util.stream.Collectors

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 31.08.2016.
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

        Files.createDirectories(new File("$annotationsTarget/$folder").toPath())

        when: "a simple Endpoint is compiled"
        List<Diagnostic<? extends JavaFileObject>> diagnostics =
                CompilerTestHelper.compileTestCase(Arrays.<Processor> asList(new TypeScriptEndpointProcessor()), folder, sourceFile)
        def model = jsonSlurper.parse(new File("$annotationsTarget/$folder/Endpoint.ts"))

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
        sourceFile.delete()

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

        and: "possible temporal types are"
        "java.util.Date"      | "return new java.util.Date()"      || "Date"
        "java.time.LocalDate" | "return java.time.LocalDate.now()" || "Date"

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

    @Unroll
    def "if a Method is annotated with TypeScriptIgnore it should be ignored"() {
        given: "an Endpoint with a TypeScriptIgnore annotated Method"
        def classFile = new File("$defaultPathBase/src/test/testcases/org/leandreck/endpoints/ignored/${ignoreClass}.java")
        def folder = "/ignored"
        Files.createDirectories(new File("$annotationsTarget/$folder").toPath())

        when: "a simple Endpoint is compiled"
        List<Diagnostic<? extends JavaFileObject>> diagnostics =
                CompilerTestHelper.compileTestCase(Arrays.<Processor> asList(new TypeScriptEndpointProcessor()), folder, classFile)
        def model = jsonSlurper.parse(new File("$annotationsTarget/$folder/${ignoreClass}.ts"))

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "the scanned model should contain no method"
        with(model) {
            serviceName == ignoreClass
            serviceUrl == "/api"
            methodCount == 0
        }

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
        def gstring = Eval.me("httpMethod", httpMethod, new File("$defaultPathBase/src/test/testcases/org/leandreck/endpoints/httpmethods/Endpoint.gstring").text)
        def classFile = new File("$defaultPathBase/src/test/testcases/org/leandreck/endpoints/httpmethods/${httpMethod}.java")
        Files.createDirectories(new File("$annotationsTarget/httpmethods").toPath())
        classFile.text = gstring.toString()
        def folder = "/httpmethods"

        when: "the Endpoint is compiled"
        List<Diagnostic<? extends JavaFileObject>> diagnostics =
                CompilerTestHelper.compileTestCase(Arrays.<Processor> asList(new TypeScriptEndpointProcessor()), folder, classFile)
        def model = jsonSlurper.parse(new File("$annotationsTarget${folder}/${httpMethod}.ts"))

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "the scanned model should contain the httpmethod"
        with(model) {
            serviceName == httpMethod
            serviceUrl == "/api"
            methodCount == 1
            getProperty("${httpMethod.toString().toLowerCase()}MethodCount") == 1
        }

        cleanup: "remove classfile"
        classFile.delete()

        where: "possible http-Methods are all possible values from RequestMethod"
        httpMethod << Arrays.stream(RequestMethod.values()).map({ m -> m.toString() }).collect(Collectors.toList())
    }

    @Unroll
    def "simple Endpoint with declared return type with mapped type #type as field member"() {
        given: "an Endpoint with a HttpMethod returning a simple declared type"
        def folder = "/complex"
        def endpointSourceFile = new File(endpointsPath + folder + "/Endpoint.java")
        def simpleRootTypeSourceFile = getSourceFile("$folder/SimpleRootType.gstring", "$folder/SimpleRootType.java", [type: type])
        def destinationFolder = new File("$annotationsTarget/$folder")
        Files.createDirectories(destinationFolder.toPath())

        when: "the Endpoint is compiled"
        List<Diagnostic<? extends JavaFileObject>> diagnostics =
                CompilerTestHelper.compileTestCase(Arrays.<Processor> asList(new TypeScriptEndpointProcessor()), folder, endpointSourceFile, simpleRootTypeSourceFile)
        def model = jsonSlurper.parse(new File("$annotationsTarget/$folder/ISimpleRootType.model.ts"))

        then: "there should be no errors"
        diagnostics.every { d -> (Diagnostic.Kind.ERROR != d.kind) }

        and: "there should only be one declared typescript interface file"
        def allTSFiles = new ArrayList<File>()
        destinationFolder.eachFileMatch FileType.FILES, ~/.*\.model\.ts/, { file -> allTSFiles << file }
        allTSFiles.size() == 1
        allTSFiles[0].name == "ISimpleRootType.model.ts"

        and: "it should contain the mapped type for the declared field"

        cleanup: "remove test java source file"
        simpleRootTypeSourceFile.delete()
        destinationFolder.eachFile(FileType.FILES, { file -> file.delete() })

        where: "possible simple values for type in SimpleRootType are"
        type                   || mappedType
        "byte"                 || "Number"
        "Byte"                 || "Number"
        "short"                || "Number"
        "Short"                || "Number"
        "int"                  || "Number"
        "Integer"              || "Number"
        "long"                 || "Number"
        "Long"                 || "Number"
        "float"                || "Number"
        "Float"                || "Number"
        "double"               || "Number"
        "Double"               || "Number"
        "java.math.BigDecimal" || "Number"
        "java.math.BigInteger" || "Number"
        "char"                 || "String"
        "Character"            || "String"
        "String"               || "String"
        "boolean"              || "Boolean"
        "Boolean"              || "Boolean"

        and: "possible temporal types are"
        "java.util.Date"      || "Date"
        "java.time.LocalDate" || "Date"

        and: "possible array values for case1 are"
        "byte[]"                 || "Number[]"
        "Byte[]"                 || "Number[]"
        "short[]"                || "Number[]"
        "Short[]"                || "Number[]"
        "int[]"                  || "Number[]"
        "Integer[]"              || "Number[]"
        "long[]"                 || "Number[]"
        "Long[]"                 || "Number[]"
        "float[]"                || "Number[]"
        "Float[]"                || "Number[]"
        "double[]"               || "Number[]"
        "Double[]"               || "Number[]"
        "java.math.BigDecimal[]" || "Number[]"
        "java.math.BigInteger[]" || "Number[]"
        "char[]"                 || "String[]"
        "Character[]"            || "String[]"
        "String[]"               || "String[]"
        "boolean[]"              || "Boolean[]"
        "Boolean[]"              || "Boolean[]"
        "Object[]"               || "any[]"

        and: "possible List values for case1 are"
        "java.util.List<Byte>"                 || "Number[]"
        "java.util.List<Short>"                || "Number[]"
        "java.util.List<Integer>"              || "Number[]"
        "java.util.List<Long>"                 || "Number[]"
        "java.util.List<Float>"                || "Number[]"
        "java.util.List<Double>"               || "Number[]"
        "java.util.List<java.math.BigDecimal>" || "Number[]"
        "java.util.List<java.math.BigInteger>" || "Number[]"
        "java.util.List<Character>"            || "String[]"
        "java.util.List<String>"               || "String[]"
        "java.util.List<Boolean>"              || "Boolean[]"
        "java.util.List<?>"                    || "any[]"
        "java.util.List<Object>"               || "any[]"
        "java.util.List"                       || "any[]"

        and: "possible Set values for case1 are"
        "java.util.Set<Byte>"                 || "Number[]"
        "java.util.Set<Short>"                || "Number[]"
        "java.util.Set<Integer>"              || "Number[]"
        "java.util.Set<Long>"                 || "Number[]"
        "java.util.Set<Float>"                || "Number[]"
        "java.util.Set<Double>"               || "Number[]"
        "java.util.Set<java.math.BigDecimal>" || "Number[]"
        "java.util.Set<java.math.BigInteger>" || "Number[]"
        "java.util.Set<Character>"            || "String[]"
        "java.util.Set<String>"               || "String[]"
        "java.util.Set<Boolean>"              || "Boolean[]"
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
        "java.util.Map<Byte, ?>"                 || "{ [index: Number]: any }"
        "java.util.Map<Short, ?>"                || "{ [index: Number]: any }"
        "java.util.Map<Integer, ?>"              || "{ [index: Number]: any }"
        "java.util.Map<Long, ?>"                 || "{ [index: Number]: any }"
        "java.util.Map<Float, ?>"                || "{ [index: Number]: any }"
        "java.util.Map<Double, ?>"               || "{ [index: Number]: any }"
        "java.util.Map<java.math.BigDecimal, ?>" || "{ [index: Number]: any }"
        "java.util.Map<java.math.BigInteger, ?>" || "{ [index: Number]: any }"
        "java.util.Map<Character, ?>"            || "{ [index: String]: any }"
        "java.util.Map<String, ?>"               || "{ [index: String]: any }"
        "java.util.Map<Boolean, ?>"              || "{ [index: Boolean]: any }"
        "java.util.Map<Object, ?>"               || "{ [index: any]: any }"
        "java.util.Map<?, Byte>"                 || "{ [index: any]: Number }"
        "java.util.Map<?, Short>"                || "{ [index: any]: Number }"
        "java.util.Map<?, Integer>"              || "{ [index: any]: Number }"
        "java.util.Map<?, Long>"                 || "{ [index: any]: Number }"
        "java.util.Map<?, Float>"                || "{ [index: any]: Number }"
        "java.util.Map<?, Double>"               || "{ [index: any]: Number }"
        "java.util.Map<?, java.math.BigDecimal>" || "{ [index: any]: Number }"
        "java.util.Map<?, java.math.BigInteger>" || "{ [index: any]: Number }"
        "java.util.Map<?, Character>"            || "{ [index: any]: String }"
        "java.util.Map<?, String>"               || "{ [index: any]: String }"
        "java.util.Map<?, Boolean>"              || "{ [index: any]: Boolean }"
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

    def getSourceFile(inputFilePath, outputFilePath, Map<?, ?> variables) {
        def text = new File("$endpointsPath/$inputFilePath").getText("utf-8")
        def sourceFile = new File("$endpointsPath/$outputFilePath")
        Files.createDirectories(sourceFile.getParentFile().toPath())
        sourceFile.createNewFile()
        sourceFile.write(new SimpleTemplateEngine().createTemplate(text).make(variables).toString(), 'UTF8')
        return sourceFile
    }
}