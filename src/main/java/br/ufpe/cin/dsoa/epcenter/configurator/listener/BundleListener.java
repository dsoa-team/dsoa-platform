package br.ufpe.cin.dsoa.epcenter.configurator.listener;

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
import br.ufpe.cin.dsoa.management.MetricCatalog;

public class BundleListener extends BundleTracker {

	private Map<String, Unmarshaller> JAXBContexts;
	private EventProcessingCenter epCenter;
	private MetricCatalog metricCatalog;
	private Map<String, Event> eventMap;
	
	private static Logger logger = Logger.getLogger(BundleListener.class.getName());
	
	public BundleListener(BundleContext context) {
		super(context, Bundle.ACTIVE, null);
		this.JAXBContexts = JAXBInitializer.initJAXBContexts();
		this.eventMap = new HashMap<String, Event>();
	}
	
	
	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		//if (event != null && event.getType() == BundleEvent.STARTED) {
			try {
				this.addMetricDefinitions(bundle);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
	//	} else if (event.getType() == BundleEvent.STOPPING)  {
			/*try {
				this.removeMetricDefinitions(bundle);
			} catch (JAXBException e) {
				e.printStackTrace();
			} */
		//}
		
		return super.addingBundle(bundle, event);
	}
	
	private void removeMetricDefinitions(Bundle bundle) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void remove(Bundle bundle) {
		super.remove(bundle);
	}
	
	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
		super.modifiedBundle(bundle, event, object);
	}
	
	public void setEventProcessingCenter(EventProcessingCenter epCenter) {
		this.epCenter = epCenter;
	}
	
	private void handleEventDefinition(Bundle bundle) throws JAXBException, ClassNotFoundException {
		URL url = bundle.getEntry(EventList.CONFIG);
		if(url != null) {
			EventList list = (EventList) JAXBContexts.get(EventList.CONFIG).unmarshal(url);
			
			//load eventMap
			for(Event e : list.getEvents()){
				eventMap.put(e.getType(), e);
			}
			
			for(Event e : list.getEvents()) {
				Map<String, Object> eventProperties = e.getProperties();
				if(null != e.getSuperType()){
					eventProperties.putAll(eventMap.get(e.getSuperType()).getProperties());
				}
				
				Set<String> keys = eventProperties.keySet();
				Map<String, Object> registedProperties = new HashMap<String, Object>(eventProperties);
				
				for(String key : keys){
					try {
						registedProperties.put(key, Class.forName((String) eventProperties.get(key)));
					} catch (ClassNotFoundException ex ){
						registedProperties.put(key, eventProperties.get(key));
					}
				}
				
				this.epCenter.defineEvent(e.getType(), registedProperties);
			}
		}
	}
	
	private void addMetricDefinitions(Bundle bundle) throws JAXBException {
		URL url = bundle.getEntry(MetricList.CONFIG);
		if(url != null) {
			Unmarshaller u = JAXBContexts.get(MetricList.CONFIG);
			MetricList list = (MetricList)u.unmarshal(url);
			this.metricCatalog.addMetrics(list);
			//define ESP statements
			for(Metric m : list.getMetrics()){
				this.epCenter.defineStatement(m.getId().toString(), m.getQuery());
			}
		}
	}
	
	private void handleContextDefinition(Bundle bundle) throws JAXBException {
		URL url = bundle.getEntry(ContextModel.CONFIG);
		if(url != null) {
			ContextModel contextModel = (ContextModel) JAXBContexts.get(ContextModel.CONFIG).unmarshal(url);
			logger.info("Context :");//LOG
			for(Context c : contextModel.getContexts()) {
				logger.info(String.format("Context Category: %s", c.getCategory()));//LOG
				
				Map<String, String> elements = c.getElements();
				
				for(String key : elements.keySet()) {
					String e = elements.get(key);
					logger.info(String.format("Element id: %s", e));
				}
			}
			
			for(ContextMapping cm : contextModel.getContextMappings()) {
				logger.info(String.format("Context Mapping: %s", cm.getCategory()));
				
				Map<String, String> contextElements = cm.getContextElements();
				
				for(String key : contextElements.keySet()) {
					String ce = contextElements.get(key);
					logger.info(String.format("Event Property: %s", ce));
				}
			}
		}
	}


	public void setMetricCatalog(MetricCatalog metricCatalog) {
		this.metricCatalog = metricCatalog;
	}


}
