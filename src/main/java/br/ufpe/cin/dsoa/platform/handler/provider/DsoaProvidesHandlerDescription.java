package br.ufpe.cin.dsoa.platform.handler.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public class DsoaProvidesHandlerDescription extends HandlerDescription {

	private static final String STATUS_ELEMENT = "status";
	private List<DsoaServiceInstanceDescription> serviceInstanceDescriptionList;
	private DsoaProvidesHandler providerHandler;

	public DsoaProvidesHandlerDescription(DsoaProvidesHandler proHandler) {
		super(proHandler);
		this.providerHandler = proHandler;
		List<ServiceInstance> serviceInstances = this.providerHandler.getServiceInstances();
		this.serviceInstanceDescriptionList = new ArrayList<DsoaServiceInstanceDescription>(serviceInstances.size());
        for (ServiceInstance instance : serviceInstances)  {
        	serviceInstanceDescriptionList.add(new DsoaServiceInstanceDescription(instance));
        }
	}
	
	public Element getHandlerInfo() {
        Element handlerDesc = super.getHandlerInfo();
        boolean valid = this.providerHandler.isValid();
        for (DsoaServiceInstanceDescription srvDescription : serviceInstanceDescriptionList) {
            Element dep = srvDescription.getInfo();
            handlerDesc.addElement(dep);
        }
        handlerDesc.addAttribute(new Attribute(STATUS_ELEMENT, String.valueOf(valid)));
        return handlerDesc;
    }

}

