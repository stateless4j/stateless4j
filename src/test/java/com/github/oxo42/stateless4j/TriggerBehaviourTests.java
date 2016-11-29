package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import org.junit.Test;

import static org.junit.Assert.*;

public class TriggerBehaviourTests {

    @Test
    public void ExposesCorrectUnderlyingTrigger() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);

        assertEquals(Trigger.X, transtioning.getTrigger());
    }

    @Test
    public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnFalse);

        assertFalse(transtioning.isGuardConditionMet());
    }

    @Test
    public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);

        assertTrue(transtioning.isGuardConditionMet());
    }
}
