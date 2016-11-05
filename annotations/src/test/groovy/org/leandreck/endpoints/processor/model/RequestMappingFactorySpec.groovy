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
package org.leandreck.endpoints.processor.model

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.*

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * Created by kowalzik on 05.11.2016.
 */
@Title("RequestMappingFactory Unittests")
@Subject(RequestMappingFactory)
class RequestMappingFactorySpec extends Specification {

    @Shared
    def factory = new RequestMappingFactory();

    @Unroll
    def "Populate with #annotation throwing different Exceptions"() {
        given: "empty Lists and an Annotation"
        def methodsList = []
        def producesList = []
        def valueList = []

        when: "the populate method is called"
        factory.populate(methodsList, producesList, valueList, annotation, RequestMethod.GET)

        then: "and all lists except methodsList must still be empty"
        methodsList == [RequestMethod.GET]
        producesList == []
        valueList == []

        where: "possible annotations are"
        annotation << [
                Stub(Annotation) {
                },
                Stub(GetMapping) {
                    produces() >> { throw new IOException() }
                }
        ]
    }
}
