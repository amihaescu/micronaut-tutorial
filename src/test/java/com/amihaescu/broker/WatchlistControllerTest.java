package com.amihaescu.broker;

import com.amihaescu.broker.account.WatchListController;
import com.amihaescu.broker.model.Symbol;
import com.amihaescu.broker.model.WatchList;
import com.amihaescu.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class WatchlistControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchlistControllerTest.class);
    final UUID TEST_UUID = WatchListController.ACCOUNT_ID;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void returnEmptyListForAccount() {

        var token = givenMyUserIsLoggedIn();

        var get = HttpRequest.GET("/account/watchlist")
                .bearerAuth(token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON);
        var result = client.toBlocking().retrieve(get, WatchList.class);
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_UUID).getSymbols().isEmpty());
    }

    @Test
    void returnWatchListForAccount() {
        var token = givenMyUserIsLoggedIn();
        var symbolList = Stream.of("AAPL", "MSFT", "TSLA").map(Symbol::new).collect(Collectors.toList());
        store.updateWatchList(TEST_UUID, new WatchList(symbolList));
        WatchList retrieve = client.toBlocking().retrieve(HttpRequest.GET("/account/watchlist").bearerAuth(token.getAccessToken()), WatchList.class);
        assertThat(retrieve.getSymbols().size()).isEqualTo(3);
    }

    @Test
    void canUpdateWatchListFroAccount() {
        var token = givenMyUserIsLoggedIn();
        var watchList = new WatchList(Stream.of("AAPL", "MSFT", "TSLA").map(Symbol::new).collect(Collectors.toList()));

        var added = client.toBlocking().exchange(HttpRequest.PUT("/account/watchlist", watchList).bearerAuth(token.getAccessToken()));
        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_UUID));
    }

    @Test
    void canDeleteWatchListForAccount() {
        var token = givenMyUserIsLoggedIn();
        var watchList = new WatchList(Stream.of("AAPL", "MSFT", "TSLA").map(Symbol::new).collect(Collectors.toList()));
        store.updateWatchList(TEST_UUID, watchList);

        var deleted = client.toBlocking()
                .exchange(HttpRequest
                        .DELETE(String.format("/account/watchlist/%s", TEST_UUID), watchList)
                        .bearerAuth(token.getAccessToken()));
        assertEquals(deleted.getStatus(), HttpStatus.OK);
        assertEquals(new WatchList(), store.getWatchList(TEST_UUID));
    }

    private BearerAccessRefreshToken givenMyUserIsLoggedIn() {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("sherlock", "password");
        var post = HttpRequest.POST("/login", creds);
        var exchange = client.toBlocking().exchange(post, BearerAccessRefreshToken.class);
        assertEquals(HttpStatus.OK, exchange.getStatus());
        BearerAccessRefreshToken token = exchange.body();
        assertNotNull(token);
        assertEquals("sherlock", token.getUsername());
        LOGGER.debug("Login Bearer Token: {} expirees in {}", token.getAccessToken(), token.getExpiresIn());
        return  token;
    }

}

