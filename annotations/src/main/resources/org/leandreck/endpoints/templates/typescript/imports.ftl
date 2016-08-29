<#-- @ftlvariable name="types" type="org.leandreck.endpoints.processor.model.TypeNode[]" -->
<#list types as type>
import { ${type.typeName} } from './${type.typeName}.model';
</#list>