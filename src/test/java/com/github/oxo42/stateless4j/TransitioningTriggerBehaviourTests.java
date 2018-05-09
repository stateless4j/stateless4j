package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class TransitioningTriggerBehaviourTests {

	@Test
	public void TransitionIsExternal() {
		TransitioningTriggerBehaviour<State, Trigger> transitioning =
				new TransitioningTriggerBehaviour<>(Trigger.X, State.C,
						InternalTriggerBehaviourTests.returnTrue, InternalTriggerBehaviourTests.nopAction);
		assertFalse(transitioning.isInternal());
	}

    @Test
    public void TransitionsToDestinationState() {
        TransitioningTriggerBehaviour<State, Trigger> transitioning =
                new TransitioningTriggerBehaviour<>(Trigger.X, State.C,
                        InternalTriggerBehaviourTests.returnTrue, InternalTriggerBehaviourTests.nopAction);
        assertEquals(State.C, transitioning.transitionsTo(State.B, new Object[0]));
    }
}
