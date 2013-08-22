package br.ufpe.cin.dsoa.attribute;

import java.util.StringTokenizer;

import br.ufpe.cin.dsoa.mapper.AttributeAttributableMapper;
import br.ufpe.cin.dsoa.util.Constants;

public class AttributeParser {

	public static AttributeAttributableMapper parse(String serviceId, String key) {
		AttributeAttributableMapper attributeAttributableMapper = null;
		if (key != null && key.toLowerCase().startsWith(Attribute.ATTRIBUTE_PREFIX)) {
			int beginIndex = key.lastIndexOf(Constants.TOKEN);
			String attId = null, opName = null;
			if (beginIndex != -1 ) {
				opName = key.substring(beginIndex);
			}
			attId = key.substring(0, beginIndex);
			AttributableId attributableId = new AttributableId(serviceId, opName);
			attributeAttributableMapper = new AttributeAttributableMapper(attId,attributableId);
		}
		return attributeAttributableMapper;
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
	
}
