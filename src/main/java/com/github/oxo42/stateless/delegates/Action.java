package com.github.oxo42.stateless.delegates;

/**
 * Represents an operation that accepts no input arguments and returns no result.
 */
public interface Action {
    /**
     * Performs this operation
     */
    void doIt() throws Exception;
}
