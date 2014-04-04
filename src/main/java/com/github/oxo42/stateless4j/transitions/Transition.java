package com.github.oxo42.stateless4j.transitions;

public class Transition<TState, TTrigger> {
    private final TState source;
    private final TState destination;
    private final TTrigger trigger;


    /**
     * Construct a transition
     *
     * @param source      The state transitioned from
     * @param destination The state transitioned to
     * @param trigger     The trigger that caused the transition
     */
    public Transition(TState source, TState destination, TTrigger trigger) {
        this.source = source;
        this.destination = destination;
        this.trigger = trigger;
    }

    /**
     * The state transitioned from
     *
     * @return The state transitioned from
     */
    public TState getSource() {
        return source;
    }


    /**
     * The state transitioned to
     *
     * @return The state transitioned to
     */
    public TState getDestination() {
        return destination;
    }


    /**
     * The trigger that caused the transition
     *
     * @return The trigger that caused the transition
     */
    public TTrigger getTrigger() {
        return trigger;
    }


    /**
     * True if the transition is a re-entry, i.e. the identity transition
     *
     * @return True if the transition is a re-entry
     */
    public Boolean isReentry() {
        return getSource().equals(getDestination());
    }
}
