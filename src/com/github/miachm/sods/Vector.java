package com.github.miachm.sods;

class Vector implements Comparable<Vector> {
    private final int x;
    private final int y;

    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector vector = (Vector) o;

        if (x != vector.x) return false;
        return y == vector.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public int compareTo(Vector o) {
        if (y > o.y)
            return y - o.y;
        if (y < o.y)
            return o.y - y;
        return o.x - x;
    }
}
