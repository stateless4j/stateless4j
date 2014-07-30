package com.github.oxo42.stateless4j.conversion;

public final class ParameterConversion {

    private ParameterConversion() {
    }

    public static Object unpack(Object[] args, Class<?> argType, int index) {
        assert args != null : "args is null";

        if (args.length <= index) {
            throw new IllegalStateException(
                    String.format("An argument of type %s is required in position %s.", argType, index));
        }

        Object arg = args[index];

        if (arg != null && !argType.isAssignableFrom(arg.getClass())) {
            throw new IllegalStateException(
                    String.format("The argument in position %s is of type %s but must be of type %s.", index, arg.getClass(), argType));
        }

        return arg;
    }

    public static void validate(Object[] args, Class<?>[] expected) {
        if (args.length > expected.length) {
            throw new IllegalStateException(
                    String.format("Too many parameters have been supplied. Expecting %s but got %s.", expected.length, args.length));
        }

        for (int i = 0; i < expected.length; ++i) {
            unpack(args, expected[i], i);
        }
    }
}
