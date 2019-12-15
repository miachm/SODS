package com.github.miachm.sods;

import java.util.Map;
import java.util.TreeMap;

abstract class TableCollection<T extends TableItem> {
    protected TreeMap<Integer, T> items = new TreeMap<>();

    public T getItemForEdit(int index) {
        Map.Entry<Integer, T> entry = items.floorEntry(index);
        T item = createItem();

        int numRepeated = entry.getValue().getNumRepeated();
        if (entry.getKey() == index) {
            if (numRepeated > 1) {
                items.put(entry.getKey() + 1, entry.getValue());
                items.remove(entry.getKey());
                entry.getValue().setNumRepeated(numRepeated - 1);
                items.put(index, item);
            }
            else {
                item = entry.getValue();
            }
        }
        else {
            int diff = index - entry.getKey();
            entry.getValue().setNumRepeated(index - entry.getKey());
            if (numRepeated - diff > 1) {
                T clone;
                try {
                    clone = (T)entry.getValue().clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                clone.setNumRepeated(numRepeated - diff - 1);
                items.put(index + 1, clone);
            }
        }
        return item;
    }

    public void remove(int index, int howmany)
    {
        for (int i = 0; i < howmany; i++)
            items.remove(index + i);

        Integer key = index + howmany;
        Map.Entry<Integer,T> entry;
        while ((entry = items.ceilingEntry(key)) != null)
        {
            items.remove(entry.getKey());
            items.put(entry.getKey() - howmany, entry.getValue());
            key = entry.getKey();
        }
    }

    public T getItem(int index)
    {
        return items.floorEntry(index).getValue();
    }

    public void addItems(int index, int howMany) {
        Integer key = Integer.MAX_VALUE;
        Map.Entry<Integer,T> entry;
        while ((entry = items.lowerEntry(key)) != null && entry.getKey() >= index)
        {
            items.remove(entry.getKey());
            items.put(entry.getKey() + howMany, entry.getValue());
            key = entry.getKey();
        }

        entry = items.floorEntry(key);
        if (entry != null) {
            int diff = index - entry.getKey(); // 5 , 3

            try {
                T newItem = (T) entry.getValue().clone();
                int repeatedRows = newItem.getNumRepeated();
                newItem.setNumRepeated(repeatedRows - diff - 1);
                if (newItem.getNumRepeated() > 0)
                    items.put(index + howMany, newItem);
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            entry.getValue().setNumRepeated(diff);
        }
        else {
            T newItem = createItem();
            newItem.setNumRepeated(howMany);
            items.put(index, newItem);
        }
    }

    protected abstract T createItem();

    public int getLastUsefulItemIndex() {
        Integer key = Integer.MAX_VALUE;
        Map.Entry<Integer,T> entry;
        while ((entry = items.lowerEntry(key)) != null && entry.getValue().isBlank())
        {
            key = entry.getKey();
        }

        return key == Integer.MAX_VALUE ? 0 : key;
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }
}
