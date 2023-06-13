package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.*;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.triggers.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Models behaviour as transitions between a finite set of states
 *
 * @param <S> The type used to represent the states
 * @param <T> The type used to represent the triggers that cause state transitions
 */
public class StateMachine<S, T> {
    
    private static final String TRIGGER_IS_NULL = "trigger is null";
    protected final StateMachineConfig<S, T> config;
    protected final Func<S> stateAccessor;
    protected final Action1<S> stateMutator;
    private Trace<S, T> trace = null;
    private boolean isStarted = false;
    private S initialState;

    protected Action3<S, T, Object[]> unhandledTriggerAction = new Action3<S, T, Object[]>() {
        @Override
        public void doIt(S state, T trigger, Object[] args) {
            throw new IllegalStateException(
                    String.format(
                            "No valid leaving transitions are permitted from state '%s' for trigger '%s'. Consider ignoring the trigger.",
                            state, trigger)
            );
        }
    };
    
    /**
     * Construct a state machine
     *
     * @param initialState The initial state
     */
    public StateMachine(S initialState) {
        this(initialState, new StateMachineConfig<S, T>());
    }
    
    /**
     * Construct a state machine
     *
     * @param initialState The initial state
     * @param config       State machine configuration
     */
    public StateMachine(S initialState, StateMachineConfig<S, T> config) {
        this.initialState = initialState;
        this.config = config;
        final StateReference<S, T> reference = new StateReference<>();
        reference.setState(initialState);
        stateAccessor = new Func<S>() {
            @Override
            public S call() {
                return reference.getState();
            }
        };
        stateMutator = new Action1<S>() {
            @Override
            public void doIt(S s) {
                reference.setState(s);
            }
        };
    }
    
    /**
     * Construct a state machine with external state storage.
     *
     * @param initialState  The initial state
     * @param stateAccessor State accessor
     * @param stateMutator  State mutator
     */
    public StateMachine(S initialState, Func<S> stateAccessor, Action1<S> stateMutator, StateMachineConfig<S, T> config) {
        this.initialState = initialState;
        this.config = config;
        this.stateAccessor = stateAccessor;
        this.stateMutator = stateMutator;
        stateMutator.doIt(initialState);
    }
    
    /**
     * Construct a state machine with external state storage. Assumes that external storage
     * has the initial state available
     *
     * @param stateAccessor State accessor
     * @param stateMutator  State mutator
     * @param config        Configuration
     */
    public StateMachine(Func<S> stateAccessor, Action1<S> stateMutator, StateMachineConfig<S, T> config) {
        this.initialState = stateAccessor.call();
        this.config = config;
        this.stateAccessor = stateAccessor;
        this.stateMutator = stateMutator;
    }

    /**
     * Fire initial transition into the initial state.
     * All super-states are entered too.
     *
     * This method can be called only once, before state machine is used.
     */
    public void fireInitialTransition() {
        S currentState = getCurrentRepresentation().getUnderlyingState();
        if (isStarted || !currentState.equals(initialState)) {
            throw new IllegalStateException("Firing initial transition after state machine has been started");
        }
        isStarted = true;
        Transition<S, T> initialTransition = new Transition<>(null, currentState, null);
        getCurrentRepresentation().enter(initialTransition);
    }

    public StateConfiguration<S, T> configure(S state) {
        return config.configure(state);
    }
    
    public StateMachineConfig<S, T> configuration() {
        return config;
    }
    
    /**
     * The current state
     *
     * @return The current state
     */
    public S getState() {
        return stateAccessor.call();
    }
    
    private void setState(S value) {
        stateMutator.doIt(value);
    }
    
    /**
     * The currently-permissible trigger values
     *
     * @return The currently-permissible trigger values
     */
    public List<T> getPermittedTriggers() {
        return getCurrentRepresentation().getPermittedTriggers();
    }
    
    StateRepresentation<S, T> getCurrentRepresentation() {
        StateRepresentation<S, T> representation = config.getRepresentation(getState());
        return representation == null ? new StateRepresentation<S, T>(getState()) : representation;
    }
    
    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked
     *
     * @param trigger The trigger to fire
     */
    public void fire(T trigger) {
        publicFire(trigger);
    }
    
    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked.
     *
     * @param trigger The trigger to fire
     * @param arg0    The first argument
     * @param <TArg0> Type of the first trigger argument
     */
    public <TArg0> void fire(TriggerWithParameters1<TArg0, T> trigger, TArg0 arg0) {
        assert trigger != null : TRIGGER_IS_NULL;
        publicFire(trigger.getTrigger(), arg0);
    }
    
    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked.
     *
     * @param trigger The trigger to fire
     * @param arg0    The first argument
     * @param arg1    The second argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     */
    public <TArg0, TArg1> void fire(TriggerWithParameters2<TArg0, TArg1, T> trigger, TArg0 arg0, TArg1 arg1) {
        assert trigger != null : TRIGGER_IS_NULL;
        publicFire(trigger.getTrigger(), arg0, arg1);
    }
    
    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked.
     *
     * @param trigger The trigger to fire
     * @param arg0    The first argument
     * @param arg1    The second argument
     * @param arg2    The third argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     * @param <TArg2> Type of the third trigger argument
     */
    public <TArg0, TArg1, TArg2> void fire(TriggerWithParameters3<TArg0, TArg1, TArg2, T> trigger, TArg0 arg0, TArg1 arg1, TArg2 arg2) {
        assert trigger != null : TRIGGER_IS_NULL;
        publicFire(trigger.getTrigger(), arg0, arg1, arg2);
    }
    
    protected void publicFire(T trigger, Object... args) {
        isStarted = true;
        if (trace != null) {
            trace.trigger(trigger);
        }
        TriggerWithParameters<T> configuration = config.getTriggerConfiguration(trigger);
        if (configuration != null) {
            configuration.validateParameters(args);
        }
        
        TriggerBehaviour<S, T> triggerBehaviour = getCurrentRepresentation().tryFindHandler(trigger);
        if (triggerBehaviour == null) {
            unhandledTriggerAction.doIt(getCurrentRepresentation().getUnderlyingState(), trigger, args);
            return;
        }
        
        if (triggerBehaviour.isInternal()) {
            triggerBehaviour.performAction(args);
        } else {
            S source = getState();
            S destination = triggerBehaviour.transitionsTo(source, args);
            Transition<S, T> transition = new Transition<>(source, destination, trigger);
            
            getCurrentRepresentation().exit(transition);
            triggerBehaviour.performAction(args);
            setState(destination);
            getCurrentRepresentation().enter(transition, args);
            if (trace != null) {
                trace.transition(trigger, source, destination);
            }
        }
    }
    
    /**
     * Override the default behaviour of throwing an exception when an unhandled trigger is fired
     *
     * @param unhandledTriggerAction An action to call when an unhandled trigger is fired
     */
    public void onUnhandledTrigger(final Action2<S, T> unhandledTriggerAction) {
        if (unhandledTriggerAction == null) {
            throw new IllegalStateException("unhandledTriggerAction");
        }
        this.unhandledTriggerAction = new Action3<S, T, Object[]>() {
            @Override
            public void doIt(S state, T trigger, Object[] arg3) {
                unhandledTriggerAction.doIt(state, trigger);
            }
        };
    }

    /**
     * Override the default behaviour of throwing an exception when an unhandled trigger is fired
     *
     * @param unhandledTriggerAction An action to call with state, trigger and params when an unhandled trigger is fired
     */
    public void onUnhandledTrigger(Action3<S, T, Object[]> unhandledTriggerAction) {
        if (unhandledTriggerAction == null) {
            throw new IllegalStateException("unhandledTriggerAction");
        }
        this.unhandledTriggerAction = unhandledTriggerAction;
    }

    /**
     * Determine if the state machine is in the supplied state
     *
     * @param state The state to test for
     * @return True if the current state is equal to, or a substate of, the supplied state
     */
    public boolean isInState(S state) {
        return getCurrentRepresentation().isIncludedIn(state);
    }
    
    /**
     * Returns true if {@code trigger} can be fired  in the current state
     *
     * @param trigger Trigger to test
     * @return True if the trigger can be fired, false otherwise
     */
    public boolean canFire(T trigger) {
        return getCurrentRepresentation().canHandle(trigger);
    }

    /**
     * Set tracer delegate. Set trace delegate to investigate what the state machine is doing
     * at runtime. Trace delegate will be called on {@link #fire(Object)} and on transition.
     *
     * @param trace Trace delegate or null, if trace should be disabled
     */
    public void setTrace(Trace<S, T> trace) {
        this.trace = trace;
    }

    /**
     * A human-readable representation of the state machine
     *
     * @return A description of the current state and permitted triggers
     */
    @Override
    public String toString() {
        List<T> permittedTriggers = getPermittedTriggers();
        List<String> parameters = new ArrayList<>();
        
        for (T tTrigger : permittedTriggers) {
            parameters.add(tTrigger.toString());
        }
        
        StringBuilder params = new StringBuilder();
        String delim = "";
        for (String param : parameters) {
            params.append(delim);
            params.append(param);
            delim = ", ";
        }
        
        return String.format(
                "StateMachine {{ State = %s, PermittedTriggers = {{ %s }}}}",
                getState(),
                params.toString());
    }
}