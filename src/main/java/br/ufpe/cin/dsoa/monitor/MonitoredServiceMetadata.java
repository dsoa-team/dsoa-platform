package br.ufpe.cin.dsoa.monitor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.util.Util;

public class MonitoredServiceMetadata {
	
	/** Numeric Id and String pid */
	private String id;
	private String pid;
	private ServiceReference reference;
	/** (className, List<operationName>) */
	private Map<String, List<String>> operationsMap;
	private Logger log;
	
	public MonitoredServiceMetadata(ServiceReference reference) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.reference = reference;
		this.id = Util.getId(reference);
		this.pid = Util.getPid(reference);
		this.operationsMap = new HashMap<String, List<String>>();
		this.parseOperations();
	}

	private void parseOperations() {
		log.fine("ServiceId: " + id);
		log.info("ServicePid: " + pid);
		for(String clazz : (String[]) reference.getProperty(Constants.OBJECTCLASS)){
			log.info("Service Interface: " + clazz);
			List<String> operations = new ArrayList<String>();
			try {
				for(Method method : reference.getBundle().loadClass(clazz).getDeclaredMethods()){
					if(Modifier.isPublic(method.getModifiers())){
						log.info("Operation: " + method.getName());
						operations.add(method.getName());
					}
				}
				this.operationsMap.put(clazz, operations);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public String getPid() {
		return pid;
	}
	
	public String getId() {
		return id;
	}
	
	public List<String> getClassNames() {
		return new ArrayList<String>(operationsMap.keySet());
	}
	
	public Map<String, List<String>> getOperationsMap() {
		return operationsMap;
	}

	public List<String> getOperations(){
		
		Set<String> operations = new HashSet<String>();
		for(List<String> values : operationsMap.values()){
			operations.addAll(values);
		}
		
		return new ArrayList<String>(operations);
	}

	public Object getProperty(String name) {
		return reference.getProperty(name);
	}
	
	ServiceReference getReference() {
		return reference;
	}

}
