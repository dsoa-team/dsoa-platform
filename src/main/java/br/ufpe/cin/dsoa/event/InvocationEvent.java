package br.ufpe.cin.dsoa.event;

public class InvocationEvent {

	private String provider;
	private String service;
	private String operation;
	private boolean success;
	private long requestTimestamp;
	private long responseTimestamp;
	private static Long nextId = 1L;

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
	
	public static Long getNextId() {
		return	nextId++;
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

	@Override
	public String toString() {
		return "InvocationEvent [provider=" + provider + ", service=" + service + ", operation=" + operation
				+ ", success=" + success + ", requestTimestamp=" + requestTimestamp + ", responseTimestamp="
				+ responseTimestamp + "]";
	}
}
