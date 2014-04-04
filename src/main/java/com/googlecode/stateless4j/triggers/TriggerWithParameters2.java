package com.googlecode.stateless4j.triggers;

public class TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> extends TriggerWithParameters<TState, TTrigger>
{
    /// <summary>
    /// Create a configured trigger.
    /// </summary>
    /// <param name="underlyingTrigger">Trigger represented by this trigger configuration.</param>
    public TriggerWithParameters2(TTrigger underlyingTrigger, Class<TArg0> classe0, Class<TArg1> classe1) throws Exception
    {
        super(underlyingTrigger, classe0, classe1);
    }
}