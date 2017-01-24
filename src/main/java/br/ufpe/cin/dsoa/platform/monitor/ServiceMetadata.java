package br.ufpe.cin.dsoa.platform.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;

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
	
	public List<Constraint> getAttributeConstraints() {
		
		List<Constraint> attConsts = new ArrayList<Constraint>(); 
		NonFunctionalSpecification nfs = service.getPort().getServiceSpecification().getNonFunctionalSpecification();
		
		if(nfs != null) {
			attConsts = nfs.getConstraints();
		}
		
		return attConsts;
	}
}
