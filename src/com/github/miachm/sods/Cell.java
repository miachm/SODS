package com.github.miachm.sods;

import java.time.LocalDate;
import java.util.Objects;

class Cell extends TableField {
    private Object value;
    private String formula;
    private Style style = Style.default_style;
    private GroupCell group;
    private OfficeAnnotation annotation;

    Cell()
    {
    }

    GroupCell getGroup() {
        return group;
    }

    void setGroup(GroupCell group) {
        this.group = group;
    }

    Style getStyle()
    {
        if (style.isDefault())
            style = new Style();

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
        style = Style.default_style;
        annotation = null;
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
        if (value instanceof LocalDate) {
            if (style.getDataStyle() == null) {
                style.setDataStyle(Style.ISO_DATE_DATA_STYLE);
            }
        } else if (Style.ISO_DATE_DATA_STYLE.equals(style.getDataStyle())) {
            style.setDataStyle(null);
        }
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

    public OfficeAnnotation getAnnotation()
    {
        return this.annotation;
    }

    public void setAnnotation(OfficeAnnotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        GroupCell groupCell = getGroup();

        if (groupCell != null) {
            if (!groupCell.equals(cell.getGroup())) {
                return false;
            }
            if (groupCell.getCell() != this){
                return groupCell.getCell().equals(cell.getGroup().getCell());
            }
        }

        if (!Objects.equals(value, cell.value)) return false;
        if (!Objects.equals(formula, cell.formula)) return false;
        if (!Objects.equals(annotation, cell.annotation)) return false;
        if (!Objects.equals(num_repeated, cell.num_repeated)) return false;
        return style.equals(cell.getStyle());
    }

    @Override
    public int hashCode()
    {
        GroupCell groupCell = getGroup();
        if (groupCell != null && groupCell.getCell() != this)
            return groupCell.getCell().hashCode();

        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        result = 31 * result + style.hashCode();
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + annotation.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (getGroup() == null || getGroup().getCell() == this)
            return "Cell{" +
                    "value=" + value +
                    ", formula='" + formula + '\'' +
                    ", style=" + style +
                    ", num_repeated=" + num_repeated +
                    '}';
        else {
            return getGroup().getCell().toString();
        }
    }
}