package com.github.oxo42.stateless4j;

import java.util.ArrayList;
import java.util.List;

import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.delegates.Action3;
import com.github.oxo42.stateless4j.delegates.Action4;
import com.github.oxo42.stateless4j.delegates.Func;
import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.delegates.Func3;
import com.github.oxo42.stateless4j.delegates.Func4;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters3;

import org.junit.Test;
import static org.junit.Assert.*;

public class DynamicTransitionActionTests {
	
    final Enum StateA = State.A, StateB = State.B, StateC = State.C,
            TriggerX = Trigger.X, TriggerY = Trigger.Y;
    
    final private TriggerWithParameters1<Integer, State, Trigger> TriggerX1 =
    		new TriggerWithParameters1<Integer, State, Trigger>(Trigger.X, Integer.class);
    
    final private TriggerWithParameters2<Integer, Integer, State, Trigger> TriggerY2 =
    		new TriggerWithParameters2<Integer, Integer, State, Trigger>(Trigger.Y, Integer.class, Integer.class);
    
    final private TriggerWithParameters3<Integer, Integer, Integer, State, Trigger> TriggerY3 =
    		new TriggerWithParameters3<Integer, Integer, Integer, State, Trigger>(Trigger.Y, Integer.class, Integer.class, Integer.class);

    
    private class DynamicallyGotoState<T> implements Func<State>, Func2<T, State>, Func3<T, T, State>, Func4<T, T, T, State> {

    	private State targetState;
    	
    	public DynamicallyGotoState(State whereToGo) {
    		this.targetState = whereToGo;
    	}
    	
        @Override
        public State call() {
            return this.targetState;
        }

        @Override
        public State call(T value) {
            return this.targetState;
        }

        @Override
        public State call(T val1, T val2) {
            return this.targetState;
        }

        @Override
        public State call(T val1, T val2, T val3) {
            return this.targetState;
        }
    };
    
    private DynamicallyGotoState<Integer> gotoA = new DynamicallyGotoState<Integer>(State.A);
    private DynamicallyGotoState<Integer> gotoB = new DynamicallyGotoState<Integer>(State.B);

    private class AccumulatingAction<T> implements Action1<T>, Action2<T,T>, Action3<T,T,T>, Action4<T,T,T,T> {
        private List<T> accumulator;

        public AccumulatingAction(List<T> accumulator) {
            this.accumulator = accumulator;
        }

		@Override
		public void doIt(T arg1, T arg2, T arg3, T arg4) {
        	accumulator.add(arg1);
        	accumulator.add(arg2);
        	accumulator.add(arg3);
        	accumulator.add(arg4);	
		}

		@Override
		public void doIt(T arg1, T arg2, T arg3) {
        	accumulator.add(arg1);
        	accumulator.add(arg2);
        	accumulator.add(arg3);	
		}

		@Override
		public void doIt(T arg1, T arg2) {
        	accumulator.add(arg1);
        	accumulator.add(arg2);
		}

		@Override
		public void doIt(T arg1) {
        	accumulator.add(arg1);
		}
    }
    
    private class FixedAccumulator<T> extends AccumulatingAction<T> implements Action {
    	private T fixedItem;
    	
    	public FixedAccumulator(List<T> accumulator, T fixedItem) {
    		super(accumulator);
    		this.fixedItem = fixedItem;
    	}
    	
    	@Override
    	public void doIt() {
    		doIt(this.fixedItem);
    	}
    }
    
    @Test
    public void UnguardedDynamicTransitionActionsArePerformed() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        List<Integer> list = new ArrayList<Integer>();
        FixedAccumulator<Integer>   actionZero = new FixedAccumulator<Integer>(list, new Integer(0));
        AccumulatingAction<Integer> actionOne = new AccumulatingAction<Integer>(list);
        AccumulatingAction<Integer> actionTwo = new AccumulatingAction<Integer>(list);
        AccumulatingAction<Integer> actionThree = new AccumulatingAction<Integer>(list);

        config.configure(State.A)
                .permitDynamic(Trigger.X, gotoB, actionZero)
                .permitDynamic(TriggerY2, gotoB, actionTwo);
        config.configure(State.B)
        		.permitDynamic(TriggerX1, gotoA, actionOne)
        		.permitDynamic(TriggerY3, gotoA, actionThree);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(Trigger.X);
        sm.fire(TriggerX1, new Integer(2));
        sm.fire(TriggerY2, new Integer(4), new Integer(6));
        sm.fire(TriggerY3, new Integer(8), new Integer(10), new Integer(12));

        assertEquals(State.A, sm.getState());
        assertEquals(7, list.size());
        assertEquals(new Integer(0), list.get(0));
        assertEquals(new Integer(2), list.get(1));
        assertEquals(new Integer(4), list.get(2));
        assertEquals(new Integer(6), list.get(3));
        assertEquals(new Integer(8), list.get(4));
        assertEquals(new Integer(10), list.get(5));
        assertEquals(new Integer(12), list.get(6));
    }

    @Test
    public void GuardedDynamicTransitionActionsArePerformed() {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();

        List<Integer> list = new ArrayList<Integer>();
        FixedAccumulator<Integer>   actionZero = new FixedAccumulator<Integer>(list, new Integer(0));
        AccumulatingAction<Integer> actionOne = new AccumulatingAction<Integer>(list);
        AccumulatingAction<Integer> actionTwo = new AccumulatingAction<Integer>(list);
        AccumulatingAction<Integer> actionThree = new AccumulatingAction<Integer>(list);

        config.configure(State.A)
                .permitDynamicIf(Trigger.X, gotoB, IgnoredTriggerBehaviourTests.returnTrue, actionZero)
                .permitDynamicIf(TriggerY2, gotoB, IgnoredTriggerBehaviourTests.returnTrue, actionTwo);
        config.configure(State.B)
        		.permitDynamicIf(TriggerX1, gotoA, IgnoredTriggerBehaviourTests.returnTrue, actionOne)
        		.permitDynamicIf(TriggerY3, gotoA, IgnoredTriggerBehaviourTests.returnTrue, actionThree);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);
        sm.fire(Trigger.X);
        sm.fire(TriggerX1, new Integer(3));
        sm.fire(TriggerY2, new Integer(6), new Integer(9));
        sm.fire(TriggerY3, new Integer(12), new Integer(15), new Integer(18));

        assertEquals(State.A, sm.getState());
        assertEquals(7, list.size());
        assertEquals(new Integer(0), list.get(0));
        assertEquals(new Integer(3), list.get(1));
        assertEquals(new Integer(6), list.get(2));
        assertEquals(new Integer(9), list.get(3));
        assertEquals(new Integer(12), list.get(4));
        assertEquals(new Integer(15), list.get(5));
        assertEquals(new Integer(18), list.get(6));
    }
}
