package com.getcoco.iotsampleapp;

import java.util.Collection;

public class Globals {
    public static final String IDENTIFIER = "identifier";

    public static <T, K extends T> Collection<K> downCast(Collection<T> collection) {
        return (Collection<K>) collection;
    }
}
