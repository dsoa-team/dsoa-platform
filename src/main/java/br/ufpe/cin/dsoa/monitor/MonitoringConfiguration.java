package br.ufpe.cin.dsoa.monitor;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.contract.AggreementMonitor;
import br.ufpe.cin.dsoa.epcenter.EventConsumer;

public class MonitoringConfiguration  implements EventConsumer {

	private AggreementMonitor listener;
	private Map<String, MonitoringConfigurationItem> itens;
	private MonitoringContext context;
	
	public MonitoringConfiguration(MonitoringContext context,
			AggreementMonitor listener) {
		this.context = context;
		this.listener = listener;
		this.itens = new HashMap<String, MonitoringConfigurationItem>();
	}

	public MonitoringContext getContext() {
		return context;
	}

	public AggreementMonitor getListener() {
		return listener;
	}

	public void addConfigurationItem(MonitoringConfigurationItem item) {
		this.itens.put(String.valueOf(item.getAttribute()), item);
	}

	public Map<String, MonitoringConfigurationItem> getItens() {
		return itens;
	}

	public void receive(Map result, Object userObject, String name) {
		MonitoringConfigurationItem brokenItem = this.itens.get(name);
		listener.listen(result, brokenItem, name);
	}

	@Override
	public void receive(Object result, String statementName) {
		// TODO Auto-generated method stub
		
	}

}
