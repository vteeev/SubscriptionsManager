package com.example.subscriptionmanager.infrastructure.exchange;

import com.example.subscriptionmanager.domain.exchange.ExchangeRateProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Currency;
import java.util.Optional;

/**
 * Implementation of ExchangeRateProvider using NBP (National Bank of Poland) API.
 * Handles currency conversion for PLN and other currencies.
 */
@Component
public class NbpExchangeRateProvider implements ExchangeRateProvider {
    private static final String NBP_API_URL = "http://api.nbp.pl/api/exchangerates/rates/a/";
    private static final Currency PLN = Currency.getInstance("PLN");
    
    private final RestTemplate restTemplate;

    public NbpExchangeRateProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Double> getExchangeRate(Currency fromCurrency, Currency toCurrency) {
        // Same currency
        if (fromCurrency.equals(toCurrency)) {
            return Optional.of(1.0);
        }

        // PLN to other currency
        if (fromCurrency.equals(PLN)) {
            return getRateFromPln(toCurrency).map(rate -> 1.0 / rate);
        }

        // Other currency to PLN
        if (toCurrency.equals(PLN)) {
            return getRateFromPln(fromCurrency);
        }

        // Other currency to other currency (via PLN)
        Optional<Double> fromRate = getRateFromPln(fromCurrency);
        Optional<Double> toRate = getRateFromPln(toCurrency);
        
        if (fromRate.isPresent() && toRate.isPresent()) {
            return Optional.of(fromRate.get() / toRate.get());
        }

        return Optional.empty();
    }

    private Optional<Double> getRateFromPln(Currency currency) {
        try {
            String url = NBP_API_URL + currency.getCurrencyCode() + "/?format=json";
            NbpResponse response = restTemplate.getForObject(url, NbpResponse.class);
            
            if (response != null && response.rates != null && !response.rates.isEmpty()) {
                return Optional.of(response.rates.get(0).mid);
            }
        } catch (Exception e) {
            // Log error in production
            System.err.println("Error fetching exchange rate for " + currency.getCurrencyCode() + ": " + e.getMessage());
        }
        
        return Optional.empty();
    }

    // NBP API response structure
    private static class NbpResponse {
        public String table;
        public String currency;
        public String code;
        public java.util.List<Rate> rates;
    }

    private static class Rate {
        public String no;
        public String effectiveDate;
        public Double mid;
    }
}
