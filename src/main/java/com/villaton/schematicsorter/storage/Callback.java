package com.villaton.schematicsorter.storage;

public interface Callback<E extends Throwable, V extends Object> {
    void call(E exception, V result);
}