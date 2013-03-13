package br.ufpe.cin.dsoa.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonitoringConfiguration  {

	List<MetricInstance> itens = new ArrayList<MetricInstance>();
	
	public void addItem(MetricInstance item) {
		this.itens.add(item);
	}

	public List<MetricInstance> getItens() {
		return Collections.unmodifiableList(itens);
	}

}
