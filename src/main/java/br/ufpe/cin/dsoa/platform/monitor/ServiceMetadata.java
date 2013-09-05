package br.ufpe.cin.dsoa.platform.monitor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Service;

public class ServiceMetadata {
	
	/** Numeric Id and String pid */
	private String id;
	/** (className, List<operationName>) */
	private List<String> operationsList;
	private Logger log;
	private Service service;
	
	public ServiceMetadata(Service service) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.id = service.getServiceId();
		this.operationsList = new ArrayList<String>();
		this.service = service;
		this.parseOperations();
	}

	private void parseOperations() {
		log.fine("ServiceId: " + id);
		Class<?> clazz = service.getSpecification().getClazz();
		log.info("Service Interface: " + clazz);
		for(Method method : clazz.getDeclaredMethods()){
			if(Modifier.isPublic(method.getModifiers())){
				log.info("Operation: " + method.getName());
				operationsList.add(method.getName());
			}
		}
	}
	
	public String getId() {
		return id;
	}
	
	public String getClassName() {
		return service.getSpecification().getServiceInterface();
	}
	
	public List<String> getOperations(){
		return new ArrayList<String>(operationsList);
	}

	public Object getProperty(String name) {
		return service.getProperties().get(name);
	}
	
	public List<AttributeConstraint> getAttributeConstraints() {
		List<AttributeConstraint> attConsts = service.getSpecification().getNonFunctionalSpecification().getAttributeConstraints();
		return attConsts;
	}
}
