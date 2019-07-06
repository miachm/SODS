package com.github.miachm.sods;

class Cell implements Cloneable {
    private Object value;
    private String formula;
    private Style style = new Style();
    private GroupCell group;

    GroupCell getGroup() {
        return group;
    }

    void setGroup(GroupCell group) {
        this.group = group;
    }

    Style getStyle()
    {
        return style;
    }

    Style getStyleCopy()
    {
        try {
            return (Style) style.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e.toString());
        }
    }

    void setStyle(Style style)
    {
        if (style == null)
            throw new IllegalArgumentException("Style can not be null");
        try {
            this.style = (Style) style.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Style is not cloneable");
        }
    }

    void clear()
    {
        value = null;
        formula = null;
        style = new Style();
    }

    String getFormula() {
        return formula;
    }

    Object getValue(){
        return value;
    }
    void setValue(Object value)
    {
        this.value = value;
    }

    public void setFormula(String formula) {
        if (formula != null && formula.startsWith("of:")) {
            formula = convertFormula(formula);
        }
        this.formula = formula;
    }

    private String convertFormula(String formula) {
        formula = formula.trim();
        formula = formula.substring("of:".length());

        StringBuilder result = new StringBuilder();
        for (int i = 0;i < formula.length();i++) {
            char character = formula.charAt(i);
            if (character != '[' && character != ']' && character != '.') {
                result.append(character);
            }
        }

        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        GroupCell groupCell = getGroup();
        if (groupCell != null && groupCell.getCell() != this) {
            if (!groupCell.equals(cell.getGroup())) {
                return false;
            }
            return groupCell.getCell().equals(cell.getGroup().getCell());
        }

        if (value != null ? !value.equals(cell.value) : cell.value != null) return false;
        if (formula != null ? !formula.equals(cell.formula) : cell.formula != null) return false;
        return style.equals(cell.getStyle());
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        result = 31 * result + style.hashCode();
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "value=" + value +
                ", formula='" + formula + '\'' +
                ", style=" + style +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}