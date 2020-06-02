package se.kth.castor.yajta.processor.util;


import java.util.Arrays;

public class MyMap<K, V> {
    private int size;
    private int DEFAULT_CAPACITY;
    @SuppressWarnings("unchecked")
    private MyEntry<K, V>[] values;

    public MyMap() {
        size = 0;
        DEFAULT_CAPACITY = 16;
        values = new MyEntry[DEFAULT_CAPACITY];
    }


    public V get(K key) {
        if(key == null) return null;
        for (int i = 0; i < size; i++) {
            if (values[i] != null) {
                if (values[i].getKey().equals(key)) {
                    return values[i].getValue();
                }
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public void put(K key, V value) {
        boolean insert = true;
        for (int i = 0; i < size; i++) {
            if (values[i].getKey().equals(key)) {
                values[i].setValue(value);
                insert = false;
            }
        }
        if (insert) {
            ensureCapa();
            values[size++] = new MyEntry<K, V>(key, value);
        }
    }

    private void ensureCapa() {
        if (size == values.length) {
            int newSize = values.length * 2;
            values = Arrays.copyOf(values, newSize);
        }
    }

    public int size() {
        return size;
    }

    public void remove(K key) {
        for (int i = 0; i < size; i++) {
            if (values[i].getKey().equals(key)) {
                values[i] = null;
                size--;
                condenseArray(i);
            }
        }
    }

    private void condenseArray(int start) {
        for (int i = start; i < size; i++) {
            values[i] = values[i + 1];
        }
    }

    public MyList<K> keyList() {
        MyList<K> set = new MyList<K>();
        for (int i = 0; i < size; i++) {
            set.add(values[i].getKey());
        }
        return set;
    }

    public MyList<V> valueList() {
        MyList<V> set = new MyList<V>();
        for (int i = 0; i < size; i++) {
            set.add(values[i].getValue());
        }
        return set;
    }

    public MyList<MyEntry<K, V>> entryList() {
        MyList<MyEntry<K, V>> set = new MyList<MyEntry<K, V>>();
        for (int i = 0; i < size; i++) {
            set.add(values[i]);
        }
        return set;
    }
}
