package com.github.oxo42.stateless.transitions;

import com.github.oxo42.stateless.delegates.Func;
import com.github.oxo42.stateless.triggers.TriggerBehaviour;

public class TransitioningTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {
    private final TState destination;

    public TransitioningTriggerBehaviour(TTrigger trigger, TState destination, Func<Boolean> guard) {
        super(trigger, guard);
        this.destination = destination;
    }

    public TState resultsInTransitionFrom(TState source, Object... args) {
        return destination;
    }
}
