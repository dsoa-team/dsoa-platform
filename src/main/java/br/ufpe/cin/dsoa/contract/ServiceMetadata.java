package br.ufpe.cin.dsoa.contract;

import java.util.Collections;
import java.util.Map;

public class ServiceMetadata {
	private Map<String, Object> metadata;
	
	public ServiceMetadata(Map<String, Object> metadata) {
		this.metadata = Collections.unmodifiableMap(metadata);
	}
	
	public Object get(String key) {
		return metadata.get(key);
	}
}
