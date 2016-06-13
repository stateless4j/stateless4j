package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public class InternalTriggerBehaviour<S, T> extends TriggerBehaviour<S, T> {
    private final Action action;

    public InternalTriggerBehaviour(T trigger, FuncBoolean guard, Action action) {
        super(trigger, guard);
        this.action = action;
    }
    
    @Override
    public void performAction(Object[] args) {
        action.doIt();
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public boolean resultsInTransitionFrom(S source, Object[] args, OutVar<S> dest) {
        dest.set(source);
        return true;
    }
}
