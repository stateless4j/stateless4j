package com.googlecode.stateless4j.triggers;

import com.googlecode.stateless4j.conversion.ParameterConversion;
import com.googlecode.stateless4j.validation.Enforce;

public abstract class TriggerWithParameters<TState, TTrigger>
{
    final TTrigger _underlyingTrigger;
    final Class<?>[] _argumentTypes;

    /// <summary>
    /// Create a configured trigger.
    /// </summary>
    /// <param name="underlyingTrigger">Trigger represented by this trigger configuration.</param>
    /// <param name="argumentTypes">The argument types expected by the trigger.</param>
    public TriggerWithParameters(TTrigger underlyingTrigger, Class<?>... argumentTypes) throws Exception
    {
        Enforce.ArgumentNotNull(argumentTypes, "argumentTypes");

        _underlyingTrigger = underlyingTrigger;
        _argumentTypes = argumentTypes;
    }

    /// <summary>
    /// Gets the underlying trigger value that has been configured.
    /// </summary>
    public TTrigger getTrigger() { return _underlyingTrigger; }

    /// <summary>
    /// Ensure that the supplied arguments are compatible with those configured for this
    /// trigger.
    /// </summary>
    /// <param name="args"></param>
    public void ValidateParameters(Object[] args) throws Exception
    {
        Enforce.ArgumentNotNull(args, "args");

        ParameterConversion.Validate(args, _argumentTypes);
    }
}