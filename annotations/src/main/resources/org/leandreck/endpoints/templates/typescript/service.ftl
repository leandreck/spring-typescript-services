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
<#function buildUrl variables url>
    <#assign result = url>
    <#list variables as item>
        '/api/{value}/{some}'
        '/api/' + value + '/' + some + ''
        <#assign result = result?replace('{', '')>
    </#list>
    <#return result>
</#function>
<#list types as type>
import { ${type.typeName} } from './${type.typeName?uncap_first}.model.generated';
</#list>

import { Http, Response, RequestOptions, Headers, RequestOptionsArgs } from "@angular/http";
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/map';

@Injectable()
export class ${serviceName} {
    private serviceBaseURL = '${serviceURL}';
    constructor(private http: Http) { }
    /* GET */
<#list getGetMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Get(<#list method.pathVariableTypes as variable>${variable.fieldName}: ${variable.type}<#sep>, </#sep></#list>): Observable<${method.returnType.type}> {
        let url = this.serviceBaseURL + '${expandedURL}';

        return this.httpGet(url)
            .map((response: Response) => <${method.returnType.type}>response.json())
            .catch((error: Response) => this.handleError(error));
    }

</#list>
<#if getGetMethods()?size gt 0>
    private httpGet(url: string): Observable<Response> {
        console.info('httpGet: ' + serviceUrl);
        return this.http.get(url);
    }
</#if>

    /* HEAD */
<#list getHeadMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Head(<#list method.pathVariableTypes as variable>${variable.fieldName}: ${variable.type}<#sep>, </#sep></#list>): Observable<Response> {
        let url = this.serviceBaseURL + '${expandedURL}';
        return this.httpHead(url)
            .catch((error: Response) => this.handleError(error));
    }

</#list>
<#if getHeadMethods()?size gt 0>
    private httpHead(url: string): Observable<Response> {
        console.info('httpHead: ' + url);
        return this.http.head(url);
    }
</#if>

    /* POST */
<#list getPostMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Post(<#list method.pathVariableTypes as variable>${variable.fieldName}: ${variable.type}<#sep>, </#sep></#list><#if method.pathVariableTypes?size gt 0>, </#if>${method.requestBodyType.fieldName}: ${method.requestBodyType.type}): Observable<${method.returnType.type}> {
        let url = this.serviceBaseURL + '${expandedURL}';
        return this.httpPost(url, ${method.requestBodyType.fieldName})
            .map((response: Response) => <${method.returnType.type}>response.json())
            .catch((error: Response) => this.handleError(error));
    }

</#list>
<#if getPostMethods()?size gt 0>
    private httpPost(url: string, body: any): Observable<Response> {
        console.info('httpPost: ' + url);
        return this.http.post(url, body);
    }
</#if>

    /* PUT */
<#list getPutMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Put(<#list method.pathVariableTypes as variable>${variable.fieldName}: ${variable.type}<#sep>, </#sep></#list><#if method.pathVariableTypes?size gt 0>, </#if>${method.requestBodyType.fieldName}: ${method.requestBodyType.type}): Observable<${method.returnType.type}> {
        let url = this.serviceBaseURL + '${expandedURL}';
        return this.httpPut(url, ${method.requestBodyType.fieldName})
            .map((response: Response) => <${method.returnType.type}>response.json())
            .catch((error: Response) => this.handleError(error));
    }

</#list>
<#if getPutMethods()?size gt 0>
    private httpPut(url: string, body: any): Observable<Response> {
        console.info('httpPut: ' + url);
        return this.http.put(url, body);
    }
</#if>

    /* PATCH */
<#list getPatchMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Patch(<#list method.pathVariableTypes as variable>${variable.fieldName}: ${variable.type}<#sep>, </#sep></#list><#if method.pathVariableTypes?size gt 0>, </#if>${method.requestBodyType.fieldName}: ${method.requestBodyType.type}): Observable<${method.returnType.type}> {
        let url = this.serviceBaseURL + '${expandedURL}';
        return this.httpPatch(url, ${method.requestBodyType.fieldName})
            .map((response: Response) => <${method.returnType.type}>response.json())
            .catch((error: Response) => this.handleError(error));
    }

</#list>
<#if getPatchMethods()?size gt 0>
    private httpPatch(url: string, body: any): Observable<Response> {
        console.info('httpPatch: ' + url);
        return this.http.patch(url, body);
    }
</#if>

    /* DELETE */
<#list getDeleteMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Delete(<#list method.pathVariableTypes as variable>${variable.fieldName}: ${variable.type}<#sep>, </#sep></#list>): Observable<Response> {
        let url = this.serviceBaseURL + '${expandedURL}';
        return this.httpDelete(url)
          .catch((error: Response) => this.handleError(error));
    }

</#list>
<#if getDeleteMethods()?size gt 0>
    private httpDelete(url: string): Observable<Response> {
        console.info('httpDelete: ' + url);
        return this.http.delete(url);
    }
</#if>

    /* OPTIONS */
<#list getOptionsMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Options(<#list method.pathVariableTypes as variable>${variable.fieldName}: ${variable.type}<#sep>, </#sep></#list><#if method.pathVariableTypes?size gt 0>, </#if>${method.requestBodyType.fieldName}: ${method.requestBodyType.type}): Observable<Response> {
        let url = this.serviceBaseURL + '${expandedURL}';
        return this.httpOptions(url)
            .catch((error: Response) => this.handleError(error));
    }

</#list>
<#if getOptionsMethods()?size gt 0>
    private httpOptions(url: string, body: any): Observable<Response> {
        console.info('httpOptions: ' + url);
        return this.http.options(url, body);
    }
</#if>

    /* TRACE */
<#list getTraceMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Trace(<#list method.pathVariableTypes as variable>${variable.fieldName}: ${variable.type}<#sep>, </#sep></#list><#if method.pathVariableTypes?size gt 0>, </#if>${method.requestBodyType.fieldName}: ${method.requestBodyType.type}): Observable<${method.requestBodyType.type}> {
        let url = this.serviceBaseURL + '${expandedURL}';
        return this.httpTrace(url, ${method.requestBodyType.fieldName})
            .map((response: Response) => <${method.returnType.type}>response.json())
            .catch((error: Response) => this.handleError(error));
    }

</#list>
<#if getTraceMethods()?size gt 0>
    private httpTrace(url: string, body: any): Observable<Response> {
        console.info('httpTrace: ' + url);
        return this.http.trace(url, body);
    }
</#if>

    private handleError(error: Response) {
        // in a real world app, we may send the error to some remote logging infrastructure
        // instead of just logging it to the console
        console.error(error);
    }

}
