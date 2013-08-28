package br.ufpe.cin.dsoa.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.service.ServiceSpecification;
import br.ufpe.cin.dsoa.util.AttributeParser;

public class OsgiServiceSpecification implements ServiceSpecification {
	
	private Class<?>[] classes;
	private ClassLoader classloader;
	private String[] classNames;
	private NonFunctionalSpecification qosSpec;
	
	public OsgiServiceSpecification(ServiceReference reference) {
		super();
		String keys[] = reference.getPropertyKeys();
		List<AttributeConstraint> attConstraints = new ArrayList<AttributeConstraint>();
		for (String key : keys) {
			Object value = reference.getProperty(key);
			AttributeConstraint attConstraint = AttributeParser.parse(key, value);
			if (attConstraint != null) {
				attConstraints.add(attConstraint);
			}
		}
		if (!attConstraints.isEmpty()) {
			this.qosSpec = new NonFunctionalSpecification(attConstraints);
		}
		this.classNames = (String[]) reference.getProperty(Constants.OBJECTCLASS);
		this.classes = new Class<?>[classNames.length];
		int i = 0;
		for (String classStr : classNames) {
			try {
				Class<?> clazz = reference.getBundle().loadClass(classStr);
				this.classloader = clazz.getClassLoader();
				this.classes[i++] = clazz;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public NonFunctionalSpecification getNonFunctionalSpecification() {
		return qosSpec;
	}
	
	public String[] getClassNames() {
		return this.classNames;
	}
	
	public Class<?>[] getClasses() {
		return this.classes;
	}
	
	ClassLoader getClassloader() {
		return this.classloader;
	}
}
