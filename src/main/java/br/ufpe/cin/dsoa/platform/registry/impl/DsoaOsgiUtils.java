package br.ufpe.cin.dsoa.platform.registry.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.Property;
import br.ufpe.cin.dsoa.api.service.ProvidedPort;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.api.service.impl.ConstraintImpl;
import br.ufpe.cin.dsoa.api.service.impl.NonFunctionalSpecificationImpl;
import br.ufpe.cin.dsoa.api.service.impl.PropertyImpl;
import br.ufpe.cin.dsoa.api.service.impl.ProvidedPortImpl;
import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceImpl;
import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceProxyImpl;
import br.ufpe.cin.dsoa.api.service.impl.ServiceSpecificationImpl;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.DsoaUtil;


/**
 * This is a utility class focused on doing service models adaptations. It is intended to make translations
 * between DSOA and OSGi service models. 
 * 
 * @author fabions
 *
 */
public class DsoaOsgiUtils {
	public static List<ServiceInstance> translateOsgiServiceToDsoa(ServiceReference reference) throws ClassNotFoundException {
		List<ServiceInstance> svcList = new ArrayList<ServiceInstance>();
		String[] serviceInterfaces = (String[]) reference.getProperty(org.osgi.framework.Constants.OBJECTCLASS);
		for (String serviceInterface : serviceInterfaces) {
			svcList.add(translateOsgiServiceToDsoa(serviceInterface, reference, false));
		}
		return svcList;
	}
		
	public static ServiceInstance translateOsgiServiceToDsoa(String itfName, ServiceReference reference, boolean isProxy) {
		// MODIFICAR A CHAMADA PARA GETCONSTRAINTS
		List<Constraint> attConstraints = getAttributeConstraints(reference);
		NonFunctionalSpecification nonFunctionalSpecification = null;
		if (!attConstraints.isEmpty()) {
			nonFunctionalSpecification = new NonFunctionalSpecificationImpl(
					attConstraints);
		}

		ServiceSpecification serviceSpec = new ServiceSpecificationImpl(itfName, nonFunctionalSpecification);
		
		// TODO POR ENQUANTO O ID DO SERVIÇO VIRÁ DE UMA DAS PROPRIEDADES ( "service.pid" ou  "service.id").
		// Quando o ProvidesHandler estiver funcional, isso deverá ser modificado para obter o nome 
		// da porta provida.
		String portName = DsoaUtil.getId(reference);
		ProvidedPort providedPort = new ProvidedPortImpl(portName, serviceSpec);
		String[] keys = reference.getPropertyKeys();
		List<Property> props = new ArrayList<Property>();
		for (String key : keys) {
			Object value = reference.getProperty(key);
			Property prop = new PropertyImpl(key, value, value.getClass().getName());
			props.add(prop);
		}
		return (isProxy ? new ServiceInstanceProxyImpl(providedPort, props, reference) : new ServiceInstanceImpl(providedPort, props, reference)); 
	}
	
	/**
	 * This method is responsible for translating service properties into attribute constraints.
	 * @param reference
	 * @return
	 */
	public static List<Constraint> getAttributeConstraints(ServiceReference reference) {
		String keys[] = reference.getPropertyKeys();
		List<Constraint> attConstraints = new ArrayList<Constraint>();
		for (String key : keys) {
			if (key != null && key.toLowerCase().startsWith(Attribute.SERVICE_CONSTRAINT) || key.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT) ) {
				Object value = reference.getProperty(key);
				Constraint attConstraint = parse(key, value);
				if (attConstraint != null) {
					attConstraints.add(attConstraint);
				}
			}
		}
		return attConstraints;
	}
	
	/**
	 * This method assumes that attribute constraints are represented as properties following the format presented bellow:
	 * 
	 * 1. For constraints on service related attributes:
	 * 		<"constraint.service"> <"."> <att-id> <"."> <expression>
	 * 		eg. constraint.service.qos.availiability.GT = 95
	 * 
	 * 2. For constraints on operation related attributes:
	 *		<"constraint.operation"> <"."> <att-id> <"."> <operation> <"."> <expression>
	 *		eg. constraint.operation.qos.avgResponseTime.LT = 10
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static ConstraintImpl parse(String key, Object value) {
		ConstraintImpl attributeConstraint = null;
		if (key != null && key.toLowerCase().startsWith(Attribute.SERVICE_CONSTRAINT) || key.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT) ) {
				Double doubleVal = null;
				if (NumberUtils.isNumber(value.toString())) {
					doubleVal = NumberUtils.createDouble(value.toString());
				}
				
				int index = key.lastIndexOf('.');
				String expStr = key.substring(index+1);
				RelationalOperator exp = RelationalOperator.valueOf(expStr);
				String attributeId = key.substring(0, index);
				String operationName = null;
				if (attributeId.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT)) {
					attributeId = attributeId.replaceFirst(Attribute.OPERATION_CONSTRAINT + Constants.TOKEN, "");
					index = attributeId.lastIndexOf('.');
					operationName = attributeId.substring(index+1);
					attributeId = attributeId.substring(0, index);
				} else {
					attributeId = attributeId.replaceFirst(Attribute.SERVICE_CONSTRAINT + Constants.TOKEN, "");
				}
				attributeConstraint = new ConstraintImpl(attributeId, operationName, exp, doubleVal, ConstraintImpl.WEIGHT_UNSET);
		}
		return attributeConstraint;
	}	
}
