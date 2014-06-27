package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.*;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import com.github.oxo42.stateless4j.triggers.*;
import com.github.oxo42.stateless4j.validation.Enforce;

public class StateConfiguration<TState , TTrigger > {
    private static final FuncBoolean NO_GUARD = new FuncBoolean() {
        @Override
        public boolean call() {
            return true;
        }
    };
    private final StateRepresentation<TState, TTrigger> representation;
    private final Func2<TState, StateRepresentation<TState, TTrigger>> lookup;

    public StateConfiguration(StateRepresentation<TState, TTrigger> representation, Func2<TState, StateRepresentation<TState, TTrigger>> lookup) {
        this.representation = Enforce.argumentNotNull(representation, "representation");
        this.lookup = Enforce.argumentNotNull(lookup, "lookup");
    }


    /**
     * Accept the specified trigger and transition to the destination state
     *
     * @param trigger          The accepted trigger
     * @param destinationState The state that the trigger will cause a transition to
     * @return The reciever
     */
    public StateConfiguration<TState, TTrigger> permit(TTrigger trigger, TState destinationState) {
        enforceNotIdentityTransition(destinationState);
        return publicPermit(trigger, destinationState);
    }


    /**
     * Accept the specified trigger and transition to the destination state
     *
     * @param trigger          The accepted trigger
     * @param destinationState The state that the trigger will cause a transition to
     * @param guard            Function that must return true in order for the trigger to be accepted
     * @return The reciever
     */
    public StateConfiguration<TState, TTrigger> permitIf(TTrigger trigger, TState destinationState, FuncBoolean guard) {
        enforceNotIdentityTransition(destinationState);
        return publicPermitIf(trigger, destinationState, guard);
    }


    /**
     * Accept the specified trigger, execute exit actions and re-execute entry actions. Reentry behaves as though the
     * configured state transitions to an identical sibling state
     * <p/>
     * Applies to the current state only. Will not re-execute superstate actions, or  cause actions to execute
     * transitioning between super- and sub-states
     *
     * @param trigger The accepted trigger
     * @return The reciever
     */
    public StateConfiguration<TState, TTrigger> permitReentry(TTrigger trigger) {
        return publicPermit(trigger, representation.getUnderlyingState());
    }


    /**
     * Accept the specified trigger, execute exit actions and re-execute entry actions. Reentry behaves as though the
     * configured state transitions to an identical sibling state
     * <p/>
     * Applies to the current state only. Will not re-execute superstate actions, or  cause actions to execute
     * transitioning between super- and sub-states
     *
     * @param trigger The accepted trigger
     * @param guard   Function that must return true in order for the trigger to be accepted
     * @return The reciever
     */
    public StateConfiguration<TState, TTrigger> permitReentryIf(TTrigger trigger, FuncBoolean guard) {
        return publicPermitIf(trigger, representation.getUnderlyingState(), guard);
    }


    /**
     * ignore the specified trigger when in the configured state
     *
     * @param trigger The trigger to ignore
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> ignore(TTrigger trigger) {
        return ignoreIf(trigger, NO_GUARD);
    }


    /**
     * ignore the specified trigger when in the configured state, if the guard returns true
     *
     * @param trigger The trigger to ignore
     * @param guard   Function that must return true in order for the trigger to be ignored
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> ignoreIf(TTrigger trigger, FuncBoolean guard) {
        Enforce.argumentNotNull(guard, "guard");
        representation.addTriggerBehaviour(new IgnoredTriggerBehaviour<TState, TTrigger>(trigger, guard));
        return this;
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param entryAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> onEntry(final Action entryAction) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        return onEntry(new Action1<Transition<TState, TTrigger>>() {
            public void doIt(Transition<TState, TTrigger> t) {
                entryAction.doIt();
            }
        });
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param entryAction Action to execute, providing details of the transition
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> onEntry(final Action1<Transition<TState, TTrigger>> entryAction) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        representation.addEntryAction(new Action2<Transition<TState, TTrigger>, Object[]>() {
            public void doIt(Transition<TState, TTrigger> arg1, Object[] arg2) {
                entryAction.doIt(arg1);
            }
        });
        return this;
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> onEntryFrom(TTrigger trigger, final Action entryAction) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        return onEntryFrom(trigger, new Action1<Transition<TState, TTrigger>>() {
            public void doIt(Transition<TState, TTrigger> arg1) {
                entryAction.doIt();
            }
        });
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> onEntryFrom(TTrigger trigger, final Action1<Transition<TState, TTrigger>> entryAction) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        representation.addEntryAction(trigger, new Action2<Transition<TState, TTrigger>, Object[]>() {
            public void doIt(Transition<TState, TTrigger> arg1, Object[] arg2) {
                entryAction.doIt(arg1);
            }
        });
        return this;
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @return The receiver
     */
    public <TArg0> StateConfiguration<TState, TTrigger> onEntryFrom(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, final Action1<TArg0> entryAction, final Class<TArg0> classe0) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        return onEntryFrom(trigger, new Action2<TArg0, Transition<TState, TTrigger>>() {
            public void doIt(TArg0 arg1, Transition<TState, TTrigger> arg2) {
                entryAction.doIt(arg1);
            }
        }, classe0);
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @return The receiver
     */
    public <TArg0> StateConfiguration<TState, TTrigger> onEntryFrom(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, final Action2<TArg0, Transition<TState, TTrigger>> entryAction, final Class<TArg0> classe0) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        Enforce.argumentNotNull(trigger, "trigger");
        representation.addEntryAction(trigger.getTrigger(), new Action2<Transition<TState, TTrigger>, Object[]>() {
            @SuppressWarnings("unchecked")
            public void doIt(Transition<TState, TTrigger> t, Object[] arg2) {
                entryAction.doIt((TArg0) arg2[0], t);
            }
        });
        return this;
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param classe1     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @param <TArg1>     Type of the second trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1> StateConfiguration<TState, TTrigger> onEntryFrom(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger, final Action2<TArg0, TArg1> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        return onEntryFrom(trigger, new Action3<TArg0, TArg1, Transition<TState, TTrigger>>() {
            public void doIt(TArg0 a0, TArg1 a1, Transition<TState, TTrigger> t) {
                entryAction.doIt(a0, a1);
            }
        }, classe0, classe1);
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param classe1     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @param <TArg1>     Type of the second trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1> StateConfiguration<TState, TTrigger> onEntryFrom(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger, final Action3<TArg0, TArg1, Transition<TState, TTrigger>> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        Enforce.argumentNotNull(trigger, "trigger");
        representation.addEntryAction(trigger.getTrigger(), new Action2<Transition<TState, TTrigger>, Object[]>() {
            @SuppressWarnings("unchecked")
            public void doIt(Transition<TState, TTrigger> t, Object[] args) {
                entryAction.doIt(
                        (TArg0) args[0],
                        (TArg1) args[1], t);
            }
        });
        return this;
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param classe1     Class argument
     * @param classe2     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @param <TArg1>     Type of the second trigger argument
     * @param <TArg2>     Type of the third trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> onEntryFrom(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, final Action3<TArg0, TArg1, TArg2> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1, final Class<TArg2> classe2) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        return onEntryFrom(trigger, new Action4<TArg0, TArg1, TArg2, Transition<TState, TTrigger>>() {
            public void doIt(TArg0 a0, TArg1 a1, TArg2 a2, Transition<TState, TTrigger> t) {
                entryAction.doIt(a0, a1, a2);
            }
        }, classe0, classe1, classe2);
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param trigger     The trigger by which the state must be entered in order for the action to execute
     * @param entryAction Action to execute, providing details of the transition
     * @param classe0     Class argument
     * @param classe1     Class argument
     * @param classe2     Class argument
     * @param <TArg0>     Type of the first trigger argument
     * @param <TArg1>     Type of the second trigger argument
     * @param <TArg2>     Type of the third trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> onEntryFrom(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, final Action4<TArg0, TArg1, TArg2, Transition<TState, TTrigger>> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1, final Class<TArg2> classe2) {
        Enforce.argumentNotNull(entryAction, "entryAction");
        Enforce.argumentNotNull(trigger, "trigger");
        representation.addEntryAction(trigger.getTrigger(), new Action2<Transition<TState, TTrigger>, Object[]>() {
            @SuppressWarnings("unchecked")
            public void doIt(Transition<TState, TTrigger> t, Object[] args) {
                entryAction.doIt(
                        (TArg0) args[0],
                        (TArg1) args[1],
                        (TArg2) args[2], t);
            }
        });
        return this;
    }


    /**
     * Specify an action that will execute when transitioning from the configured state
     *
     * @param exitAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> onExit(final Action exitAction) {
        Enforce.argumentNotNull(exitAction, "exitAction");
        return onExit(new Action1<Transition<TState, TTrigger>>() {
            public void doIt(Transition<TState, TTrigger> arg1) {
                exitAction.doIt();
            }
        });
    }


    /**
     * Specify an action that will execute when transitioning from the configured state
     *
     * @param exitAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> onExit(Action1<Transition<TState, TTrigger>> exitAction) {
        Enforce.argumentNotNull(exitAction, "exitAction");
        representation.addExitAction(exitAction);
        return this;
    }


    /**
     * Sets the superstate that the configured state is a substate of
     * <p/>
     * Substates inherit the allowed transitions of their superstate.
     * When entering directly into a substate from outside of the superstate,
     * entry actions for the superstate are executed.
     * Likewise when leaving from the substate to outside the supserstate,
     * exit actions for the superstate will execute.
     *
     * @param superstate The superstate
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> substateOf(TState superstate) {
        StateRepresentation<TState, TTrigger> superRepresentation = lookup.call(superstate);
        representation.setSuperstate(superRepresentation);
        superRepresentation.addSubstate(representation);
        return this;
    }


    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @return The reciever
     */
    public StateConfiguration<TState, TTrigger> permitDynamic(TTrigger trigger, final Func<TState> destinationStateSelector) {
        return permitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }


    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param <TArg0>                  Type of the first trigger argument
     * @return The receiver
     */
    public <TArg0> StateConfiguration<TState, TTrigger> permitDynamic(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, Func2<TArg0, TState> destinationStateSelector) {
        return permitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }


    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param <TArg0>                  Type of the first trigger argument
     * @param <TArg1>                  Type of the second trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1> StateConfiguration<TState, TTrigger> permitDynamic(
            TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger,
            Func3<TArg0, TArg1, TState> destinationStateSelector) {
        return permitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param <TArg0>                  Type of the first trigger argument
     * @param <TArg1>                  Type of the second trigger argument
     * @param <TArg2>                  Type of the third trigger argument
     * @return The receiver
     */
    public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> permitDynamic(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, final Func4<TArg0, TArg1, TArg2, TState> destinationStateSelector) {
        return permitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }


    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param guard                    Function that must return true in order for the  trigger to be accepted
     * @return The reciever
     */
    public StateConfiguration<TState, TTrigger> permitDynamicIf(TTrigger trigger, final Func<TState> destinationStateSelector, FuncBoolean guard) {
        Enforce.argumentNotNull(destinationStateSelector, "destinationStateSelector");
        return publicPermitDynamicIf(trigger, new Func2<Object[], TState>() {
            public TState call(Object[] arg0) {
                return destinationStateSelector.call();
            }
        }, guard);
    }


    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param guard                    Function that must return true in order for the  trigger to be accepted
     * @param <TArg0>                  Type of the first trigger argument
     * @return The reciever
     */
    public <TArg0> StateConfiguration<TState, TTrigger> permitDynamicIf(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, final Func2<TArg0, TState> destinationStateSelector, FuncBoolean guard) {
        Enforce.argumentNotNull(trigger, "trigger");
        Enforce.argumentNotNull(destinationStateSelector, "destinationStateSelector");
        return publicPermitDynamicIf(
                trigger.getTrigger(), new Func2<Object[], TState>() {
                    @SuppressWarnings("unchecked")
                    public TState call(Object[] args) {
                        return destinationStateSelector.call((TArg0) args[0]);

                    }
                },
                guard
        );
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param guard                    Function that must return true in order for the  trigger to be accepted
     * @param <TArg0>                  Type of the first trigger argument
     * @param <TArg1>                  Type of the second trigger argument
     * @return The reciever
     */
    public <TArg0, TArg1> StateConfiguration<TState, TTrigger> permitDynamicIf(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger, final Func3<TArg0, TArg1, TState> destinationStateSelector, FuncBoolean guard) {
        Enforce.argumentNotNull(trigger, "trigger");
        Enforce.argumentNotNull(destinationStateSelector, "destinationStateSelector");
        return publicPermitDynamicIf(
                trigger.getTrigger(), new Func2<Object[], TState>() {
                    @SuppressWarnings("unchecked")

                    public TState call(Object[] args) {
                        return destinationStateSelector.call(
                                (TArg0) args[0],
                                (TArg1) args[1]);
                    }
                },
                guard
        );
    }

    /**
     * Accept the specified trigger and transition to the destination state, calculated dynamically by the supplied
     * function
     *
     * @param trigger                  The accepted trigger
     * @param destinationStateSelector Function to calculate the state that the trigger will cause a transition to
     * @param guard                    Function that must return true in order for the  trigger to be accepted
     * @param <TArg0>                  Type of the first trigger argument
     * @param <TArg1>                  Type of the second trigger argument
     * @param <TArg2>                  Type of the third trigger argument
     * @return The reciever
     */
    public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> permitDynamicIf(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, final Func4<TArg0, TArg1, TArg2, TState> destinationStateSelector, FuncBoolean guard) {
        Enforce.argumentNotNull(trigger, "trigger");
        Enforce.argumentNotNull(destinationStateSelector, "destinationStateSelector");
        return publicPermitDynamicIf(
                trigger.getTrigger(), new Func2<Object[], TState>() {
                    @SuppressWarnings("unchecked")

                    public TState call(Object[] args) {
                        return destinationStateSelector.call(
                                (TArg0) args[0],
                                (TArg1) args[1],
                                (TArg2) args[2]
                        );
                    }
                }, guard
        );
    }

    void enforceNotIdentityTransition(TState destination) {
        if (destination.equals(representation.getUnderlyingState())) {
            throw new IllegalStateException("Permit() (and PermitIf()) require that the destination state is not equal to the source state. To accept a trigger without changing state, use either Ignore() or PermitReentry().");
        }
    }

    StateConfiguration<TState, TTrigger> publicPermit(TTrigger trigger, TState destinationState) {
        return publicPermitIf(trigger, destinationState, NO_GUARD);
    }

    StateConfiguration<TState, TTrigger> publicPermitIf(TTrigger trigger, TState destinationState, FuncBoolean guard) {
        Enforce.argumentNotNull(guard, "guard");
        representation.addTriggerBehaviour(new TransitioningTriggerBehaviour<>(trigger, destinationState, guard));
        return this;
    }

    StateConfiguration<TState, TTrigger> publicPermitDynamic(TTrigger trigger, Func2<Object[], TState> destinationStateSelector) {
        return publicPermitDynamicIf(trigger, destinationStateSelector, NO_GUARD);
    }

    StateConfiguration<TState, TTrigger> publicPermitDynamicIf(TTrigger trigger, Func2<Object[], TState> destinationStateSelector, FuncBoolean guard) {
        Enforce.argumentNotNull(destinationStateSelector, "destinationStateSelector");
        Enforce.argumentNotNull(guard, "guard");
        representation.addTriggerBehaviour(new DynamicTriggerBehaviour<>(trigger, destinationStateSelector, guard));
        return this;
    }
}
