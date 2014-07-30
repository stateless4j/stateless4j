package com.github.oxo42.stateless4j.delegates;

/**
 * Represents an operation that accepts an input and returns no result
 *
 * @param <T>  The type of the input
 * @param <T1> The type of the input
 */
public interface Action2<T, T1> {

    /**
     * Performs this operation on the given input
     *
     * @param arg1 Input argument
     * @param arg2 Input argument
     */
    void doIt(T arg1, T1 arg2);
}
