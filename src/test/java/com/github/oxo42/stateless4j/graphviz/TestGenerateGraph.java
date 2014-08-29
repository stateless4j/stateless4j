package com.github.oxo42.stateless4j.graphviz;

import com.github.oxo42.stateless4j.State;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.Trigger;
import com.github.oxo42.stateless4j.helpers.InputStreamHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class TestGenerateGraph {

    // This isn't going to work because the StateMachine uses a HashMap which does not maintain a consistent output
    // Changing it to LinkedHashMap will make this test work all the time but will incur a runtime performance penalty
    // @Test
    public void testGenerateSimpleGraph() throws UnsupportedEncodingException, IOException {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();
        config.configure(State.A)
                .permit(Trigger.X, State.B)
                .permit(Trigger.Y, State.C);

        config.configure(State.B)
                .permit(Trigger.Y, State.C);

        config.configure(State.C)
                .permit(Trigger.X, State.A);

        StateMachine<State, Trigger> sm = new StateMachine<>(State.A, config);

        ByteArrayOutputStream dotFile = new ByteArrayOutputStream();
        config.generateDotFileInto(dotFile);
        InputStream expected = this.getClass().getResourceAsStream("/simpleGraph.txt");
        String expectedStr = InputStreamHelper.readAsString(expected);
        String actual = new String(dotFile.toByteArray(), "UTF-8");

        assertEquals(expectedStr, actual);
    }
}
