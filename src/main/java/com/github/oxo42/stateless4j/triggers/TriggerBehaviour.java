package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.Func;


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

    public Boolean isGuardConditionMet() {
        return guard.call();
    }

    public abstract boolean resultsInTransitionFrom(TState source, Object[] args, OutVar<TState> dest);
}
