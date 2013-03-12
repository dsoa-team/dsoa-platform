package br.ufpe.cin.dsoa.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonitoringConfiguration  {

	List<MonitoringConfigurationItem> itens = new ArrayList<MonitoringConfigurationItem>();
	
	public void addItem(MonitoringConfigurationItem item) {
		this.itens.add(item);
	}

	public List<MonitoringConfigurationItem> getItens() {
		return Collections.unmodifiableList(itens);
	}

}
