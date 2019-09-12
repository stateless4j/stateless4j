package com.github.oxo42.stateless4j.delegates;

public interface Trace<S, T> {
    void trigger(T trigger);
    void transition(T trigger, S source, S destination);
}
