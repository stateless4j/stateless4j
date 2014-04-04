package com.googlecode.stateless4j;

import com.googlecode.stateless4j.delegates.Action;
import com.googlecode.stateless4j.delegates.Action1;
import com.googlecode.stateless4j.delegates.Action2;
import com.googlecode.stateless4j.delegates.Action3;
import com.googlecode.stateless4j.delegates.Action4;
import com.googlecode.stateless4j.delegates.Func;
import com.googlecode.stateless4j.delegates.Func2;
import com.googlecode.stateless4j.delegates.Func3;
import com.googlecode.stateless4j.delegates.Func4;
import com.googlecode.stateless4j.resources.StateConfigurationResources;
import com.googlecode.stateless4j.transitions.Transition;
import com.googlecode.stateless4j.transitions.TransitioningTriggerBehaviour;
import com.googlecode.stateless4j.triggers.DynamicTriggerBehaviour;
import com.googlecode.stateless4j.triggers.IgnoredTriggerBehaviour;
import com.googlecode.stateless4j.triggers.TriggerWithParameters1;
import com.googlecode.stateless4j.triggers.TriggerWithParameters2;
import com.googlecode.stateless4j.triggers.TriggerWithParameters3;
import com.googlecode.stateless4j.validation.Enforce;

public class StateConfiguration<TState, TTrigger> {
    final StateRepresentation<TState, TTrigger> _representation;
    final Func2<TState, StateRepresentation<TState, TTrigger>> _lookup;
    final Func<Boolean> NoGuard = new Func<Boolean>() {


        public Boolean call() {
            return true;
        }
    };

    public StateConfiguration(StateRepresentation<TState, TTrigger> representation, Func2<TState, StateRepresentation<TState, TTrigger>> lookup) throws Exception {
        _representation = Enforce.ArgumentNotNull(representation, "representation");
        _lookup = Enforce.ArgumentNotNull(lookup, "lookup");
    }


    /**
     * Accept the specified trigger and transition to the destination state
     *
     * @param trigger          The accepted trigger
     * @param destinationState The state that the trigger will cause a transition to
     * @return The reciever
     */
    public StateConfiguration<TState, TTrigger> Permit(TTrigger trigger, TState destinationState) throws Exception {
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
    public StateConfiguration<TState, TTrigger> PermitIf(TTrigger trigger, TState destinationState, Func<Boolean> guard) throws Exception {
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
    public StateConfiguration<TState, TTrigger> PermitReentry(TTrigger trigger) throws Exception {
        return publicPermit(trigger, _representation.getUnderlyingState());
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
    public StateConfiguration<TState, TTrigger> PermitReentryIf(TTrigger trigger, Func<Boolean> guard) throws Exception {
        return publicPermitIf(trigger, _representation.getUnderlyingState(), guard);
    }


    /**
     * Ignore the specified trigger when in the configured state
     *
     * @param trigger The trigger to ignore
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> Ignore(TTrigger trigger) throws Exception {
        return IgnoreIf(trigger, NoGuard);
    }


    /**
     * Ignore the specified trigger when in the configured state, if the guard returns true
     *
     * @param trigger The trigger to ignore
     * @param guard   Function that must return true in order for the trigger to be ignored
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> IgnoreIf(TTrigger trigger, Func<Boolean> guard) throws Exception {
        Enforce.ArgumentNotNull(guard, "guard");
        _representation.AddTriggerBehaviour(new IgnoredTriggerBehaviour<TState, TTrigger>(trigger, guard));
        return this;
    }


    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param entryAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<TState, TTrigger> OnEntry(final Action entryAction) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        return OnEntry(new Action1<Transition<TState, TTrigger>>() {
            public void doIt(Transition<TState, TTrigger> t) throws Exception {
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
    public StateConfiguration<TState, TTrigger> OnEntry(final Action1<Transition<TState, TTrigger>> entryAction) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        _representation.AddEntryAction(new Action2<Transition<TState, TTrigger>, Object[]>() {
            public void doIt(Transition<TState, TTrigger> arg1, Object[] arg2) throws Exception {
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
    public StateConfiguration<TState, TTrigger> OnEntryFrom(TTrigger trigger, final Action entryAction) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        return OnEntryFrom(trigger, new Action1<Transition<TState, TTrigger>>() {
            public void doIt(Transition<TState, TTrigger> arg1) throws Exception {
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
    public StateConfiguration<TState, TTrigger> OnEntryFrom(TTrigger trigger, final Action1<Transition<TState, TTrigger>> entryAction) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        _representation.AddEntryAction(trigger, new Action2<Transition<TState, TTrigger>, Object[]>() {
            public void doIt(Transition<TState, TTrigger> arg1, Object[] arg2) throws Exception {
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
    public <TArg0> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, final Action1<TArg0> entryAction, final Class<TArg0> classe0) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        return OnEntryFrom(trigger, new Action2<TArg0, Transition<TState, TTrigger>>() {
            public void doIt(TArg0 arg1, Transition<TState, TTrigger> arg2) throws Exception {
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
    public <TArg0> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, final Action2<TArg0, Transition<TState, TTrigger>> entryAction, final Class<TArg0> classe0) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        Enforce.ArgumentNotNull(trigger, "trigger");
        _representation.AddEntryAction(trigger.getTrigger(), new Action2<Transition<TState, TTrigger>, Object[]>() {
            @SuppressWarnings("unchecked")
            public void doIt(Transition<TState, TTrigger> t, Object[] arg2) throws Exception {
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
    public <TArg0, TArg1> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger, final Action2<TArg0, TArg1> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        return OnEntryFrom(trigger, new Action3<TArg0, TArg1, Transition<TState, TTrigger>>() {
            public void doIt(TArg0 a0, TArg1 a1, Transition<TState, TTrigger> t) throws Exception {
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
    public <TArg0, TArg1> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger, final Action3<TArg0, TArg1, Transition<TState, TTrigger>> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        Enforce.ArgumentNotNull(trigger, "trigger");
        _representation.AddEntryAction(trigger.getTrigger(), new Action2<Transition<TState, TTrigger>, Object[]>() {
            @SuppressWarnings("unchecked")
            public void doIt(Transition<TState, TTrigger> t, Object[] args) throws Exception {
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
    public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, final Action3<TArg0, TArg1, TArg2> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1, final Class<TArg2> classe2) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        return OnEntryFrom(trigger, new Action4<TArg0, TArg1, TArg2, Transition<TState, TTrigger>>() {
            public void doIt(TArg0 a0, TArg1 a1, TArg2 a2, Transition<TState, TTrigger> t) throws Exception {
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
    public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, final Action4<TArg0, TArg1, TArg2, Transition<TState, TTrigger>> entryAction, final Class<TArg0> classe0, final Class<TArg1> classe1, final Class<TArg2> classe2) throws Exception {
        Enforce.ArgumentNotNull(entryAction, "entryAction");
        Enforce.ArgumentNotNull(trigger, "trigger");
        _representation.AddEntryAction(trigger.getTrigger(), new Action2<Transition<TState, TTrigger>, Object[]>() {
            @SuppressWarnings("unchecked")
            public void doIt(Transition<TState, TTrigger> t, Object[] args) throws Exception {
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
    public StateConfiguration<TState, TTrigger> OnExit(final Action exitAction) throws Exception {
        Enforce.ArgumentNotNull(exitAction, "exitAction");
        return OnExit(new Action1<Transition<TState, TTrigger>>() {
            public void doIt(Transition<TState, TTrigger> arg1) throws Exception {
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
    public StateConfiguration<TState, TTrigger> OnExit(Action1<Transition<TState, TTrigger>> exitAction) throws Exception {
        Enforce.ArgumentNotNull(exitAction, "exitAction");
        _representation.AddExitAction(exitAction);
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
    public StateConfiguration<TState, TTrigger> SubstateOf(TState superstate) throws Exception {
        StateRepresentation<TState, TTrigger> superRepresentation = _lookup.call(superstate);
        _representation.setSuperstate(superRepresentation);
        superRepresentation.AddSubstate(_representation);
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
    public StateConfiguration<TState, TTrigger> PermitDynamic(TTrigger trigger, final Func<TState> destinationStateSelector) throws Exception {
        return PermitDynamicIf(trigger, destinationStateSelector, NoGuard);
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
    public <TArg0> StateConfiguration<TState, TTrigger> PermitDynamic(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, Func2<TArg0, TState> destinationStateSelector) throws Exception {
        return permitDynamicIf(trigger, destinationStateSelector, NoGuard);
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
    public <TArg0, TArg1> StateConfiguration<TState, TTrigger> PermitDynamic(
            TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger,
            Func3<TArg0, TArg1, TState> destinationStateSelector) throws Exception {
        return permitDynamicIf(trigger, destinationStateSelector, NoGuard);
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
    public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> PermitDynamic(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, final Func4<TArg0, TArg1, TArg2, TState> destinationStateSelector) throws Exception {
        return permitDynamicIf(trigger, destinationStateSelector, NoGuard);
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
    public StateConfiguration<TState, TTrigger> PermitDynamicIf(TTrigger trigger, final Func<TState> destinationStateSelector, Func<Boolean> guard) throws Exception {
        Enforce.ArgumentNotNull(destinationStateSelector, "destinationStateSelector");
        return publicPermitDynamicIf(trigger, new Func2<Object[], TState>() {
            public TState call(Object[] arg0) throws Exception {
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
    public <TArg0> StateConfiguration<TState, TTrigger> permitDynamicIf(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, final Func2<TArg0, TState> destinationStateSelector, Func<Boolean> guard) throws Exception {
        Enforce.ArgumentNotNull(trigger, "trigger");
        Enforce.ArgumentNotNull(destinationStateSelector, "destinationStateSelector");
        return publicPermitDynamicIf(
                trigger.getTrigger(), new Func2<Object[], TState>() {
                    @SuppressWarnings("unchecked")
                    public TState call(Object[] args) throws Exception {
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
    public <TArg0, TArg1> StateConfiguration<TState, TTrigger> permitDynamicIf(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger, final Func3<TArg0, TArg1, TState> destinationStateSelector, Func<Boolean> guard) throws Exception {
        Enforce.ArgumentNotNull(trigger, "trigger");
        Enforce.ArgumentNotNull(destinationStateSelector, "destinationStateSelector");
        return publicPermitDynamicIf(
                trigger.getTrigger(), new Func2<Object[], TState>() {
                    @SuppressWarnings("unchecked")

                    public TState call(Object[] args) throws Exception {
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
    public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> permitDynamicIf(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, final Func4<TArg0, TArg1, TArg2, TState> destinationStateSelector, Func<Boolean> guard) throws Exception {
        Enforce.ArgumentNotNull(trigger, "trigger");
        Enforce.ArgumentNotNull(destinationStateSelector, "destinationStateSelector");
        return publicPermitDynamicIf(
                trigger.getTrigger(), new Func2<Object[], TState>() {
                    @SuppressWarnings("unchecked")

                    public TState call(Object[] args) throws Exception {
                        return destinationStateSelector.call(
                                (TArg0) args[0],
                                (TArg1) args[1],
                                (TArg2) args[2]
                        );
                    }
                }, guard
        );
    }

    void enforceNotIdentityTransition(TState destination) throws Exception {
        if (destination.equals(_representation.getUnderlyingState())) {
            throw new Exception(StateConfigurationResources.SelfTransitionsEitherIgnoredOrReentrant);
        }
    }

    StateConfiguration<TState, TTrigger> publicPermit(TTrigger trigger, TState destinationState) throws Exception {
        return publicPermitIf(trigger, destinationState, new Func<Boolean>() {


            public Boolean call() {
                return true;
            }
        });
    }

    StateConfiguration<TState, TTrigger> publicPermitIf(TTrigger trigger, TState destinationState, Func<Boolean> guard) throws Exception {
        Enforce.ArgumentNotNull(guard, "guard");
        _representation.AddTriggerBehaviour(new TransitioningTriggerBehaviour<TState, TTrigger>(trigger, destinationState, guard));
        return this;
    }

    StateConfiguration<TState, TTrigger> publicPermitDynamic(TTrigger trigger, Func2<Object[], TState> destinationStateSelector) throws Exception {
        return publicPermitDynamicIf(trigger, destinationStateSelector, NoGuard);
    }

    StateConfiguration<TState, TTrigger> publicPermitDynamicIf(TTrigger trigger, Func2<Object[], TState> destinationStateSelector, Func<Boolean> guard) throws Exception {
        Enforce.ArgumentNotNull(destinationStateSelector, "destinationStateSelector");
        Enforce.ArgumentNotNull(guard, "guard");
        _representation.AddTriggerBehaviour(new DynamicTriggerBehaviour<TState, TTrigger>(trigger, destinationStateSelector, guard));
        return this;
    }
}