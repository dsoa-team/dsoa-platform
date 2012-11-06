package br.ufpe.cin.dsoa.monitor;

import java.util.Map;

public interface Listener {
	
	public void listen(Map result, Object userObject, String statementName);
	
}
