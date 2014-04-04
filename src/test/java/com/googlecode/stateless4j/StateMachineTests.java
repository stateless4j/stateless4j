package com.googlecode.stateless4j;

import com.googlecode.stateless4j.delegates.Action;
import com.googlecode.stateless4j.delegates.Func;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class StateMachineTests {
    final String
            StateA = "A", StateB = "B", StateC = "C",
            TriggerX = "X", TriggerY = "Y";
    Boolean fired = false;
    String entryArgS = null;
    int entryArgI = 0;

    @Test
    public void CanUseReferenceTypeMarkers() throws Exception {
        RunSimpleTest(
                new String[]{StateA, StateB, StateC},
                new String[]{TriggerX, TriggerY});
    }

    @Test
    public void CanUseValueTypeMarkers() throws Exception {
        RunSimpleTest(State.values(), Trigger.values());
    }

    <TState, TTransition> void RunSimpleTest(TState[] states, TTransition[] transitions) throws Exception {
        TState a = states[0];
        TState b = states[1];
        TTransition x = transitions[0];

        StateMachine<TState, TTransition> sm = new StateMachine<TState, TTransition>(a);

        sm.configure(a)
                .permit(x, b);

        sm.fire(x);

        assertEquals(b, sm.getState());
    }

    @Test
    public void InitialStateIsCurrent() throws Exception {
        State initial = State.B;
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(initial);
        assertEquals(initial, sm.getState());
    }

    @Test
    public void SubstateIsIncludedInCurrentState() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);
        sm.configure(State.B).substateOf(State.C);

        assertEquals(State.B, sm.getState());
        assertTrue(sm.isInState(State.C));
    }

    @Test
    public void WhenInSubstate_TriggerIgnoredInSuperstate_RemainsInSubstate() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.configure(State.B)
                .substateOf(State.C);

        sm.configure(State.C)
                .ignore(Trigger.X);

        sm.fire(Trigger.X);

        assertEquals(State.B, sm.getState());
    }

    @Test
    public void PermittedTriggersIncludeSuperstatePermittedTriggers() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.configure(State.A)
                .permit(Trigger.Z, State.B);

        sm.configure(State.B)
                .substateOf(State.C)
                .permit(Trigger.X, State.A);

        sm.configure(State.C)
                .permit(Trigger.Y, State.A);

        List<Trigger> permitted = sm.getPermittedTriggers();

        assertTrue(permitted.contains(Trigger.X));
        assertTrue(permitted.contains(Trigger.Y));
        assertFalse(permitted.contains(Trigger.Z));
    }

    @Test
    public void PermittedTriggersAreDistinctValues() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.configure(State.B)
                .substateOf(State.C)
                .permit(Trigger.X, State.A);

        sm.configure(State.C)
                .permit(Trigger.X, State.B);

        List<Trigger> permitted = sm.getPermittedTriggers();
        assertEquals(1, permitted.size());
        assertEquals(Trigger.X, permitted.get(0));
    }

    @Test
    public void AcceptedTriggersRespectGuards() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.configure(State.B)
                .permitIf(Trigger.X, State.A, new Func<Boolean>() {

                    public Boolean call() {
                        return false;
                    }
                });

        assertEquals(0, sm.getPermittedTriggers().size());
    }

    @Test
    public void WhenDiscriminatedByGuard_ChoosesPermitedTransition() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.configure(State.B)
                .permitIf(Trigger.X, State.A, IgnoredTriggerBehaviourTests.returnFalse)
                .permitIf(Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);

        sm.fire(Trigger.X);

        assertEquals(State.C, sm.getState());
    }

    private void setFired() {
        fired = true;
    }

    @Test
    public void WhenTriggerIsIgnored_ActionsNotExecuted() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        fired = false;

        sm.configure(State.B)
                .onEntry(new Action() {


                    public void doIt() {
                        setFired();
                    }
                })
                .ignore(Trigger.X);

        sm.fire(Trigger.X);

        assertFalse(fired);
    }

    @Test
    public void IfSelfTransitionPermited_ActionsFire() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        fired = false;

        sm.configure(State.B)
                .onEntry(new Action() {


                    public void doIt() {
                        setFired();
                    }
                })
                .permitReentry(Trigger.X);

        sm.fire(Trigger.X);

        assertTrue(fired);
    }

    @Test
    //[Test, ExpectedException(typeof(ArgumentException))]
    public void ImplicitReentryIsDisallowed() {
        try {

            StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

            sm.configure(State.B)
                    .permit(Trigger.X, State.B);
        } catch (Exception e) {

        }
    }

    @Test
//        [Test, ExpectedException(typeof(InvalidOperationException))]
    public void TriggerParametersAreImmutableOnceSet() throws Exception {
        try {
            StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

            sm.setTriggerParameters(Trigger.X, String.class, int.class);
            sm.setTriggerParameters(Trigger.X, String.class);
            fail();
        } catch (Exception e) {

        }
    }

//        @Test
//        public void ParametersSuppliedToFireArePassedToEntryAction()
//        {
//        	StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);
//
//        	TriggerWithParameters2<String, Integer, State, Trigger> x = sm.setTriggerParameters(Trigger.X, String.class, int.class);
//
//            sm.configure(State.B)
//                .permit(Trigger.X, State.C);
//
//
//            sm.configure(State.C)
//                .onEntryFrom(x, new Action2<String, int>() {
//                	public void doIt(String s, int i) {
//                		entryArgS = s;
//                        entryArgI = i;
//				}); 
//
//            var suppliedArgS = "something";
//            var suppliedArgI = 42;
//
//            sm.fire(x, suppliedArgS, suppliedArgI);
//
//            AreEqual(suppliedArgS, entryArgS);
//            AreEqual(suppliedArgI, entryArgI);
//        }
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

