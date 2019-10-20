package com.github.miachm.sods;

class A1NotationCord {

    private String a1Notation;
    private Pair<Integer, Integer> origen;
    private Pair<Integer, Integer> dest;

    A1NotationCord(String a1Notation)
    {
        if (a1Notation == null)
            throw new NullPointerException("a1Notation can not be null");

        if (a1Notation.isEmpty())
            throw new IllegalArgumentException("a1Notation can not be empty");

        this.a1Notation = a1Notation;
        parse();
    }

    private void parse()
    {
        String[] parts = a1Notation.split(":");
        if (parts.length > 2)
            throw new IllegalArgumentException("Only a symbol ':' is allowed in a1Notation. Input: " + a1Notation);

        origen = parsePart(parts[0]);
        if (parts.length == 2)
        {
            dest = parsePart(parts[1]);
            normalize();
        }
        else
            dest = origen;
    }

    private void normalize()
    {
        if (dest.first < origen.first)
        {
            int aux = dest.first;
            dest.first = origen.first;
            origen.first = aux;
        }
        if (dest.second < origen.second)
        {
            int aux = dest.second;
            dest.second = origen.second;
            origen.second = aux;
        }
    }

    private Pair<Integer, Integer> parsePart(String part)
    {
        int row = 0;
        int column = 0;
        int multRow = 1;
        int multColumn = 1;

        for (int i = part.length() - 1; i >= 0; i--) {
            char letter = part.charAt(i);
            if (Character.isAlphabetic(letter)) {
                int num = letter - 'A';
                column += multColumn * (num+1);
                multColumn *= 26;
            }
            else if (Character.isDigit(letter)) {
                int num = letter - '0';
                row += multRow * num;
                multRow *= 10;
            }
            else
                throw new IllegalArgumentException("Invalid character found: '" + letter + "' at position " + i);
        }

        column--;
        row--;

        return new Pair<>(row, column);
    }

    int getInitRow()
    {
        return origen.first;
    }

    int getInitColumn()
    {
        return origen.second;
    }

    int getLastRow()
    {
        return dest.first;
    }

    int getLastColumn()
    {
        return dest.second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        A1NotationCord that = (A1NotationCord) o;

        return a1Notation.equals(that.a1Notation);
    }

    @Override
    public int hashCode() {
        return a1Notation.hashCode();
    }
}
