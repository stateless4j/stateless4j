package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.triggers.TriggerBehaviour;
import com.github.oxo42.stateless4j.validation.Enforce;

import java.util.*;

public class StateRepresentation<TState, TTrigger> {
    private final TState state;

    private final Map<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> triggerBehaviours = new HashMap<>();
    private final List<Action2<Transition<TState, TTrigger>, Object[]>> entryActions = new ArrayList<>();
    private final List<Action1<Transition<TState, TTrigger>>> exitActions = new ArrayList<>();
    private final List<StateRepresentation<TState, TTrigger>> substates = new ArrayList<>();
    private StateRepresentation<TState, TTrigger> superstate; // null

    public StateRepresentation(TState state) {
        this.state = state;
    }

    protected Map<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> getTriggerBehaviours() {
        return triggerBehaviours;
    }

    public Boolean canHandle(TTrigger trigger) {
        try {
            tryFindHandler(trigger);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public TriggerBehaviour<TState, TTrigger> tryFindHandler(TTrigger trigger) {
        try {
            return tryFindLocalHandler(trigger);
        } catch (Exception e) {
            return getSuperstate().tryFindHandler(trigger);
        }
    }

    TriggerBehaviour<TState, TTrigger> tryFindLocalHandler(TTrigger trigger/*, out TriggerBehaviour handler*/) {
        List<TriggerBehaviour<TState, TTrigger>> possible;
        if (!triggerBehaviours.containsKey(trigger)) {
            throw new IllegalStateException();
        }
        possible = triggerBehaviours.get(trigger);

        List<TriggerBehaviour<TState, TTrigger>> actual = new ArrayList<>();
        for (TriggerBehaviour<TState, TTrigger> triggerBehaviour : possible) {
            if (triggerBehaviour.isGuardConditionMet()) {
                actual.add(triggerBehaviour);
            }
        }

        if (actual.size() > 1) {
            throw new IllegalStateException(
                    String.format("Multiple permitted exit transitions are configured from state '%s' for trigger '%s'. Guard clauses must be mutually exclusive.",
                            trigger, state)
            );
        }

        TriggerBehaviour<TState, TTrigger> handler = actual.get(0);
        if (handler == null) {
            throw new IllegalStateException();
        }
        return handler;
    }

    public void addEntryAction(final TTrigger trigger, final Action2<Transition<TState, TTrigger>, Object[]> action) {
        Enforce.argumentNotNull(action, "action");


        entryActions.add(new Action2<Transition<TState, TTrigger>, Object[]>() {
            @Override
            public void doIt(Transition<TState, TTrigger> t, Object[] args) {
                if (t.getTrigger().equals(trigger)) {
                    action.doIt(t, args);
                }
            }
        });
    }

    public void addEntryAction(Action2<Transition<TState, TTrigger>, Object[]> action) {
        entryActions.add(Enforce.argumentNotNull(action, "action"));
    }

    public void insertEntryAction(Action2<Transition<TState, TTrigger>, Object[]> action) {
        entryActions.add(0, Enforce.argumentNotNull(action, "action"));
    }

    public void addExitAction(Action1<Transition<TState, TTrigger>> action) {
        exitActions.add(Enforce.argumentNotNull(action, "action"));
    }

    public void enter(Transition<TState, TTrigger> transition, Object... entryArgs) {
        Enforce.argumentNotNull(transition, "transtion");

        if (transition.isReentry()) {
            executeEntryActions(transition, entryArgs);
        } else if (!includes(transition.getSource())) {
            if (superstate != null) {
                superstate.enter(transition, entryArgs);
            }

            executeEntryActions(transition, entryArgs);
        }
    }

    public void exit(Transition<TState, TTrigger> transition) {
        Enforce.argumentNotNull(transition, "transtion");

        if (transition.isReentry()) {
            executeExitActions(transition);
        } else if (!includes(transition.getDestination())) {
            executeExitActions(transition);
            if (superstate != null) {
                superstate.exit(transition);
            }
        }
    }

    void executeEntryActions(Transition<TState, TTrigger> transition, Object[] entryArgs) {
        Enforce.argumentNotNull(transition, "transtion");
        Enforce.argumentNotNull(entryArgs, "entryArgs");
        for (Action2<Transition<TState, TTrigger>, Object[]> action : entryActions) {
            action.doIt(transition, entryArgs);
        }
    }

    void executeExitActions(Transition<TState, TTrigger> transition) {
        Enforce.argumentNotNull(transition, "transtion");
        for (Action1<Transition<TState, TTrigger>> action : exitActions) {
            action.doIt(transition);
        }
    }

    public void addTriggerBehaviour(TriggerBehaviour<TState, TTrigger> triggerBehaviour) {
        List<TriggerBehaviour<TState, TTrigger>> allowed;
        if (!triggerBehaviours.containsKey(triggerBehaviour.getTrigger())) {
            allowed = new ArrayList<>();
            triggerBehaviours.put(triggerBehaviour.getTrigger(), allowed);
        }
        allowed = triggerBehaviours.get(triggerBehaviour.getTrigger());
        allowed.add(triggerBehaviour);
    }

    public StateRepresentation<TState, TTrigger> getSuperstate() {
        return superstate;
    }

    public void setSuperstate(StateRepresentation<TState, TTrigger> value) {
        superstate = value;
    }

    public TState getUnderlyingState() {
        return state;
    }

    public void addSubstate(StateRepresentation<TState, TTrigger> substate) {
        Enforce.argumentNotNull(substate, "substate");
        substates.add(substate);
    }

    public Boolean includes(TState stateToCheck) {
        Boolean isIncluded = false;
        for (StateRepresentation<TState, TTrigger> s : substates) {
            if (s.includes(stateToCheck)) {
                isIncluded = true;
            }
        }
        return this.state.equals(stateToCheck) || isIncluded;
    }

    public Boolean isIncludedIn(TState stateToCheck) {
        return this.state.equals(stateToCheck) || (superstate != null && superstate.isIncludedIn(stateToCheck));
    }

    @SuppressWarnings("unchecked")
    public List<TTrigger> getPermittedTriggers() {
        Set<TTrigger> result = new HashSet<>();

        for (TTrigger t : triggerBehaviours.keySet()) {
            boolean isOk = false;
            for (TriggerBehaviour<TState, TTrigger> v : triggerBehaviours.get(t)) {
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

        return new ArrayList<>(result);
    }
}
