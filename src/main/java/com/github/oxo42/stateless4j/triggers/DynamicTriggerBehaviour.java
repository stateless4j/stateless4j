package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.delegates.Func;
import com.github.oxo42.stateless4j.delegates.Func2;

public class DynamicTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {

    private Func2<Object[], TState> destination;

    public DynamicTriggerBehaviour(final TTrigger trigger, final Func2<Object[], TState> destination, final Func<Boolean> guard) {
        super(trigger, guard);
        assert destination != null : "destination is null";
        this.destination = destination;
    }

    public TState resultsInTransitionFrom(TState source, Object... args) {
        return destination.call(args);
    }
}
