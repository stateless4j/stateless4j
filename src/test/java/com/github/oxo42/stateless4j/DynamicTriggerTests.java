package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Func;
import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import org.junit.Assert;
import org.junit.Test;

public class DynamicTriggerTests {
    @Test
    public void DestinationStateIsDynamic() {
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
    public void DestinationStateIsCalculatedBasedOnTriggerParameters() {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.A);
        TriggerWithParameters1<Integer, State, Trigger> trigger = sm.setTriggerParameters(
                Trigger.X, Integer.class);
        sm.configure(State.A).permitDynamic(trigger, new Func2<Integer, State>() {
            public State call(Integer i) {
                return i == 1 ? State.B : State.C;
            }
        });

        sm.fire(trigger, 1);

        Assert.assertEquals(State.B, sm.getState());
    }
}
