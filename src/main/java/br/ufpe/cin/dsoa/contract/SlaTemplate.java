package br.ufpe.cin.dsoa.contract;

import java.util.List;

public class SlaTemplate {

	private String consumerPID;

	private String consumerName;

	private String qosMode;

	/* Interface do servi�o */
	private Class<?> specification;

	/* Lista de requisitos n�o funcionais */
	private List<Slo> slos;

	public SlaTemplate(String consumerPid, String consumerName, String qosMode,
			Class<?> specification, List<Slo> slos) {
		this.consumerPID = consumerPid;
		this.consumerName = consumerName;
		this.qosMode = qosMode;
		this.specification = specification;
		this.slos = slos;
	}

	public String getConsumerPid() {
		return consumerPID;
	}

	public void setConsumerPID(String consumerPID) {
		this.consumerPID = consumerPID;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	public String getQosMode() {
		return qosMode;
	}

	public void setQosMode(String qosMode) {
		this.qosMode = qosMode;
	}

	public Class<?> getSpecification() {
		return specification;
	}

	public void setSpecification(Class<?> specification) {
		this.specification = specification;
	}

	public List<Slo> getSlos() {
		return slos;
	}

	public void setSlos(List<Slo> slos) {
		this.slos = slos;
	}

}
