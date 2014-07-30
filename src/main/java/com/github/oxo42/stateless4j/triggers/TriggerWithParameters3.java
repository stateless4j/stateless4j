package com.github.oxo42.stateless4j.triggers;

public class TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> extends TriggerWithParameters<TState, TTrigger> {

    /**
     * Create a configured trigger
     *
     * @param underlyingTrigger Trigger represented by this trigger configuration
     * @param classe0           Class argument
     * @param classe1           Class argument
     * @param classe2           Class argument
     */
    public TriggerWithParameters3(TTrigger underlyingTrigger, Class<TArg0> classe0, Class<TArg1> classe1, Class<TArg2> classe2) {
        super(underlyingTrigger, classe0, classe1, classe2);
    }
}
