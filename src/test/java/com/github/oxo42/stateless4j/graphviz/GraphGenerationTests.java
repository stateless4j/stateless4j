package com.github.oxo42.stateless4j.graphviz;

import com.github.oxo42.stateless4j.State;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.Trigger;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

public class GraphGenerationTests {

    @Test
    public void GeneratesDotFileForGraph() throws IOException {
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
        String actual = new String(dotFile.toByteArray(), StandardCharsets.UTF_8);

        assertTrue(actual.contains("digraph G"));
        assertTrue(actual.contains("\tC -> A;\n"));
        assertTrue(actual.contains("\tB -> C;\n"));
        assertTrue(actual.contains("\tA -> C;\n"));
        assertTrue(actual.contains("\tA -> B;\n"));
    }

    @Test
    public void GeneratesDotFileForGraphWithLabels() throws IOException {
        StateMachineConfig<State, Trigger> config = new StateMachineConfig<>();
        config.configure(State.A)
                .permit(Trigger.X, State.B)
                .permit(Trigger.Y, State.C);

        config.configure(State.B)
                .permit(Trigger.Y, State.C);

        config.configure(State.C)
                .permit(Trigger.X, State.A);

        ByteArrayOutputStream dotFile = new ByteArrayOutputStream();
        config.generateDotFileInto(dotFile, true);
        String actual = new String(dotFile.toByteArray(), StandardCharsets.UTF_8);

        assertTrue(actual.contains("digraph G"));
        assertTrue(actual.contains("\tA -> C [label = \"Y\" ];\n"));
        assertTrue(actual.contains("\tA -> B [label = \"X\" ];\n"));
        assertTrue(actual.contains("\tC -> A [label = \"X\" ];\n"));
        assertTrue(actual.contains("\tB -> C [label = \"Y\" ];\n"));
    }

}
