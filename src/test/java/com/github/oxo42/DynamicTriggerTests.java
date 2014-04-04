package com.github.oxo42;

import com.github.oxo42.stateless.StateMachine;
import com.github.oxo42.stateless.delegates.Func;
import com.github.oxo42.stateless.delegates.Func2;
import com.github.oxo42.stateless.triggers.TriggerWithParameters1;
import org.junit.Assert;
import org.junit.Test;

public class DynamicTriggerTests {
    @Test
    public void DestinationStateIsDynamic() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.A);
        sm.configure(State.A).permitDynamic(Trigger.X, new Func<State>() {

            public State call() {
                return State.B;
            }
        });

        sm.fire(Trigger.X);

        Assert.assertEquals(State.B, sm.getState());
    }

    @Test
    public void DestinationStateIsCalculatedBasedOnTriggerParameters() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.A);
        TriggerWithParameters1<Integer, State, Trigger> trigger = sm.setTriggerParameters(
                Trigger.X, Integer.class);
        sm.configure(State.A).permitDynamic(trigger, new Func2<Integer, State>() {
            public State call(Integer i) throws Exception {
                return i == 1 ? State.B : State.C;
            }
        });

        sm.fire(trigger, 1);

        Assert.assertEquals(State.B, sm.getState());
    }
}
