Forked from [stateless4j](https://code.google.com/p/stateless4j/).

## Maven ##
```xml
    <dependency>
        <groupId>com.github.oxo42</groupId>
        <artifactId>stateless4j</artifactId>
        <version>2.4.0</version>
    </dependency>
```

## Introduction ##
Create **state machines** and lightweight state machine-based workflows **directly in java code**:

```java
Action callStartTimer = new Action() {
        @Override
        public void doIt() {
                startCallTimer();
        }
};
Action callStopTimer = new Action() {
        @Override
        public void doIt() {
                stopCallTimer();
        }
};
StateMachine<State, Trigger> phoneCall = new StateMachine<State, Trigger>(State.OffHook);

phoneCall.configure(State.OffHook)
                  .permit(Trigger.CallDialed, State.Ringing);

phoneCall.configure(State.Ringing)
                  .permit(Trigger.HungUp, State.OffHook)
                  .permit(Trigger.CallConnected, State.Connected);

phoneCall.configure(State.Connected)
                  .onEntry(callStartTimer)
                  .onExit(callStopTimer)
                  .permit(Trigger.LeftMessage, State.OffHook)
                  .permit(Trigger.HungUp, State.OffHook)
                  .permit(Trigger.PlacedOnHold, State.OnHold);

// ...

phoneCall.fire(Trigger.CallDialed);
assertEquals(State.Ringing, phoneCall.getState());
```

stateless4j is a port of [stateless](http://code.google.com/p/stateless/) for java


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
phoneCall.configure(State.OnHold)
    .substateOf(State.Connected)
    .permit(Trigger.TakenOffHold, State.Connected)
    .permit(Trigger.HungUp, State.OffHook)
    .permit(Trigger.PhoneHurledAgainstWall, State.PhoneDestroyed);
```

In addition to the `StateMachine.getState()` property, which will report the precise current state, an `isInState(State)`
method is provided. `isInState(State)` will take substates into account, so that if the example above was in the
`OnHold` state, `isInState(State.Connected)` would also evaluate to `true`.

## Entry/Exit Events ##
In the example, the `startCallTimer()` method will be executed when a call is connected. The `stopCallTimer()` will be
executed when call completes (by either hanging up or hurling the phone against the wall.)

The call can move between the `Connected` and `OnHold` states without the `startCallTimer(`) and `stopCallTimer()`
methods being called repeatedly because the `OnHold` state is a substate of the `Connected` state.

Entry/Exit event handlers can be supplied with a parameter of type `Transition` that describes the trigger,
source and destination states.