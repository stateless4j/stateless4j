package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

public class InitialStateEntryTest {

    private enum State {
        SUPER,
        ALPHA,
        BRAVO
    }

    private enum Trigger {
        NEXT
    }

    private StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();
    private StateMachine<State, Trigger> fsm;

    @Mock
    private Action onEntrySuper;

    @Mock
    private Action onEntryAlpha;

    @Mock
    private Action onEntryBravo;

    /*
     *  @startuml
     *  [*] -> SUPER
     *  state SUPER {
     *      [*] -> ALPHA
     *      ALPHA -> BRAVO: NEXT
     *      BRAVO -> ALPHA: NEXT
     *  }
     *  @enduml
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        config.configure(State.SUPER)
                .onEntry(onEntrySuper);
        config.configure(State.ALPHA)
                .substateOf(State.SUPER)
                .onEntry(onEntryAlpha)
                .permit(Trigger.NEXT, State.BRAVO);
        config.configure(State.BRAVO)
                .onEntry(onEntryBravo)
                .substateOf(State.SUPER)
                .permit(Trigger.NEXT, State.ALPHA);

        fsm = new StateMachine<>(State.ALPHA, config);
    }

    @Test
    public void initialStateActionsNotCalledByDefault() {
        // GIVEN
        //      state machine is configured with initial state
        //      initial state is a substate of parent
        //      state machine is not started yet
        fsm.isInState(State.SUPER);
        fsm.isInState(State.ALPHA);

        // WHEN
        //      state machine handles transition
        fsm.fire(Trigger.NEXT);

        // THEN
        //      transition is performed
        //      initial state entry actions are not called
        //      destination state entry actions are called
        verify(onEntrySuper, never()).doIt();
        verify(onEntryAlpha, never()).doIt();
        verify(onEntryBravo).doIt();
    }

    @Test
    public void initialStateTransition() {
        // GIVEN
        //      state machine is configured with initial state
        //      initial state is a substate of parent
        //      state machine is not started yet
        fsm.isInState(State.SUPER);
        fsm.isInState(State.ALPHA);

        // WHEN
        //      initial transition is fired
        fsm.fireInitialTransition();

        // THEN
        //      state machine enters superstate
        //      state machine enters inner state (initial)
        verify(onEntrySuper).doIt();
        verify(onEntryAlpha).doIt();
    }

    @Test(expected = IllegalStateException.class)
    public void initialStateTransitionCanBeTriggeredOnlyOnce() {
        // GIVEN
        //      state machine is configured with initial state
        //      initial state is a substate of parent
        //      state machine initial transition has been called
        fsm.isInState(State.SUPER);
        fsm.isInState(State.ALPHA);
        fsm.fireInitialTransition();
        // WHEN
        //      initial transition is fired 2nd time
        fsm.fireInitialTransition();

        // THEN
        //      exception is thrown
    }

    @Test(expected = IllegalStateException.class)
    public void initialStateTransitionCanBeTriggeredBeforeAnyTrigger() {
        // GIVEN
        //      state machine is configured with initial state
        //      initial state is a substate of parent
        //      state machine normal transition has been performed
        fsm.isInState(State.SUPER);
        fsm.isInState(State.ALPHA);
        fsm.fire(Trigger.NEXT);
        fsm.isInState(State.BRAVO);

        // WHEN
        //      initial transition is fired after state machine has been started
        fsm.fireInitialTransition();

        // THEN
        //      exception is thrown
    }
}
