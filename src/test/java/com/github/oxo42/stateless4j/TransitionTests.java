package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.transitions.Transition;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TransitionTests {

    @Test
    public void IdentityTransitionIsNotChange() {
        Transition<Integer, Integer> t = new Transition<>(1, 1, 0);
        assertTrue(t.isReentry());
    }

    @Test
    public void TransitioningTransitionIsChange() {
        Transition<Integer, Integer> t = new Transition<>(1, 2, 0);
        assertFalse(t.isReentry());
    }
}
