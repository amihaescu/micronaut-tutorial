package com.amihaescu.broker;

import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class MarketsControllerTest {

    @Inject
    EmbeddedApplication application;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    void getAllSymbolsTest(){
        final List<LinkedHashMap<String, String>> markets = client.toBlocking().retrieve("/markets", List.class);
        assertEquals(4, markets.size());
        assertThat(markets)
                .extracting(entry -> entry.get("value"))
                .containsExactlyInAnyOrder("AAPL", "AMZN", "FB", "GGL");
    }
}
