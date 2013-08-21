package br.ufpe.cin.dsoa.attribute.mappers;

import java.util.List;

import br.ufpe.cin.dsoa.attribute.AttributeValue;
import br.ufpe.cin.dsoa.attribute.AttributeValueData;
import br.ufpe.cin.dsoa.attribute.AttributeValueMetadata;
import br.ufpe.cin.dsoa.event.legacy.Property;
import br.ufpe.cin.dsoa.event.legacy.Event;

public class AttributeEventMapper {
	public AttributeValue mapEventAttribute(Event event) {
		AttributeValueData data = this.getAttributeValueData(event.getDataProperties());
		AttributeValueMetadata metadata = this.getAttributeValueMetadata(event.getMetadataProperties());
		return new AttributeValue(data, metadata);
	}

	private AttributeValueMetadata getAttributeValueMetadata(List<Property> attList) {
		// TODO Auto-generated method stub
		return null;
	}

	private AttributeValueData getAttributeValueData(List<Property> attList) {
		// TODO Auto-generated method stub
		return null;
	}
}
