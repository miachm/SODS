package com.github.miachm.SODS.spreadsheet;

final public class Color {

    private final int red;
    private final int blue;
    private final int green;

    public Color(int red, int blue, int green) {
        if (red < 0 || red > 255 ||
                blue < 0 || blue > 255 ||
                green < 0 || green > 255)
            throw new IllegalArgumentException("Error, parameters out of range (0-255)");

        this.red = red;
        this.blue = blue;
        this.green = green;
    }

    public Color(String hexform)
    {
        if (hexform.length() != 7) {
            throw new IllegalArgumentException("Error in Color, the length of the string is not correct (" + hexform.length() + ")");
        }

        this.red = Integer.valueOf(hexform.substring(1, 3), 16);
        this.blue = Integer.valueOf(hexform.substring(3, 5), 16);
        this.green = Integer.valueOf(hexform.substring(5, 7), 16);
    }

    public int getRed() {
        return red;
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    private static String fill(String text, int len)
    {
        int diff = text.length() - len;
        while (diff < 0) {
            text = "0" + text;
            diff++;
        }

        return text;
    }

    @Override
    public String toString()
    {
        return "#" + fill(Integer.toHexString(red),2) + fill(Integer.toHexString(blue), 2) + fill(Integer.toHexString(green), 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        if (red != color.red) return false;
        if (blue != color.blue) return false;
        return green == color.green;
    }

    @Override
    public int hashCode() {
        int result = red;
        result = 31 * result + blue;
        result = 31 * result + green;
        return result;
    }
}
