package br.ufpe.cin.dsoa.event;

public class InvocationEvent {

	private String provider;
	private String service;
	private String operation;
	private boolean success;
	private long requestTimestamp;
	private long responseTimestamp;

	public InvocationEvent(String providerId, String service, String operation,
			boolean success, long requestTimestamp, long responseTimestamp) {
		super();
		this.service = service;
		this.provider = providerId;
		this.operation = operation;
		this.success = success;
		this.requestTimestamp = requestTimestamp;
		this.responseTimestamp = responseTimestamp;
	}

	public String getProvider() {
		return provider;
	}

	public String getService() {
		return this.service;
	}
	
	public String getOperation() {
		return operation;
	}

	public boolean isSuccess() {
		return success;
	}

	public long getRequestTimestamp() {
		return requestTimestamp;
	}

	public long getResponseTimestamp() {
		return responseTimestamp;
	}
}
