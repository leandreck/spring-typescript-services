<!-- [![Stories in Ready](https://badge.waffle.io/mkowalzik/spring-typescript-services.png?label=ready&title=Ready)](https://waffle.io/mkowalzik/spring-typescript-services) -->
[![Build Status][travisbadge img]][travisbadge]
[![Coverity Scan Build Status][coveritybadge img]][coveritybadge]
[![codecov][codecov img]][codecov]
[![Tech Debt][sonar tech img]][sonar tech]
[![Maven Status][mavenbadge img]][mavenbadge]
[![Known Vulnerabilities][snykbadge img]][snykbadge]
[![Codacy Badge][codacy img]][codacy]
[![license][license img]][license]

[![Quality Gate][sonar quality img]][sonar quality]

# spring-typescript-services
Generate **TypeScript interfaces** and **Angular services** from Spring annotated **@RestControllers**. 
Get strongly typed interfaces for your **Spring Boot microservices** in no time.

## Basic example
Spring `@RestController`:
```java
@TypeScriptEndpoint
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerListDTO getAllCustomers() { ... }
}
```
Generated Angular service (TypeScript interfaces not shown here):
```typescript
@Injectable()
export class CustomerController {
    
    private get serviceBaseURL(): string {
        return this.serviceConfig.context + '/api/v1/customers';
    }
    ...

    constructor(private httpClient: HttpClient, private serviceConfig: ServiceConfig) { }

    /* GET */
    public getAllCustomersGet(): Observable<CustomerListDTO> {
        const url = this.serviceBaseURL + '';
        const params = this.createHttpParams({});

        return this.httpClient.get<CustomerListDTO>(url, {params: params})
          .catch(error: HttpErrorResponse) => this.onError(error));
    }
    ...
}
```

## Features

* Generate Angular services from Spring @RestControllers
    * Support of **Angular >= 5** (RxJS >= 5.5)
* Generate **TypeScript interfaces** to map types of your Spring endpoints
* **All HTTP methods** supported via `@RequestMapping` or its shortcut annotations
* Support for **JSON producing endpoints** (more coming)
* Support for **path variables** (`@PathVariable` annotation)

Currently *not* supported:
* Endpoints which do *not* produce JSON
* Support for path parameters (`@PathParam` annotation) 


## Getting started
Just specify the dependency in your Maven or Gradle based build and annotate a Spring `@RestController` 
with `@TypeScriptEndpoint`. Your next compile will then generate TypeScript files for every method with 
a `@RequestMapping` (or any shortcut annotation) producing "application/json" (`MediaType.APPLICATION_JSON_VALUE`).

### Maven
```xml
<dependency>
    <groupId>org.leandreck.endpoints</groupId>
    <artifactId>annotations</artifactId>
    <version>0.4.0</version>
    <scope>provided</scope> <!-- the annotations and processor are only needed at compile time -->
    <optional>true</optional> <!-- they need not to be transitively included in dependent artifacts -->
</dependency>
<!-- * because of spring-boot dependency handling they nevertheless get included in fat jars -->
```

If you are using the **maven-compiler-plugin**, add spring-typescript-services to 
`annotationProcesserPaths` within the plugin configuration:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    ...
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.leandreck.endpoints</groupId>
                <artifactId>annotations</artifactId>
                <version>0.4.0</version>
            </path>
            ...
        </annotationProcessorPaths>
        ...
    </configuration>
</plugin>
```

### Gradle
```gradle
compileOnly 'org.leandreck.endpoints:annotations:0.4.0'
```

### Usage
If supported Spring @RestContollers were found, you will find the generated TypeScript files under `./target/generated-sources/`.

All services are part of an Angular module called `APIModule`, which you only need to import within your own module. 
Currently you also need to manually include `HttpClientModule` from `@angular/common/http`:

```typescript
@NgModule({
  ...
  imports: [
    ...
    APIModule.forRoot(),
    HttpClientModule
  ],
  ...
})
export class AppModule { }
```
See [API](#API) for configuration options.


## Known Issues
* spring-typescript-services does **not work with endpoints returning only a HTTP status code**. Currently it does only 
generate TypeScript services for endpoints producing "application/json" (`MediaType.APPLICATION_JSON_VALUE`).
* When working with **IntelliJ**: "Build Project" by default only does incremental builds. This might break consistency
of the generated files when the annotation processor only receives a part of the Spring @RestControllers. 
Better rely on the output from Maven compile.
* There might be some **unused imports** (e.g. from RxJS) in the generated TypeScript files. This potentially increases 
your bundle size by a tiny amount. Feel free to manually remove those imports.


## API

### Spring Boot Annotations

#### @TypeScriptEndpoint
Annotate your `@RestController` to generate a corresponding Angular service and interface files.

By default the Angular services will have the same name as the `@RestController`. You can overwrite this by specifying 
a custom name:
```java
@TypeScriptEndpoint(value = "FooService")
public class FooController { }
```

You can provide your own [<#FREEMARKER>](freemarker)-template to use for generating TypeScript files for this specific TypeScriptEndpoint.
This overwrites any defaults. The default template is located at `/org/leandreck/endpoints/templates/typescript/service.ftl`.
```java
@TypeScriptEndpoint(template = "/org/leandreck/endpoints/templates/typescript/custom.ftl")
public class FooController { }
```


#### @TypeScriptTemplatesConfiguration
Configure if and what method suffixes are used in the default templates or provide your own.

Disable suffixes:
```java
@TypeScriptTemplatesConfiguration(useSuffixes = false)
public class FooController { }
```

Modify suffix based on HTTP method (e.g. replace `suffixGet` by `suffixPost`):
```java
@TypeScriptTemplatesConfiguration(suffixGet = "GET")
public class FooController { }
```

Modify the TypeScript output by providing your own [<#FREEMARKER>](freemarker)-templates. Have a look at the default ones
[org.leandreck.enpoints.templates.typescript](https://github.com/leandreck/spring-typescript-services/tree/development/annotations/src/main/resources/org/leandreck/endpoints/templates/typescript).
```java
@TypeScriptTemplatesConfiguration(apimodule = "/org/leandreck/endpoints/templates/typescript/custom.ftl")
public class FooController { }
```

Available options:
* `apimodule`
* `enumeration`
* `index`
* `interface`
* `endpoint`

#### @TypeScriptIgnore
Methods or fields annotated with `@TypeScriptIgnore` will be ignored by the annotation processor.

Annotate methods in your `@RestController` if you do not want to include them in the generated Angular service.
If applied to a field it is not included in the respective interface file.

#### @TypeScriptType
Annotate Fields with `@TypeScriptType` to specify a custom template or override default mappings.

```java
@TypeScriptType("any")
private Foobar foo;
```
will result in
```typescript
foo: any
```


### Angular Module

#### APIModule.forRoot()
The `APIModule` optionally receives a `ServiceConfig` which you may use to further configure the generated services.

Modify your endpoint's base URL path:
```typescript
APIModule.forRoot({ context: 'http://www.example.com/path/to/api' })
```

Enable debugging to get some console output:
```typescript
APIModule.forRoot({ debug: true })
```

Provide your own error handler function:
```typescript
APIModule.forRoot({ onError: (error) => Observable.throw('An API error occured:' + error.message) })
```


## How does it work?
spring-typescript-services is a Java annotation processor to generate Angular services and TypeScript types to access your spring @RestControllers. 

It generates a service.ts file for every with @TypeScriptEndpoint annotated class and includes functions 
for every enclosed public Java Method with a @RequestMapping producing JSON.
The TypeScript files are generated by populating [<#FREEMARKER>](freemarker)-template files. 
You can specify your own template files or use the bundled defaults.


[freemarker]: http://freemarker.org/

[travisbadge]:https://travis-ci.org/leandreck/spring-typescript-services
[travisbadge img]:https://travis-ci.org/leandreck/spring-typescript-services.svg?branch=master

[coveritybadge]:https://scan.coverity.com/projects/mkowalzik-spring-typescript-services
[coveritybadge img]:https://scan.coverity.com/projects/10040/badge.svg

[sonar quality]:https://sonarcloud.io/dashboard?id=org.leandreck.endpoints%3Aparent
[sonar quality img]:https://sonarcloud.io/api/project_badges/quality_gate?project=org.leandreck.endpoints:parent&branch=master

[sonar tech]:https://sonarcloud.io/dashboard?id=org.leandreck.endpoints%3Aparent
[sonar tech img]:https://img.shields.io/sonar/http/sonarcloud.io/org.leandreck.endpoints:parent/tech_debt.svg?label=tech%20debt

[mavenbadge]:http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.leandreck.endpoints%22%20AND%20a%3A%22annotations%22
[mavenbadge img]:https://maven-badges.herokuapp.com/maven-central/org.leandreck.endpoints/annotations/badge.svg

[snykbadge]:https://snyk.io/test/github/leandreck/spring-typescript-services?targetFile=pom.xml
[snykbadge img]:https://snyk.io/test/github/leandreck/spring-typescript-services/badge.svg?targetFile=pom.xml

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg

[codecov]:https://codecov.io/gh/leandreck/spring-typescript-services
[codecov img]:https://codecov.io/gh/leandreck/spring-typescript-services/branch/master/graph/badge.svg

[codacy]:https://www.codacy.com/app/leandreck/spring-typescript-services?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=leandreck/spring-typescript-services&amp;utm_campaign=Badge_Grade
[codacy img]:https://api.codacy.com/project/badge/Grade/fac6b09d290845d7bb1ef1f03cf3b95b