package com.googlecode.stateless4j;

import com.googlecode.stateless4j.delegates.Action1;
import com.googlecode.stateless4j.delegates.Action2;
import com.googlecode.stateless4j.transitions.Transition;
import com.googlecode.stateless4j.triggers.IgnoredTriggerBehaviour;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class StateRepresentationTests {
    Transition<State, Trigger> actualTransition = null;
    Boolean executed = false;
    int order = 0, subOrder = 0, superOrder = 0;

    @Test
    public void UponEntering_EnteringActionsExecuted() throws Exception {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        Transition<State, Trigger>
                transition = new Transition<State, Trigger>(State.A, State.B, Trigger.X);
        actualTransition = null;
        stateRepresentation.AddEntryAction(new Action2<Transition<State, Trigger>, Object[]>() {


            public void doIt(Transition<State, Trigger> t, Object[] a)
                    throws Exception {
                actualTransition = t;
            }
        });
        stateRepresentation.Enter(transition);
        Assert.assertEquals(transition, actualTransition);
    }

    @Test
    public void UponLeaving_EnteringActionsNotExecuted() throws Exception {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        Transition<State, Trigger>
                transition = new Transition<State, Trigger>(State.A, State.B, Trigger.X);
        actualTransition = null;
        stateRepresentation.AddEntryAction(new Action2<Transition<State, Trigger>, Object[]>() {


            public void doIt(Transition<State, Trigger> t, Object[] a)
                    throws Exception {
                actualTransition = t;
            }
        });
        stateRepresentation.Exit(transition);
        Assert.assertNull(actualTransition);
    }

    @Test
    public void UponLeaving_LeavingActionsExecuted() throws Exception {

        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.A);
        Transition<State, Trigger>
                transition = new Transition<State, Trigger>(State.A, State.B, Trigger.X);
        actualTransition = null;
        stateRepresentation.AddExitAction(new Action1<Transition<State, Trigger>>() {

            public void doIt(Transition<State, Trigger> t) {
                actualTransition = t;
            }
        });
        stateRepresentation.Exit(transition);
        Assert.assertEquals(transition, actualTransition);
    }

    @Test
    public void UponEntering_LeavingActionsNotExecuted() throws Exception {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.A);
        Transition<State, Trigger>
                transition = new Transition<State, Trigger>(State.A, State.B, Trigger.X);
        actualTransition = null;
        stateRepresentation.AddExitAction(new Action1<Transition<State, Trigger>>() {

            public void doIt(Transition<State, Trigger> t) {
                actualTransition = t;
            }
        });
        stateRepresentation.Enter(transition);
        Assert.assertNull(actualTransition);
    }

    @Test
    public void IncludesUnderlyingState() {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        Assert.assertTrue(stateRepresentation.Includes(State.B));
    }

    @Test
    public void DoesNotIncludeUnrelatedState() {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        Assert.assertFalse(stateRepresentation.Includes(State.C));
    }

    @Test
    public void IncludesSubstate() throws Exception {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        stateRepresentation.AddSubstate(CreateRepresentation(State.C));
        Assert.assertTrue(stateRepresentation.Includes(State.C));
    }

    @Test
    public void DoesNotIncludeSuperstate() {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        stateRepresentation.setSuperstate(CreateRepresentation(State.C));
        Assert.assertFalse(stateRepresentation.Includes(State.C));
    }

    @Test
    public void IsIncludedInUnderlyingState() {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        Assert.assertTrue(stateRepresentation.IsIncludedIn(State.B));
    }

    @Test
    public void IsNotIncludedInUnrelatedState() {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        Assert.assertFalse(stateRepresentation.IsIncludedIn(State.C));
    }

    @Test
    public void IsNotIncludedInSubstate() throws Exception {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        stateRepresentation.AddSubstate(CreateRepresentation(State.C));
        Assert.assertFalse(stateRepresentation.IsIncludedIn(State.C));
    }

    @Test
    public void IsIncludedInSuperstate() {
        StateRepresentation<State, Trigger> stateRepresentation = CreateRepresentation(State.B);
        stateRepresentation.setSuperstate(CreateRepresentation(State.C));
        Assert.assertTrue(stateRepresentation.IsIncludedIn(State.C));
    }

    @Test
    public void WhenTransitioningFromSubToSuperstate_SubstateEntryActionsExecuted() throws Exception {
        StateRepresentation<State, Trigger> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger> sub = CreateRepresentation(State.B);
        superState.AddSubstate(sub);
        sub.setSuperstate(superState);

        sub.AddEntryAction(new Action2<Transition<State, Trigger>, Object[]>() {


            public void doIt(Transition<State, Trigger> t, Object[] a)
                    throws Exception {
                executed = true;
            }
        });
        Transition<State, Trigger> transition = new Transition<State, Trigger>(superState.getUnderlyingState(), sub.getUnderlyingState(), Trigger.X);
        sub.Enter(transition);
        Assert.assertTrue(executed);
    }

    @Test
    public void WhenTransitioningFromSubToSuperstate_SubstateExitActionsExecuted() throws Exception {
        StateRepresentation<State, Trigger> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger> sub = CreateRepresentation(State.B);
        superState.AddSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        sub.AddExitAction(new Action1<Transition<State, Trigger>>() {


            public void doIt(Transition<State, Trigger> arg1) {
                executed = true;
            }
        });
        Transition<State, Trigger> transition = new Transition<State, Trigger>(sub.getUnderlyingState(), superState.getUnderlyingState(), Trigger.X);
        sub.Exit(transition);
        Assert.assertTrue(executed);
    }

    @Test
    public void WhenTransitioningToSuperFromSubstate_SuperEntryActionsNotExecuted() throws Exception {
        StateRepresentation<State, Trigger> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger> sub = CreateRepresentation(State.B);
        superState.AddSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        superState.AddEntryAction(new Action2<Transition<State, Trigger>, Object[]>() {


            public void doIt(Transition<State, Trigger> t, Object[] a)
                    throws Exception {
                executed = true;
            }
        });
        Transition<State, Trigger> transition = new Transition<State, Trigger>(superState.getUnderlyingState(), sub.getUnderlyingState(), Trigger.X);
        superState.Enter(transition);
        Assert.assertFalse(executed);
    }

    @Test
    public void WhenTransitioningFromSuperToSubstate_SuperExitActionsNotExecuted() throws Exception {
        StateRepresentation<State, Trigger> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger> sub = CreateRepresentation(State.B);
        superState.AddSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        superState.AddExitAction(new Action1<Transition<State, Trigger>>() {


            public void doIt(Transition<State, Trigger> arg1) {
                executed = true;
            }
        });
        Transition<State, Trigger> transition = new Transition<State, Trigger>(superState.getUnderlyingState(), sub.getUnderlyingState(), Trigger.X);
        superState.Exit(transition);
        Assert.assertFalse(executed);
    }

    @Test
    public void WhenEnteringSubstate_SuperEntryActionsExecuted() throws Exception {
        StateRepresentation<State, Trigger> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger> sub = CreateRepresentation(State.B);
        superState.AddSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        superState.AddEntryAction(new Action2<Transition<State, Trigger>, Object[]>() {


            public void doIt(Transition<State, Trigger> t, Object[] a)
                    throws Exception {
                executed = true;
            }
        });
        Transition<State, Trigger> transition = new Transition<State, Trigger>(State.C, sub.getUnderlyingState(), Trigger.X);
        sub.Enter(transition);
        Assert.assertTrue(executed);
    }

    @Test
    public void WhenLeavingSubstate_SuperExitActionsExecuted() throws Exception {
        StateRepresentation<State, Trigger> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger> sub = CreateRepresentation(State.B);
        superState.AddSubstate(sub);
        sub.setSuperstate(superState);

        executed = false;
        superState.AddExitAction(new Action1<Transition<State, Trigger>>() {


            public void doIt(Transition<State, Trigger> arg1) {
                executed = true;
            }
        });
        Transition<State, Trigger> transition = new Transition<State, Trigger>(sub.getUnderlyingState(), State.C, Trigger.X);
        sub.Exit(transition);
        Assert.assertTrue(executed);
    }

    @Test
    public void EntryActionsExecuteInOrder() throws Exception {
        final ArrayList<Integer> actual = new ArrayList<Integer>();

        StateRepresentation<State, Trigger> rep = CreateRepresentation(State.B);
        rep.AddEntryAction(new Action2<Transition<State, Trigger>, Object[]>() {


            public void doIt(Transition<State, Trigger> arg1, Object[] arg2)
                    throws Exception {
                actual.add(0);

            }
        });
        rep.AddEntryAction(new Action2<Transition<State, Trigger>, Object[]>() {


            public void doIt(Transition<State, Trigger> arg1, Object[] arg2)
                    throws Exception {
                actual.add(1);

            }
        });

        rep.Enter(new Transition<State, Trigger>(State.A, State.B, Trigger.X));

        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(0, actual.get(0).intValue());
        Assert.assertEquals(1, actual.get(1).intValue());
    }

    @Test
    public void ExitActionsExecuteInOrder() throws Exception {
        final List<Integer> actual = new ArrayList<Integer>();

        StateRepresentation<State, Trigger> rep = CreateRepresentation(State.B);
        rep.AddExitAction(new Action1<Transition<State, Trigger>>() {


            public void doIt(Transition<State, Trigger> arg1) {
                actual.add(0);
            }
        });
        rep.AddExitAction(new Action1<Transition<State, Trigger>>() {


            public void doIt(Transition<State, Trigger> arg1) {
                actual.add(1);
            }
        });

        rep.Exit(new Transition<State, Trigger>(State.B, State.C, Trigger.X));

        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(0, actual.get(0).intValue());
        Assert.assertEquals(1, actual.get(1).intValue());
    }

    @Test
    public void WhenTransitionExists_TriggerCanBeFired() {
        StateRepresentation<State, Trigger> rep = CreateRepresentation(State.B);
        Assert.assertFalse(rep.CanHandle(Trigger.X));
    }

    @Test
    public void WhenTransitionDoesNotExist_TriggerCannotBeFired() {
        StateRepresentation<State, Trigger> rep = CreateRepresentation(State.B);
        rep.AddTriggerBehaviour(new IgnoredTriggerBehaviour<State, Trigger>(Trigger.X, IgnoredTriggerBehaviourTests.returnTrue));
        Assert.assertTrue(rep.CanHandle(Trigger.X));
    }

    @Test
    public void WhenTransitionExistsInSupersate_TriggerCanBeFired() throws Exception {
        StateRepresentation<State, Trigger> rep = CreateRepresentation(State.B);
        rep.AddTriggerBehaviour(new IgnoredTriggerBehaviour<State, Trigger>(Trigger.X, IgnoredTriggerBehaviourTests.returnTrue));
        StateRepresentation<State, Trigger> sub = CreateRepresentation(State.C);
        sub.setSuperstate(rep);
        rep.AddSubstate(sub);
        Assert.assertTrue(sub.CanHandle(Trigger.X));
    }

    @Test
    public void WhenEnteringSubstate_SuperstateEntryActionsExecuteBeforeSubstate() throws Exception {
        StateRepresentation<State, Trigger> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger> sub = CreateRepresentation(State.B);
        superState.AddSubstate(sub);
        sub.setSuperstate(superState);

        order = 0;
        subOrder = 0;
        superOrder = 0;
        superState.AddEntryAction(new Action2<Transition<State, Trigger>, Object[]>() {


            public void doIt(Transition<State, Trigger> arg1, Object[] arg2)
                    throws Exception {
                superOrder = order++;
            }
        });
        sub.AddEntryAction(new Action2<Transition<State, Trigger>, Object[]>() {


            public void doIt(Transition<State, Trigger> arg1, Object[] arg2)
                    throws Exception {
                subOrder = order++;
            }
        });
        Transition<State, Trigger> transition = new Transition<State, Trigger>(State.C, sub.getUnderlyingState(), Trigger.X);
        sub.Enter(transition);
        Assert.assertTrue(superOrder < subOrder);
    }

    @Test
    public void WhenExitingSubstate_SubstateEntryActionsExecuteBeforeSuperstate() throws Exception {
        StateRepresentation<State, Trigger> superState = CreateRepresentation(State.A);
        StateRepresentation<State, Trigger> sub = CreateRepresentation(State.B);
        superState.AddSubstate(sub);
        sub.setSuperstate(superState);

        order = 0;
        subOrder = 0;
        superOrder = 0;
        superState.AddExitAction(new Action1<Transition<State, Trigger>>() {


            public void doIt(Transition<State, Trigger> arg1) {
                superOrder = order++;
            }
        });
        sub.AddExitAction(new Action1<Transition<State, Trigger>>() {


            public void doIt(Transition<State, Trigger> arg1) {
                subOrder = order++;
            }
        });
        Transition<State, Trigger> transition = new Transition<State, Trigger>(sub.getUnderlyingState(), State.C, Trigger.X);
        sub.Exit(transition);
        Assert.assertTrue(subOrder < superOrder);
    }


    StateRepresentation<State, Trigger> CreateRepresentation(State state) {
        return new StateRepresentation<State, Trigger>(state);
    }
}
