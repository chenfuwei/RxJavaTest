package com.rxjava.test.net;

public class HttpReqError extends Throwable{
    private int errorCode;

    public HttpReqError(int errorCode, String throwMsg)
    {
        super(throwMsg);
        this.errorCode = errorCode;
    }
}
