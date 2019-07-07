package com.github.miachm.sods;

final class GroupCell implements Comparable<GroupCell> {
    private final Vector cord;
    private final Vector length;
    private final Cell cell;

    public GroupCell(Vector cord, Vector length, Cell cell) {
        this.cord = cord;
        this.length = length;
        this.cell = cell;
    }

    public Vector getCord() {
        return cord;
    }

    public Vector getLength() {
        return length;
    }

    public Cell getCell() {
        return cell;
    }

    @Override
    public int compareTo(GroupCell o) {
        return cord.compareTo(o.getCord());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupCell groupCell = (GroupCell) o;

        if (!cord.equals(groupCell.cord)) return false;
        return length.equals(groupCell.length);
    }

    @Override
    public int hashCode() {
        int result = cord.hashCode();
        result = 31 * result + length.hashCode();
        return result;
    }
}
