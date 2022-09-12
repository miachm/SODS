package com.github.miachm.sods;

import java.util.Objects;

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

    public static ConditionalFormat conditionWhenValueIsGreater(Style apply, double value)
    {
        return new ConditionalFormat(apply, "cell-content()>"+value);
    }

    public static ConditionalFormat conditionWhenValueIsLower(Style apply, double value)
    {
        return new ConditionalFormat(apply, "cell-content()<"+value);
    }

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
