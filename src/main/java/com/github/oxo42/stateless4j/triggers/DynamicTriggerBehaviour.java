package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public class DynamicTriggerBehaviour<S, T> extends TriggerBehaviour<S, T> {

    private final Func2<Object[], S> destination;
    private final Action1<Object[]> action;

    public DynamicTriggerBehaviour(T trigger, Func2<Object[], S> destination, FuncBoolean guard, Action1<Object[]> action) {
        super(trigger, guard);
        assert destination != null : "destination is null";
        this.destination = destination;
        this.action = action;
    }
    
    @Override
    public void performAction(Object[] args) {
    	action.doIt(args);
    }

    @Override
    public boolean resultsInTransitionFrom(S source, Object[] args, OutVar<S> dest) {
        dest.set(destination.call(args));
        return true;
    }
}
