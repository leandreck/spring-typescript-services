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
package org.leandreck.endpoints.examples;

import org.leandreck.endpoints.annotations.TypeScriptEndpoint;
import org.leandreck.endpoints.annotations.TypeScriptIgnore;
import org.leandreck.endpoints.annotations.TypeScriptTemplatesConfiguration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 19.08.2016.
 */
@TypeScriptEndpoint
@RestController
@RequestMapping("/api")
@TypeScriptTemplatesConfiguration(
        apimodule = TypeScriptTemplatesConfiguration.DEFAULT_API_MODULE
)
public class TestTypeScriptEndpoint {

    @RequestMapping(value = "/type/{id}/{typeRef}", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<SubType> setId(@PathVariable Long id, @RequestBody SubType body) {
        // do something
        return Collections.singletonList(body);
    }

    @RequestMapping(value = "/persons", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<RootType> getPersons() {
        final List<RootType> rootTypes = new ArrayList<>();
        rootTypes.add(new RootType());
        return rootTypes;
    }

    @RequestMapping(value = "/person/{id}/{typeRef}", method = GET, produces = APPLICATION_JSON_VALUE)
    public RootType getPerson(@PathVariable Long id, @PathVariable String typeRef) {
        return new RootType(id);
    }

    @RequestMapping(value = "/person/{id}", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public RootType updatePerson(@PathVariable Long id, @RequestBody RootType rootType) {
        rootType.setId(id);
        return rootType;
    }

    @RequestMapping(value = "/maps", method = GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Map<String, RootType> maps() {
        final Map<String, RootType> map = new HashMap<>();
        map.put("one", new RootType());
        return map;
    }

    @TypeScriptIgnore
    @RequestMapping(value = "/photos/{id}", method = GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getPhoto(@PathVariable Long id) {
        return ResponseEntity
                .ok()
                .contentLength(0)
                .contentType(IMAGE_PNG)
                .body(new InputStreamResource(new ByteArrayInputStream("No Content".getBytes())));
    }

    private List<RootType> ignoreList() {
        return null;
    }

    protected List<RootType> ignoredTwo() {
        return null;
    }

    List<RootType> ignoredAlso() {
        return null;
    }

    @RequestMapping(value = "/subType", method = {HEAD, PUT, PATCH, DELETE, OPTIONS, TRACE}, produces = APPLICATION_JSON_VALUE)
    public SubType handleSubType(@RequestBody final SubType param) {
        return param;
    }
}
