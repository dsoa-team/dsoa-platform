package br.ufpe.cin.dsoa.contract;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.monitor.MonitoringListener;

public class Sla {

	/* Template do Sla */
	private SlaTemplate template;

	/* Referencia para o serviço real */
	private ServiceReference serviceReference;
	
	/* Responsavel por escutar as quebras de contrato */
	private MonitoringListener listener;

	public Sla(String consumerPid, String consumerName, String qosMode,
			Class<?> specification, List<Slo> slos) {
		this.template = new SlaTemplate(consumerPid, consumerName, qosMode, specification, slos);
	}

	public String getConsumerPid() {
		return template.getConsumerPid();
	}

	public void setConsumerPid(String consumerPid) {
		this.template.setConsumerPid(consumerPid);
	}

	public String getConsumerName() {
		return template.getConsumerName();
	}

	public void setConsumerName(String consumerName) {
		template.setConsumerName(consumerName);
	}

	public String getQosMode() {
		return template.getQosMode();
	}

	public void setQosMode(String qosMode) {
		template.setQosMode(qosMode);
	}

	public Class<?> getSpecification() {
		return template.getSpecification();
	}

	public void setSpecification(Class<?> specification) {
		template.setSpecification(specification);
	}

	public List<Slo> getSlos() {
		return template.getSlos();
	}

	public void setSlos(List<Slo> slos) {
		template.setSlos(slos);
	}

	public String toString() {
		return template.toString();
	}

	public void setServiceReference(ServiceReference serviceReference) {
		this.serviceReference = serviceReference;
	}

	public Object getService() {
		return this.serviceReference.getBundle().getBundleContext().getService(serviceReference);
	}

	public ServiceReference getServiceReference() {
		return serviceReference;
	}
}
