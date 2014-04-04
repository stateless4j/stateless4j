package com.github.oxo42.stateless;

public class StateReference<TState, TTrigger> {
    private TState state;

    public TState getState() {
        return state;
    }

    public void setState(TState value) {
        state = value;
    }
}
