package com.amihaescu.broker.store;

import com.amihaescu.broker.model.Quote;
import com.amihaescu.broker.model.Symbol;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class InMemoryStore {

    private final List<Symbol> symbols;
    private final Map<String,  Quote> cachedQuotes = new HashMap<>();
    private final ThreadLocalRandom current = ThreadLocalRandom.current();

    public InMemoryStore() {
        symbols = Stream.of("AAPL", "AMZN", "FB", "GGL").map(Symbol::new).collect(Collectors.toList());
        symbols.forEach(symbol -> cachedQuotes.put(symbol.getValue(), randomQuote(symbol.getValue())));
    }

    public List<Symbol> getAllSymbols() {
        return symbols;
    }

    public Optional<Quote> fetchQuote(String symbol) {
        return Optional.ofNullable(cachedQuotes.get(symbol));
    }

    private Quote randomQuote(String symbol) {
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


    public void update(Quote quote) {
        cachedQuotes.put(quote.getSymbol().getValue(), quote);
    }
}
