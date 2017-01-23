package br.ufpe.cin.dsoa.platform.handler.requires;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.api.service.Binding;

public class DsoaRequiresHandlerDescription extends HandlerDescription {

	private static final String STATUS_ELEMENT = "status";
	private List<DsoaBindingDescription> bindingDescriptionList;

	public DsoaRequiresHandlerDescription(DsoaRequiresHandler depHandler) {
		super(depHandler);
		List<Binding> bindings = depHandler.getBindings();
		this.bindingDescriptionList = new ArrayList<DsoaBindingDescription>(bindings.size());
        for (Binding binding : bindings)  {
        	bindingDescriptionList.add(new DsoaBindingDescription(binding));
        }
	}
	
	public Element getHandlerInfo() {
        Element handlerDesc = super.getHandlerInfo();
        String state = "valid";
        for (DsoaBindingDescription bndDescription : bindingDescriptionList) {
            if (!bndDescription.isValid()) {
                state = "invalid";
            }
            Element dep = bndDescription.getInfo();
            handlerDesc.addElement(dep);
        }
        handlerDesc.addAttribute(new Attribute(STATUS_ELEMENT, state));
        return handlerDesc;
    }

}
