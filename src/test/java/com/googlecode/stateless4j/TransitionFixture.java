package com.googlecode.stateless4j;
import org.junit.Assert;
import org.junit.Test;

import com.googlecode.stateless4j.transitions.Transition;


public class TransitionFixture
{
    
    @Test
    public void IdentityTransitionIsNotChange()
    {
        Transition<Integer, Integer> t = new Transition<Integer, Integer>(1, 1, 0);
        Assert.assertTrue(t.isReentry());
    }

    
    @Test
    public void TransitioningTransitionIsChange()
    {
        Transition<Integer, Integer> t = new Transition<Integer, Integer>(1, 2, 0);
        Assert.assertFalse(t.isReentry());
    }
}
