<#-- @ftlvariable name="" type="org.leandreck.endpoints.processor.model.TypeNode" -->
export interface ${typeName} {
<#list children as property>
    ${property.fieldName}: ${property.typeName};
</#list>
}