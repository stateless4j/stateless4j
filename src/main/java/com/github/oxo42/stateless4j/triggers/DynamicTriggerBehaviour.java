package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.validation.Enforce;

public class DynamicTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {

    private final Func2<Object[], TState> destination;

    public DynamicTriggerBehaviour(TTrigger trigger, Func2<Object[], TState> destination, FuncBoolean guard) {
        super(trigger, guard);
        this.destination = Enforce.argumentNotNull(destination, "destination");
    }

    public boolean resultsInTransitionFrom(TState source, Object[] args, OutVar<TState> dest) {
        dest.set(destination.call(args));
        return true;
    }
}
