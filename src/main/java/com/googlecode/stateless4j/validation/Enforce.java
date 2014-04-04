package com.googlecode.stateless4j.validation;

public class Enforce
{
    public static <T extends Object> T ArgumentNotNull(T argument, String description) throws Exception
    {
        if (argument == null)
            throw new Exception(description);

        return argument;
    }
}
