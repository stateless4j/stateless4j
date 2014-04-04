package com.googlecode.stateless4j.triggers;
import com.googlecode.stateless4j.delegates.Func;


public abstract class TriggerBehaviour<TState, TTrigger>
{
    final TTrigger _trigger;
    final Func<Boolean> _guard;

    protected TriggerBehaviour(TTrigger trigger, Func<Boolean> guard)
    {
        _trigger = trigger;
        _guard = guard;
    }

    public TTrigger getTrigger() { return _trigger; } 

    public Boolean isGuardConditionMet()
    {
        return _guard.call();
    }

    public abstract TState ResultsInTransitionFrom(TState source, Object... args) throws Exception;
}