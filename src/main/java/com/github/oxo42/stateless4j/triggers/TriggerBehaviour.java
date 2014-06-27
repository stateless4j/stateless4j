package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.delegates.FuncBoolean;


public abstract class TriggerBehaviour<TState, TTrigger> {
    private final TTrigger trigger;
    private final FuncBoolean guard;

    protected TriggerBehaviour(TTrigger trigger, FuncBoolean guard) {
        this.trigger = trigger;
        this.guard = guard;
    }

    public TTrigger getTrigger() {
        return trigger;
    }

    public boolean isGuardConditionMet() {
        return guard.call();
    }

    public abstract TState resultsInTransitionFrom(TState source, Object... args);
}
