package br.ufpe.cin.dsoa.api.attribute;

import br.ufpe.cin.dsoa.util.Constants;


public class AttributableId {

	private String serviceId;
	private String operationName;
	private transient String id;

	public AttributableId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public AttributableId(String serviceId, String operationName) {
		this.serviceId = serviceId;
		this.operationName = operationName;
	}

	public String getServiceId() {
		return this.serviceId;
	}
	
	public String getOperationName() {
		return this.operationName;
	}
	
	public String toString() {
		return getId();
	}

	public String getId() {
		if (id == null) {
			StringBuffer buffer = new StringBuffer(this.serviceId);
			if (this.operationName != null) {
				buffer.append(Constants.TOKEN).append(this.operationName);
			}
			id = buffer.toString();
		}
		return id;
	}
}
