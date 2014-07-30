package com.github.oxo42.stateless4j;

import org.junit.Test;

import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;

import static org.junit.Assert.*;

public class TriggerWithParametersTests {

    @Test
    public void DescribesUnderlyingTrigger() {
        TriggerWithParameters1<String, State, Trigger> twp = new TriggerWithParameters1<>(Trigger.X, String.class);
        assertEquals(Trigger.X, twp.getTrigger());
    }

    @Test
    public void ParametersOfCorrectTypeAreAccepted() {
        TriggerWithParameters1<String, State, Trigger> twp = new TriggerWithParameters1<>(Trigger.X, String.class);
        twp.validateParameters(new Object[]{"arg"});
    }

    @Test
    public void ParametersArePolymorphic() {
        TriggerWithParameters1<String, State, Trigger> twp = new TriggerWithParameters1<>(Trigger.X, String.class);
        twp.validateParameters(new Object[]{"arg"});
    }

    @Test(expected = IllegalStateException.class)
    public void IncompatibleParametersAreNotValid() {
        TriggerWithParameters1<String, State, Trigger> twp = new TriggerWithParameters1<>(Trigger.X, String.class);
        twp.validateParameters(new Object[]{123});
    }

    @Test(expected = IllegalStateException.class)
    public void TooFewParametersDetected() {
        TriggerWithParameters2<String, String, State, Trigger> twp = new TriggerWithParameters2<>(Trigger.X, String.class, String.class);
        twp.validateParameters(new Object[]{"a"});
    }

    @Test(expected = IllegalStateException.class)
    public void TooManyParametersDetected() {
        TriggerWithParameters2<String, String, State, Trigger> twp = new TriggerWithParameters2<>(Trigger.X, String.class, String.class);
        twp.validateParameters(new Object[]{"a", "b", "c"});
    }
}
