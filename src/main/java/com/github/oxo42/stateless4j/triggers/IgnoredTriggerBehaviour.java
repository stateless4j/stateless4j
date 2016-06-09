package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public class IgnoredTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {

    public IgnoredTriggerBehaviour(TTrigger trigger, FuncBoolean guard) {
        super(trigger, guard);
    }
    
    @Override
    public void performAction(Object[] args) {
        // no need to do anything. This is never called (no transition => no action)
    }

    @Override
    public boolean resultsInTransitionFrom(TState source, Object[] args, OutVar<TState> dest) {
        return false;
    }
}
