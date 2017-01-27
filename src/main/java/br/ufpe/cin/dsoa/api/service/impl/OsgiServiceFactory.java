package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.Property;
import br.ufpe.cin.dsoa.api.service.ProvidedPort;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.component.DsoaConstraintParser;
import br.ufpe.cin.dsoa.util.DsoaUtil;

public class OsgiServiceFactory {
	public static List<ServiceInstance> getOsgiServices(ServiceReference reference) throws ClassNotFoundException {
		List<ServiceInstance> svcList = new ArrayList<ServiceInstance>();
		String[] serviceInterfaces = (String[]) reference.getProperty(org.osgi.framework.Constants.OBJECTCLASS);
		for (String serviceInterface : serviceInterfaces) {
			svcList.add(getOsgiService(serviceInterface, reference, false));
		}
		return svcList;
	}
		
	public static ServiceInstance getOsgiService(String itfName, ServiceReference reference, boolean isProxy) {
		// MODIFICAR A CHAMADA PARA GETCONSTRAINTS
		List<Constraint> attConstraints = DsoaConstraintParser.getAttributeConstraints(reference);
		NonFunctionalSpecification nonFunctionalSpecification = null;
		if (!attConstraints.isEmpty()) {
			nonFunctionalSpecification = new NonFunctionalSpecificationImpl(
					attConstraints);
		}

		// TODO VER O EFEITO DA RETIRADA DA CLASSE DA INTERFACE DO CONSTRUTOR ABAIXO
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
}
