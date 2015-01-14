package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import com.github.oxo42.stateless4j.triggers.TriggerBehaviour;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The state machine configuration. Reusable.
 */
public class StateMachineConfig<TState,TTrigger> {

    private final Map<TState, StateRepresentation<TState, TTrigger>> stateConfiguration = new HashMap<>();
    private final Map<TTrigger, TriggerWithParameters<TState, TTrigger>> triggerConfiguration = new HashMap<>();
    /**
     * Added in 2.5.2.
     * Default MUST be false for backward compatibility reasons. Prior to 2.5.2,
     * entering the initial state never fires its entry action.
     */
    private boolean entryActionOfInitialStateEnabled = false;

    /**
     * Gets whether the entry action of the initial state of the state machine
     * must be executed when the state machine starts.
     * Default is false for backward compatibility sake.
     * 
     * Added in 2.5.2
     *
     * @return true if the entry action of the initial state of the state machine
     * must be executed when the state machine starts.
     */
    public boolean isEntryActionOfInitialStateEnabled() {
        return entryActionOfInitialStateEnabled;
    }
    
    /**
     * Enables the state machine to execute the entry action of the initial state
     * when the state machine starts.
     * This configuration is disabled by default.
     */
    public void enableEntryActionOfInitialState() {
        this.entryActionOfInitialStateEnabled = true;
    }
    
    /**
     * Disables the state machine to execute the entry action of the initial state
     * when the state machine starts.
     * This is the default.
     */
    public void disableEntryActionOfInitialState() {
        this.entryActionOfInitialStateEnabled = true;
    }
    
    /**
     * Return StateRepresentation for the specified state. May return null.
     *
     * @param state The state
     * @return StateRepresentation for the specified state, or null.
     */
    public StateRepresentation<TState, TTrigger> getRepresentation(TState state) {
        return  stateConfiguration.get(state);
    }

    /**
     * Return StateRepresentation for the specified state. Creates representation if it does not exist.
     *
     * @param state The state
     * @return StateRepresentation for the specified state.
     */
    private StateRepresentation<TState, TTrigger> getOrCreateRepresentation(TState state) {
        StateRepresentation<TState, TTrigger> result = stateConfiguration.get(state);
        if (result == null) {
            result = new StateRepresentation<>(state);
            stateConfiguration.put(state, result);
        }

        return result;
    }

    public TriggerWithParameters<TState, TTrigger> getTriggerConfiguration(TTrigger trigger) {
        return triggerConfiguration.get(trigger);
    }

    /**
     * Begin configuration of the entry/exit actions and allowed transitions
     * when the state machine is in a particular state
     *
     * @param state The state to configure
     * @return A configuration object through which the state can be configured
     */
    public StateConfiguration<TState, TTrigger> configure(TState state) {
        return new StateConfiguration<>(getOrCreateRepresentation(state), new Func2<TState, StateRepresentation<TState, TTrigger>>() {

            public StateRepresentation<TState, TTrigger> call(TState arg0) {
                return getOrCreateRepresentation(arg0);
            }
        });
    }

    private void saveTriggerConfiguration(TriggerWithParameters<TState, TTrigger> trigger) {
        if (triggerConfiguration.containsKey(trigger.getTrigger())) {
            throw new IllegalStateException("Parameters for the trigger '" + trigger + "' have already been configured.");
        }

        triggerConfiguration.put(trigger.getTrigger(), trigger);
    }

    /**
     * Specify the arguments that must be supplied when a specific trigger is fired
     *
     * @param trigger The underlying trigger value
     * @param classe0 Class argument
     * @param <TArg0> Type of the first trigger argument
     * @return An object that can be passed to the fire() method in order to fire the parameterised trigger
     */
    public <TArg0> TriggerWithParameters1<TArg0, TState, TTrigger> setTriggerParameters(TTrigger trigger, Class<TArg0> classe0) {
        TriggerWithParameters1<TArg0, TState, TTrigger> configuration = new TriggerWithParameters1<>(trigger, classe0);
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
    public <TArg0, TArg1> TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> setTriggerParameters(TTrigger trigger, Class<TArg0> classe0, Class<TArg1> classe1) {
        TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> configuration = new TriggerWithParameters2<>(trigger, classe0, classe1);
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
    public <TArg0, TArg1, TArg2> TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> setTriggerParameters(TTrigger trigger, Class<TArg0> classe0, Class<TArg1> classe1, Class<TArg2> classe2) {
        TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> configuration = new TriggerWithParameters3<>(trigger, classe0, classe1, classe2);
        saveTriggerConfiguration(configuration);
        return configuration;
    }

    public void generateDotFileInto(final OutputStream dotFile) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(dotFile, "UTF-8")) {
            PrintWriter writer = new PrintWriter(w);
            writer.write("digraph G {\n");
            OutVar<TState> destination = new OutVar<>();
            for (Map.Entry<TState, StateRepresentation<TState, TTrigger>> entry : this.stateConfiguration.entrySet()) {
                Map<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> behaviours = entry.getValue().getTriggerBehaviours();
                for (Map.Entry<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> behaviour : behaviours.entrySet()) {
                    for (TriggerBehaviour<TState, TTrigger> triggerBehaviour : behaviour.getValue()) {
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
