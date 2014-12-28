package com.github.oxo42.stateless4j.delegates;

/**
 * Represents an operation that accepts an input and returns success or fail
 *
 * @param <T> The type of the input to the operation
 */
public interface BooleanAction<T> {

    /**
     * Performs this operation on the given input
     *
     * @param arg1 Input argument
     */
    boolean doIt(T arg1);
}
