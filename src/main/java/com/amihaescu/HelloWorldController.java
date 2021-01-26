package com.amihaescu;

import com.amihaescu.broker.model.Greeting;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("${hello.controller.path}")
@Secured(SecurityRule.IS_ANONYMOUS)
public class HelloWorldController {

    private final HelloWorldService helloWorldService;
    private final GreetingConfig config;

    public HelloWorldController(HelloWorldService helloWorldService, GreetingConfig config) {
        this.helloWorldService = helloWorldService;
        this.config = config;
    }

    @Get("/")
    public String index() {
        return helloWorldService.sayHi();
    }

    @Get("/de")
    public String de(){
        return config.getDe();
    }

    @Get("/en")
    public String en(){
        return config.getEn();
    }

    @Get("/json")
    public Greeting json() {
        return new Greeting();
    }

}
