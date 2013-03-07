package br.ufpe.cin.dsoa.management;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.JAXBInitializer;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.agent.Agent;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.agent.AgentList;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.contextmodel.Context;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.contextmodel.ContextMapping;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.contextmodel.ContextModel;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.event.Event;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.event.EventList;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.MetricList;

public class BundleListener extends BundleTracker {

	private Map<String, Unmarshaller> JAXBContexts;
	private MetricCatalog metricCatalog;
	private static Logger logger = Logger.getLogger(BundleListener.class.getName());
	
	public BundleListener(BundleContext context) {
		super(context, Bundle.ACTIVE, null);
		this.JAXBContexts = JAXBInitializer.initJAXBContexts();
	}
	
	
	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		if (event != null && event.getType() == BundleEvent.STARTED) {
			try {
				this.addMetricDefinitions(bundle);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		} else if (event.getType() == BundleEvent.STOPPING)  {
			/*try {
				this.removeMetricDefinitions(bundle);
			} catch (JAXBException e) {
				e.printStackTrace();
			} */
		}
		
		return super.addingBundle(bundle, event);
	}
	
	private void removeMetricDefinitions(Bundle bundle) {
	}

	@Override
	public void remove(Bundle bundle) {
		super.remove(bundle);
	}
	
	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
		super.modifiedBundle(bundle, event, object);
	}
	
	private void addMetricDefinitions(Bundle bundle) throws JAXBException {
		URL url = bundle.getEntry(MetricList.CONFIG);
		if(url != null) {
			MetricList list = (MetricList) JAXBContexts.get(MetricList.CONFIG).unmarshal(url);
			this.metricCatalog.addMetrics(list);
		}
	}
	
}
