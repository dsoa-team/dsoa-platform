package br.ufpe.cin.dsoa.monitor;

import java.util.Map;

public interface SlaListener {
	
	public void listen(Map result, Object userObject,String statementName);

}
