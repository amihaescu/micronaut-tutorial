package com.amihaescu;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OpenAPIDefinition(
        info = @Info(
                title = "mn-stock-broker",
                version = "0.1",
                description = "Udemy Micronaut course",
                license = @License(name = "MIT")
        )
)
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ApplicationContext context = Micronaut.run(Application.class, args);
        final HelloWorldService service = context.getBean(HelloWorldService.class);
        LOGGER.info(service.sayHi());
        //context.close();
    }
}
