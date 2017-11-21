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
package org.leandreck.endpoints.processor.model.typefactories;

import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.model.TypeNodeFactory;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public interface ConcreteTypeNodeFactory {

    /**
     * @param typeNodeFactory {@link TypeNodeFactory}
     * @param configuration {@link TemplateConfiguration}
     * @param typeUtils {@link Types}
     * @param elementUtils {@link Elements}
     * @return properly configured instance
     */
    ConcreteTypeNodeFactory newConfiguredInstance(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration,
                                                  final Types typeUtils,
                                                  final Elements elementUtils);

    /**
     * Factory-Method for creating concrete {@link TypeNode} instances.
     * @param fieldName {@link TypeNode#getFieldName()}
     * @param parameterName {@link TypeNode#getParameterName()}
     * @param optional {@link TypeNode#isOptional()}
     * @param typeMirror {@link TypeMirror} of which the {@link TypeNode} will be created
     * @return concrete {@link TypeNode}
     *
     * @see TypeNode
     */
    TypeNode createTypeNode(final String fieldName,
                            final String parameterName,
                            final boolean optional,
                            final TypeMirror typeMirror,
                            final TypeMirror containingType);
}
