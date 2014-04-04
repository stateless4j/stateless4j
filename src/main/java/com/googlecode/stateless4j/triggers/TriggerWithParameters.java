package com.googlecode.stateless4j.triggers;

import com.googlecode.stateless4j.conversion.ParameterConversion;
import com.googlecode.stateless4j.validation.Enforce;

public abstract class TriggerWithParameters<TState, TTrigger> {
    private final TTrigger underlyingTrigger;
    private final Class<?>[] argumentTypes;


    /**
     * Create a configured trigger
     *
     * @param underlyingTrigger Trigger represented by this trigger configuration
     * @param argumentTypes     The argument types expected by the trigger
     */
    public TriggerWithParameters(TTrigger underlyingTrigger, Class<?>... argumentTypes) throws Exception {
        Enforce.argumentNotNull(argumentTypes, "argumentTypes");

        this.underlyingTrigger = underlyingTrigger;
        this.argumentTypes = argumentTypes;
    }


    /**
     * Gets the underlying trigger value that has been configured
     *
     * @return Gets the underlying trigger value that has been configured
     */
    public TTrigger getTrigger() {
        return underlyingTrigger;
    }


    /**
     * Ensure that the supplied arguments are compatible with those configured for this trigger
     *
     * @param args Args
     */
    public void validateParameters(Object[] args) throws Exception {
        Enforce.argumentNotNull(args, "args");
        ParameterConversion.validate(args, argumentTypes);
    }
}
