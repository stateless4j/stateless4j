package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import org.junit.Assert;
import org.junit.Test;


public class TransitioningTriggerBehaviourTests {
    @Test
    public void TransitionsToDestinationState() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);
        State destination = transtioning.resultsInTransitionFrom(State.B, new Object[0]);
        Assert.assertEquals(State.C, destination);
    }
}
