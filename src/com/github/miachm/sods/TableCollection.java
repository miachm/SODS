package com.github.miachm.sods;

import java.util.Map;
import java.util.TreeMap;

abstract class TableCollection<T extends TableItem> {
    protected TreeMap<Integer, T> items = new TreeMap<>();

    public void compact() {
        Map.Entry<Integer, T> previousEntry = null;
        Map.Entry<Integer, T> entry = null;
        T previousValue = null;
        int key = Integer.MIN_VALUE;
        while ((entry = items.higherEntry(key)) != null) {
            if (entry.getValue().equals(previousValue)) {
                items.remove(entry.getKey());
                previousEntry.getValue().setNumRepeated(previousValue.getNumRepeated() + entry.getValue().getNumRepeated());
            }
            else {
                previousEntry = entry;
                previousValue = entry.getValue();
            }
            key = entry.getKey();
        }
    }

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
            items.put(index, item);
        }
        return item;
    }

    public void remove(int index, int howmany)
    {
        final int limit = index + howmany;
        Integer key = limit;
        Map.Entry<Integer,T> entry;
        while ((entry = items.lowerEntry(key)) != null)
        {
            key = entry.getKey();
            if (key < index) {
                entry.getValue().setNumRepeated(index - key);
                break;
            }
            int lastIndex = key + entry.getValue().getNumRepeated();
            if (lastIndex > limit)
            {
                int diff = lastIndex - limit;
                entry.getValue().setNumRepeated(diff);
                items.put(limit, entry.getValue());
            }
            items.remove(key);

        }

        key = index + howmany - 1;
        entry = null;
        while ((entry = items.higherEntry(key)) != null)
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

    public T addItems(int index, int howMany) {
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

            if (diff > 1) {
                try {
                    T newItem = (T) entry.getValue().clone();
                    int repeatedRows = newItem.getNumRepeated();
                    newItem.setNumRepeated(repeatedRows - diff - 1);
                    if (newItem.getNumRepeated() > 0)
                        items.put(index + howMany, newItem);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
            entry.getValue().setNumRepeated(diff);
        }
        T newItem = createItem();
        newItem.setNumRepeated(howMany);
        items.put(index, newItem);

        return newItem;
    }

    protected abstract T createItem();

    public int getLastUsefulItemIndex() {
        Integer key = Integer.MAX_VALUE;
        Map.Entry<Integer,T> entry;
        while ((entry = items.lowerEntry(key)) != null && entry.getValue().isBlank())
        {
            key = entry.getKey();
        }

        return entry == null ? 0 : (entry.getKey() + entry.getValue().getNumRepeated());
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public void trim()
    {
        Integer key = Integer.MAX_VALUE;
        Map.Entry<Integer,T> entry;
        while ((entry = items.lowerEntry(key)) != null && entry.getValue().isBlank()) {
            key = entry.getKey();
            remove(key, getNumItems() - key);
        }
    }

    public void trim(int limit)
    {
        if (limit >= getNumItems())
            return;
        Integer key = Integer.MAX_VALUE;
        Map.Entry<Integer,T> entry;
        while ((entry = items.lowerEntry(key)) != null && entry.getValue().isBlank()) {
            int itemKey = Math.max(limit, entry.getKey());
            remove(itemKey, getNumItems() - itemKey);
            if (itemKey == limit)
                return;
            key = itemKey;
        }
    }

    public int getNumItems()
    {
        Map.Entry<Integer, T> entry = items.lastEntry();
        if (entry != null)
            return entry.getKey() + entry.getValue().getNumRepeated();
        else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableCollection<?> that = (TableCollection<?>) o;

        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }
}
