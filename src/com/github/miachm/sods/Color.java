package com.github.miachm.sods;

/** Color is a inmutable class for represent colors.
 */

final public class Color implements Cloneable {
    private final int red;
    private final int green;
    private final int blue;

    /**
     * It creates a color object from a RGB format (red, green, blue).
     *
     * @param red the red color intensity. It must be in a 0-255 range
     * @param green the green color intensity. It must be in a 0-255 range
     * @param blue the blue color intensity. It must be in a 0-255 range
     */
    public Color(int red, int green, int blue) {
        if (red < 0 || red > 255 ||
                green < 0 || green > 255 ||
                blue < 0 || blue > 255)
            throw new IllegalArgumentException("Error, parameters out of range (0-255)");

        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * It creates a color object from a String representation (CSS Style).
     * The first character is the hashtag '#'
     * The next two characters are the red intensity in hex (ex: A4)
     * The next two characters are the green intensity in hex (ex: 3B)
     * The next two characters are the blue intensity in hex (ex: 53)
     *
     * @param hexform The string representation of the color. For example: "#A24C21"
     * @throws IllegalArgumentException if the string hasn't 7 characters or is ill-formed
     */
    public Color(String hexform)
    {
        hexform = hexform.toLowerCase();
        if (hexform.equals("transparent"))
            throw new OperationNotSupportedException("Transparent color not supported, use a null color object instead");

        if (hexform.length() != 7)
            throw new IllegalArgumentException("Error in Color, the length of the string is not correct (" + hexform.length() + ")");

        this.red = Integer.valueOf(hexform.substring(1, 3), 16);
        this.green = Integer.valueOf(hexform.substring(3, 5), 16);
        this.blue = Integer.valueOf(hexform.substring(5, 7), 16);
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

    /**
     * A string representation of the color
     *
     * @return A string of 7 characters with the hex representation of the color (ex: #4a1a4b)
     */

    @Override
    public String toString()
    {
        return "#" + fill(Integer.toHexString(red),2) + fill(Integer.toHexString(green), 2) + fill(Integer.toHexString(blue), 2);
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
