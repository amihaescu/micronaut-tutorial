package com.amihaescu.broker.store;

import com.amihaescu.broker.model.WatchList;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class InMemoryAccountStore {

    private final Map<UUID, WatchList> watchListPerAccount = new HashMap<>();
    public WatchList getWatchList(UUID accountId) {
        return watchListPerAccount.getOrDefault(accountId, new WatchList());
    }

    public WatchList updateWatchList(UUID randomUUID, WatchList watchList) {
        watchListPerAccount.put(randomUUID, watchList);
        return getWatchList(randomUUID);
    }

    public void deleteWatchList(UUID accountId) {
        watchListPerAccount.remove(accountId);
    }
}
