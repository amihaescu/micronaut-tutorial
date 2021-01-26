package com.amihaescu;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class HelloWorldService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldService.class);

    @Property(name ="hello.service.greeting", defaultValue = "default value")
    private String greeting;

    @EventListener
    public void onStartup(StartupEvent startupEvent){
        LOGGER.debug("Startup: {}", HelloWorldService.class.toString());
    }

    public String sayHi(){
        return this.greeting;
    }
}
