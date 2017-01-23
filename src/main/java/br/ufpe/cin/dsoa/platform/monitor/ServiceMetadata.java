package br.ufpe.cin.dsoa.platform.monitor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.impl.ConstraintImpl;

public class ServiceMetadata {
	
	/** Numeric Id and String pid */
	private String id;
	/** (className, List<operationName>) */
	private List<String> operationsList;
	private Logger log;
	private ServiceInstance service;
	
	public ServiceMetadata(ServiceInstance service) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.id = service.getName();
		this.operationsList = new ArrayList<String>();
		this.service = service;
		this.parseOperations();
	}

	private void parseOperations() {
		log.fine("ServiceId: " + id);
		
		// TODO: AJUSTAR CÓDIGO ABAIXO
		/*Class<?> clazz = service.getPort().getServiceSpecification().getFunctionalInterface().getInterfaceClass();
		log.info("Service Interface: " + clazz);
		for(Method method : clazz.getDeclaredMethods()){
			if(Modifier.isPublic(method.getModifiers())){
				log.info("Operation: " + method.getName());
				operationsList.add(method.getName());
			}
		}*/
	}
	
	public String getId() {
		return id;
	}
	
	public String getClassName() {
		return service.getPort().getServiceSpecification().getFunctionalInterface().getInterfaceName();
	}
	
	public List<String> getOperations(){
		return new ArrayList<String>(operationsList);
	}

	public Object getProperty(String name) {
		return service.getProperty(name);
	}
	
	public List<ConstraintImpl> getAttributeConstraints() {
		
		List<ConstraintImpl> attConsts = new ArrayList<ConstraintImpl>(); 
		NonFunctionalSpecification nfs = service.getPort().getServiceSpecification().getNonFunctionalSpecification();
		
		if(nfs != null) {
			attConsts = nfs.getConstraints();
		}
		
		return attConsts;
	}
}
