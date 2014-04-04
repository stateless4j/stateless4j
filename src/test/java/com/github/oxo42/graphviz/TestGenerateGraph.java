package com.github.oxo42.graphviz;

import com.github.oxo42.State;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.Trigger;
import com.github.oxo42.helpers.InputStreamHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class TestGenerateGraph {

    // This isn't going to work because the StateMachine uses a HashMap which does not maintain a consistent output
    // Changing it to LinkedHashMap will make this test work all the time but will incur a runtime performance penalty
    // @Test
    public void testGenerateSimpleGraph() throws Exception {
        StateMachine<State, Trigger> sm = new StateMachine<State, Trigger>(State.A);
        sm.configure(State.A)
                .permit(Trigger.X, State.B)
                .permit(Trigger.Y, State.C);

        sm.configure(State.B)
                .permit(Trigger.Y, State.C);

        sm.configure(State.C)
                .permit(Trigger.X, State.A);

        ByteArrayOutputStream dotFile = new ByteArrayOutputStream();
        sm.generateDotFileInto(dotFile);
        InputStream expected = this.getClass().getResourceAsStream("/simpleGraph.txt");
        String expectedStr = InputStreamHelper.readAsString(expected);
        String actual = new String(dotFile.toByteArray(), "UTF-8");

        assertEquals(expectedStr, actual);
    }
}
