package br.ufpe.cin.dsoa.event;

public class InvocationEvent {

	private String providerId;
	private String operationName;
	private boolean success;
	private long resquestTimestamp;
	private long responseTimestamp;

	public InvocationEvent(String providerId, String operationName,
			boolean success, long resquestTimestamp, long responseTimestamp) {
		super();
		this.providerId = providerId;
		this.operationName = operationName;
		this.success = success;
		this.resquestTimestamp = resquestTimestamp;
		this.responseTimestamp = responseTimestamp;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getOperationName() {
		return operationName;
	}

	public boolean isSuccess() {
		return success;
	}

	public long getRequestTimestamp() {
		return resquestTimestamp;
	}

	public long getResponseTimestamp() {
		return responseTimestamp;
	}
}
