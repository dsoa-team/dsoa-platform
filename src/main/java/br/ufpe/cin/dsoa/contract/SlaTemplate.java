package br.ufpe.cin.dsoa.contract;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.monitor.MonitoringListener;

public class SlaTemplate {

	/* Id do consumer */
	private String consumerPID;

	/* Nome do consumer */
	private String consumerName;

	
	private String qosMode;

	/* Interface do serviço */
	private Class<?> specification;

	/* Lista de requisitos não funcionais */
	private List<Slo> slos;

	/* Responsavel por escutar as quebras de contrato */
	private MonitoringListener listener;

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

	public void setConsumerPid(String consumerPid) {
		this.consumerPID = consumerPid;
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
