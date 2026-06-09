package com.github.miachm.sods;

import java.util.Objects;

/**
 * This class refers to a specific rule to apply a conditional style.
 * It's usually composed by a boolean condition and the specific style to be applied.
 */
public class ConditionalFormat implements Cloneable {
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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

    /**
     * Create a ConditionalFormat rule where the boolean condition is "values greater than or equal to X".
     * @param apply the style to be applied if the condition turns true
     * @param value the value to compare to
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenValueIsGreaterOrEqual(Style apply, double value)
    {
        return new ConditionalFormat(apply, "cell-content()>="+value);
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "values lower than or equal to X".
     * @param apply the style to be applied if the condition turns true
     * @param value the value to compare to
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenValueIsLowerOrEqual(Style apply, double value)
    {
        return new ConditionalFormat(apply, "cell-content()<="+value);
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "values not equal to X".
     * @param apply the style to be applied if the condition turns true
     * @param value the value to compare to
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenValueIsNotEqual(Style apply, double value)
    {
        return new ConditionalFormat(apply, "cell-content()!="+value);
    }

    public static ConditionalFormat conditionWhenValueIsNotEqual(Style apply, String value)
    {
        return new ConditionalFormat(apply, "cell-content()!=\""+value+"\"");
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "text contains X".
     * @param apply the style to be applied if the condition turns true
     * @param text the text to search for
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenTextContains(Style apply, String text)
    {
        return new ConditionalFormat(apply, "contains(cell-content(),\""+text+"\")");
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "text does not contain X".
     * @param apply the style to be applied if the condition turns true
     * @param text the text to search for
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenTextDoesNotContain(Style apply, String text)
    {
        return new ConditionalFormat(apply, "not(contains(cell-content(),\""+text+"\"))");
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "text starts with X".
     * @param apply the style to be applied if the condition turns true
     * @param text the text prefix to check for
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenTextStartsWith(Style apply, String text)
    {
        return new ConditionalFormat(apply, "starts-with(cell-content(),\""+text+"\")");
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "text ends with X".
     * @param apply the style to be applied if the condition turns true
     * @param text the text suffix to check for
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenTextEndsWith(Style apply, String text)
    {
        return new ConditionalFormat(apply, "ends-with(cell-content(),\""+text+"\")");
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "cell is empty".
     * @param apply the style to be applied if the condition turns true
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenCellIsEmpty(Style apply)
    {
        return new ConditionalFormat(apply, "cell-content()=\"\"");
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "cell is not empty".
     * @param apply the style to be applied if the condition turns true
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenCellIsNotEmpty(Style apply)
    {
        return new ConditionalFormat(apply, "cell-content()!=\"\"");
    }

    public static ConditionalFormat conditionWhenValueIsBetween(Style apply, double init, double end)
    {
        if (init >= end) {
            throw new IllegalArgumentException("Value init " + init + " is greater than end " + end + " argument");
        }
        return new ConditionalFormat(apply, "cell-content-is-between("+init+","+end+")");
    }

    /**
     * Create a ConditionalFormat rule where the boolean condition is "value is not between X and Y".
     * @param apply the style to be applied if the condition turns true
     * @param init the lower bound (must be less than end)
     * @param end the upper bound
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenValueIsNotBetween(Style apply, double init, double end)
    {
        if (init >= end) {
            throw new IllegalArgumentException("Value init " + init + " is greater than end " + end + " argument");
        }
        return new ConditionalFormat(apply, "cell-content-is-not-between("+init+","+end+")");
    }

    /**
     * Create a ConditionalFormat rule where the condition is a custom boolean formula.
     * The formula should evaluate to true/false, e.g. "=ISEVEN(A1)" or "=A1&gt;AVERAGE($A$1:$A$10)".
     * Do not include the leading '=' — write the formula expression directly.
     * @param apply the style to be applied if the formula evaluates to true
     * @param formula the formula expression (without leading '=')
     * @return The conditionalFormat style
     */
    public static ConditionalFormat conditionWhenFormulaIs(Style apply, String formula)
    {
        if (formula == null || formula.isEmpty())
            throw new IllegalArgumentException("Formula cannot be null or empty");
        return new ConditionalFormat(apply, "is-true-formula("+formula+")");
    }
}
