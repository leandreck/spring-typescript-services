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
    "getMethodCount": ${getGetMethods()?size},
    "headMethodCount": ${getHeadMethods()?size},
    "postMethodCount": ${getPostMethods()?size},
    "putMethodCount": ${getPutMethods()?size},
    "patchMethodCount": ${getPatchMethods()?size},
    "deleteMethodCount": ${getDeleteMethods()?size},
    "optionsMethodCount": ${getOptionsMethods()?size},
    "traceMethodCount": ${getTraceMethods()?size},
    "methods": [
        <#list methods as method>
        {
            "name": "${method.name}",
            "url": "${method.url}",
            "httpMethods": ["${method.httpMethods?join("\", \"")}"],
            "returnType": "${method.returnType.type}"
        }<#sep>,</#sep>
        </#list>
    ],
    "getMethods": [
        <#list getGetMethods() as method>
        {
            "name": "${method.name}",
            "url": "${method.url}",
            "httpMethods": ["${method.httpMethods?join("\", \"")}"],
            "returnType": "${method.returnType.type}"
        }<#sep>,</#sep>
        </#list>
    ],
    "headMethods": [
        <#list getHeadMethods() as method>
        {
            "name": "${method.name}",
            "url": "${method.url}",
            "httpMethods": ["${method.httpMethods?join("\", \"")}"],
            "returnType": "${method.returnType.type}"
        }<#sep>,</#sep>
        </#list>
    ],
    "postMethods": [
        <#list getPostMethods() as method>
        {
            "name": "${method.name}",
            "url": "${method.url}",
            "httpMethods": ["${method.httpMethods?join("\", \"")}"],
            "returnType": "${method.returnType.type}"
        }<#sep>,</#sep>
        </#list>
    ],
    "putMethods": [
        <#list getPutMethods() as method>
        {
            "name": "${method.name}",
            "url": "${method.url}",
            "httpMethods": ["${method.httpMethods?join("\", \"")}"],
            "returnType": "${method.returnType.type}"
        }<#sep>,</#sep>
        </#list>
    ],
    "patchMethods": [
        <#list getPatchMethods() as method>
        {
            "name": "${method.name}",
            "url": "${method.url}",
            "httpMethods": ["${method.httpMethods?join("\", \"")}"],
            "returnType": "${method.returnType.type}"
        }<#sep>,</#sep>
        </#list>
    ],
    "deleteMethods": [
        <#list getDeleteMethods() as method>
        {
            "name": "${method.name}",
            "url": "${method.url}",
            "httpMethods": ["${method.httpMethods?join("\", \"")}"],
            "returnType": "${method.returnType.type}"
        }<#sep>,</#sep>
        </#list>
    ],
    "optionsMethods": [
        <#list getOptionsMethods() as method>
        {
            "name": "${method.name}",
            "url": "${method.url}",
            "httpMethods": ["${method.httpMethods?join("\", \"")}"],
            "returnType": "${method.returnType.type}"
        }<#sep>,</#sep>
        </#list>
    ],
    "traceMethods": [
        <#list getTraceMethods() as method>
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