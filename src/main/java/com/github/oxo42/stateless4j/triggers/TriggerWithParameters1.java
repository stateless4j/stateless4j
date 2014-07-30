package com.github.oxo42.stateless4j.triggers;

public class TriggerWithParameters1<TArg0, TState, TTrigger> extends TriggerWithParameters<TState, TTrigger> {

    /**
     * Create a configured trigger
     *
     * @param underlyingTrigger Trigger represented by this trigger configuration
     * @param classe            Class argument
     */
    public TriggerWithParameters1(TTrigger underlyingTrigger, Class<TArg0> classe) {
        super(underlyingTrigger, classe);
    }
}
