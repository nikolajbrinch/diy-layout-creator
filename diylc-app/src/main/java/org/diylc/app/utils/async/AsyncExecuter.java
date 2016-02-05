package org.diylc.app.utils.async;


@FunctionalInterface
public interface AsyncExecuter<T> {

    public T execute() throws Exception;
    
}
