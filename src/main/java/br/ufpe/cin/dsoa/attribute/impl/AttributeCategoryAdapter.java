package br.ufpe.cin.dsoa.attribute.impl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.attribute.AttributeCategory;
import br.ufpe.cin.dsoa.attribute.AttributeParser;

public class AttributeCategoryAdapter extends XmlAdapter<String, AttributeCategory> {

	private AttributeCatalog attributeCatalog;

	public AttributeCategoryAdapter(AttributeCatalog attributeCatalog) {
		this.attributeCatalog = attributeCatalog;
	}

	@Override
	public AttributeCategory unmarshal(String catId) throws Exception {
		AttributeCategory attCat = attributeCatalog.getCategory(catId);
		if (attCat == null) {
			attCat = AttributeParser.parseCategory(attributeCatalog, catId);
		}
		return attCat;
	}

	@Override
	public String marshal(AttributeCategory v) throws Exception {
		// TODO Auto-generated method stub
		return v.toString();
	}

}
