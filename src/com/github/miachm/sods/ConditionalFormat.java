package com.github.miachm.sods;

import java.util.Objects;

/**
 * This class refers to a specific rule to apply a conditional style.
 * It's usually composed by a boolean condition and the specific style to be applied.
 */
public class ConditionalFormat {
    private Style apply;
    private String rawCondition;

    ConditionalFormat(Style apply, String rawCondition)
    {
        this.apply = apply;
        this.rawCondition = rawCondition;
    }

    public Style getStyleApplied() {
        return apply;
    }

    String getRawCondition() {
        return rawCondition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionalFormat that = (ConditionalFormat) o;
        return apply.equals(that.apply) && rawCondition.equals(that.rawCondition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apply, rawCondition);
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "values greater than X".
     * @param apply the style to ble applied if the condition turns true
     * @param value the value to compare to
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenValueIsGreater(Style apply, double value)
    {
        return new ConditionalFormat(apply, "cell-content()>"+value);
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "values lower than X".
     * @param apply the style to ble applied if the condition turns true
     * @param value the value to compare to
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenValueIsLower(Style apply, double value)
    {
        return new ConditionalFormat(apply, "cell-content()<"+value);
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "values equals to X".
     * @param apply the style to ble applied if the condition turns true
     * @param value the value to compare to
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenValueIsEqual(Style apply, double value)
    {
        return new ConditionalFormat(apply, "cell-content()="+value);
    }

    public static ConditionalFormat conditionWhenValueIsEqual(Style apply, String value)
    {
        return new ConditionalFormat(apply, "cell-content()=\""+value+"\"");
    }

    public static ConditionalFormat conditionWhenValueIsBetween(Style apply, double init, double end)
    {
        if (init >= end) {
            throw new IllegalArgumentException("Value init " + init + " is greater than end " + end + " argument");
        }
        return new ConditionalFormat(apply, "cell-content-is-between("+init+","+end+")");
    }
}
