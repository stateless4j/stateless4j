package com.googlecode.stateless4j;

import junit.framework.Assert;

import org.junit.Test;

import com.googlecode.stateless4j.triggers.TriggerWithParameters1;
import com.googlecode.stateless4j.triggers.TriggerWithParameters2;


public class TriggerWithParametersTests {
    @Test
    public void DescribesUnderlyingTrigger() throws Exception {
        TriggerWithParameters1<String, State, Trigger> twp = new TriggerWithParameters1<String, State, Trigger>(Trigger.X, String.class);
        Assert.assertEquals(Trigger.X, twp.getTrigger());
    }

    @Test
    public void ParametersOfCorrectTypeAreAccepted() throws Exception {
        TriggerWithParameters1<String, State, Trigger> twp = new TriggerWithParameters1<String, State, Trigger>(Trigger.X, String.class);
        twp.ValidateParameters(new Object[]{"arg"});
    }

    @Test
    public void ParametersArePolymorphic() throws Exception {
        TriggerWithParameters1<String, State, Trigger> twp = new TriggerWithParameters1<String, State, Trigger>(Trigger.X, String.class);
        twp.ValidateParameters(new Object[]{"arg"});
    }

    @Test
    public void IncompatibleParametersAreNotValid() {
        try {
            TriggerWithParameters1<String, State, Trigger> twp = new TriggerWithParameters1<String, State, Trigger>(Trigger.X, String.class);
            twp.ValidateParameters(new Object[]{123});
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void TooFewParametersDetected() throws Exception {
        try {
            TriggerWithParameters2<String, String, State, Trigger> twp = new TriggerWithParameters2<String, String, State, Trigger>(Trigger.X, String.class, String.class);
            twp.ValidateParameters(new Object[]{"a"});
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void TooManyParametersDetected() throws Exception {
        try {
            TriggerWithParameters2<String, String, State, Trigger> twp = new TriggerWithParameters2<String, String, State, Trigger>(Trigger.X, String.class, String.class);
            twp.ValidateParameters(new Object[]{"a", "b", "c"});
            Assert.fail();
        } catch (Exception e) {
        }
    }
}
