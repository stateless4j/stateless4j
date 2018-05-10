package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.delegates.Action3;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class StateMachineTests {

    final Enum StateA = State.A, StateB = State.B, StateC = State.C,
            TriggerX = Trigger.X, TriggerY = Trigger.Y;
    boolean fired = false;
    String entryArgS = null;
    int entryArgI = 0;
    Object[] receivedArgs;

    @Test
    public void CanUseReferenceTypeMarkers() {
        RunSimpleTest(
                new Enum[]{StateA, StateB, StateC},
                new Enum[]{TriggerX, TriggerY});
    }

    @Test
    public void CanUseValueTypeMarkers() {
        RunSimpleTest(State.values(), Trigger.values());
    }

    <S extends Enum, T extends Enum> void RunSimpleTest(S[] states, T[] transitions) {
        S a = states[0];
        S b = states[1];
        T x = transitions[0];

        StateMachineConfig<S, T> config = new StateMachineConfig<>();

        config.configure(a)
                .permit(x, b);

        StateMachine<S, T> sm = new StateMachine<>(a, config);
        sm.fire(x);

        assertEquals(b, sm.getState());
    }

    @Test
    public void InitialStateIsCurrent() {
        State initial = State.B;
        StateMachine<State, Trigger> sm = new StateMachine<>(initial, new StateMachineConfig<State, Trigger>());
        assertEquals(initial, sm.getState());
    }

    @Test
    public void SubstateIsIncludedInCurrentState() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        config.configure(State.B).substateOf(State.C);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);

        assertEquals(State.B, sm.getState());
        assertTrue(sm.isInState(State.C));
    }

    @Test
    public void WhenInSubstate_TriggerIgnoredInSuperstate_RemainsInSubstate() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        config.configure(State.B)
                .substateOf(State.C);

        config.configure(State.C)
                .ignore(Trigger.X);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);
        sm.fire(Trigger.X);

        assertEquals(State.B, sm.getState());
    }

    @Test
    public void PermittedTriggersIncludeSuperstatePermittedTriggers() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        config.configure(State.A)
                .permit(Trigger.Z, State.B);

        config.configure(State.B)
                .substateOf(State.C)
                .permit(Trigger.X, State.A);

        config.configure(State.C)
                .permit(Trigger.Y, State.A);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);
        List<Trigger> permitted = sm.getPermittedTriggers();

        assertTrue(permitted.contains(Trigger.X));
        assertTrue(permitted.contains(Trigger.Y));
        assertFalse(permitted.contains(Trigger.Z));
    }

    @Test
    public void PermittedTriggersAreDistinctValues() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        config.configure(State.B)
                .substateOf(State.C)
                .permit(Trigger.X, State.A);

        config.configure(State.C)
                .permit(Trigger.X, State.B);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);
        List<Trigger> permitted = sm.getPermittedTriggers();

        assertEquals(1, permitted.size());
        assertEquals(Trigger.X, permitted.get(0));
    }

    @Test
    public void AcceptedTriggersRespectGuards() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        config.configure(State.B)
                .permitIf(Trigger.X, State.A, new FuncBoolean() {

                    @Override
                    public boolean call() {
                        return false;
                    }
                });

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);

        assertEquals(0, sm.getPermittedTriggers().size());
    }

    @Test
    public void WhenDiscriminatedByGuard_ChoosesPermittedTransition() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        config.configure(State.B)
                .permitIf(Trigger.X, State.A, InternalTriggerBehaviourTests.returnFalse)
                .permitIf(Trigger.X, State.C, InternalTriggerBehaviourTests.returnTrue);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);
        sm.fire(Trigger.X);

        assertEquals(State.C, sm.getState());
    }

    private void setFired() {
        fired = true;
    }

    @Test
    public void WhenTriggerIsIgnored_ActionsNotExecuted() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        config.configure(State.B)
                .onEntry(new Action() {

                    @Override
                    public void doIt() {
                        setFired();
                    }
                })
                .ignore(Trigger.X);

        fired = false;

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);
        sm.fire(Trigger.X);

        assertFalse(fired);
    }



    @Test
    public void WhenUnhandledTriggerisFired_UnhandledTriggerActionIsCalled() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();
        config.configure(State.B);

        fired = false;
        entryArgS = null;
        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);
        sm.onUnhandledTrigger(new Action3<State, Trigger, Object[]>() {
            @Override
            public void doIt(State trigger, Trigger state, Object[] args) {
                fired = true;
                entryArgS = (String) args[0];
            }
        });

        sm.fire(new TriggerWithParameters1<>(Trigger.X, String.class),"Param");

        assertTrue(fired);
        assertEquals(entryArgS, "Param");
    }

    @Test
    public void IfSelfTransitionPermitted_ActionsFire() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        config.configure(State.B)
                .onEntry(new Action() {

                    @Override
                    public void doIt() {
                        setFired();
                    }
                })
                .permitReentry(Trigger.X);

        fired = false;

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);
        sm.fire(Trigger.X);

        assertTrue(fired);
    }

    @Test(expected = IllegalStateException.class)
    public void ImplicitReentryIsDisallowed() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);

        config.configure(State.B)
                .permit(Trigger.X, State.B);
    }

    @Test(expected = IllegalStateException.class)
    public void TriggerParametersAreImmutableOnceSet() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        StateMachine<State, Trigger> sm = new StateMachine<>(State.B, config);

        config.setTriggerParameters(Trigger.X, String.class, int.class);
        config.setTriggerParameters(Trigger.X, String.class);
    }

        @Test
        public void ParametersSuppliedToFireArePassedToEntryAction()
        {
        	StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        	TriggerWithParameters2<String, Integer, Trigger> x = new TriggerWithParameters2<>(Trigger.X, String.class, int.class);

            sm.configure(State.B)
                .permit(Trigger.X, State.C);

            sm.configure(State.C)
                .onEntry(new Action2<Transition<State,Trigger>, Object[]>() {
                	public void doIt(Transition<State, Trigger> trans, Object[] args) {
                		receivedArgs = args;
				}});

            String suppliedArgS = "something";
            int suppliedArgI = 42;

            sm.fire(x, suppliedArgS, suppliedArgI);

            assertEquals(receivedArgs[0], suppliedArgS);
            assertEquals(receivedArgs[1], suppliedArgI);
        }
//
//        @Test
//        public void WhenAnUnhandledTriggerIsFired_TheProvidedHandlerIsCalledWithStateAndTrigger()
//        {
//            var sm = new StateMachine<State, Trigger>(State.B);
//
//            State? state = null;
//            Trigger? trigger = null;
//            sm.onUnhandledTrigger((s, t) =>
//                                      {
//                                          state = s;
//                                          trigger = t;
//                                      });
//
//            sm.fire(Trigger.Z);
//
//            AreEqual(State.B, state);
//            AreEqual(Trigger.Z, trigger);
//        }
}
