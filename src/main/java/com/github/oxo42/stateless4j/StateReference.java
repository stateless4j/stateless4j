package com.github.oxo42.stateless4j;

public class StateReference<S, T> {

    private S state;

    public S getState() {
        return state;
    }

    public void setState(S value) {
        state = value;
    }
}
