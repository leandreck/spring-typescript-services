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
    ],
    "values": [
    <#list enumValues as value>
        {
            "value": "${value.name}",
            "ordinal": "${value.ordinal}"
        }<#sep>,</#sep>
    </#list>
    ]
}