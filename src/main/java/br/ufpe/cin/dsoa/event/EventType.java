package br.ufpe.cin.dsoa.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventType {
	
	private String id;
	
	private boolean composite;
	
	private Map<String, AttributeType<?>> headerAttributeTypes;
	
	private Map<String, AttributeType<?>> applicationAttributeTypes;
	
	private EventType superType = null;

	public EventType(String id, boolean composite,
			Map<String, AttributeType<?>> headerAttributes,
			Map<String, AttributeType<?>> applicationAttributes) {
		super();
		this.id = id;
		this.composite = composite;
		this.headerAttributeTypes = headerAttributes;
		this.applicationAttributeTypes = applicationAttributes;
	}
	
	public EventType(String id, boolean composite,
			Map<String, AttributeType<?>> headerAttributes,
			Map<String, AttributeType<?>> applicationAttributes, EventType superType) {
		this(id, composite, headerAttributes, applicationAttributes);
		this.superType = superType;
	}

	public String getId() {
		return id;
	}

	public boolean isComposite() {
		return composite;
	}
	
	private List<AttributeType<?>> getAttributeTypeList(Map<String, AttributeType<?>> attributeTypes, 
			Map<String, AttributeType<?>> superAttrTypes) {
		
		Set<AttributeType<?>> attSet = new HashSet<AttributeType<?>>(attributeTypes.values());
		attSet.addAll(superAttrTypes.values());
		
		return new ArrayList<AttributeType<?>>(attSet);
	}
	
	private List<AttributeType<?>> getRequiredAttributeTypeList(List<AttributeType<?>> attributeTypes) {
		List<AttributeType<?>> required = new ArrayList<AttributeType<?>>();
		for(AttributeType<?> attr : attributeTypes){
			if (attr.isRequired()) {
				required.add(attr);
			}
		}
		return required;
	}
	
	public List<AttributeType<?>> getRequiredHeaderAttributeTypeList() {
		return getRequiredAttributeTypeList(getHeaderAttributeTypeList());
	}
	
	public List<AttributeType<?>> getRequiredApplicationAttributeTypeList() {
		return getRequiredAttributeTypeList(getApplicationAttributeTypeList());
	}
	
	public List<AttributeType<?>> getHeaderAttributeTypeList() {
		return getAttributeTypeList(headerAttributeTypes, superType.headerAttributeTypes);
	}
	
	public List<AttributeType<?>> getApplicationAttributeTypeList() {
		return getAttributeTypeList(applicationAttributeTypes, superType.applicationAttributeTypes);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((applicationAttributeTypes == null) ? 0
						: applicationAttributeTypes.hashCode());
		result = prime * result + (composite ? 1231 : 1237);
		result = prime
				* result
				+ ((headerAttributeTypes == null) ? 0 : headerAttributeTypes
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventType other = (EventType) obj;
		if (applicationAttributeTypes == null) {
			if (other.applicationAttributeTypes != null)
				return false;
		} else if (!applicationAttributeTypes
				.equals(other.applicationAttributeTypes))
			return false;
		if (composite != other.composite)
			return false;
		if (headerAttributeTypes == null) {
			if (other.headerAttributeTypes != null)
				return false;
		} else if (!headerAttributeTypes.equals(other.headerAttributeTypes))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
