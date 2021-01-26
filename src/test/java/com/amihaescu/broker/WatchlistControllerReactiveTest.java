package com.amihaescu.broker;

import com.amihaescu.broker.account.WatchListController;
import com.amihaescu.broker.account.WatchListControllerReactive;
import com.amihaescu.broker.model.Symbol;
import com.amihaescu.broker.model.WatchList;
import com.amihaescu.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class WatchlistControllerReactiveTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchlistControllerReactiveTest.class);
    final UUID TEST_UUID = WatchListControllerReactive.ACCOUNT_ID;

    @Inject
    JWTWatchListClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void returnEmptyListForAccount() {
        var result = client.retrieveWatchList(getAuthorizationHeader()).singleOrError();
        assertTrue(result.blockingGet().getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_UUID).getSymbols().isEmpty());
    }

    @Test
    void returnWatchListForAccount() {
        var symbolList = Stream.of("AAPL", "MSFT", "TSLA").map(Symbol::new).collect(Collectors.toList());
        store.updateWatchList(TEST_UUID, new WatchList(symbolList));
        var result = client.retrieveWatchList(getAuthorizationHeader()).singleOrError().blockingGet();
        assertThat(result.getSymbols().size()).isEqualTo(3);
    }

    @Test
    void canUpdateWatchListFroAccount() {
        var watchList = new WatchList(Stream.of("AAPL", "MSFT", "TSLA").map(Symbol::new).collect(Collectors.toList()));

        var added = client.updateWatchList(getAuthorizationHeader(), watchList);
        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_UUID));
    }

    @Test
    void canDeleteWatchListForAccount() {
        var watchList = new WatchList(Stream.of("AAPL", "MSFT", "TSLA").map(Symbol::new).collect(Collectors.toList()));
        store.updateWatchList(TEST_UUID, watchList);

        var deleted = client.deleteWatchList(getAuthorizationHeader(), TEST_UUID);
        assertEquals(deleted.getStatus(), HttpStatus.OK);
        assertEquals(new WatchList(), store.getWatchList(TEST_UUID));
    }

    @Test
    void returnWatchListForAccountAsync() {
        var symbolList = Stream.of("AAPL", "MSFT", "TSLA").map(Symbol::new).collect(Collectors.toList());
        store.updateWatchList(TEST_UUID, new WatchList(symbolList));
        WatchList retrieve = client.retrieveWatchListAsSingle(getAuthorizationHeader());
        assertThat(retrieve.getSymbols().size()).isEqualTo(3);
    }

    private String getAuthorizationHeader() {
        return "Bearer " + givenMyUserLoggedIn().getAccessToken();
    }

    private BearerAccessRefreshToken givenMyUserLoggedIn() {
        return client.login(new UsernamePasswordCredentials("sherlock", "password"));
    }

}

