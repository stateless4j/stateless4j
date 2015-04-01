package com.github.oxo42.stateless4j;

public final class OutVar<T> {

    private T obj;

    public T get() {
        return obj;
    }

    public void set(T v) {
        this.obj = v;
    }

    @Override
    public String toString() {
        return String.valueOf(obj);
    }
}
