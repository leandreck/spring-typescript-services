<#-- @ftlvariable name="" type="org.leandreck.endpoints.processor.model.EndpointNode" -->
<#function map seq key>
    <#assign result = []>
    <#list seq as item>
        <#assign result = result + [item[key]]>
    </#list>
    <#return result>
</#function>
<#function flatten seq>
    <#assign result = []>
    <#list seq as innerSeq>
        <#list innerSeq as item>
            <#assign result = result + [item]>
        </#list>
    </#list>
    <#return result>
</#function>
{
    "serviceName": "${serviceName}",
    "serviceUrl": "${serviceURL}",
    "methodCount": ${methods?size},
    "methods": [
        <#list methods as method>
        {
            "name": "${method.name}",
            "url": "${method.url}",
            "httpMethods": ["${method.httpMethods?join("\", \"")}"],
            "returnType": "${method.returnType.type}"
        }<#sep>,</#sep>
        </#list>
    ]
}

<#--methodNames=${map(methods, "name")?join(", ")}-->
<#--httpMethods=${flatten(map(methods, "httpMethods"))?join(", ")}-->
<#--methodUrls=${map(methods, "url")?join(", ")}-->