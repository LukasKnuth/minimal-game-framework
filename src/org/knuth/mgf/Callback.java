package org.knuth.mgf;

/**
 * A simple callback, used throughout the framework to execute a previously
 *  specified task at a specific time or in reaction to a specified action.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface Callback{
    public void call();
}
