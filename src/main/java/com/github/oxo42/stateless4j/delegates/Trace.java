package com.github.oxo42.stateless4j.delegates;

/**
 * Tracing delegate allows one to investigate state machine working at runtime.
 *
 * @see com.github.oxo42.stateless4j.StateMachine#setTrace(Trace)
 * 
 * @param <S> State type
 * @param <T> Trigger type
 */
public interface Trace<S, T> {

    /**
     * This callback is called each time a trigger is fired, before evaluation,
     * allowing to trace all events sent to the sate machine
     *
     * @param trigger Trigger sent to the state machine
     */
    void trigger(T trigger);

    /**
     * This callback is called each time a transition is performed, after trigger evaluation.
     *
     * @param trigger Trigger sent to the state machine
     * @param source Source state
     * @param destination Destination state
     */
    void transition(T trigger, S source, S destination);
}
