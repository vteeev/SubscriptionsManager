package com.example.subscriptionmanager.domain;

import com.example.subscriptionmanager.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithValidAmountAndCurrency() {
        Money money = new Money(BigDecimal.valueOf(100.50), Currency.getInstance("PLN"));
        
        assertEquals(BigDecimal.valueOf(100.50), money.getAmount());
        assertEquals("PLN", money.getCurrencyCode());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Money(null, Currency.getInstance("PLN"));
        });
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Money(BigDecimal.valueOf(100), null);
        });
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Money(BigDecimal.valueOf(-10), Currency.getInstance("PLN"));
        });
    }

    @Test
    void shouldAddMoneyWithSameCurrency() {
        Money money1 = new Money(BigDecimal.valueOf(100), Currency.getInstance("PLN"));
        Money money2 = new Money(BigDecimal.valueOf(50), Currency.getInstance("PLN"));
        
        Money result = money1.add(money2);
        
        assertEquals(BigDecimal.valueOf(150), result.getAmount());
        assertEquals("PLN", result.getCurrencyCode());
    }

    @Test
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money money1 = new Money(BigDecimal.valueOf(100), Currency.getInstance("PLN"));
        Money money2 = new Money(BigDecimal.valueOf(50), Currency.getInstance("USD"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            money1.add(money2);
        });
    }

    @Test
    void shouldMultiplyMoney() {
        Money money = new Money(BigDecimal.valueOf(100), Currency.getInstance("PLN"));
        Money result = money.multiply(BigDecimal.valueOf(1.5));
        
        assertEquals(0, BigDecimal.valueOf(150).compareTo(result.getAmount()));
        assertEquals("PLN", result.getCurrencyCode());
    }

    @Test
    void shouldRoundAmountToTwoDecimalPlaces() {
        Money money = new Money(BigDecimal.valueOf(100.999), Currency.getInstance("PLN"));
        
        assertEquals(BigDecimal.valueOf(101.00).setScale(2), money.getAmount());
    }

    @Test
    void shouldBeEqualWhenAmountAndCurrencyAreSame() {
        Money money1 = new Money(BigDecimal.valueOf(100), Currency.getInstance("PLN"));
        Money money2 = new Money(BigDecimal.valueOf(100), Currency.getInstance("PLN"));
        
        assertEquals(money1, money2);
        assertEquals(money1.hashCode(), money2.hashCode());
    }
}
