package br.ufpe.cin.dsoa.event.legancy;

public class InvocationEventOld {

	private String providerId;
	private String operationName;
	private Class<?>[] parameterTypes;
	private Object[] parameterValues;
	private Class<?> returnType;
	private Object returnValue;
	private boolean success;
	private Exception exception;
	private long resquestTimestamp;
	private long responseTimestamp;

	public InvocationEventOld(String providerId,
			String operationName, Class<?>[] parameterTypes, Object[] parameterValues,
			Class<?> returnType, Object returnValue, boolean success,
			Exception exception, long startTime, long nanoTime) {
		super();
		this.providerId = providerId;
		this.operationName = operationName;
		this.parameterTypes = parameterTypes;
		this.parameterValues = parameterValues;
		this.returnType = returnType;
		this.returnValue = returnValue;
		this.success = success;
		this.exception = exception;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getOperationName() {
		return operationName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public Object[] getParameterValues() {
		return parameterValues;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public boolean isSuccess() {
		return success;
	}

	public Exception getException() {
		return exception;
	}

	public long getRequestTimestamp() {
		return resquestTimestamp;
	}

	public long getResponseTimestamp() {
		return responseTimestamp;
	}
}
