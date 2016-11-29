package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.transitions.Transition;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransitionTests {

    @Test
    public void IdentityTransitionIsNotChange() {
        Transition<Integer, Integer> t = new Transition<Integer, Integer>(1, 1, 0);
        assertTrue(t.isReentry());
    }

    @Test
    public void TransitioningTransitionIsChange() {
        Transition<Integer, Integer> t = new Transition<Integer, Integer>(1, 2, 0);
        assertFalse(t.isReentry());
    }
}
