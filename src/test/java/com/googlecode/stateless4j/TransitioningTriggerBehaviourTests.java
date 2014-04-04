package com.googlecode.stateless4j;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.stateless4j.transitions.TransitioningTriggerBehaviour;


public class TransitioningTriggerBehaviourTests {
    @Test
    public void TransitionsToDestinationState() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);
        State destination = transtioning.ResultsInTransitionFrom(State.B, new Object[0]);
        Assert.assertEquals(State.C, destination);
    }
}
