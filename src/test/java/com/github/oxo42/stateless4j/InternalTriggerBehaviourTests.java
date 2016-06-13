package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.triggers.InternalTriggerBehaviour;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InternalTriggerBehaviourTests {

    public static FuncBoolean returnTrue = new FuncBoolean() {

        @Override
        public boolean call() {
            return true;
        }
    };

    public static FuncBoolean returnFalse = new FuncBoolean() {

        @Override
        public boolean call() {
            return false;
        }
    };

    public static Action nopAction = new Action() {

        @Override
        public void doIt() {
        }
    };

    @Test
    public void StateRemainsUnchanged() {
        InternalTriggerBehaviour<State, Trigger> ignored = new InternalTriggerBehaviour<>(Trigger.X, returnTrue, nopAction);
        State target = ignored.transitionsTo(State.B, new Object[0]);
        assertEquals(State.B, target);
    }

    @Test
    public void ExposesCorrectUnderlyingTrigger() {
    	InternalTriggerBehaviour<State, Trigger> ignored = new InternalTriggerBehaviour<>(Trigger.X, returnTrue, nopAction);
        assertEquals(Trigger.X, ignored.getTrigger());
    }

    @Test
    public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() {
    	InternalTriggerBehaviour<State, Trigger> ignored = new InternalTriggerBehaviour<>(Trigger.X, returnFalse, nopAction);
        assertFalse(ignored.isGuardConditionMet());
    }

    @Test
    public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() {
    	InternalTriggerBehaviour<State, Trigger> ignored = new InternalTriggerBehaviour<>(Trigger.X, returnTrue, nopAction);
        assertTrue(ignored.isGuardConditionMet());
    }
    
    @Test
    public void TransitionIsInternal() {
    	InternalTriggerBehaviour<State, Trigger> ignored = new InternalTriggerBehaviour<>(Trigger.X, returnTrue, nopAction);
    	assertTrue(ignored.isInternal());
    }
}
