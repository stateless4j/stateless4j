package com.googlecode.stateless4j;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.googlecode.stateless4j.delegates.Action1;
import com.googlecode.stateless4j.delegates.Action2;
import com.googlecode.stateless4j.delegates.Func;
import com.googlecode.stateless4j.delegates.Func2;
import com.googlecode.stateless4j.resources.StateMachineResources;
import com.googlecode.stateless4j.transitions.Transition;
import com.googlecode.stateless4j.transitions.TransitioningTriggerBehaviour;
import com.googlecode.stateless4j.triggers.TriggerBehaviour;
import com.googlecode.stateless4j.triggers.TriggerWithParameters;
import com.googlecode.stateless4j.triggers.TriggerWithParameters1;
import com.googlecode.stateless4j.triggers.TriggerWithParameters2;
import com.googlecode.stateless4j.triggers.TriggerWithParameters3;
import com.googlecode.stateless4j.validation.Enforce;


    /// <summary>
    /// Models behaviour as transitions between a finite set of states.
    /// </summary>
    /// <typeparam name="TState">The type used to represent the states.</typeparam>
    /// <typeparam name="TTrigger">The type used to represent the triggers that cause state transitions.</typeparam>
    public class StateMachine<TState, TTrigger>
    {
      final Map<TState, StateRepresentation<TState, TTrigger>> _stateConfiguration = new HashMap<TState, StateRepresentation<TState, TTrigger>>();
        final Map<TTrigger, TriggerWithParameters<TState, TTrigger>> _triggerConfiguration = new HashMap<TTrigger, TriggerWithParameters<TState, TTrigger>>();
        final Func<TState> _stateAccessor;
        final Action1<TState> _stateMutator;
        Action2<TState, TTrigger> _unhandledTriggerAction = new Action2<TState, TTrigger>() {
            
            public void doIt(TState state, TTrigger trigger) throws Exception {
                throw new Exception(
                        String.format(
                            StateMachineResources.NoTransitionsPermitted,
                            trigger, state));
            }
        
        };

        /// <summary>
        /// Construct a state machine.
        /// </summary>
        /// <param name="initialState">The initial state.</param>
        public StateMachine(TState initialState)
        {
            final StateReference<TState, TTrigger> reference = new StateReference<TState, TTrigger>();
            reference.setState(initialState);
            _stateAccessor = new Func<TState>() {
                public TState call() {
                    return reference.getState();
                }
            };
            _stateMutator = new Action1<TState>() {
                public void doIt(TState s) {
                    reference.setState(s);
                };
                
            };
        }

        /// <summary>
        /// The current state.
        /// </summary>
        public TState getState()
        {
            return _stateAccessor.call();
        }
        
        private void setState(TState value)
        {
            _stateMutator.doIt(value);
        }

        /// <summary>
        /// The currently-permissible trigger values.
        /// </summary>
        public List<TTrigger> getPermittedTriggers()
        {
            return getCurrentRepresentation().getPermittedTriggers();
        }

        StateRepresentation<TState, TTrigger> getCurrentRepresentation()
        {
            return GetRepresentation(getState());
        }

        StateRepresentation<TState, TTrigger> GetRepresentation(TState state)
        {

            if (!_stateConfiguration.containsKey(state))
            {
                StateRepresentation<TState, TTrigger> result = new StateRepresentation<TState, TTrigger>(state);
                _stateConfiguration.put(state, result);
            }

            return _stateConfiguration.get(state);
        }

        /// <summary>
        /// Begin configuration of the entry/exit actions and allowed transitions
        /// when the state machine is in a particular state.
        /// </summary>
        /// <param name="state">The state to configure.</param>
        /// <returns>A configuration object through which the state can be configured.</returns>
        public StateConfiguration<TState, TTrigger> Configure(TState state) throws Exception
        {
            return new StateConfiguration<TState, TTrigger>(GetRepresentation(state), new Func2<TState, StateRepresentation<TState, TTrigger>>() {
                
                public StateRepresentation<TState, TTrigger> call(TState arg0) {
                    return GetRepresentation(arg0);
                }
            });
        }

        /// <summary>
        /// Transition from the current state via the specified trigger.
        /// The target state is determined by the configuration of the current state.
        /// Actions associated with leaving the current state and entering the new one
        /// will be invoked.
        /// </summary>
        /// <param name="trigger">The trigger to fire.</param>
        /// <exception cref="System.InvalidOperationException">The current state does
        /// not allow the trigger to be fired.</exception>
        public void Fire(TTrigger trigger) throws Exception
        {
            publicFire(trigger, new Object[0]);
        }

        /// <summary>
        /// Transition from the current state via the specified trigger.
        /// The target state is determined by the configuration of the current state.
        /// Actions associated with leaving the current state and entering the new one
        /// will be invoked.
        /// </summary>
        /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
        /// <param name="trigger">The trigger to fire.</param>
        /// <param name="arg0">The first argument.</param>
        /// <exception cref="System.InvalidOperationException">The current state does
        /// not allow the trigger to be fired.</exception>
        public <TArg0> void Fire(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, TArg0 arg0) throws Exception
        {
            Enforce.ArgumentNotNull(trigger, "trigger");
            publicFire(trigger.getTrigger(), arg0);
        }

        /// <summary>
        /// Transition from the current state via the specified trigger.
        /// The target state is determined by the configuration of the current state.
        /// Actions associated with leaving the current state and entering the new one
        /// will be invoked.
        /// </summary>
        /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
        /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
        /// <param name="arg0">The first argument.</param>
        /// <param name="arg1">The second argument.</param>
        /// <param name="trigger">The trigger to fire.</param>
        /// <exception cref="System.InvalidOperationException">The current state does
        /// not allow the trigger to be fired.</exception>
        public <TArg0, TArg1> void Fire(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger, TArg0 arg0, TArg1 arg1) throws Exception
        {
            Enforce.ArgumentNotNull(trigger, "trigger");
            publicFire(trigger.getTrigger(), arg0, arg1);
        }

        /// <summary>
        /// Transition from the current state via the specified trigger.
        /// The target state is determined by the configuration of the current state.
        /// Actions associated with leaving the current state and entering the new one
        /// will be invoked.
        /// </summary>
        /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
        /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
        /// <typeparam name="TArg2">Type of the third trigger argument.</typeparam>
        /// <param name="arg0">The first argument.</param>
        /// <param name="arg1">The second argument.</param>
        /// <param name="arg2">The third argument.</param>
        /// <param name="trigger">The trigger to fire.</param>
        /// <exception cref="System.InvalidOperationException">The current state does
        /// not allow the trigger to be fired.</exception>
        public <TArg0, TArg1, TArg2> void Fire(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, TArg0 arg0, TArg1 arg1, TArg2 arg2) throws Exception
        {
            Enforce.ArgumentNotNull(trigger, "trigger");
            publicFire(trigger.getTrigger(), arg0, arg1, arg2);
        }
        
        void publicFire(TTrigger trigger, Object... args) throws Exception
        {
            TriggerWithParameters<TState, TTrigger> configuration;
            if (_triggerConfiguration.containsKey(trigger)) {
                configuration = _triggerConfiguration.get(trigger);
                configuration.ValidateParameters(args);
            }

            TriggerBehaviour<TState, TTrigger> triggerBehaviour;
            try {
                triggerBehaviour = getCurrentRepresentation().TryFindHandler(trigger);
            } catch (Exception e) {
                _unhandledTriggerAction.doIt(getCurrentRepresentation().getUnderlyingState(), trigger);
                return;
            }

            TState source = getState();
            TState destination;
            try {
                destination = triggerBehaviour.ResultsInTransitionFrom(source, args);
                Transition<TState, TTrigger> transition = new Transition<TState, TTrigger>(source, destination, trigger);
                
                getCurrentRepresentation().Exit(transition);
                setState(transition.getDestination());
                getCurrentRepresentation().Enter(transition, args);
                
            } catch (Exception e) {
                
            }
        }

        /// <summary>
        /// Override the default behaviour of throwing an exception when an unhandled trigger
        /// is fired.
        /// </summary>
        /// <param name="unhandledTriggerAction">An action to call when an unhandled trigger is fired.</param>
        public void OnUnhandledTrigger(Action2<TState, TTrigger> unhandledTriggerAction) throws Exception
        {
            if (unhandledTriggerAction == null) throw new Exception("unhandledTriggerAction");
            _unhandledTriggerAction = unhandledTriggerAction;
        }

        /// <summary>
        /// Determine if the state machine is in the supplied state.
        /// </summary>
        /// <param name="state">The state to test for.</param>
        /// <returns>True if the current state is equal to, or a substate of,
        /// the supplied state.</returns>
        public Boolean IsInState(TState state)
        {
            return getCurrentRepresentation().IsIncludedIn(state);
        }

        /// <summary>
        /// Returns true if <paramref name="trigger"/> can be fired
        /// in the current state.
        /// </summary>
        /// <param name="trigger">Trigger to test.</param>
        /// <returns>True if the trigger can be fired, false otherwise.</returns>
        public Boolean CanFire(TTrigger trigger)
        {
            return getCurrentRepresentation().CanHandle(trigger);
        }

        /// <summary>
        /// A human-readable representation of the state machine.
        /// </summary>
        /// <returns>A description of the current state and permitted triggers.</returns>
        public String toString()
        {
            List<TTrigger> permittedTriggers = getPermittedTriggers();
            List<String> parameters = new ArrayList<String>();
            
            for (TTrigger tTrigger : permittedTriggers) {
                parameters.add(tTrigger.toString());
            }
            
            return String.format(
                "StateMachine {{ State = {0}, PermittedTriggers = {{ {1} }}}}",
                getState(),
                StringUtils.join(parameters, ", "));
        }

        /// <summary>
        /// Specify the arguments that must be supplied when a specific trigger is fired.
        /// </summary>
        /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
        /// <param name="trigger">The underlying trigger value.</param>
        /// <returns>An object that can be passed to the Fire() method in order to 
        /// fire the parameterised trigger.</returns>
        public <TArg0> TriggerWithParameters1<TArg0, TState, TTrigger> SetTriggerParameters(TTrigger trigger, Class<TArg0> classe0) throws Exception
        {
            TriggerWithParameters1<TArg0, TState, TTrigger> configuration = new TriggerWithParameters1<TArg0, TState, TTrigger>(trigger, classe0);
            SaveTriggerConfiguration(configuration);
            return configuration;
        }

        /// <summary>
        /// Specify the arguments that must be supplied when a specific trigger is fired.
        /// </summary>
        /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
        /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
        /// <param name="trigger">The underlying trigger value.</param>
        /// <returns>An object that can be passed to the Fire() method in order to 
        /// fire the parameterised trigger.</returns>
        public <TArg0, TArg1> TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> SetTriggerParameters(TTrigger trigger, Class<TArg0> classe0, Class<TArg1> classe1) throws Exception
        {
            TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> configuration = new TriggerWithParameters2<TArg0, TArg1, TState, TTrigger>(trigger, classe0, classe1);
            SaveTriggerConfiguration(configuration);
            return configuration;
        }

        /// <summary>
        /// Specify the arguments that must be supplied when a specific trigger is fired.
        /// </summary>
        /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
        /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
        /// <typeparam name="TArg2">Type of the third trigger argument.</typeparam>
        /// <param name="trigger">The underlying trigger value.</param>
        /// <returns>An object that can be passed to the Fire() method in order to 
        /// fire the parameterised trigger.</returns>
        public <TArg0, TArg1, TArg2> TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> SetTriggerParameters(TTrigger trigger, Class<TArg0> classe0, Class<TArg1> classe1, Class<TArg2> classe2) throws Exception
        {
            TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> configuration = new TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger>(trigger, classe0, classe1, classe2);
            SaveTriggerConfiguration(configuration);
            return configuration;
        }

        void SaveTriggerConfiguration(TriggerWithParameters<TState, TTrigger> trigger) throws Exception
        {
            if (_triggerConfiguration.containsKey(trigger.getTrigger()))
                throw new Exception(
                    String.format(StateMachineResources.CannotReconfigureParameters, trigger));

            _triggerConfiguration.put(trigger.getTrigger(), trigger);
        }

		public void GenerateDotFileInto(OutputStream dotFile) throws Exception {
			OutputStreamWriter w = new OutputStreamWriter(dotFile, "UTF-8");
			PrintWriter writer = new PrintWriter(w);
			writer.write("digraph G {\n");
			for (Entry<TState, StateRepresentation<TState, TTrigger>> entry : this._stateConfiguration.entrySet()) {
				Map<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> behaviours = entry.getValue()._triggerBehaviours;
				for (Entry<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> behaviour : behaviours.entrySet()) {
					for (TriggerBehaviour<TState, TTrigger> triggerBehaviour : behaviour.getValue()) {
						if (triggerBehaviour instanceof TransitioningTriggerBehaviour) {							
							writer.write(String.format("\t%s -> %s;\n", entry.getKey(), triggerBehaviour.ResultsInTransitionFrom(null)));
						}
					}
				}
			}
			writer.write("}");
			writer.close();
		}
    }
