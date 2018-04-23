package com.rxjava.test.net;

public class HttpResult {
	private int result;
	private String value;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static class ResultCode {
		public static final int RESULT_OK = 200;
		public static final int RESULT_FAILURE = -1000;
		public static final int RESULT_TIME_OUT = -1001;
		public static final int RESULT_CONNECT_FAILURE = -1002;
	}

	@Override
	public String toString() {
		return "HttpResult [result=" + result + ", value=" + value + "]";
	}

}
