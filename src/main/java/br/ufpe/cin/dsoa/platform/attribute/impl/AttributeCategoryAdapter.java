package br.ufpe.cin.dsoa.platform.attribute.impl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import br.ufpe.cin.dsoa.api.attribute.AttributeCategory;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;

public class AttributeCategoryAdapter extends XmlAdapter<String, AttributeCategory> {

	private AttributeCatalog attributeCatalog;

	public AttributeCategoryAdapter(AttributeCatalog attributeCatalog) {
		this.attributeCatalog = attributeCatalog;
	}

	@Override
	public AttributeCategory unmarshal(String catId) throws Exception {
		AttributeCategory attCat = attributeCatalog.getCategory(catId);
		if (attCat == null) {
			attCat = AttributeManager.parseCategory(attributeCatalog, catId);
		}
		return attCat;
	}

	@Override
	public String marshal(AttributeCategory v) throws Exception {
		// TODO Auto-generated method stub
		return v.toString();
	}

}
