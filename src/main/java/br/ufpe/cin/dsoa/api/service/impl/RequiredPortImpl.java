package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.RequiredPort;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;



public class RequiredPortImpl extends PortImpl implements RequiredPort {
	
	private boolean mandatory = true;
	
	public RequiredPortImpl(String name, ServiceSpecification serviceSpec) {
		super(name, serviceSpec);
	}
	
	public RequiredPortImpl(String name, ServiceSpecification serviceSpec,
			boolean mandatory) {
		super(name, serviceSpec);
		this.mandatory = mandatory;
	}

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.api.service.impl.RequiredPort#setMandatory(boolean)
	 */
	@Override
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.api.service.impl.RequiredPort#isMandatory()
	 */
	@Override
	public boolean isMandatory() {
		return mandatory;
	}
	
}
