package org.leandreck.endpoints.case1;

import org.leandreck.endpoints.annotations.TypeScriptEndpoint;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@TypeScriptEndpoint(template = "/org/leandreck/endpoints/templates/testing/service.ftl")
@RestController
@RequestMapping("/api")
public class Endpoint {

    @RequestMapping(value = "/int", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Boolean getInt() {
        return true;
    }
}