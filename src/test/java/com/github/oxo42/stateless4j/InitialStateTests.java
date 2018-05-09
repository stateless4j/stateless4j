package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InitialStateTests {

    private boolean executed = false;

    @Test
    public void testInitialStateEntryActionNotExecuted() {
        final State initial = State.B;
        
        StateMachineConfig<State, Trigger> config = config(initial);
        
        StateMachine<State, Trigger> sm = new StateMachine<>(initial, config);
        assertEquals(initial, sm.getState());
        assertFalse(executed);
    }

    @Test
    public void testInitialStateEntryActionNotExecutedIfDisabled() {
        final State initial = State.B;

        StateMachineConfig<State, Trigger> config = config(initial);
        config.disableEntryActionOfInitialState();

        StateMachine<State, Trigger> sm = new StateMachine<>(initial, config);
        assertEquals(initial, sm.getState());
        assertFalse(executed);
    }

    @Test
    public void testInitialStateEntryActionExecuted() {
        final State initial = State.B;
        
        StateMachineConfig<State, Trigger> config = config(initial);
        config.enableEntryActionOfInitialState();
        
        StateMachine<State, Trigger> sm = new StateMachine<>(initial, config);
        assertEquals(initial, sm.getState());
        assertTrue(executed);
    }

    private StateMachineConfig<State, Trigger> config(final State initial) {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();
        config.configure(initial)
                .onEntry(new Action() {

                    @Override
                    public void doIt() {
                        executed = true;
                    }
                });
        return config;
    }

    @Test
    public void testInitialStateEntryActionWithParameterNotExecuted() {
        final State initial = State.B;

        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        TriggerWithParameters1<Object, Trigger> trigger =
                config.setTriggerParameters(Trigger.X, Object.class);

        config.configure(initial)
                .onEntryFrom(
                        trigger,
                        new Action1<Object>() {

                            @Override
                            public void doIt(Object arg1) {
                                executed = true;
                            }
                        },
                        Object.class
                );

        config.enableEntryActionOfInitialState();

        StateMachine<State, Trigger> sm = new StateMachine<>(initial, config);
        assertEquals(initial, sm.getState());
        assertFalse(executed);
    }
}
