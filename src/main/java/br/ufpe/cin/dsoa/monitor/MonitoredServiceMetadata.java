package br.ufpe.cin.dsoa.monitor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public MonitoredServiceMetadata(ServiceReference reference) {
		this.reference = reference;
		this.id = Util.getId(reference);
		this.pid = Util.getPid(reference);
		this.operationsMap = new HashMap<String, List<String>>();
		this.parseOperations();
	}

	private void parseOperations() {
		for(String clazz : (String[]) reference.getProperty(Constants.OBJECTCLASS)){
			List<String> operations = new ArrayList<String>();
			try {
				for(Method method : Class.forName(clazz).getDeclaredMethods()){
					if(Modifier.isPublic(method.getModifiers())){
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Dictionary getProperties() {
		String[] keys = reference.getPropertyKeys();
		Dictionary dict = new Hashtable();
		for (String key : keys) {
			dict.put(key, reference.getProperty(key));
		}
		return dict;
	}
	
	public Object getProperty(String name) {
		return reference.getProperty(name);
	}
	
	ServiceReference getReference() {
		return reference;
	}

}
