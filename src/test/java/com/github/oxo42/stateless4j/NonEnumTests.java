package com.github.oxo42.stateless4j;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NonEnumTests {

    public static final String StateA = "StateA";
    public static final String StateB = "StateB";
    public static final String StateC = "StateC";

    public static final String TriggerX = "TriggerX";
    public static final String TriggerY = "TriggerY";

    @Test
    public void CanUseReferenceTypeMarkers() {
        RunSimpleTest(
                new String[]{StateA, StateB, StateC},
                new String[]{TriggerX, TriggerY});
    }

    @Test
    public void CanUseValueTypeMarkers() {
        RunSimpleTest(State.values(), Trigger.values());
    }

    <S, T> void RunSimpleTest(S[] states, T[] transitions) {
        S a = states[0];
        S b = states[1];
        T x = transitions[0];

        StateMachine<S, T> sm = new StateMachine<S, T>(a);

        sm.configure(a)
                .permit(x, b);

        sm.fire(x);

        assertEquals(b, sm.getState());
    }

}
