package com.github.oxo42.stateless4j.transitions;

import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.triggers.TriggerBehaviour;

public class TransitioningTriggerBehaviour<S, T> extends TriggerBehaviour<S, T> {

    private final S destination;
    private final Action action;

    public TransitioningTriggerBehaviour(T trigger, S destination, FuncBoolean guard, Action action) {
        super(trigger, guard);
        this.destination = destination;
        this.action = action;
    }
    
    @Override
    public void performAction(Object[] args) {
        action.doIt();
    }

    @Override
    public S transitionsTo(S source, Object[] args) {
        return destination;
    }
}
