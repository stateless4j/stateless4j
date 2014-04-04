package com.googlecode.stateless4j.triggers;

public class TriggerWithParameters1<TArg0, TState, TTrigger> extends TriggerWithParameters<TState, TTrigger>
{
    /// <summary>
    /// Create a configured trigger.
    /// </summary>
    /// <param name="underlyingTrigger">Trigger represented by this trigger configuration.</param>
    public TriggerWithParameters1(TTrigger underlyingTrigger, Class<TArg0> classe) throws Exception
    {
       super(underlyingTrigger, classe);
    }
}