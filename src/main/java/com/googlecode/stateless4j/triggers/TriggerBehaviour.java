package com.googlecode.stateless4j.triggers;

import com.googlecode.stateless4j.delegates.Func;


public abstract class TriggerBehaviour<TState, TTrigger> {
    private final TTrigger trigger;
    private final Func<Boolean> guard;

    protected TriggerBehaviour(TTrigger trigger, Func<Boolean> guard) {
        this.trigger = trigger;
        this.guard = guard;
    }

    public TTrigger getTrigger() {
        return trigger;
    }

    public Boolean isGuardConditionMet() throws Exception {
        return guard.call();
    }

    public abstract TState resultsInTransitionFrom(TState source, Object... args) throws Exception;
}
