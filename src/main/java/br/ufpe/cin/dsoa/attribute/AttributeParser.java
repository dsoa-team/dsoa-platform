package br.ufpe.cin.dsoa.attribute;

import java.util.StringTokenizer;

import br.ufpe.cin.dsoa.attribute.mappers.AttributeAttributableMapper;
import br.ufpe.cin.dsoa.configurator.parser.attribute.Attribute;
import br.ufpe.cin.dsoa.util.Constants;

public class AttributeParser {

	public static AttributeAttributableMapper parse(String serviceId, String key) {
		AttributeAttributableMapper attributeAttributableMapper = null;
		if (key.toLowerCase().startsWith(Attribute.ATTRIBUTE_PREFIX)) {
			StringTokenizer tokenizer = new StringTokenizer(key.substring(Attribute.ATTRIBUTE_PREFIX.length()), Constants.TOKEN);
			int ntokens = tokenizer.countTokens();
			if (ntokens == 2 || ntokens == 3) {
				String attCategory = tokenizer.nextToken();
				String attName = tokenizer.nextToken();
				String operationName = null;
				AttributeId attributeId = new AttributeId(attCategory, attName);
				if (ntokens == 3) {
					operationName = tokenizer.nextToken();
				}
				AttributableId attributableId = new AttributableId(serviceId,operationName);
				attributeAttributableMapper = new AttributeAttributableMapper(attributeId,attributableId);
			}
		}
		return attributeAttributableMapper;
	}
	
}
