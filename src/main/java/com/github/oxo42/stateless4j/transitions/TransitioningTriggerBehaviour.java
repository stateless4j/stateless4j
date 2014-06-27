package com.github.oxo42.stateless4j.transitions;

import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.triggers.TriggerBehaviour;

public class TransitioningTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {
    private final TState destination;

    public TransitioningTriggerBehaviour(TTrigger trigger, TState destination, FuncBoolean guard) {
        super(trigger, guard);
        this.destination = destination;
    }

    public TState resultsInTransitionFrom(TState source, Object... args) {
        return destination;
    }
}
