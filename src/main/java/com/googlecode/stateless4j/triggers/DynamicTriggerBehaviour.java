package com.googlecode.stateless4j.triggers;

import com.googlecode.stateless4j.delegates.Func;
import com.googlecode.stateless4j.delegates.Func2;
import com.googlecode.stateless4j.validation.Enforce;

public class DynamicTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger>
{
    Func2<Object[], TState> _destination;

    public DynamicTriggerBehaviour(TTrigger trigger, Func2<Object[], TState> destination, Func<Boolean> guard) throws Exception
    {
        super(trigger, guard);
        _destination = Enforce.ArgumentNotNull(destination, "destination");
    }

    public TState ResultsInTransitionFrom(TState source, Object... args) throws Exception
    {
        return _destination.call(args);
    }
}