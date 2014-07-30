package com.github.oxo42.stateless4j.delegates;

/**
 * Represents an operation that accepts an input and returns no result
 *
 * @param <T> The type of the input to the operation
 */
public interface Action1<T> {

    /**
     * Performs this operation on the given input
     *
     * @param arg1 Input argument
     */
    void doIt(T arg1);
}
