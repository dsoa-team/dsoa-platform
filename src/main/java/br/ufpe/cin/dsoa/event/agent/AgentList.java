package br.ufpe.cin.dsoa.event.agent;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "agents")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentList {

	public static final String CONFIG = "DSOA-INF/agent.xml";
	
	@XmlElement(name = "agent")
	private List<EventProcessingAgent> agentList;

	public List<EventProcessingAgent> getAgents() {
		return agentList;
	}

	public void setAgents(List<EventProcessingAgent> agentList) {
		this.agentList = agentList;
	}
}
