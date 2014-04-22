package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.conversion.ParameterConversion;
import com.github.oxo42.stateless4j.validation.Enforce;

public abstract class TriggerWithParameters<TState, TTrigger> {
    private final TTrigger underlyingTrigger;
    private final Class<?>[] argumentTypes;


    /**
     * Create a configured trigger
     *
     * @param underlyingTrigger Trigger represented by this trigger configuration
     * @param argumentTypes     The argument types expected by the trigger
     */
    public TriggerWithParameters(TTrigger underlyingTrigger, Class<?>... argumentTypes) {
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
    public void validateParameters(Object[] args) {
        Enforce.argumentNotNull(args, "args");
        ParameterConversion.validate(args, argumentTypes);
    }
}
