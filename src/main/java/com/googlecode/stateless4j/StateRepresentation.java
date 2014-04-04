package com.googlecode.stateless4j;

import com.googlecode.stateless4j.delegates.Action1;
import com.googlecode.stateless4j.delegates.Action2;
import com.googlecode.stateless4j.resources.StateRepresentationResources;
import com.googlecode.stateless4j.transitions.Transition;
import com.googlecode.stateless4j.triggers.TriggerBehaviour;
import com.googlecode.stateless4j.validation.Enforce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateRepresentation<TState, TTrigger> {
    final TState _state;

    final Map<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> _triggerBehaviours =
            new HashMap<TTrigger, List<TriggerBehaviour<TState, TTrigger>>>();

    final List<Action2<Transition<TState, TTrigger>, Object[]>> _entryActions = new ArrayList<Action2<Transition<TState, TTrigger>, Object[]>>();
    final List<Action1<Transition<TState, TTrigger>>> _exitActions = new ArrayList<Action1<Transition<TState, TTrigger>>>();
    final List<StateRepresentation<TState, TTrigger>> _substates = new ArrayList<StateRepresentation<TState, TTrigger>>();
    StateRepresentation<TState, TTrigger> _superstate; // null

    public StateRepresentation(TState state) {
        _state = state;
    }

    public Boolean CanHandle(TTrigger trigger) {
        try {
            TryFindHandler(trigger);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public TriggerBehaviour<TState, TTrigger> TryFindHandler(TTrigger trigger) {
        try {
            return TryFindLocalHandler(trigger);
        } catch (Exception e) {
            return getSuperstate().TryFindHandler(trigger);
        }
    }

    TriggerBehaviour<TState, TTrigger> TryFindLocalHandler(TTrigger trigger/*, out TriggerBehaviour handler*/) throws Exception {
        List<TriggerBehaviour<TState, TTrigger>> possible;
        if (!_triggerBehaviours.containsKey(trigger)) {
            throw new Exception();
        }
        possible = _triggerBehaviours.get(trigger);

        List<TriggerBehaviour<TState, TTrigger>> actual = new ArrayList<TriggerBehaviour<TState, TTrigger>>();
        for (TriggerBehaviour<TState, TTrigger> triggerBehaviour : possible) {
            if (triggerBehaviour.isGuardConditionMet()) {
                actual.add(triggerBehaviour);
            }
        }

        if (actual.size() > 1)
            throw new Exception(
                    String.format(StateRepresentationResources.MultipleTransitionsPermitted,
                            trigger, _state)
            );

        TriggerBehaviour<TState, TTrigger> handler = actual.get(0);
        if (handler == null) {
            throw new Exception();
        }
        return handler;
    }

    public void AddEntryAction(final TTrigger trigger, final Action2<Transition<TState, TTrigger>, Object[]> action) throws Exception {
        Enforce.ArgumentNotNull(action, "action");


        _entryActions.add(new Action2<Transition<TState, TTrigger>, Object[]>() {
            public void doIt(Transition<TState, TTrigger> t, Object[] args) throws Exception {
                if (t.getTrigger().equals(trigger))
                    action.doIt(t, args);
            }
        });
    }

    public void AddEntryAction(Action2<Transition<TState, TTrigger>, Object[]> action) throws Exception {
        _entryActions.add(Enforce.ArgumentNotNull(action, "action"));
    }

    public void AddExitAction(Action1<Transition<TState, TTrigger>> action) throws Exception {
        _exitActions.add(Enforce.ArgumentNotNull(action, "action"));
    }

    public void Enter(Transition<TState, TTrigger> transition, Object... entryArgs) throws Exception {
        Enforce.ArgumentNotNull(transition, "transtion");

        if (transition.isReentry()) {
            ExecuteEntryActions(transition, entryArgs);
        } else if (!Includes(transition.getSource())) {
            if (_superstate != null)
                _superstate.Enter(transition, entryArgs);

            ExecuteEntryActions(transition, entryArgs);
        }
    }

    public void Exit(Transition<TState, TTrigger> transition) throws Exception {
        Enforce.ArgumentNotNull(transition, "transtion");

        if (transition.isReentry()) {
            ExecuteExitActions(transition);
        } else if (!Includes(transition.getDestination())) {
            ExecuteExitActions(transition);
            if (_superstate != null)
                _superstate.Exit(transition);
        }
    }

    void ExecuteEntryActions(Transition<TState, TTrigger> transition, Object[] entryArgs) throws Exception {
        Enforce.ArgumentNotNull(transition, "transtion");
        Enforce.ArgumentNotNull(entryArgs, "entryArgs");
        for (Action2<Transition<TState, TTrigger>, Object[]> action : _entryActions)
            action.doIt(transition, entryArgs);
    }

    void ExecuteExitActions(Transition<TState, TTrigger> transition) throws Exception {
        Enforce.ArgumentNotNull(transition, "transtion");
        for (Action1<Transition<TState, TTrigger>> action : _exitActions)
            action.doIt(transition);
    }

    public void AddTriggerBehaviour(TriggerBehaviour<TState, TTrigger> triggerBehaviour) {
        List<TriggerBehaviour<TState, TTrigger>> allowed;
        if (!_triggerBehaviours.containsKey(triggerBehaviour.getTrigger())) {
            allowed = new ArrayList<TriggerBehaviour<TState, TTrigger>>();
            _triggerBehaviours.put(triggerBehaviour.getTrigger(), allowed);
        }
        allowed = _triggerBehaviours.get(triggerBehaviour.getTrigger());
        allowed.add(triggerBehaviour);
    }

    public StateRepresentation<TState, TTrigger> getSuperstate() {
        return _superstate;
    }

    public void setSuperstate(StateRepresentation<TState, TTrigger> value) {
        _superstate = value;
    }

    public TState getUnderlyingState() {
        return _state;
    }

    public void AddSubstate(StateRepresentation<TState, TTrigger> substate) throws Exception {
        Enforce.ArgumentNotNull(substate, "substate");
        _substates.add(substate);
    }

    public Boolean Includes(TState state) {
        Boolean isIncluded = false;
        for (StateRepresentation<TState, TTrigger> s : _substates) {
            if (s.Includes(state)) {
                isIncluded = true;
            }
        }
        return _state.equals(state) || isIncluded;
    }

    public Boolean IsIncludedIn(TState state) {
        return
                _state.equals(state) ||
                        (_superstate != null && _superstate.IsIncludedIn(state));
    }

    @SuppressWarnings("unchecked")
    public List<TTrigger> getPermittedTriggers() throws Exception {
        List<TTrigger> result = new ArrayList<TTrigger>();

        for (TTrigger t : _triggerBehaviours.keySet()) {
            Boolean isOk = false;
            for (TriggerBehaviour<TState, TTrigger> v : _triggerBehaviours.get(t)) {
                if (v.isGuardConditionMet()) {
                    isOk = true;
                }
            }
            if (isOk) {
                result.add(t);
            }
        }

        if (getSuperstate() != null) {
            result.addAll(getSuperstate().getPermittedTriggers());
        }

        return result;
    }
}