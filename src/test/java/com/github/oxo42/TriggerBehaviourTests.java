package com.github.oxo42;

import org.junit.Assert;
import org.junit.Test;

import com.github.oxo42.stateless.transitions.TransitioningTriggerBehaviour;


public class TriggerBehaviourTests {
    @Test
    public void ExposesCorrectUnderlyingTrigger() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);

        Assert.assertEquals(Trigger.X, transtioning.getTrigger());
    }

    @Test
    public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() throws Exception {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnFalse);

        Assert.assertFalse(transtioning.isGuardConditionMet());
    }

    @Test
    public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() throws Exception {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(
                Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);

        Assert.assertTrue(transtioning.isGuardConditionMet());
    }
}
