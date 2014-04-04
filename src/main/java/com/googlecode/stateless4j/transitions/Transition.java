package com.googlecode.stateless4j.transitions;

public class Transition<TState, TTrigger> {
    final TState _source;
    final TState _destination;
    final TTrigger _trigger;


    /**
     * Construct a transition
     *
     * @param source      The state transitioned from
     * @param destination The state transitioned to
     * @param trigger     The trigger that caused the transition
     */
    public Transition(TState source, TState destination, TTrigger trigger) {
        _source = source;
        _destination = destination;
        _trigger = trigger;
    }

    /**
     * The state transitioned from
     *
     * @return The state transitioned from
     */
    public TState getSource() {
        return _source;
    }


    /**
     * The state transitioned to
     *
     * @return The state transitioned to
     */
    public TState getDestination() {
        return _destination;
    }


    /**
     * The trigger that caused the transition
     *
     * @return The trigger that caused the transition
     */
    public TTrigger getTrigger() {
        return _trigger;
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