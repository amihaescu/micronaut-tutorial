package com.amihaescu;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class HelloWorldControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testHello() {
        final String retrieve = client.toBlocking().retrieve("/hello");
        assertEquals("Hello from service", retrieve);
    }

    @Test
    void returnGermanGreeting(){
        final String retrieve = client.toBlocking().retrieve("/hello/de");
        assertEquals("Hallo", retrieve);
    }

    @Test
    void returnEnglishGreeting(){
        final String retrieve = client.toBlocking().retrieve("/hello/en");
        assertEquals("Hello", retrieve);
    }

    @Test
    void returnGrettingAsJSON(){
        var retrieve = client.toBlocking().retrieve("/hello/json", ObjectNode.class);
        //assertEquals("{\"my_text\":\"Hello World\",\"id\":1,\"time_utc\":1611491230305}", retrieve.toString());
    }
}
