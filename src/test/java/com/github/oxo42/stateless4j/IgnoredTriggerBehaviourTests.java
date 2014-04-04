package com.github.oxo42.stateless4j;

import org.junit.Assert;
import org.junit.Test;

import com.github.oxo42.stateless4j.delegates.Func;
import com.github.oxo42.stateless4j.triggers.IgnoredTriggerBehaviour;


public class IgnoredTriggerBehaviourTests {
    public static Func<Boolean> returnTrue = new Func<Boolean>() {

        public Boolean call() {
            return true;
        }
    };

    public static Func<Boolean> returnFalse = new Func<Boolean>() {

        public Boolean call() {
            return false;
        }
    };

    @Test
    public void StateRemainsUnchanged() {
        IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<State, Trigger>(Trigger.X, returnTrue);
        try {
            ignored.resultsInTransitionFrom(State.B, new Object[0]);
            Assert.fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void ExposesCorrectUnderlyingTrigger() {
        IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<State, Trigger>(
                Trigger.X, returnTrue);

        Assert.assertEquals(Trigger.X, ignored.getTrigger());
    }

    @Test
    public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() throws Exception {
        IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<State, Trigger>(
                Trigger.X, returnFalse);

        Assert.assertFalse(ignored.isGuardConditionMet());
    }

    @Test
    public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() throws Exception {
        IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<State, Trigger>(
                Trigger.X, returnTrue);

        Assert.assertTrue(ignored.isGuardConditionMet());
    }
}
