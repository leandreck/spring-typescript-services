<#-- @ftlvariable name="" type="org.leandreck.endpoints.processor.printer.TypesPackage" -->
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
import { NgModule, ModuleWithProviders, Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
<#list endpoints as service>
import { ${service.serviceName} } from './${service.serviceName?lower_case}.generated';
</#list>

@Injectable()
export interface ServiceConfig {
    context?: string;
    debug?: boolean;
    onError()?: Observable<any>;
}

@NgModule({})
export class APIModule {
    static forRoot(serviceConfig: ServiceConfig = {context: ''}): ModuleWithProviders {
        return {
            ngModule: APIModule,
            providers: [
                {provide: ServiceConfig, useValue: serviceConfig},
                <#list endpoints as service>
                ${service.serviceName}<#sep>,</#sep>
                </#list>
            ]
        };
    }
}