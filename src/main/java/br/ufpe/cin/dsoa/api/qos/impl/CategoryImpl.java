package br.ufpe.cin.dsoa.api.qos.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.core.impl.NamedElementImpl;
import br.ufpe.cin.dsoa.api.qos.Attribute;
import br.ufpe.cin.dsoa.api.qos.Category;

public class CategoryImpl extends NamedElementImpl implements Category {
	
	private Map<String, Attribute> attMap;

	public CategoryImpl(String name) {
		super(name);
		this.attMap = new HashMap<String,Attribute>();
	}

	@Override
	public List<Attribute> getAttributes() {
		return new ArrayList<Attribute>(attMap.values());
	}

	@Override
	public Attribute getAttribute(String attName) {
		return attMap.get(attName);
	}
	
	public void addAttribute(String attName) {
		if (!this.attMap.containsKey(attName)) {
			this.attMap.put(attName, new AttributeImpl(this, attName));
		}
	}

}
