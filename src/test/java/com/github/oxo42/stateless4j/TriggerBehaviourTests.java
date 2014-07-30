package com.github.oxo42.stateless4j;

import org.junit.Assert;
import org.junit.Test;

import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TriggerBehaviourTests {

    @Test
    public void ExposesCorrectUnderlyingTrigger() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);

        assertEquals(Trigger.X, transtioning.getTrigger());
    }

    @Test
    public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnFalse);

        assertFalse(transtioning.isGuardConditionMet());
    }

    @Test
    public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);

        assertTrue(transtioning.isGuardConditionMet());
    }
}
