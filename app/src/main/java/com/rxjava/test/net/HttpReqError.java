package com.rxjava.test.net;

public class HttpReqError extends Throwable{
    private int httpCode;
    private int dataCode;

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public HttpReqError(int httpCode, int dataCode)
    {
        this.httpCode = httpCode;
        this.dataCode = dataCode;
    }
}
