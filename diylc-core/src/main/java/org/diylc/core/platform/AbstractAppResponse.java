package org.diylc.core.platform;

public abstract class AbstractAppResponse {

    private final Object response;

    private final Class<?> responseClass;

    public AbstractAppResponse(Object response) {
        this.response = response;
        this.responseClass = getResponse() != null ? getResponse().getClass() : null;
    }

    public Object getResponse() {
        return response;
    }

    public Class<?> getResponseClass() {
        return responseClass;
    }
}
