package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.OutVar;

public abstract class TriggerBehaviour<S, T> {

    private final T trigger;
    private final FuncBoolean guard;
    private final Action action;

    protected TriggerBehaviour(T trigger, FuncBoolean guard, Action action) {
        this.trigger = trigger;
        this.guard = guard;
        this.action = action;
    }

    public T getTrigger() {
        return trigger;
    }

    public void performAction() {
        action.doIt();
    }

    public boolean isGuardConditionMet() {
        return guard.call();
    }

    public abstract boolean resultsInTransitionFrom(S source, Object[] args, OutVar<S> dest);
}
