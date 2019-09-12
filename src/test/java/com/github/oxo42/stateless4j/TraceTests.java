package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.delegates.Trace;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class TraceTests {

    private enum Trigger { NEXT, PREV }
    private enum State { ALPHA, BRAVO }

    private StateMachine<State, Trigger> fsm;

    @Mock
    private Trace<State, Trigger> trace;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();
        config.configure(State.ALPHA)
                .permit(Trigger.NEXT, State.BRAVO);
        config.configure(State.BRAVO)
                .permit(Trigger.PREV, State.ALPHA);
        fsm = new StateMachine<>(State.ALPHA, config);
        fsm.setTrace(trace);
    }

    @Test
    public void triggerIsCaptured() {
        // GIVEN
        //      tracer is set
        //      unhandled transitions are allowed
        fsm.onUnhandledTrigger(new Action2<State, Trigger>() {
            @Override
            public void doIt(State arg1, Trigger arg2) {

            }
        });

        // WHEN
        //      trigger is fired
        //      no transition occurs
        fsm.fire(Trigger.PREV);
        assertEquals(State.ALPHA, fsm.getState());

        // THEN
        //      tracer captures trigger
        //      tracer is not called with transition
        verify(trace).trigger(eq(Trigger.PREV));
        verify(trace, never()).transition(any(Trigger.class), any(State.class), any(State.class));
    }

    @Test
    public void transitionIsCaptured() {
        // GIVEN
        //      tracer is set
        // WHEN
        //      trigger is fired
        //      transition occurs
        fsm.fire(Trigger.NEXT);
        assertEquals(State.BRAVO, fsm.getState());

        // THEN
        //      tracer captures trigger
        //      tracer captures transition
        verify(trace).trigger(eq(Trigger.NEXT));
        verify(trace).transition(Trigger.NEXT, State.ALPHA, State.BRAVO);
    }

    @Test
    public void traceDelegateCanBeDisabled() {
        // GIVEN
        //      tracer is enabled
        // WHEN
        //      tracer is disabled
        //      trigger is fired
        //      transition occurs
        fsm.setTrace(null);
        fsm.fire(Trigger.NEXT);
        assertEquals(State.BRAVO, fsm.getState());

        // THEN
        //      tracer is not called
        verify(trace, never()).trigger(any(Trigger.class));
        verify(trace, never()).transition(any(Trigger.class), any(State.class), any(State.class));
    }
}
