package br.ufpe.cin.dsoa.agent.metadata;

//TODO:
public class AgentBuilder {

	private AgentMetadata metadata;

	public AgentBuilder() {
		metadata = new AgentMetadata();
	}

	public AgentBuilder qosAttribute(String attribute) {
		metadata.setQosAttribute(attribute);
		return this;
	}

	public AgentBuilder returnType(String returnType) {
		metadata.setReturnType(returnType);
		return this;
	}

	public AgentBuilder description(String description) {
		metadata.setDescription(description);
		return this;
	}

	public AgentBuilder eventDefinition(String clazz) {
		metadata.setEventClass(clazz);
		return this;
	}

	public AgentMetadata build() {
		return metadata;
	}
}
