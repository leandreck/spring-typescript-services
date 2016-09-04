<#-- @ftlvariable name="" type="org.leandreck.endpoints.processor.model.EndpointNode" -->
<#list types as type>
import { ${type.typeName} } from './${type.typeName}.model';
</#list>
import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

@Injectable()
export class ${serviceName} {

    private serviceBaseURL = '${serviceURL}'

    constructor(private _http: Http) { }

<#list methods as method>
    <#list method.httpMethods as webMethod>
    ${webMethod}_${method.name}(): ${method.returnType.typeName} {
    return this._http.${webMethod}(this.serviceBaseURL + '${method.url}')
        .map((res: Response) => res.json())
        .catch(this.handleError);
    }

    </#list>
</#list>

    private handleError(error: Response) {
        // in a real world app, we may send the error to some remote logging infrastructure
        // instead of just logging it to the console
        console.error(error);
    }
}


