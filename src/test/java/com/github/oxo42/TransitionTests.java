package com.github.oxo42;
import com.github.oxo42.stateless.transitions.Transition;
import org.junit.Assert;
import org.junit.Test;


public class TransitionTests
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
