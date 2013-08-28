package br.ufpe.cin.dsoa.util;

import java.util.StringTokenizer;

import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.AttributeCategory;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.service.Expression;

public class AttributeParser {

	public static AttributeConstraint parse(String key, Object value) {
		AttributeConstraint attributeConstraint = null;
		if (key != null) {
			if (key.toLowerCase().startsWith(Attribute.SERVICE_CONSTRAINT) || key.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT) ) {
				double val = (Double)value;
				int index = key.lastIndexOf('.');
				String expStr = key.substring(index+1);
				Expression exp = Expression.valueOf(expStr);
				String attributeId = key.substring(0, index);
				String operationName = null;
				if (attributeId.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT)) {
					attributeId = attributeId.replaceFirst(Attribute.OPERATION_CONSTRAINT + Constants.TOKEN, "");
					index = attributeId.lastIndexOf('.');
					operationName = attributeId.substring(index+1);
					attributeId = attributeId.substring(0, index);
				} else {
					attributeId = attributeId.replaceFirst(Attribute.SERVICE_CONSTRAINT + Constants.TOKEN, "");
				}
				attributeConstraint = new AttributeConstraint(operationName, attributeId, exp, val);
			}
		}
		return attributeConstraint;
	}
	
	public static AttributeCategory parseCategory(AttributeCatalog attributeCatalog, String catName) {
		AttributeCategory cat = null, parentCat = null;
		if (catName != null) {
			StringTokenizer tokenizer = new StringTokenizer(catName, Constants.TOKEN);
			String parentId = tokenizer.nextToken();
			String catId = null;
			parentCat = attributeCatalog.getCategory(parentId);
			if (parentCat == null) {
				parentCat = new AttributeCategory(parentId);
				attributeCatalog.addCategory(parentCat);
			}
			while (tokenizer.hasMoreTokens()) {
				catId = tokenizer.nextToken();
				cat = attributeCatalog.getCategory(catId);
				if (cat == null) {
					cat = new AttributeCategory(catId, parentCat);
					attributeCatalog.addCategory(cat);
				}
				parentCat = cat;
			}
		}
		return parentCat;
	}
	
	public static String format(String category, String attribute) {
		return category + Constants.TOKEN + attribute;
	}
	
	public static void main(String args[] ) {
		// constraint.operation.qos.performance.avgResponseTime.getCotation.LT = 500
		// constraint.service.qos.availability.LT = 99
		
		System.out.println(parse ("constraint.operation.qos.performance.avgResponseTime.getCotation.LT",500));
		System.out.println(parse ("constraint.service.qos.availability.LT", 99));
	}
}
