package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.OutVar;

public abstract class TriggerBehaviour<S, T> {

    private final T trigger;
    private final FuncBoolean guard;

    protected TriggerBehaviour(T trigger, FuncBoolean guard) {
        this.trigger = trigger;
        this.guard = guard;
    }

    public T getTrigger() {
        return trigger;
    }

    public boolean isGuardConditionMet() {
        return guard.call();
    }

    public abstract boolean resultsInTransitionFrom(S source, Object[] args, OutVar<S> dest);
}
