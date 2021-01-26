package com.amihaescu.broker;

import com.amihaescu.broker.error.CustomError;
import com.amihaescu.broker.model.Quote;
import com.amihaescu.broker.model.Symbol;
import com.amihaescu.broker.store.InMemoryStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class QuotesControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotesControllerTest.class);
    private final ThreadLocalRandom current = ThreadLocalRandom.current();

    @Inject
    @Client("/")
    RxHttpClient client;

    @Inject
    InMemoryStore store;

    @Test
    void  returnQuotePerSymbol() {
        var applQuote = initRandomQuote("APPL");
        store.update(applQuote);

        var amznQuote = initRandomQuote("AMZN");
        store.update(amznQuote);
        final Quote result = client.toBlocking().retrieve(HttpRequest.GET("/quotes/APPL"), Quote.class);
        assertThat(applQuote).isEqualTo(result);

        final Quote amznResult = client.toBlocking().retrieve(HttpRequest.GET("/quotes/AMZN"), Quote.class);
        assertThat(amznQuote).isEqualTo(amznResult);
    }

    @Test
    void notFoundReturnWhenSymbolIfUnsupported() {
        try {
            client.toBlocking().retrieve(HttpRequest.GET("/quotes/UNSUPPORTED"));
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getResponse().getStatus());
            var customError = e.getResponse().getBody(CustomError.class);
            assertTrue(customError.isPresent());
            assertEquals(404, customError.get().getStatus());
            assertEquals("NOT_FOUND", customError.get().getError());
            assertEquals("quote for symbol not found", customError.get().getMessage());
            assertEquals("/quotes/UNSUPPORTED",customError.get().getPath());
        }
    }

    private Quote initRandomQuote(String symbol) {
        return Quote.builder().symbol(new Symbol(symbol))
                .bid(randomValue())
                .ask(randomValue())
                .volume(randomValue())
                .lastPrice(randomValue())
                .build();
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(current.nextDouble(1, 100));
    }
}

