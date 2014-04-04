package com.googlecode.stateless4j.graphviz;

import com.googlecode.stateless4j.State;
import com.googlecode.stateless4j.StateMachine;
import com.googlecode.stateless4j.Trigger;
import com.googlecode.stateless4j.helpers.InputStreamHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class TestGenerateGraph {

    // This isn't going to work because the StateMachine uses a HashMap which does not maintain a consistent output
    // Changing it to LinkedHashMap will make this test work all the time but will incur a runtime performance penalty
    // @Test
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

        assertEquals(expectedStr, actual);
    }
}
