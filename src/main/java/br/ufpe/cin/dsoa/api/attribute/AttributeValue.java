package br.ufpe.cin.dsoa.api.attribute;

import java.util.Map;

import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.util.Constants;


public class AttributeValue {

	private Attribute attribute;
	private Map<String, Property> metadata;
	private Map<String, Property> data;
	
	
	public AttributeValue(Attribute attribute, Map<String, Property> attMetadata, Map<String, Property> attData) {
		this.attribute = attribute;
		this.metadata = attMetadata;
		this.data = attData;
	}


	public Attribute getAttribute() {
		return attribute;
	}


	public Map<String, Property> getMetadata() {
		return metadata;
	}


	public Map<String, Property> getData() {
		return data;
	}


	public Object getValue() {
		return this.data.get(Constants.ATTRIBUTE_VALUE).getValue();
	}


	@Override
	public String toString() {
		return "AttributeValue [attribute=" + attribute + ", metadata=" + metadata + ", data=" + data + "]";
	}
}
