package com.github.oxo42.stateless4j;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NonEnumTests {

  public static final String StateA = "StateA";
  public static final String StateB = "StateB";
  public static final String StateC = "StateC";

  public static final String TriggerX =  "TriggerX";
  public static final String TriggerY =  "TriggerY";


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

  <TState, TTransition> void RunSimpleTest(TState[] states, TTransition[] transitions) {
    TState a = states[0];
    TState b = states[1];
    TTransition x = transitions[0];

    StateMachine<TState, TTransition> sm = new StateMachine<TState, TTransition>(a);

    sm.configure(a)
        .permit(x, b);

    sm.fire(x);

    assertEquals(b, sm.getState());
  }

}
