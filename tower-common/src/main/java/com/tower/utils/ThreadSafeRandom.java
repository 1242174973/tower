package com.tower.utils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 9:47
 */
public class ThreadSafeRandom {
    private Random random = new Random();

    public int next() {
        synchronized (this) {
            return this.random.nextInt();
        }
    }

    public int next(int maxValue) {
        synchronized (this) {
            return this.random.nextInt(maxValue);
        }
    }

    public float nextFloat() {
        synchronized (this) {
            return this.random.nextFloat();
        }
    }

    public static void main(String[] args) {
        ThreadSafeRandom ts = new ThreadSafeRandom();
        for (int i = 0; i < 100; i++) {
            System.out.print(ts.next(1) + ",");
        }
    }

    public int next(int minValue, int maxValue) {
        synchronized (this) {
            if (minValue < maxValue) {
                return this.random.nextInt(maxValue - minValue) + minValue;
            }
        }
        return minValue;
    }

    public float next(float minValue, float maxValue) {
        synchronized (this) {
            if (minValue < maxValue) {
                return this.random.nextFloat() * (maxValue - minValue) + minValue;
            }
        }
        return minValue;
    }

    public boolean inRandom(int value, int maxValue) {
        synchronized (this) {
            int ran = this.random.nextInt(maxValue);
            return ran <= value;
        }
    }

    public <K, V> Map.Entry<K, V> randomMapEntry(Map<K, V> map) {
        if (map == null) {
            return null;
        }
        int index = this.random.nextInt(map.size());
        int i = 0;
        for (Map.Entry entry : map.entrySet()) {
            if (i == index) {
                return entry;
            }
            i++;
        }
        return null;
    }

    public <E> E randomListElement(List<E> list) {
        if ((list == null) || (list.size() < 1)) {
            return null;
        }
        return list.get(this.random.nextInt(list.size()));
    }

    public <E> E randomArrayElement(E[] array) {
        if ((array == null) || (array.length < 1)) {
            return null;
        }
        return array[this.random.nextInt(array.length)];
    }

    public <E> E randomSetElement(Set<E> set) {
        if (set == null) {
            return null;
        }
        int index = this.random.nextInt(set.size());
        int i = 0;
        for (Object e : set) {
            if (index == i) {
                return (E) e;
            }
            i++;
        }
        return null;
    }
}
