package com.example.subscriptionmanager.domain.exchange;

import com.example.subscriptionmanager.domain.model.Money;

import java.util.Currency;
import java.util.Optional;

/**
 * Interface for exchange rate providers.
 * Follows Dependency Inversion Principle - domain depends on abstraction.
 */
public interface ExchangeRateProvider {
    /**
     * Gets exchange rate from source currency to target currency.
     * @param fromCurrency source currency
     * @param toCurrency target currency
     * @return exchange rate (how many units of toCurrency per 1 unit of fromCurrency)
     */
    Optional<Double> getExchangeRate(Currency fromCurrency, Currency toCurrency);

    /**
     * Converts money from one currency to another.
     * @param money money to convert
     * @param targetCurrency target currency
     * @return converted money or empty if conversion is not possible
     */
    default Optional<Money> convert(Money money, Currency targetCurrency) {
        if (money.getCurrency().equals(targetCurrency)) {
            return Optional.of(money);
        }
        
        return getExchangeRate(money.getCurrency(), targetCurrency)
                .map(rate -> new Money(money.getAmount().multiply(java.math.BigDecimal.valueOf(rate)), targetCurrency));
    }
}
