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
/*
  Copyright © 2016 Mathias Kowalzik (Mathias.Kowalzik@leandreck.org)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package org.leandreck.endpoints.examples.lombok;

import org.leandreck.endpoints.annotations.TypeScriptEndpoint;
import org.leandreck.endpoints.examples.RootType;
import org.leandreck.endpoints.examples.SubType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 */
@TypeScriptEndpoint
@RestController
@RequestMapping("/api")
public class LombokTypeScriptEndpoint {

    @RequestMapping(value = "/type/{idPathVariable}/{typeRef}", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public LombokResponse<ArrayList<SubType>, List<Object>, Boolean, Map<String, String>, SubType> setId(
            @PathVariable(name = "idPathVariable") Long id,
            @PathVariable String typeRef,
            @RequestParam(name = "queryRequestParam", required = false) Optional<String> queryParameter,
            @RequestBody(required = false) LombokRequest body) {
        // do something
        return new LombokResponse<>();
    }

    @RequestMapping(value = "/foo/{idPathVariable}/{typeRef}", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public LombokResponse<List<RootType>, List<Object>, Boolean, SubType, RootType> setFoo(
            @PathVariable(name = "idPathVariable") Long id,
            @PathVariable String typeRef,
            @RequestParam(name = "queryRequestParam", required = false) Optional<String> queryParameter,
            @RequestBody(required = false) LombokRequest body) {
        // do something
        return new LombokResponse<>();
    }

    @RequestMapping(value = "/foo/{idPathVariable}/{typeRef}", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LombokResponse<List<RootType>, List<Object>, Boolean, SubType, RootType>> setResponseEntity(
            @PathVariable(name = "idPathVariable") Long id,
            @PathVariable String typeRef,
            @RequestParam(name = "queryRequestParam", required = false) Optional<String> queryParameter,
            @RequestBody(required = false) LombokRequest body) {
        // do something
        return ResponseEntity.ok(new LombokResponse<>());
    }

    @RequestMapping(value = "/upload/{id}", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LombokResponse<List<RootType>, List<Object>, Boolean, SubType, RootType>> fileUpload(
            @PathVariable("id") long id,
            @RequestParam("uploadfile") MultipartFile uploadfile,
            @Context HttpServletRequest request) {
        // do something
        return ResponseEntity.ok(new LombokResponse<>());
    }
}
