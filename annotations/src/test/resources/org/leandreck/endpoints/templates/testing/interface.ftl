<#-- @ftlvariable name="" type="org.leandreck.endpoints.processor.model.TypeNode" -->
{
    "typeName": "${typeName}",
    "children": [
    <#list children as property>
        {
            "fieldName": "${property.fieldName}",
            "typeName": "${property.typeName}",
            "type": "${property.type}",
        }<#sep>,</#sep>
    </#list>
    ]
}