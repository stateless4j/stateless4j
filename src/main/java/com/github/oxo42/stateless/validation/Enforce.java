package com.github.oxo42.stateless.validation;

public final class Enforce {
    private Enforce() {

    }

    public static <T> T argumentNotNull(T argument, String description) throws Exception {
        if (argument == null) {
            throw new Exception(description);
        }

        return argument;
    }
}

