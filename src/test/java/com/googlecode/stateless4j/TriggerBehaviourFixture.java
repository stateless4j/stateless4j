package com.googlecode.stateless4j;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.stateless4j.transitions.TransitioningTriggerBehaviour;


public class TriggerBehaviourFixture {
    @Test
    public void ExposesCorrectUnderlyingTrigger() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(
                Trigger.X, State.C, IgnoredTriggerBehaviourFixture.returnTrue);

        Assert.assertEquals(Trigger.X, transtioning.getTrigger());
    }

    @Test
    public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() throws Exception {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(
                Trigger.X, State.C, IgnoredTriggerBehaviourFixture.returnFalse);

        Assert.assertFalse(transtioning.isGuardConditionMet());
    }

    @Test
    public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() throws Exception {
        TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(
                Trigger.X, State.C, IgnoredTriggerBehaviourFixture.returnTrue);

        Assert.assertTrue(transtioning.isGuardConditionMet());
    }
}
