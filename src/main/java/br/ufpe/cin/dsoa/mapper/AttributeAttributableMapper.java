package br.ufpe.cin.dsoa.mapper;

import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * It represents an association between a Property, a service and
 * an operation, that is, between a Property and a target
 * 
 * @author fabions
 */
public class AttributeAttributableMapper {

	private final String attributeId;
	private final AttributableId attributableId;
	
	public AttributeAttributableMapper(String attributeId, AttributableId attributableId) {
		this.attributeId = attributeId;
		this.attributableId = attributableId;
	}

	public String getAtttributeId() {
		return this.attributeId;
	}
	
	public AttributableId getAttributableId() {
		return this.attributableId;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(attributeId);
		sb.append(Constants.TOKEN).append(attributableId.toString());
		return  sb.toString();
	}

}
