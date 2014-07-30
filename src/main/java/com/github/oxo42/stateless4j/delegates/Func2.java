package com.github.oxo42.stateless4j.delegates;

/**
 * Represents a function that accepts an input and produces a result
 *
 * @param <T1> Input argument type
 * @param <R>  Result type
 */
public interface Func2<T1, R> {

    /**
     * Applies this function to the given input
     *
     * @param arg1 Input argument
     * @return Result
     */
    R call(T1 arg1);
}
