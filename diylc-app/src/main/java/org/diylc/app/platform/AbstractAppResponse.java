package org.diylc.app.platform;

public abstract class AbstractAppResponse {

    private final Object response;

    private final Class<?> responseClass;

    public AbstractAppResponse(Object response) {
        this.response = response;
        this.responseClass = getResponse().getClass();
    }

    public Object getResponse() {
        return response;
    }

    public Class<?> getResponseClass() {
        return responseClass;
    }
}
