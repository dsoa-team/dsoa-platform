package br.ufpe.cin.dsoa.contract;

import java.util.List;


public class Contract {

	/*
	 * <wsla:sla xmlns:xsi="http://www3.org/2001/XMLSchema-instance"
xmlns:wsla="http://www.ibm.com/wsla"

name="StockquoteServiceLevelAgreement12345">

  <parties> ... </parties>

  <servicedefinition> ... </servicedefinition>

  <obligations> ... </obligations>

</wsla:sla>
	 */
	private Consumer consumer;
	private Provider provider;
	private List<Slo> slos;

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public List<Slo> getSlos() {
		return slos;
	}

	public void setSlos(List<Slo> slos) {
		this.slos = slos;
	}

}
