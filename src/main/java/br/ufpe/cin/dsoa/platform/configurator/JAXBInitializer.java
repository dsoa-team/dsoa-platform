package br.ufpe.cin.dsoa.platform.configurator;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import br.ufpe.cin.dsoa.api.attribute.AttributeList;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperList;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.agent.AgentList;

public class JAXBInitializer {
	public static Map<String, Unmarshaller> initJAXBContexts() {
		Map<String, Unmarshaller> contexts = new HashMap<String, Unmarshaller>();
		try {
			contexts.put(EventTypeList.CONFIG, JAXBInitializer.createUnmarshaller(EventTypeList.class));
			contexts.put(AgentList.CONFIG, JAXBInitializer.createUnmarshaller(AgentList.class));
			contexts.put(AttributeList.CONFIG, createUnmarshaller(AttributeList.class));
			contexts.put(AttributeEventMapperList.CONFIG, createUnmarshaller(AttributeEventMapperList.class));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return contexts;
	}
	
	private static Unmarshaller createUnmarshaller(Class<?> clazz) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(clazz);
		return context.createUnmarshaller();
	}
}
