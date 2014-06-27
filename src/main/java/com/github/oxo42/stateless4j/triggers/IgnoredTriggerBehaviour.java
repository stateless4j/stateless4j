package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public class IgnoredTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {
    public IgnoredTriggerBehaviour(TTrigger trigger, FuncBoolean guard) {
        super(trigger, guard);
    }

    @Override
    public TState resultsInTransitionFrom(TState source, Object... args) {
        throw new IllegalStateException();
    }
}
