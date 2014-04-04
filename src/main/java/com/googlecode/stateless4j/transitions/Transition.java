package com.googlecode.stateless4j.transitions;

public class Transition<TState, TTrigger>
{
    final TState _source;
    final TState _destination;
    final TTrigger _trigger;

    /// <summary>
    /// Construct a transition.
    /// </summary>
    /// <param name="source">The state transitioned from.</param>
    /// <param name="destination">The state transitioned to.</param>
    /// <param name="trigger">The trigger that caused the transition.</param>
    public Transition(TState source, TState destination, TTrigger trigger)
    {
        _source = source;
        _destination = destination;
        _trigger = trigger;
    }

    /// <summary>
    /// The state transitioned from.
    /// </summary>
    public TState getSource() { return _source; }
    
    /// <summary>
    /// The state transitioned to.
    /// </summary>
    public TState getDestination() { return _destination; } 
    
    /// <summary>
    /// The trigger that caused the transition.
    /// </summary>
    public TTrigger getTrigger() { return _trigger; }

    /// <summary>
    /// True if the transition is a re-entry, i.e. the identity transition.
    /// </summary>
    public Boolean isReentry() { return getSource().equals(getDestination()); }
}