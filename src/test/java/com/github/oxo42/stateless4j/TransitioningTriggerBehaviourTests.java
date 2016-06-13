package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class TransitioningTriggerBehaviourTests {

	@Test
	public void TransitionIsExternal() {
		TransitioningTriggerBehaviour<State, Trigger> transtioning =
				new TransitioningTriggerBehaviour<>(Trigger.X, State.C,
						InternalTriggerBehaviourTests.returnTrue, InternalTriggerBehaviourTests.nopAction);
		assertFalse(transtioning.isInternal());
	}

    @Test
    public void TransitionsToDestinationState() {
        TransitioningTriggerBehaviour<State, Trigger> transtioning =
                new TransitioningTriggerBehaviour<>(Trigger.X, State.C,
                        InternalTriggerBehaviourTests.returnTrue, InternalTriggerBehaviourTests.nopAction);
        assertEquals(State.C, transtioning.transitionsTo(State.B, new Object[0]));
    }
}
