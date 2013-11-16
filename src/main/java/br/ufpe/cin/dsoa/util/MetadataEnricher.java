package br.ufpe.cin.dsoa.util;

import java.util.UUID;

public class MetadataEnricher {

	public static Object generate(String propertyName) {
		Object result = null;
		
		if(propertyName.equals(Constants.METADATA_TIMESTAMP)){
			result = generateTimestamp();
		} else if(propertyName.equals(Constants.METADATA_ID)) {
			result = generateId();
		}
		return result;
	}

	private static String generateId() {
		return UUID.randomUUID().toString();
	}

	private static long generateTimestamp() {
		return System.currentTimeMillis();
	}
}
