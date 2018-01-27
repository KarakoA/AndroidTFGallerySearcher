package de.htw_berlin.f4.ml.gallerysearcher.utils;

/**
 * A simple callback interface.
 */
public interface Callback<T> {
    /**
     * Call the callback with the given parameter.
     */
    void call(T param);
}
