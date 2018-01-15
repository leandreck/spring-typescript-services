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

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
class LombokResponse<T extends List<B>, X extends List<? super Y>, Y, Z, B> {

    Integer size;
    T listWithTypeBElements;
    X listWithSuperTypesOfY;
    List<Y> typeY;
    Z[] typeZ;
    Map<Y, Z> mapTypeYZ;

}
