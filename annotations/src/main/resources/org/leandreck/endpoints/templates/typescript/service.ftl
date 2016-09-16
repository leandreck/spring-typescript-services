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

/* GET */
<#list getGetMethods() as method>
    ${method.name}(): ${method.returnType.type} {
    return this._http.get(this.serviceBaseURL + '${method.url}')
        .map((res: Response) => res.json())
        .catch(this.handleError);
    }

</#list>

/* HEAD */
<#list getHeadMethods() as method>
    ${method.name}(): ${method.returnType.type} {
    return this._http.head(this.serviceBaseURL + '${method.url}')
        .map((res: Response) => res.json())
        .catch(this.handleError);
    }

</#list>

/* POST */
<#list getPostMethods() as method>
    ${method.name}(): ${method.returnType.type} {
    return this._http.post(this.serviceBaseURL + '${method.url}')
        .map((res: Response) => res.json())
        .catch(this.handleError);
    }

</#list>

/* PUT */
<#list getPutMethods() as method>
    ${method.name}(): ${method.returnType.type} {
    return this._http.put(this.serviceBaseURL + '${method.url}')
        .map((res: Response) => res.json())
        .catch(this.handleError);
    }

</#list>

/* PATCH */
<#list getPatchMethods() as method>
    ${method.name}(): ${method.returnType.type} {
    return this._http.patch(this.serviceBaseURL + '${method.url}')
        .map((res: Response) => res.json())
        .catch(this.handleError);
    }

</#list>

/* DELETE */
<#list getDeleteMethods() as method>
    ${method.name}(): ${method.returnType.type} {
    return this._http.delete(this.serviceBaseURL + '${method.url}')
        .map((res: Response) => res.json())
        .catch(this.handleError);
    }

</#list>

/* OPTIONS */
<#list getOptionsMethods() as method>
    ${method.name}(): ${method.returnType.type} {
    return this._http.delete(this.serviceBaseURL + '${method.url}')
        .map((res: Response) => res.json())
        .catch(this.handleError);
    }

</#list>

/* TRACE */
<#list getTraceMethods() as method>
    ${method.name}(): ${method.returnType.type} {
    return this._http.delete(this.serviceBaseURL + '${method.url}')
        .map((res: Response) => res.json())
        .catch(this.handleError);
    }

</#list>

    private handleError(error: Response) {
        // in a real world app, we may send the error to some remote logging infrastructure
        // instead of just logging it to the console
        console.error(error);
    }
}


