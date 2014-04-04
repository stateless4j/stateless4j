package com.github.oxo42.stateless.triggers;

import com.github.oxo42.stateless.delegates.Func;
import com.github.oxo42.stateless.delegates.Func2;
import com.github.oxo42.stateless.validation.Enforce;

public class DynamicTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {
    private Func2<Object[], TState> destination;

    public DynamicTriggerBehaviour(TTrigger trigger, Func2<Object[], TState> destination, Func<Boolean> guard) throws Exception {
        super(trigger, guard);
        this.destination = Enforce.argumentNotNull(destination, "destination");
    }

    public TState resultsInTransitionFrom(TState source, Object... args) throws Exception {
        return destination.call(args);
    }
}
