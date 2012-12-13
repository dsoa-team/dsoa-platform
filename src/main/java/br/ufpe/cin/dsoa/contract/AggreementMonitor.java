package br.ufpe.cin.dsoa.contract;

import java.util.Map;

public interface AggreementMonitor {
	
	public void listen(Map result, Object userObject,String statementName);

}
