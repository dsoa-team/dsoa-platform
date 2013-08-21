package br.ufpe.cin.dsoa.attribute;

public class AttributeValue {
	private AttributeValueData 		valueData;
	private AttributeValueMetadata 	valueMetadata;
	
	public AttributeValue(AttributeValueData valueData, AttributeValueMetadata valueMetadata) {
		super();
		this.valueData = valueData;
		this.valueMetadata = valueMetadata;
	}

	public AttributeValueData getValueData() {
		return valueData;
	}

	public AttributeValueMetadata getValueMetadata() {
		return valueMetadata;
	}
	
}
