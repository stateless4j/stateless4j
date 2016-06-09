package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public class IgnoredTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {

    public IgnoredTriggerBehaviour(TTrigger trigger, FuncBoolean guard, Action action) {
        super(trigger, guard, action);
    }

    @Override
    public boolean resultsInTransitionFrom(TState source, Object[] args, OutVar<TState> dest) {
        return false;
    }
}
