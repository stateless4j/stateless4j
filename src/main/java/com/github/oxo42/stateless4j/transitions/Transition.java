package com.github.oxo42.stateless4j.transitions;

public class Transition<S, T> {

    private final S source;
    private final S destination;
    private final T trigger;

    /**
     * Construct a transition
     *
     * @param source      The state transitioned from
     * @param destination The state transitioned to
     * @param trigger     The trigger that caused the transition
     */
    public Transition(S source, S destination, T trigger) {
        this.source = source;
        this.destination = destination;
        this.trigger = trigger;
    }

    /**
     * The state transitioned from
     *
     * @return The state transitioned from
     */
    public S getSource() {
        return source;
    }

    /**
     * The state transitioned to
     *
     * @return The state transitioned to
     */
    public S getDestination() {
        return destination;
    }

    /**
     * The trigger that caused the transition
     *
     * @return The trigger that caused the transition
     */
    public T getTrigger() {
        return trigger;
    }

    /**
     * True if the transition is a re-entry, i.e. the identity transition
     *
     * @return True if the transition is a re-entry
     */
    public boolean isReentry() {
        return getSource().equals(getDestination());
    }
}
