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
<#list types as type>
import { ${type.typeName} } from './${type.typeName}.model';
</#list>

import {Http, Response, RequestOptions, Headers, RequestOptionsArgs} from "@angular/http";
import { Injectable } from '@angular/core';

import {Observable} from "rxjs/Observable";
import {ErrorObservable} from "rxjs/observable/ErrorObservable";
import "rxjs/add/operator/do";
import "rxjs/add/operator/catch";
import "rxjs/add/observable/throw";

@Injectable()
export class ${serviceName} {

    private serviceBaseURL = '${serviceURL}'

    constructor(private http: Http) { }

    /* GET */
<#list getGetMethods() as method>
    public ${method.name}Get(): Observable<${method.returnType.type}> {
        let url = this.serviceBaseURL + '${method.url}';
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
    public ${method.name}Head(): Observable<Response> {
        let url = this.serviceBaseURL + '${method.url}';
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
    public ${method.name}Post(${method.paramType.fieldName}: ${method.paramType.type}): Observable<${method.returnType.type}> {
        let url = this.serviceBaseURL + '${method.url}';
        return this.httpPost(url, ${method.paramType.fieldName})
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
    public ${method.name}Put(${method.paramType.fieldName}: ${method.paramType.type}): Observable<${method.returnType.type}> {
        let url = this.serviceBaseURL + '${method.url}';
        return this.httpPut(url, ${method.paramType.fieldName})
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
    public ${method.name}Patch(${method.paramType.fieldName}: ${method.paramType.type}): Observable<${method.returnType.type}> {
        let url = this.serviceBaseURL + '${method.url}';
        return this.httpPatch(url, ${method.paramType.fieldName})
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
    public ${method.name}Delete(): Observable<Response> {
        let url = this.serviceBaseURL + '${method.url}';
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
    public ${method.name}Options(${method.paramType.fieldName}: ${method.paramType.type}): Observable<Response> {
        let url = this.serviceBaseURL + '${method.url}';
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
    public ${method.name}Trace(${method.paramType.fieldName}: ${method.paramType.type}): Observable<${method.paramType.type}> {
        let url = this.serviceBaseURL + '${method.url}';
        return this.httpTrace(url, ${method.paramType.fieldName})
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
