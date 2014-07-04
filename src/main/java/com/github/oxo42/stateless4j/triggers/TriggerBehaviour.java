package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.OutVar;


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

    public abstract boolean resultsInTransitionFrom(TState source, Object[] args, OutVar<TState> dest);
}
