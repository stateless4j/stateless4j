package com.github.oxo42.stateless4j;

import org.junit.Test;

import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TriggerBehaviourTests {

    @Test
    public void ExposesCorrectUnderlyingTrigger() {
        TransitioningTriggerBehaviour<State, Trigger> transitioning = new TransitioningTriggerBehaviour<>(
                Trigger.X, State.C, InternalTriggerBehaviourTests.returnTrue, InternalTriggerBehaviourTests.nopAction);

        assertEquals(Trigger.X, transitioning.getTrigger());
    }

    @Test
    public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() {
        TransitioningTriggerBehaviour<State, Trigger> transitioning = new TransitioningTriggerBehaviour<>(
                Trigger.X, State.C, InternalTriggerBehaviourTests.returnFalse, InternalTriggerBehaviourTests.nopAction);

        assertFalse(transitioning.isGuardConditionMet());
    }

    @Test
    public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() {
        TransitioningTriggerBehaviour<State, Trigger> transitioning = new TransitioningTriggerBehaviour<>(
                Trigger.X, State.C, InternalTriggerBehaviourTests.returnTrue, InternalTriggerBehaviourTests.nopAction);

        assertTrue(transitioning.isGuardConditionMet());
    }
}
