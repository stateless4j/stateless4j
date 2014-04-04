package com.googlecode.stateless4j;

public class StateReference<TState, TTrigger>
{
    TState state;
    public TState getState() { return state; }
    public void setState(TState value) { state = value; }
}