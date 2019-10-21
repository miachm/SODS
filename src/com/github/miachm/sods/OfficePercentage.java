package com.github.miachm.sods;

/**
 * This class defines a percentage in a Spreadsheet
 *
 */
public class OfficePercentage {
    private final Double value;

    /**
     * Build a default object with a value
     *
     * @param value The percentage in a normalized value (1.0 is a 100%). It can be null
     */
    public OfficePercentage(Double value)
    {
        this.value = value;
    }

    /**
     * Build a default object using the string representation of percentage
     *
     * @param value The percentage in a string representation (ex: "40%")
     * @throws IllegalArgumentException If the string does not represent a valid percentage representation.
     */
    public OfficePercentage(String value)
    {
        if (!value.endsWith("%"))
            throw new IllegalArgumentException("The string representation does not end with a '%'");

        try {
            this.value = Double.parseDouble(value.substring(0, value.length() - 1));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid number", e);
        }
    }

    public Double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfficePercentage that = (OfficePercentage) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    /**
     * It prints a representation of the percentage in a '%' format
     */

    @Override
    public String toString() {
        if (this.value == null)
            return null;
        else
            return "" + (this.value * 100.0) + "%";
    }
}
