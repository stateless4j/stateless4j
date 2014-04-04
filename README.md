## Introduction ##
Create **state machines** and lightweight state machine-based workflows **directly in java code**:

```java
Action callStartTimer = new Action() {
        @Override
        public void doIt() {
                StartCallTimer();
        }
};
Action callStopTimer = new Action() {
        @Override
        public void doIt() {
                StopCallTimer();
        }
};
StateMachine<State, Trigger> phoneCall = new StateMachine<State, Trigger>(State.OffHook);

phoneCall.Configure(State.OffHook)
                  .Permit(Trigger.CallDialed, State.Ringing);

phoneCall.Configure(State.Ringing)
                  .Permit(Trigger.HungUp, State.OffHook)
                  .Permit(Trigger.CallConnected, State.Connected);

phoneCall.Configure(State.Connected)
                  .OnEntry(callStartTimer)
                  .OnExit(callStopTimer)
                  .Permit(Trigger.LeftMessage, State.OffHook)
                  .Permit(Trigger.HungUp, State.OffHook)
                  .Permit(Trigger.PlacedOnHold, State.OnHold);

// ...

phoneCall.Fire(Trigger.CallDialed);
Assert.assertEquals(State.Ringing, phoneCall.getState());
```

stateless4j is a port of [stateless](http://code.google.com/p/stateless/) for java

## Maven ##
```xml
    <dependency>
        <groupId>com.googlecode</groupId>
        <artifactId>stateless4j</artifactId>
        <version>1.0</version>
    </dependency>
```

## Features ##
Most standard state machine constructs are supported:

* Generic support for states and triggers of any java type (numbers, strings, enums, etc.)
* Hierarchical states
* Entry/exit events for states
* Guard clauses to support conditional transitions
* Introspection


Some useful extensions are also provided:
* Parameterised triggers
* Reentrant states


## Hierarchical States ##
In the example below, the `OnHold` state is a substate of the `Connected` state. This means that an `OnHold` call is
still connected.

```java
phoneCall.Configure(State.OnHold)
    .SubstateOf(State.Connected)
    .Permit(Trigger.TakenOffHold, State.Connected)
    .Permit(Trigger.HungUp, State.OffHook)
    .Permit(Trigger.PhoneHurledAgainstWall, State.PhoneDestroyed);
```

In addition to the `StateMachine.State` property, which will report the precise current state, an `IsInState(State)`
method is provided. `IsInState(State)` will take substates into account, so that if the example above was in the
`OnHold` state, `IsInState(State.Connected)` would also evaluate to `true`.

## Entry/Exit Events ##
In the example, the `StartCallTimer()` method will be executed when a call is connected. The `StopCallTimer()` will be
executed when call completes (by either hanging up or hurling the phone against the wall.)

The call can move between the `Connected` and `OnHold` states without the `StartCallTimer(`) and `StopCallTimer()`
methods being called repeatedly because the `OnHold` state is a substate of the `Connected` state.

Entry/Exit event handlers can be supplied with a parameter of type `Transition` that describes the trigger,
source and destination states.