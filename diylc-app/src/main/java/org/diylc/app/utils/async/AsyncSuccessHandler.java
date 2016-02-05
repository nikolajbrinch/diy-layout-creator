package org.diylc.app.utils.async;


@FunctionalInterface
public interface AsyncSuccessHandler<T> {

    public void success(T value);
    
}
