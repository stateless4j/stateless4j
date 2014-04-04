package com.googlecode.stateless4j.conversion;

import com.googlecode.stateless4j.validation.Enforce;

public final class ParameterConversion {
    private ParameterConversion() {
    }

    public static Object unpack(Object[] args, Class<?> argType, int index) throws Exception {
        Enforce.argumentNotNull(args, "args");

        if (args.length <= index) {
            throw new Exception(
                    String.format("An argument of type %s is required in position %s.", argType, index));
        }

        Object arg = args[index];

        if (arg != null && !argType.isAssignableFrom(arg.getClass())) {
            throw new Exception(
                    String.format("The argument in position %s is of type %s but must be of type %s.", index, arg.getClass(), argType));
        }

        return arg;
    }

    public static void validate(Object[] args, Class<?>[] expected) throws Exception {
        if (args.length > expected.length) {
            throw new Exception(
                    String.format("Too many parameters have been supplied. Expecting %s but got %s.", expected.length, args.length));
        }

        for (int i = 0; i < expected.length; ++i) {
            unpack(args, expected[i], i);
        }
    }
}
