package com.googlecode.stateless4j.delegates;

public interface Action4<T, T1, T2, T3> {
    public void doIt(T arg1, T1 arg2, T2 arg3, T3 arg4) throws Exception;
}
