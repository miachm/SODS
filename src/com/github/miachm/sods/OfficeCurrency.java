package com.github.miachm.sods;

import java.text.NumberFormat;
import java.util.Currency;

/**
 * This class represents a Currency in a Spreadsheet
 *
 * It contains the current currency object (java.util.Currency) and the numeric value
 */

public class OfficeCurrency {
    private final Currency currency;
    private final Double value;
    private final NumberFormat format;

    /**
     * It builds an inmutable class of the OfficeCurrency
     *
     * @param currency The currency instance class, it specify which currency is. It can be null
     * @param value The numeric value. It can be null.
     */

    public OfficeCurrency(Currency currency, Double value)
    {
        this(currency, value, NumberFormat.getCurrencyInstance());
    }


    /**
     * It builds an inmutable class of the OfficeCurrency
     *
     * @param currency The currency instance class, it specify which currency is. It can be null
     * @param value The numeric value. It can be null.
     * @param format How the currency value should be formatted in a string
     */
    public OfficeCurrency(Currency currency, Double value, NumberFormat format)
    {
        this.currency = currency;
        this.value = value;
        this.format = format;
        this.format.setCurrency(currency);
    }

    public Currency getCurrency() {
        return currency;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfficeCurrency that = (OfficeCurrency) o;

        if (currency != null ? !currency.getCurrencyCode().equals(that.currency.getCurrencyCode()) : that.currency != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = currency != null ? currency.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    /** It prints the money quantity in a standard form (value + currency name)
     *
     * For example:
     * 5EUR
     * 10USD
     */

    @Override
    public String toString() {
        return "" + format.format(value);
    }
}
