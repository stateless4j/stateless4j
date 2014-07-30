package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.conversion.ParameterConversion;

public abstract class TriggerWithParameters<TState, TTrigger> {

    private final TTrigger underlyingTrigger;
    private final Class<?>[] argumentTypes;

    /**
     * Create a configured trigger
     *
     * @param underlyingTrigger Trigger represented by this trigger configuration
     * @param argumentTypes     The argument types expected by the trigger
     */
    public TriggerWithParameters(final TTrigger underlyingTrigger, final Class<?>... argumentTypes) {
        assert argumentTypes != null : "argumentTypes is null";

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
        assert args != null : "args is null";
        ParameterConversion.validate(args, argumentTypes);
    }
}
