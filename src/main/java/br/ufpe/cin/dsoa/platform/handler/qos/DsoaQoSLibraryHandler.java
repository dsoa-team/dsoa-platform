package br.ufpe.cin.dsoa.platform.handler.qos;

import java.util.Dictionary;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.api.qos.Attribute;
import br.ufpe.cin.dsoa.api.qos.Category;
import br.ufpe.cin.dsoa.api.qos.Metric;
import br.ufpe.cin.dsoa.api.qos.QoSLibrary;
import br.ufpe.cin.dsoa.api.qos.impl.AttributeImpl;
import br.ufpe.cin.dsoa.api.qos.impl.CategoryImpl;
import br.ufpe.cin.dsoa.api.qos.impl.MetricImpl;
import br.ufpe.cin.dsoa.api.qos.impl.QoSLibraryImpl;
import br.ufpe.cin.dsoa.api.service.ComponentInstance;
import br.ufpe.cin.dsoa.api.service.impl.ComponentInstanceImpl;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentInstanceManager;
import br.ufpe.cin.dsoa.util.Constants;

public class DsoaQoSLibraryHandler extends PrimitiveHandler {

	@Override
	public void configure(Element metadata, Dictionary configuration)
			throws ConfigurationException {
		DsoaComponentInstanceManager manager = (DsoaComponentInstanceManager)this.getInstanceManager();
		ComponentInstanceImpl componentInstance = manager.getDsoaComponentInstance();
		
		Element[] qosTags = metadata.getElements(Constants.QOS_LIBRARY_HANDLER_TAG, Constants.QOS_LIBRARY_HANDLER_TAG_NAMESPACE);
		if (qosTags != null && qosTags.length != 0) {
			String libraryName = (String) qosTags[0].getAttribute(Constants.QOS_LIBRARY_NAME_TAG);
			QoSLibrary qosLib = new QoSLibraryImpl(libraryName);
			componentInstance.setQosLib(qosLib);
			
			Element[] categoryTags = qosTags[0].getElements(Constants.CATEGORY_TAG);			
			for (Element categoryTag : categoryTags) {
				String categoryName = (String) categoryTag.getAttribute(Constants.CATEGORY_NAME_TAG);
				Category category = new CategoryImpl(categoryName);
				
				Element[] attTags = categoryTag.getElements(Constants.ATTRIBUTE_TAG);
				for (Element attTag : attTags) {
					String attName = (String) attTag.getAttribute(Constants.ATTRIBUTE_NAME_TAG);
					Attribute attribute = new AttributeImpl(category, attName);
					
					Element[] metricTags = attTag.getElements(Constants.METRIC_TAG);
					for (Element metricTag : metricTags) {
						String metricName = (String) metricTag.getAttribute(Constants.METRIC_NAME_TAG);
						Metric metric = new MetricImpl(attribute, metricName);
						qosLib.addMetric(metric);
					}
				}
			}
		}

	}

	@Override
	public void stop() {
		this.setValidity(false);
	}

	@Override
	public void start() {
		this.setValidity(true);
	}
	
	@Override
	public HandlerDescription getDescription() {
		
		return new DsoaQoSLibraryHandlerDescription(this);
	}
	
	class DsoaQoSLibraryHandlerDescription extends HandlerDescription {

		public DsoaQoSLibraryHandlerDescription(DsoaQoSLibraryHandler autonomicHandler) {
			super(autonomicHandler);
		}
		
		public Element getHandlerInfo() {
			DsoaComponentInstanceManager manager = (DsoaComponentInstanceManager)getInstanceManager();
			ComponentInstance componentInstance = manager.getDsoaComponentInstance();
			Element descInfo = new Element("Handler", "");
			descInfo.addAttribute(new org.apache.felix.ipojo.metadata.Attribute("name",Constants.QOS_LIBRARY_HANDLER_TAG_NAMESPACE +":"+ Constants.QOS_LIBRARY_HANDLER_TAG));
			if (componentInstance.getQosLib() != null) {
		        for (Metric metric : componentInstance.getQosLib().getMetrics()) {
		            descInfo.addElement(new Element("Metric",metric.getFullname()));
		        }
			}
	        return descInfo;
	        
	    }

	}

}
