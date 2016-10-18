<#--

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

-->
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
            <#if method.requestBodyType??>,
            "requestBodyType": "${method.requestBodyType.type}"
            </#if>
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
            <#if method.requestBodyType??>,
            "requestBodyType": "${method.requestBodyType.type}"
            </#if>
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
            <#if method.requestBodyType??>,
            "requestBodyType": "${method.requestBodyType.type}"
            </#if>
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
            <#if method.requestBodyType??>,
            "requestBodyType": "${method.requestBodyType.type}"
            </#if>
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
            <#if method.requestBodyType??>,
            "requestBodyType": "${method.requestBodyType.type}"
            </#if>
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
            <#if method.requestBodyType??>,
            "requestBodyType": "${method.requestBodyType.type}"
            </#if>
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
            <#if method.requestBodyType??>,
            "requestBodyType": "${method.requestBodyType.type}"
            </#if>
        }<#sep>,</#sep>
        </#list>
    ]
}

<#--methodNames=${map(methods, "name")?join(", ")}-->
<#--httpMethods=${flatten(map(methods, "httpMethods"))?join(", ")}-->
<#--methodUrls=${map(methods, "url")?join(", ")}-->