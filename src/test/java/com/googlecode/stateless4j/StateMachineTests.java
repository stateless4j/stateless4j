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

        sm.Configure(a)
                .Permit(x, b);

        sm.Fire(x);

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
        sm.Configure(State.B).SubstateOf(State.C);

        assertEquals(State.B, sm.getState());
        assertTrue(sm.IsInState(State.C));
    }

    @Test
    public void WhenInSubstate_TriggerIgnoredInSuperstate_RemainsInSubstate() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.Configure(State.B)
                .SubstateOf(State.C);

        sm.Configure(State.C)
                .Ignore(Trigger.X);

        sm.Fire(Trigger.X);

        assertEquals(State.B, sm.getState());
    }

    @Test
    public void PermittedTriggersIncludeSuperstatePermittedTriggers() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.Configure(State.A)
                .Permit(Trigger.Z, State.B);

        sm.Configure(State.B)
                .SubstateOf(State.C)
                .Permit(Trigger.X, State.A);

        sm.Configure(State.C)
                .Permit(Trigger.Y, State.A);

        List<Trigger> permitted = sm.getPermittedTriggers();

        assertTrue(permitted.contains(Trigger.X));
        assertTrue(permitted.contains(Trigger.Y));
        assertFalse(permitted.contains(Trigger.Z));
    }

    @Test
    public void PermittedTriggersAreDistinctValues() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.Configure(State.B)
                .SubstateOf(State.C)
                .Permit(Trigger.X, State.A);

        sm.Configure(State.C)
                .Permit(Trigger.X, State.B);

        List<Trigger> permitted = sm.getPermittedTriggers();
        assertEquals(1, permitted.size());
        assertEquals(Trigger.X, permitted.get(0));
    }

    @Test
    public void AcceptedTriggersRespectGuards() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.Configure(State.B)
                .PermitIf(Trigger.X, State.A, new Func<Boolean>() {

                    public Boolean call() {
                        return false;
                    }
                });

        assertEquals(0, sm.getPermittedTriggers().size());
    }

    @Test
    public void WhenDiscriminatedByGuard_ChoosesPermitedTransition() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        sm.Configure(State.B)
                .PermitIf(Trigger.X, State.A, IgnoredTriggerBehaviourTests.returnFalse)
                .PermitIf(Trigger.X, State.C, IgnoredTriggerBehaviourTests.returnTrue);

        sm.Fire(Trigger.X);

        assertEquals(State.C, sm.getState());
    }

    private void setFired() {
        fired = true;
    }

    @Test
    public void WhenTriggerIsIgnored_ActionsNotExecuted() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        fired = false;

        sm.Configure(State.B)
                .OnEntry(new Action() {


                    public void doIt() {
                        setFired();
                    }
                })
                .Ignore(Trigger.X);

        sm.Fire(Trigger.X);

        assertFalse(fired);
    }

    @Test
    public void IfSelfTransitionPermited_ActionsFire() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

        fired = false;

        sm.Configure(State.B)
                .OnEntry(new Action() {


                    public void doIt() {
                        setFired();
                    }
                })
                .PermitReentry(Trigger.X);

        sm.Fire(Trigger.X);

        assertTrue(fired);
    }

    @Test
    //[Test, ExpectedException(typeof(ArgumentException))]
    public void ImplicitReentryIsDisallowed() {
        try {

            StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

            sm.Configure(State.B)
                    .Permit(Trigger.X, State.B);
        } catch (Exception e) {

        }
    }

    @Test
//        [Test, ExpectedException(typeof(InvalidOperationException))]
    public void TriggerParametersAreImmutableOnceSet() throws Exception {
        try {
            StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);

            sm.SetTriggerParameters(Trigger.X, String.class, int.class);
            sm.SetTriggerParameters(Trigger.X, String.class);
            fail();
        } catch (Exception e) {

        }
    }

//        @Test
//        public void ParametersSuppliedToFireArePassedToEntryAction()
//        {
//        	StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.B);
//
//        	TriggerWithParameters2<String, Integer, State, Trigger> x = sm.SetTriggerParameters(Trigger.X, String.class, int.class);
//
//            sm.Configure(State.B)
//                .Permit(Trigger.X, State.C);
//
//
//            sm.Configure(State.C)
//                .OnEntryFrom(x, new Action2<String, int>() {
//                	public void doIt(String s, int i) {
//                		entryArgS = s;
//                        entryArgI = i;
//				}); 
//
//            var suppliedArgS = "something";
//            var suppliedArgI = 42;
//
//            sm.Fire(x, suppliedArgS, suppliedArgI);
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
//            sm.OnUnhandledTrigger((s, t) =>
//                                      {
//                                          state = s;
//                                          trigger = t;
//                                      });
//
//            sm.Fire(Trigger.Z);
//
//            AreEqual(State.B, state);
//            AreEqual(Trigger.Z, trigger);
//        }
}

