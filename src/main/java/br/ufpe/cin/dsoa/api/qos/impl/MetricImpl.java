package br.ufpe.cin.dsoa.api.qos.impl;

import br.ufpe.cin.dsoa.api.core.impl.NamedElementImpl;
import br.ufpe.cin.dsoa.api.qos.Attribute;
import br.ufpe.cin.dsoa.api.qos.Metric;
import br.ufpe.cin.dsoa.util.Constants;

public class MetricImpl extends NamedElementImpl implements Metric {

	private Attribute attribute;
	private String fullname;
	
	public MetricImpl(Attribute att, String name) {
		super(name);
		this.attribute = att;
		this.fullname = attribute.getFullname() + Constants.TOKEN + name;
	}

	public Attribute getAttribute() {
		// TODO Auto-generated method stub
		return attribute;
	}

	@Override
	public String getFullname() {
		// TODO Auto-generated method stub
		return this.fullname;
	}

}
