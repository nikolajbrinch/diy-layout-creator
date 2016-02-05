package org.diylc.app.utils.async;

/**
 * Abstract layer between the app and platform worker thread implementation.
 * 
 * @author Branislav Stojkovic
 * 
 * @param <T>
 */
public interface AsyncTask<T> {

    /**
     * Runs in background thread.
     * 
     * @return
     * @throws Exception
     */
    public T execute() throws Exception;

    /**
     * Called if background thread is executed correctly, the result is passed
     * as a parameter.
     * 
     * @param result
     */
    default void complete(T result) {
    }

    /**
     * Called if background thread fails.
     * 
     * @param e
     */
    default void failed(Exception e) {
    }

}
