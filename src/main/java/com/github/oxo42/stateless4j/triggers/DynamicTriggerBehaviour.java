package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public class DynamicTriggerBehaviour<S, T> extends TriggerBehaviour<S, T> {

    private final Func2<Object[], S> destination;

    private static final Action NO_ACTION = new Action() {
        @Override
        public void doIt() {
        }
    };

    public DynamicTriggerBehaviour(T trigger, Func2<Object[], S> destination, FuncBoolean guard) {
        super(trigger, guard, NO_ACTION);
        assert destination != null : "destination is null";
        this.destination = destination;
    }

    @Override
    public boolean resultsInTransitionFrom(S source, Object[] args, OutVar<S> dest) {
        dest.set(destination.call(args));
        return true;
    }
}
