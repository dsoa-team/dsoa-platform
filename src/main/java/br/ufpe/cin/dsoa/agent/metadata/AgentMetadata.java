package br.ufpe.cin.dsoa.agent.metadata;

public class AgentMetadata {

	private String qosAttribute;
	private String returnType;
	private String description;
	private String eventClass;

	public String getQosAttribute() {
		return qosAttribute;
	}

	public void setQosAttribute(String qosAttribute) {
		this.qosAttribute = qosAttribute;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventClass() {
		return eventClass;
	}

	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}

}
