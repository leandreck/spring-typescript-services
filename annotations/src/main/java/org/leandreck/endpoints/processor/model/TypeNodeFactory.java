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
import org.leandreck.endpoints.processor.config.TemplateConfiguration;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 */
class TypeNodeFactory {

    private static final Map<String, String> mappings = new HashMap<>(20);
    private static final String NUMBER_TYPE = "number";
    private static final String STRING_TYPE = "string";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String UNDEFINED = "UNDEFINED";
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";
    private final TypeMirror objectMirror;

    static {
        //Void
        mappings.put("VOID", "void");

        //Number
        mappings.put("BYTE", NUMBER_TYPE);
        mappings.put("Byte", NUMBER_TYPE);
        mappings.put("SHORT", NUMBER_TYPE);
        mappings.put("Short", NUMBER_TYPE);
        mappings.put("INT", NUMBER_TYPE);
        mappings.put("Integer", NUMBER_TYPE);
        mappings.put("LONG", NUMBER_TYPE);
        mappings.put("Long", NUMBER_TYPE);
        mappings.put("FLOAT", NUMBER_TYPE);
        mappings.put("Float", NUMBER_TYPE);
        mappings.put("DOUBLE", NUMBER_TYPE);
        mappings.put("Double", NUMBER_TYPE);
        mappings.put("BigDecimal", NUMBER_TYPE);
        mappings.put("BigInteger", NUMBER_TYPE);

        //String
        mappings.put("CHAR", STRING_TYPE);
        mappings.put("Character", STRING_TYPE);
        mappings.put("String", STRING_TYPE);

        //Boolean
        mappings.put("BOOLEAN", BOOLEAN_TYPE);
        mappings.put("Boolean", BOOLEAN_TYPE);

        //Date
        mappings.put("Date", "Date");
        mappings.put("LocalDate", "Date");

        //any
        mappings.put("Object", "any");
    }

    private final Types typeUtils;
    private final Elements elementUtils;

    private final Map<String, List<TypeNode>> createdChildren = new ConcurrentHashMap<>(200);
    private final TemplateConfiguration configuration;

    TypeNodeFactory(final TemplateConfiguration configuration,
                           final Types typeUtils,
                           final Elements elementUtils) {
        this.configuration = configuration;
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        objectMirror = elementUtils.getTypeElement(JAVA_LANG_OBJECT).asType();
    }

    /**
     * Factory Method to create new Root-TypeNodes like Returnvalues of Methods.
     *
     * @param typeMirror {@link TypeMirror} of Returnvalue or Parameter.
     * @return created {@link TypeNode} from given typeMirror
     */
    TypeNode createTypeNode(final TypeMirror typeMirror) {
        final String fieldName = "TYPE-ROOT";
        return initType(fieldName, null, typeMirror);
    }

    private <A extends Annotation> A getAnnotationForClass(final TypeMirror typeMirror, final Class<A> annotation) {
        final TypeKind kind = typeMirror.getKind();
        final TypeMirror realMirror;
        if (TypeKind.ARRAY.equals(kind)) {
            realMirror = ((ArrayType) typeMirror).getComponentType();
        } else {
            realMirror = typeMirror;
        }

        final Element definingElement = typeUtils.asElement(realMirror);
        return (definingElement != null) ? definingElement.getAnnotation(annotation) : null;
    }

    /**
     * Factory Method to create new TypeNodes from Methodparameters or Children-TypeNodes of TypeNodes.
     * TypeNodes created from {@link VariableElement} include the name of the Field in which they were encountered.
     *
     * @param variableElement {@link VariableElement} of Methodparameter or Field.
     * @return created {@link TypeNode} from given variableElement
     */
    TypeNode createTypeNode(final VariableElement variableElement, final String parameterName) {
        final TypeMirror typeMirror = variableElement.asType();

        final String fieldName = variableElement.getSimpleName().toString();
        return initType(fieldName, parameterName, typeMirror);
    }

    private TypeNode initType(String fieldName, String parameterName, TypeMirror typeMirror) {
        final TypeScriptType typeScriptTypeAnnotation = getAnnotationForClass(typeMirror, TypeScriptType.class);
        final TypeNodeKind typeNodeKind = defineKind(typeMirror);
        final String typeName = defineName(typeMirror, typeNodeKind, typeScriptTypeAnnotation);

        final TypeNode newTypeNode;
        //don't traverse mapped types
        if (mappings.containsValue(typeName)) {
            newTypeNode = new TypeNode(fieldName, parameterName, typeName, typeNodeKind);
        } else {
            final List<TypeNode> typeParameters = defineTypeParameters(typeNodeKind, typeMirror);
            final List<TypeNode> cachedChildren = createdChildren.get(typeName);
            final TypeElement typeElement = getDefiningClassElement(typeNodeKind, typeMirror);
            final String template = defineTemplate(typeElement, configuration, typeScriptTypeAnnotation, typeNodeKind);

            if (cachedChildren != null) {
                newTypeNode = new TypeNode(fieldName, parameterName, typeName, typeParameters, template, typeNodeKind, cachedChildren, defineEnumValues(typeMirror));
            } else {
                final List<String> publicGetter = definePublicGetter(typeElement, isLombokAnnotatedType(typeMirror));
                final List<TypeNode> children = defineChildren(typeElement, publicGetter);
                newTypeNode = new TypeNode(fieldName, parameterName, typeName, typeParameters, template, typeNodeKind, children, defineEnumValues(typeMirror));
                createdChildren.put(typeName, children); //as traversing children happens in parallel we might have done useless work, who cares?
            }
        }
        return newTypeNode;
    }

    private boolean isLombokAnnotatedType(final TypeMirror typeMirror) {
        return Arrays.stream(new String[]{"lombok.Data", "lombok.Value", "lombok.Getter"})
            .anyMatch(annotationName -> {
                try {
                    Class dataAnnotationClass = Class.forName(annotationName);
                    Object dataAnnotation = getAnnotationForClass(typeMirror, dataAnnotationClass);
                    return (dataAnnotation != null);
                } catch (Exception e) {
                    //ignored
                }
                return false;
            });
    }

    private TypeElement getDefiningClassElement(final TypeNodeKind typeNodeKind, final TypeMirror typeMirror) {
        final TypeMirror realMirror;
        if (TypeNodeKind.ARRAY.equals(typeNodeKind)) {
            realMirror = ((ArrayType) typeMirror).getComponentType();
        } else {
            realMirror = typeMirror;
        }
        return (TypeElement) typeUtils.asElement(realMirror);
    }

    private List<TypeNode> defineTypeParameters(final TypeNodeKind typeNodeKind, final TypeMirror typeMirror) {
        final List<TypeNode> typeParameters;
        if (TypeNodeKind.COLLECTION.equals(typeNodeKind) || TypeNodeKind.MAP.equals(typeNodeKind)) {
            final DeclaredType declaredType = (DeclaredType) typeMirror;
            final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

            typeParameters = typeArguments.stream()
                    .map(t -> t.getKind().equals(TypeKind.WILDCARD) ? objectMirror : t)
                    .map(this::createTypeNode)
                    .collect(toList());

            if (typeParameters.isEmpty()) {
                typeParameters.add(createTypeNode(objectMirror));
                if (TypeNodeKind.MAP.equals(typeNodeKind)) {
                    typeParameters.add(createTypeNode(objectMirror));
                }
            }
        } else {
            typeParameters = Collections.emptyList();
        }

        return typeParameters;
    }

    private List<String> definePublicGetter(final TypeElement typeElement, boolean lombokType) {

        final List<String> publicGetters = ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
                .filter(g -> g.getSimpleName().toString().startsWith("get") || g.getSimpleName().toString().startsWith("is"))
                .filter(g -> g.getModifiers().contains(Modifier.PUBLIC))
                .filter(g -> !g.getModifiers().contains(Modifier.ABSTRACT))//FIXME filter remaining modifiers
                .map(g -> g.getSimpleName().toString())
                .collect(toList());

        if (lombokType) {
            ElementFilter.fieldsIn(typeElement.getEnclosedElements()).stream()
                    .filter(g -> !g.getModifiers().contains(Modifier.STATIC))
                    .map(g -> g.getSimpleName().toString())
                    .forEach(publicGetters::add);
        }

        return publicGetters;
    }

    private boolean filterVariableElements(final VariableElement variableElement, final List<String> publicGetter) {
        return publicGetter.stream().map(g -> g.toLowerCase().endsWith(variableElement.getSimpleName().toString().toLowerCase()))
                .reduce(false, (a, b) -> a || b);
    }

    private List<TypeNode> defineChildren(final TypeElement typeElement, final List<String> publicGetter) {
        return ElementFilter.fieldsIn(typeElement.getEnclosedElements()).stream()
                .filter(c -> c.getAnnotation(TypeScriptIgnore.class) == null)
                .filter(c -> !c.getModifiers().contains(Modifier.TRANSIENT))
                .filter(c -> filterVariableElements(c, publicGetter))
                .map(it -> this.createTypeNode(it, /* parameterName */ null))
                .collect(toList());
    }

    static String defineTemplate(final TypeElement typeElement, final TemplateConfiguration templateConfiguration,
                                         final TypeScriptType typeScriptTypeAnnotation,
                                         final TypeNodeKind kind) {

        if (templateConfiguration == null) {
            throw new MissingConfigurationTemplateException("TemplateConfiguration is null while processing Element", typeElement);
        }

        final String template;
        if (typeScriptTypeAnnotation == null || typeScriptTypeAnnotation.template() == null || typeScriptTypeAnnotation.template().isEmpty()) {
            if (TypeNodeKind.ENUM.equals(kind)) {
                template = templateConfiguration.getEnumTemplate();
            } else {
                template = templateConfiguration.getInterfaceTemplate();
            }
        } else {
            template = typeScriptTypeAnnotation.template();
        }

        return template;
    }

    private String defineName(final TypeMirror typeMirror, final TypeNodeKind typeNodeKind, final TypeScriptType typeScriptTypeAnnotation) {
        //check if has a annotation and a type
        if (typeScriptTypeAnnotation != null) {
            final String typeFromAnnotation = defineTypeFromAnnotation(typeScriptTypeAnnotation);
            if (!UNDEFINED.equals(typeFromAnnotation)) {
                return typeFromAnnotation;
            }
        }

        final String name;
        switch (typeNodeKind) {
            case ARRAY:
                name = defineNameFromArrayType((ArrayType) typeMirror);
                break;
            case COLLECTION:
                name = defineNameFromCollectionType((DeclaredType) typeMirror);
                break;
            case MAP:
                name = defineNameFromMapType((DeclaredType) typeMirror);
                break;
            case SIMPLE:
            default:
                name = defineNameFromSimpleType(typeMirror);
                break;
        }
        return name;
    }

    private static String defineTypeFromAnnotation(final TypeScriptType annotation) {
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        return UNDEFINED;
    }

    private String defineNameFromSimpleType(final TypeMirror typeMirror) {
        final TypeKind kind = typeMirror.getKind();
        final String typeName;
        if (kind.isPrimitive() || TypeKind.VOID.equals(kind)) {
            typeName = mappings.get(kind.name());
        } else if (TypeKind.DECLARED.equals(kind)) {
            final String key = typeUtils.asElement(typeMirror).getSimpleName().toString();
            final String mappedValue = mappings.get(key);
            if (mappedValue == null) {
                typeName = key;
            } else {
                typeName = mappedValue;
            }
        } else {
            typeName = UNDEFINED;
        }
        return typeName;
    }

    private String defineNameFromArrayType(final ArrayType arrayMirror) {
        final TypeMirror componentMirror = arrayMirror.getComponentType();
        return defineNameFromSimpleType(componentMirror);
    }

    private String defineNameFromCollectionType(final DeclaredType declaredType) {
        final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        final TypeElement listElement;
        if (typeArguments.isEmpty()) {
            listElement = elementUtils.getTypeElement(JAVA_LANG_OBJECT);
        } else {
            final TypeMirror genericMirror = typeArguments.get(0);
            if (TypeKind.WILDCARD.equals(genericMirror.getKind())) {
                listElement = elementUtils.getTypeElement(JAVA_LANG_OBJECT);
            } else {
                listElement = (TypeElement) typeUtils.asElement(genericMirror);
            }
        }

        return defineNameFromSimpleType(listElement.asType());
    }

    private String defineNameFromMapType(final DeclaredType declaredType) {
        final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        final TypeElement keyElement;
        final TypeElement valueElement;
        if (typeArguments.isEmpty()) {
            keyElement = elementUtils.getTypeElement(JAVA_LANG_OBJECT);
            valueElement = elementUtils.getTypeElement(JAVA_LANG_OBJECT);
        } else {
            final TypeMirror keyMirror = typeArguments.get(0);
            if (TypeKind.WILDCARD.equals(keyMirror.getKind())) {
                keyElement = elementUtils.getTypeElement(JAVA_LANG_OBJECT);
            } else {
                keyElement = (TypeElement) typeUtils.asElement(keyMirror);
            }

            final TypeMirror valueMirror = typeArguments.get(1);
            if (TypeKind.WILDCARD.equals(valueMirror.getKind())) {
                valueElement = elementUtils.getTypeElement(JAVA_LANG_OBJECT);
            } else {
                valueElement = (TypeElement) typeUtils.asElement(valueMirror);
            }
        }

        final String keyName = defineNameFromSimpleType(keyElement.asType());
        final String valueName = defineNameFromSimpleType(valueElement.asType());

        return keyName + "/" + valueName;
    }

    private TypeNodeKind defineKind(final TypeMirror typeMirror) {
        final TypeKind kind = typeMirror.getKind();
        final TypeNodeKind typeNodeKind;

        switch (kind) {
            case ARRAY:
                typeNodeKind = TypeNodeKind.ARRAY;
                break;

            case DECLARED:
                typeNodeKind = defineDeclaredTypeNodeKind(typeMirror);
                break;

            default:
                typeNodeKind = TypeNodeKind.SIMPLE;
                break;
        }

        return typeNodeKind;
    }

    private TypeNodeKind defineDeclaredTypeNodeKind(final TypeMirror typeMirror) {
        final ElementKind elementKind = typeUtils.asElement(typeMirror).getKind();
        final TypeMirror collectionMirror = elementUtils.getTypeElement("java.util.Collection").asType();
        final TypeMirror mapMirror = typeUtils.getDeclaredType(elementUtils.getTypeElement("java.util.Map"));

        final TypeNodeKind typeNodeKind;
        if (ElementKind.ENUM.equals(elementKind)) {
            typeNodeKind = TypeNodeKind.ENUM;
        } else if (typeUtils.isAssignable(typeMirror, typeUtils.erasure(collectionMirror))) {
            typeNodeKind = TypeNodeKind.COLLECTION;
        } else if (typeUtils.isAssignable(typeMirror, typeUtils.erasure(mapMirror))) {
            typeNodeKind = TypeNodeKind.MAP;
        } else {
            typeNodeKind = TypeNodeKind.SIMPLE;
        }
        return typeNodeKind;
    }

    private Set<EnumValue> defineEnumValues(final TypeMirror typeMirror) {
        final Element enumElement = typeUtils.asElement(typeMirror);
        if (enumElement == null) {
            return Collections.emptySet();
        }

        return enumElement.getEnclosedElements().stream()
                .filter(e -> ElementKind.ENUM_CONSTANT.equals(e.getKind()))
                .map(e -> new EnumValue(e.getSimpleName().toString()))
                .collect(Collectors.toSet());
    }
}
