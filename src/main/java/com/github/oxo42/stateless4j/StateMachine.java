package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.delegates.Func;
import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import com.github.oxo42.stateless4j.triggers.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Models behaviour as transitions between a finite set of states
 *
 * @param <S>   The type used to represent the states
 * @param <T> The type used to represent the triggers that cause state transitions
 */
public class StateMachine<S, T> {

    protected final Map<S, StateRepresentation<S, T>> stateConfiguration = new HashMap<>();
    protected final Map<T, TriggerWithParameters<S, T>> triggerConfiguration = new HashMap<>();
    protected final Func<S> stateAccessor;
    protected final Action1<S> stateMutator;
    protected Action2<S, T> unhandledTriggerAction = new Action2<S, T>() {

        @Override
        public void doIt(S state, T trigger) {
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
        return getRepresentation(getState());
    }

    protected StateRepresentation<S, T> getRepresentation(S state) {
        StateRepresentation<S, T> result = stateConfiguration.get(state);
        if (result == null) {
            result = new StateRepresentation<>(state);
            stateConfiguration.put(state, result);
        }

        return result;
    }

    /**
     * Begin configuration of the entry/exit actions and allowed transitions
     * when the state machine is in a particular state
     *
     * @param state The state to configure
     * @return A configuration object through which the state can be configured
     */
    public StateConfiguration<S, T> configure(S state) {
        return new StateConfiguration<>(getRepresentation(state), new Func2<S, StateRepresentation<S, T>>() {

            @Override
            public StateRepresentation<S, T> call(S arg0) {
                return getRepresentation(arg0);
            }
        });
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
    public <TArg0> void fire(TriggerWithParameters1<TArg0, S, T> trigger, TArg0 arg0) {
        assert trigger != null : "trigger is null";
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
    public <TArg0, TArg1> void fire(TriggerWithParameters2<TArg0, TArg1, S, T> trigger, TArg0 arg0, TArg1 arg1) {
        assert trigger != null : "trigger is null";
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
    public <TArg0, TArg1, TArg2> void fire(TriggerWithParameters3<TArg0, TArg1, TArg2, S, T> trigger, TArg0 arg0, TArg1 arg1, TArg2 arg2) {
        assert trigger != null : "trigger is null";
        publicFire(trigger.getTrigger(), arg0, arg1, arg2);
    }

    protected void publicFire(T trigger, Object... args) {
        TriggerWithParameters<S, T> configuration = triggerConfiguration.get(trigger);
        if (configuration != null) {
            configuration.validateParameters(args);
        }

        TriggerBehaviour<S, T> triggerBehaviour = getCurrentRepresentation().tryFindHandler(trigger);
        if (triggerBehaviour == null) {
            unhandledTriggerAction.doIt(getCurrentRepresentation().getUnderlyingState(), trigger);
            return;
        }

        S source = getState();
        OutVar<S> destination = new OutVar<>();
        if (triggerBehaviour.resultsInTransitionFrom(source, args, destination)) {
            Transition<S, T> transition = new Transition<>(source, destination.get(), trigger);

            getCurrentRepresentation().exit(transition);
            setState(destination.get());
            getCurrentRepresentation().enter(transition, args);
        }
    }

    /**
     * Override the default behaviour of throwing an exception when an unhandled trigger is fired
     *
     * @param unhandledTriggerAction An action to call when an unhandled trigger is fired
     */
    public void onUnhandledTrigger(Action2<S, T> unhandledTriggerAction) {
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

    /**
     * Specify the arguments that must be supplied when a specific trigger is fired
     *
     * @param trigger The underlying trigger value
     * @param classe0 Class argument
     * @param <TArg0> Type of the first trigger argument
     * @return An object that can be passed to the fire() method in order to fire the parameterised trigger
     */
    public <TArg0> TriggerWithParameters1<TArg0, S, T> setTriggerParameters(T trigger, Class<TArg0> classe0) {
        TriggerWithParameters1<TArg0, S, T> configuration = new TriggerWithParameters1<>(trigger, classe0);
        saveTriggerConfiguration(configuration);
        return configuration;
    }

    /**
     * Specify the arguments that must be supplied when a specific trigger is fired
     *
     * @param trigger The underlying trigger value
     * @param classe0 Class argument
     * @param classe1 Class argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     * @return An object that can be passed to the fire() method in order to fire the parameterised trigger
     */
    public <TArg0, TArg1> TriggerWithParameters2<TArg0, TArg1, S, T> setTriggerParameters(T trigger, Class<TArg0> classe0, Class<TArg1> classe1) {
        TriggerWithParameters2<TArg0, TArg1, S, T> configuration = new TriggerWithParameters2<>(trigger, classe0, classe1);
        saveTriggerConfiguration(configuration);
        return configuration;
    }

    /**
     * Specify the arguments that must be supplied when a specific trigger is fired
     *
     * @param trigger The underlying trigger value
     * @param classe0 Class argument
     * @param classe1 Class argument
     * @param classe2 Class argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     * @param <TArg2> Type of the third trigger argument
     * @return An object that can be passed to the fire() method in order to fire the parameterised trigger
     */
    public <TArg0, TArg1, TArg2> TriggerWithParameters3<TArg0, TArg1, TArg2, S, T> setTriggerParameters(T trigger, Class<TArg0> classe0, Class<TArg1> classe1, Class<TArg2> classe2) {
        TriggerWithParameters3<TArg0, TArg1, TArg2, S, T> configuration = new TriggerWithParameters3<>(trigger, classe0, classe1, classe2);
        saveTriggerConfiguration(configuration);
        return configuration;
    }

    private void saveTriggerConfiguration(TriggerWithParameters<S, T> trigger) {
        if (triggerConfiguration.containsKey(trigger.getTrigger())) {
            throw new IllegalStateException("Parameters for the trigger '" + trigger + "' have already been configured.");
        }

        triggerConfiguration.put(trigger.getTrigger(), trigger);
    }

    public void generateDotFileInto(final OutputStream dotFile) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(dotFile, "UTF-8")) {
            PrintWriter writer = new PrintWriter(w);
            writer.write("digraph G {\n");
            OutVar<S> destination = new OutVar<>();
            for (Entry<S, StateRepresentation<S, T>> entry : this.stateConfiguration.entrySet()) {
                Map<T, List<TriggerBehaviour<S, T>>> behaviours = entry.getValue().getTriggerBehaviours();
                for (Entry<T, List<TriggerBehaviour<S, T>>> behaviour : behaviours.entrySet()) {
                    for (TriggerBehaviour<S, T> triggerBehaviour : behaviour.getValue()) {
                        if (triggerBehaviour instanceof TransitioningTriggerBehaviour) {
                            destination.set(null);
                            triggerBehaviour.resultsInTransitionFrom(null, null, destination);
                            writer.write(String.format("\t%s -> %s;\n", entry.getKey(), destination));
                        }
                    }
                }
            }
            writer.write("}");
        }
    }
}
