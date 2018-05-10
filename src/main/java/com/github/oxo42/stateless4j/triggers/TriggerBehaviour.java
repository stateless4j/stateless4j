package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public abstract class TriggerBehaviour<S, T> {

    private final T trigger;

    /**
     * Note that this guard gets called quite often, and sometimes multiple times per fire() call.
     * Thus, it should not be anything performance intensive.
     */
    private final FuncBoolean guard;

    protected TriggerBehaviour(T trigger, FuncBoolean guard) {
        this.trigger = trigger;
        this.guard = guard;
    }

    public T getTrigger() {
        return trigger;
    }

    public abstract void performAction(Object[] args);

    public boolean isInternal() {
        return false;
    }

    public boolean isGuardConditionMet() {
        return guard.call();
    }

    public abstract S transitionsTo(S source, Object[] args);
}
