package br.ufpe.cin.dsoa.event;

public class InvocationEvent {

	private String providerId;
	private String service;
	private String operation;
	private boolean success;
	private long resquestTimestamp;
	private long responseTimestamp;

	public InvocationEvent(String providerId, String service, String operationName,
			boolean success, long resquestTimestamp, long responseTimestamp) {
		super();
		this.service = service;
		this.providerId = providerId;
		this.operation = operationName;
		this.success = success;
		this.resquestTimestamp = resquestTimestamp;
		this.responseTimestamp = responseTimestamp;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getService() {
		return this.service;
	}
	
	public String getOperationName() {
		return operation;
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
