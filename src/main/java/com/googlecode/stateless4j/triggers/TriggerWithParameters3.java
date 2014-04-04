package com.googlecode.stateless4j.triggers;

public class TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> extends TriggerWithParameters<TState, TTrigger>
{
    /// <summary>
    /// Create a configured trigger.
    /// </summary>
    /// <param name="underlyingTrigger">Trigger represented by this trigger configuration.</param>
    public TriggerWithParameters3(TTrigger underlyingTrigger, Class<TArg0> classe0, Class<TArg1> classe1, Class<TArg2> classe2) throws Exception
    {
        super(underlyingTrigger, classe0, classe1, classe2);
    }
}