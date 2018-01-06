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
import { HttpClient, HttpParams, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/map';
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
import { ${type.typeName} } from './${type.typeName?lower_case}.model.generated';
</#list>
import { ServiceConfig } from './api.module';

@Injectable()
export class ${serviceName} {
    private get serviceBaseURL(): string {
        return this.serviceConfig.context + '${serviceURL}';
    }
    private get onError(): (error: Response) => ErrorObservable {
        return this.serviceConfig.onError || this.handleError.bind(this);
    }

    constructor(private httpClient: HttpClient, private serviceConfig: ServiceConfig) { }
    /* GET */
<#list getGetMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Get(<#list method.functionParameterTypes as variable>${variable.asFunctionParameter}: ${variable.type}<#sep>, </#sep></#list>): Observable<${method.returnType.type}> {
        const url = this.serviceBaseURL + '${expandedURL}';
        const params = this.getHttpParams({<#list method.queryParameterTypes><#items as queryParam>
            ${queryParam.asVariableName}: ${queryParam.asVariableName}<#sep>,</#sep>
        </#items></#list>});

        return this.httpClient.get<${method.returnType.type}>(url, {params: params})
            .catch((error: Response) => this.onError(error));
    }

</#list>

    /* HEAD */
<#list getHeadMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Head(<#list method.functionParameterTypes as variable>${variable.asFunctionParameter}: ${variable.type}<#sep>, </#sep></#list>): Observable<${method.returnType.type}> {
        const url = this.serviceBaseURL + '${expandedURL}';
        const params = this.getHttpParams({<#list method.queryParameterTypes><#items as queryParam>
            ${queryParam.asVariableName}: ${queryParam.asVariableName}<#sep>,</#sep>
        </#items></#list>});

        return this.httpClient.head<${method.returnType.type}>(url, {params: params})
            .catch((error: Response) => this.onError(error));
    }

</#list>

    /* POST */
<#list getPostMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Post(<#list method.functionParameterTypes as variable>${variable.asFunctionParameter}: ${variable.type}<#sep>, </#sep></#list>): Observable<${method.returnType.type}> {
        const url = this.serviceBaseURL + '${expandedURL}';
        const params = this.getHttpParams({<#list method.queryParameterTypes><#items as queryParam>
            ${queryParam.asVariableName}: ${queryParam.asVariableName}<#sep>,</#sep>
        </#items></#list>});

        return this.httpClient.post<${method.returnType.type}>(url, ${method.requestBodyType.fieldName}, {params: params})
            .catch((error: Response) => this.onError(error));
    }

</#list>

    /* PUT */
<#list getPutMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Put(<#list method.functionParameterTypes as variable>${variable.asFunctionParameter}: ${variable.type}<#sep>, </#sep></#list>): Observable<${method.returnType.type}> {
        const url = this.serviceBaseURL + '${expandedURL}';
        const params = this.getHttpParams({<#list method.queryParameterTypes><#items as queryParam>
            ${queryParam.asVariableName}: ${queryParam.asVariableName}<#sep>,</#sep>
        </#items></#list>});

        return this.httpClient.put<${method.returnType.type}>(url, ${method.requestBodyType.fieldName}, {params: params})
            .catch((error: Response) => this.onError(error));
    }

</#list>

    /* PATCH */
<#list getPatchMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Patch(<#list method.functionParameterTypes as variable>${variable.asFunctionParameter}: ${variable.type}<#sep>, </#sep></#list>): Observable<${method.returnType.type}> {
        const url = this.serviceBaseURL + '${expandedURL}';
        const params = this.getHttpParams({<#list method.queryParameterTypes><#items as queryParam>
            ${queryParam.asVariableName}: ${queryParam.asVariableName}<#sep>,</#sep>
        </#items></#list>});

        return this.httpClient.patch<${method.returnType.type}>(url, ${method.requestBodyType.fieldName}, {params: params})
            .catch((error: Response) => this.onError(error));
    }

</#list>

    /* DELETE */
<#list getDeleteMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Delete(<#list method.functionParameterTypes as variable>${variable.asFunctionParameter}: ${variable.type}<#sep>, </#sep></#list>): Observable<${method.returnType.type}> {
        const url = this.serviceBaseURL + '${expandedURL}';
        const params = this.getHttpParams({<#list method.queryParameterTypes><#items as queryParam>
            ${queryParam.asVariableName}: ${queryParam.asVariableName}<#sep>,</#sep>
        </#items></#list>});

        return this.httpClient.delete<${method.returnType.type}>(url, {params: params})
            .catch((error: Response) => this.onError(error));
    }

</#list>

    /* OPTIONS */
<#list getOptionsMethods() as method>
    <#assign expandedURL = method.url?replace('{', '\' + ')>
    <#assign expandedURL = expandedURL?replace('}', ' + \'')>
    public ${method.name}Options(<#list method.functionParameterTypes as variable>${variable.asFunctionParameter}: ${variable.type}<#sep>, </#sep></#list>): Observable<${method.returnType.type}> {
        const url = this.serviceBaseURL + '${expandedURL}';
        const params = this.getHttpParams({<#list method.queryParameterTypes><#items as queryParam>
            ${queryParam.asVariableName}: ${queryParam.asVariableName}<#sep>,</#sep>
        </#items></#list>});

        return this.httpClient.options<${method.returnType.type}>(url, {params: params})
            .catch((error: Response) => this.onError(error));
    }

</#list>

    <#--/* TRACE NOT SUPPORTED BY HTTPCLIENT*/-->
<#--<#list getTraceMethods() as method>-->
    <#--<#assign expandedURL = method.url?replace('{', '\' + ')>-->
    <#--<#assign expandedURL = expandedURL?replace('}', ' + \'')>-->
    <#--public ${method.name}Trace(<#list method.pathVariableTypes as variable>${variable.fieldName}: ${variable.type}<#sep>, </#sep></#list><#if method.pathVariableTypes?size gt 0>, </#if>${method.requestBodyType.fieldName}: ${method.requestBodyType.type}): Observable<${method.returnType.type}> {-->
        <#--const url = this.serviceBaseURL + '${expandedURL}';-->
        <#--const request = new HttpRequest<${method.requestBodyType.type}>('TRACE', url, ${method.requestBodyType.fieldName}, {-->
            <#--responseType: 'json'-->
        <#--});-->
        <#--return this.httpClient.request<${method.returnType.type}>(request)-->
            <#--.catch((error: Response) => this.handleError(error));-->
    <#--}-->

<#--</#list>-->

    private getHttpParams(data: any): HttpParams {
        let params: HttpParams = new HttpParams();

        Object.keys(data).forEach((key: string) => {
            const value: any = data[key];
            if (value != null) { // Check for null AND undefined
                params = params.set(key, String(value));
            }
        });

        return params;
    }
    
    private handleError(error: Response): ErrorObservable {
        // in a real world app, we may send the error to some remote logging infrastructure
        // instead of just logging it to the console
        this.log('error', error);

        return Observable.throw(error);
    }

    private log(level: string, message: any): void {
        if (this.serviceConfig.debug) {
            console[level](message);
        }
    }
}