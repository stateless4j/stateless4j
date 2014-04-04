package com.googlecode.stateless4j.triggers;

import com.googlecode.stateless4j.delegates.Func;

public class IgnoredTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {
    public IgnoredTriggerBehaviour(TTrigger trigger, Func<Boolean> guard) {
        super(trigger, guard);
    }

    public TState resultsInTransitionFrom(TState source, Object... args) throws Exception {
        throw new Exception();
    }
}
