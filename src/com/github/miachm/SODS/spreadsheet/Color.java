package com.github.miachm.SODS.spreadsheet;

final class Color {

    private final int red;
    private final int blue;
    private final int green;

    Color(int red, int blue, int green) {
        this.red = red;
        this.blue = blue;
        this.green = green;
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

    @Override
    public String toString()
    {
        return "#" + Integer.toHexString(red) + Integer.toHexString(blue) + Integer.toHexString(green);
    }
}
