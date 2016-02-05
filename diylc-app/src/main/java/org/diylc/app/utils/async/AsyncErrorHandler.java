package org.diylc.app.utils.async;

@FunctionalInterface
public interface AsyncErrorHandler {

    public void failure(Exception e);

}
