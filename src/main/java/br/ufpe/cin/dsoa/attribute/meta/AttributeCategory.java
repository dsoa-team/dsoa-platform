package br.ufpe.cin.dsoa.attribute.meta;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import br.ufpe.cin.dsoa.util.Constants;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.NONE)
public class AttributeCategory {

	private static final String NAME = "name";
	
	private String id;
	
	@XmlAttribute(name = NAME)
	private String name;
	
	private AttributeCategory supercategory;
	private List<AttributeCategory> subcategoryList  = new ArrayList<AttributeCategory>();
	private List<AttributeType> attributeList  = new ArrayList<AttributeType>();
	
	public AttributeCategory(String name, AttributeCategory supercat) {
		this.supercategory = supercat;
		if (this.supercategory != null) {
			this.supercategory.addSubcategory(this);
		}
		this.name = name;
	}
	
	public AttributeCategory(String name) {
		this(name, null);
	}

	public synchronized String getId() {
		if (id == null) {
			id = name;
			if (supercategory != null) {
				id = supercategory.getId() + Constants.TOKEN + id;
			}
		}
		return id;
	}
	
	public AttributeCategory getSupercategory() {
		return supercategory;
	}

	public List<AttributeCategory> getSubcategoryList() {
		return new ArrayList<AttributeCategory>(subcategoryList);
	}

	public String getName() {
		return name;
	}
	
	public void addAttribute(AttributeType att) {
		this.attributeList.add(att);
	}
	
	public void addSubcategory(AttributeCategory cat) {
		this.subcategoryList.add(cat);
	}

	@Override
	public String toString() {
		return "AttributeCategory [id=" + id + ", subcategoryList=" + subcategoryList + ", attributeList="
				+ attributeList + "]";
	}

}
