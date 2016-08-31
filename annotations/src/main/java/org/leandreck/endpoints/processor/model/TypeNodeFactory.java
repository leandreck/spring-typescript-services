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
package org.leandreck.endpoints.processor.model;

import org.leandreck.endpoints.annotations.TypeScriptIgnore;
import org.leandreck.endpoints.annotations.TypeScriptType;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 28.08.2016.
 */
public class TypeNodeFactory {

    private static final Map<String, String> mappings = new HashMap<>(20);

    static {
        //Void
        mappings.put("VOID", "Void");

        //Number
        mappings.put("BYTE", "Number");
        mappings.put("Byte", "Number");
        mappings.put("SHORT", "Number");
        mappings.put("Short", "Number");
        mappings.put("INT", "Number");
        mappings.put("Integer", "Number");
        mappings.put("LONG", "Number");
        mappings.put("Long", "Number");
        mappings.put("FLOAT", "Number");
        mappings.put("Float", "Number");
        mappings.put("DOUBLE", "Number");
        mappings.put("Double", "Number");
        mappings.put("BigDecimal", "Number");
        mappings.put("BigInteger", "Number");

        //String
        mappings.put("CHAR", "String");
        mappings.put("Character", "String");
        mappings.put("String", "String");

        //Boolean
        mappings.put("BOOLEAN", "Boolean");
        mappings.put("Boolean", "Boolean");

        //Date
        mappings.put("Date", "Date");
    }

    private final Types typeUtils;

    private final Map<String, List<TypeNode>> createdChildren = new ConcurrentHashMap<>(200);

    public TypeNodeFactory(final Types typeUtils) {
        this.typeUtils = typeUtils;
    }

    public TypeNode createTypeNode(final VariableElement variableElement) {
        final TypeScriptType typeScriptTypeAnnotation = variableElement.getAnnotation(TypeScriptType.class);
        final TypeMirror typeMirror = variableElement.asType();
        final String typeName = defineName(typeMirror, typeScriptTypeAnnotation);
        final String fieldName = variableElement.getSimpleName().toString();
        final List<TypeNode> children = createdChildren.get(typeName);
        if (children != null) {
            return initType(fieldName, typeName, typeMirror, typeScriptTypeAnnotation, children);
        }
        return initType(fieldName, typeName, typeMirror, typeScriptTypeAnnotation);
    }

    public TypeNode createTypeNode(final TypeMirror typeMirror) {
        final String typeName = defineName(typeMirror);
        final String fieldName = "TYPE-ROOT";
        final List<TypeNode> children = createdChildren.get(typeName);
        if (children != null) {
            return initType(fieldName, typeName, typeMirror, null, children);
        }
        return initType(fieldName, typeName, typeMirror, null);
    }

    private TypeNode initType(String fieldName, String typeName, TypeMirror typeMirror, final TypeScriptType typeScriptTypeAnnotation) {
        final TypeNode newTypeNode;
        if (mappings.containsValue(typeName)) {
            //don't traverse mapped types
            newTypeNode = new TypeNode(fieldName, typeName);
        } else {
            final TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
            final String template = defineTemplate(typeElement, typeScriptTypeAnnotation);
            final List<String> publicGetter = definePublicGetter(typeElement);
            final List<TypeNode> children = defineChildren(typeElement, publicGetter);
            newTypeNode = new TypeNode(fieldName, typeName, template, children);
            createdChildren.put(typeName, children); //as traversing children happens in parallel we might have done useless work, who cares?
        }
        return newTypeNode;
    }

    private TypeNode initType(String fieldName, String typeName, TypeMirror typeMirror, final TypeScriptType typeScriptTypeAnnotation, List<TypeNode> children) {
        final TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
        final String template = defineTemplate(typeElement, typeScriptTypeAnnotation);
        return new TypeNode(fieldName, typeName, template, children);
    }

    private List<String> definePublicGetter(final TypeElement typeElement) {
        return ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
                .filter(g -> g.getSimpleName().toString().startsWith("get") || g.getSimpleName().toString().startsWith("is"))
                .filter(g -> g.getModifiers().contains(Modifier.PUBLIC))
                .filter(g -> !g.getModifiers().contains(Modifier.ABSTRACT))//FIXME filter remaining modifiers
                .map(g -> g.getSimpleName().toString())
                .collect(toList());
    }

    private boolean filterVariableElements(final VariableElement variableElement, final List<String> publicGetter) {
        return publicGetter.stream().map(g -> g.toLowerCase().indexOf(variableElement.getSimpleName().toString().toLowerCase()) > -1)
                .reduce(false, (a, b) -> a || b);
    }

    private List<TypeNode> defineChildren(final TypeElement typeElement, final List<String> publicGetter) {
        return ElementFilter.fieldsIn(typeElement.getEnclosedElements()).stream()
                .filter(c -> c.getAnnotation(TypeScriptIgnore.class) == null)
                .filter(c -> !c.getModifiers().contains(Modifier.TRANSIENT))
                .filter(c -> filterVariableElements(c, publicGetter))
                .map(this::createTypeNode).collect(toList());
    }

    private static String defineTemplate(final TypeElement typeElement, final TypeScriptType typeScriptTypeAnnotation) {
        final String template;
        if (typeScriptTypeAnnotation == null || typeScriptTypeAnnotation.template().isEmpty()) {
            template = "/org/leandreck/endpoints/templates/typescript/interface.ftl";
        } else {
            template = typeScriptTypeAnnotation.template();
        }
        return template;
    }

    private String defineName(final TypeMirror typeMirror, final TypeScriptType typeScriptTypeAnnotation) {
        //check if has a annotation and a type
        if (typeScriptTypeAnnotation != null) {
            final String typeFromAnnotation = defineTypeFromAnnotation(typeScriptTypeAnnotation);
            if (!"UNDEFINED".equals(typeFromAnnotation)) {
                return typeFromAnnotation;
            }
        }

        return defineName(typeMirror);
    }

    private String defineName(final TypeMirror typeMirror) {

        //check if it is a primitiv or default mapped type
        final String mappedType = defineMappedType(typeMirror);
        if (!"UNDEFINED".equals(mappedType)) {
            return mappedType;
        }

        //as of now it is selfdeclared, array or generic-type
        final List<? extends TypeMirror> typeArguments;
        if (TypeKind.DECLARED.equals(typeMirror.getKind())) {
            final DeclaredType declaredType = (DeclaredType) typeMirror;
            typeArguments = declaredType.getTypeArguments();
        } else {
            typeArguments = Collections.emptyList();
        }

        final String typeString;
        try {
            if (typeArguments.isEmpty()) {
                //Resolve simple type
                typeString = "I" + typeUtils.asElement(typeMirror).getSimpleName().toString();
            } else if (typeArguments.size() == 1) {
                //Resolve array type
                final TypeElement arrayType = (TypeElement) typeUtils.asElement(typeArguments.get(0));
                typeString = "I" + arrayType.getSimpleName().toString() + "[]";
            } else if (typeArguments.size() == 2) {
                //Resolve hash type
                final TypeElement keyType = (TypeElement) typeUtils.asElement(typeArguments.get(0));
                final TypeElement valueType = (TypeElement) typeUtils.asElement(typeArguments.get(1));
                typeString = "{ [index: I" + keyType.getSimpleName().toString() + "]: I" + valueType.getSimpleName().toString() + " }";
            } else {
                typeString = "ERROR";
            }
            return typeString;
        } catch (NullPointerException nullex) {
            nullex.printStackTrace();
        }
        return "ERROR";
    }

    private String defineMappedType(TypeMirror typeMirror) {
        final TypeKind kind = typeMirror.getKind();
        final String mappedType;
        if (kind.isPrimitive() || TypeKind.VOID.equals(kind)) {
            mappedType = mappings.get(kind.name());
        } else if (TypeKind.DECLARED.equals(kind)) {
            final String key = typeUtils.asElement(typeMirror).getSimpleName().toString();
            mappedType = mappings.get(key);
        } else {
            mappedType = null;
        }

        return (mappedType == null) ? "UNDEFINED" : mappedType;
    }

    private String defineTypeFromAnnotation(final TypeScriptType annotation) {
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        return "UNDEFINED";
    }
}
