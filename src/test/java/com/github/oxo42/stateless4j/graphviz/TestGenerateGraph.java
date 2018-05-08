package com.github.oxo42.stateless4j.graphviz;

import com.github.oxo42.stateless4j.State;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.Trigger;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertTrue;

public class TestGenerateGraph {

    @Test
    public void testGenerateSimpleGraph() throws IOException {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();
        config.configure(State.A)
                .permit(Trigger.X, State.B)
                .permit(Trigger.Y, State.C);

        config.configure(State.B)
                .permit(Trigger.Y, State.C);

        config.configure(State.C)
                .permit(Trigger.X, State.A);

        ByteArrayOutputStream dotFile = new ByteArrayOutputStream();
        config.generateDotFileInto(dotFile);
        String actual = new String(dotFile.toByteArray(), "UTF-8");

        assertTrue(actual.contains("digraph G"));
        assertTrue(actual.contains("\tC -> A;\n"));
        assertTrue(actual.contains("\tB -> C;\n"));
        assertTrue(actual.contains("\tA -> C;\n"));
        assertTrue(actual.contains("\tA -> B;\n"));
    }
}
