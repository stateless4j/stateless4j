package com.github.oxo42.stateless4j;

import org.junit.Test;
import static org.junit.Assert.*;

import com.github.oxo42.stateless4j.delegates.Action;

public class InternalTransitionActionTests {

    final Enum StateA = State.A,
            TriggerX = Trigger.X, TriggerY = Trigger.Y;

    private class TripwireAction implements Action {
        private boolean beenThere;

        public TripwireAction() {
            beenThere = false;
        }

        public boolean wasPerformed() {
            return beenThere;
        }

        @Override
        public void doIt() {
            beenThere = true;
        }
    }

    @Test
    public void UnguardedActionIsPerformed() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        TripwireAction action = new TripwireAction();

        config.configure(State.A)
                .permitInternal(Trigger.X, action);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(Trigger.X);

        assertEquals(State.A, sm.getState());
        assertTrue(action.wasPerformed());
    }

    @Test
    public void ExitAndEntryAreNotPerformed() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        TripwireAction entry = new TripwireAction();
        TripwireAction action = new TripwireAction();
        TripwireAction exit = new TripwireAction();

        config.configure(State.A)
                .onEntry(entry)
                .onExit(exit)
                .permitInternal(Trigger.X, action);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(Trigger.X);

        assertEquals(State.A, sm.getState());
        assertFalse(entry.wasPerformed());
        assertFalse(exit.wasPerformed());
    }

    @Test
    public void ActionWithPositiveGuardIsPerformed() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        TripwireAction action = new TripwireAction();

        config.configure(State.A)
            .permitInternalIf(Trigger.X, InternalTriggerBehaviourTests.returnTrue, action);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(Trigger.X);

        assertEquals(State.A, sm.getState());
        assertTrue(action.wasPerformed());
    }

    @Test(expected = IllegalStateException.class)
    public void ActionWithNegativeGuardIsNotPerformed() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        TripwireAction action = new TripwireAction();

        config.configure(State.A)
            .permitInternalIf(Trigger.X, InternalTriggerBehaviourTests.returnFalse, action);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(Trigger.X);
    }

    @Test(expected = IllegalStateException.class)
    public void MultipleInternalActionsWithSameTriggerNotAllowed() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        TripwireAction action1 = new TripwireAction();
        TripwireAction action2 = new TripwireAction();

        config.configure(State.A)
            .permitInternal(Trigger.X, action1)
            .permitInternal(Trigger.X, action2);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(Trigger.X);
    }

    @Test
    public void MultipleInternalActionsWithDistinctTriggersAreAllowed() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        TripwireAction action1 = new TripwireAction();
        TripwireAction action2 = new TripwireAction();

        config.configure(State.A)
            .permitInternal(Trigger.X, action1)
            .permitInternal(Trigger.Y, action2);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(Trigger.X);

        assertEquals(State.A, sm.getState());
        assertTrue(action1.wasPerformed());
        assertFalse(action2.wasPerformed());

        sm.fire(Trigger.Y);

        assertEquals(State.A, sm.getState());
        assertTrue(action2.wasPerformed());
    }
}
