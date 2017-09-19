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
package org.leandreck.endpoints.examples.lombok;

import org.leandreck.endpoints.annotations.TypeScriptEndpoint;
import org.leandreck.endpoints.annotations.TypeScriptIgnore;
import org.leandreck.endpoints.examples.RootType;
import org.leandreck.endpoints.examples.SubType;
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
public class LombokTypeScriptEndpoint {

    @RequestMapping(value = "/type/{id}/{typeRef}", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public LombokResponse setId(@PathVariable Long id, @RequestBody LombokRequest body) {
        // do something
        return new LombokResponse();
    }
}
