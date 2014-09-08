package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Func;
import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DynamicTriggerTests {

    @Test
    public void DestinationStateIsDynamic() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();
        config.configure(State.A).permitDynamic(Trigger.X, new Func<State>() {

            @Override
            public State call() {
                return State.B;
            }
        });

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(Trigger.X);

        assertEquals(State.B, sm.getState());
    }

    @Test
    public void DestinationStateIsCalculatedBasedOnTriggerParameters() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();
        TriggerWithParameters1<Integer, State, Trigger> trigger = config.setTriggerParameters(
                Trigger.X, Integer.class);
        config.configure(State.A).permitDynamic(trigger, new Func2<Integer, State>() {
            @Override
            public State call(Integer i) {
                return i == 1 ? State.B : State.C;
            }
        });

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(trigger, 1);

        assertEquals(State.B, sm.getState());
    }
}
