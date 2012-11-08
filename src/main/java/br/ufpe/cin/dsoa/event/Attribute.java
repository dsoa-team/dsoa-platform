package br.ufpe.cin.dsoa.event;

public class Attribute<T> {
	private AttributeType<T> attributeType;
	private T value;
	
	public AttributeType<T> getAttributeType() {
		return attributeType;
	}
	public T getValue() {
		return value;
	}
	
	
}
