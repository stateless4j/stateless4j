package com.github.oxo42.stateless4j.validation;

public final class Enforce {
    private Enforce() {

    }

    public static <T> T argumentNotNull(T argument, String description) {
        if (argument == null) {
            throw new IllegalStateException(description);
        }

        return argument;
    }
}

