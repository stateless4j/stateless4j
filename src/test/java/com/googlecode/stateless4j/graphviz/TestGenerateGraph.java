package com.googlecode.stateless4j.graphviz;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;

import com.googlecode.stateless4j.State;
import com.googlecode.stateless4j.StateMachine;
import com.googlecode.stateless4j.Trigger;
import com.googlecode.stateless4j.helpers.InputStreamHelper;

public class TestGenerateGraph {

	@Test
	public void testGenerateSimpleGraph() throws Exception {
		StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.A);
        sm.Configure(State.A)
	    	.Permit(Trigger.X, State.B)
	    	.Permit(Trigger.Y, State.C);
        
        sm.Configure(State.B)
    		.Permit(Trigger.Y, State.C);
        
        sm.Configure(State.C)
    		.Permit(Trigger.X, State.A);
        
        ByteArrayOutputStream dotFile = new ByteArrayOutputStream();
        sm.GenerateDotFileInto(dotFile);
        InputStream expected = this.getClass().getResourceAsStream("/simpleGraph.txt");
        String expectedStr = InputStreamHelper.readAsString(expected);
        String actual = new String(dotFile.toByteArray(), "UTF-8");
        Assert.assertEquals(expectedStr, actual);
	}
}
