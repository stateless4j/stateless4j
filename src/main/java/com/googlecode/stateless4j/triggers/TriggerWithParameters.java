package com.googlecode.stateless4j.triggers;

import com.googlecode.stateless4j.conversion.ParameterConversion;
import com.googlecode.stateless4j.validation.Enforce;

public abstract class TriggerWithParameters<TState, TTrigger> {
    final TTrigger _underlyingTrigger;
    final Class<?>[] _argumentTypes;


    /**
     * Create a configured trigger
     *
     * @param underlyingTrigger Trigger represented by this trigger configuration
     * @param argumentTypes     The argument types expected by the trigger
     */
    public TriggerWithParameters(TTrigger underlyingTrigger, Class<?>... argumentTypes) throws Exception {
        Enforce.ArgumentNotNull(argumentTypes, "argumentTypes");

        _underlyingTrigger = underlyingTrigger;
        _argumentTypes = argumentTypes;
    }


    /**
     * Gets the underlying trigger value that has been configured
     *
     * @return Gets the underlying trigger value that has been configured
     */
    public TTrigger getTrigger() {
        return _underlyingTrigger;
    }


    /**
     * Ensure that the supplied arguments are compatible with those configured for this trigger
     *
     * @param args Args
     */
    public void ValidateParameters(Object[] args) throws Exception {
        Enforce.ArgumentNotNull(args, "args");
        ParameterConversion.Validate(args, _argumentTypes);
    }
}