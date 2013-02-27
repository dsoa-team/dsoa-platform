package br.ufpe.cin.dsoa.epcenter.configurator.parser;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import br.ufpe.cin.dsoa.epcenter.configurator.parser.agent.AgentList;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.contextmodel.ContextModel;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.event.EventList;

public class JAXBInitializer {

	public static Map<String, Unmarshaller> initJAXBContexts() {

		Map<String, Unmarshaller> contexts = new HashMap<String, Unmarshaller>();

		try {
			contexts.put(EventList.CONFIG, JAXBInitializer.initEvent());
			contexts.put(AgentList.CONFIG, JAXBInitializer.initAgent());
			contexts.put(ContextModel.CONFIG, JAXBInitializer.initContextModel());
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return contexts;
	}

	private static Unmarshaller initAgent() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(AgentList.class);
		return context.createUnmarshaller();
	}

	private static Unmarshaller initEvent() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(EventList.class);
		return context.createUnmarshaller();
	}

	private static Unmarshaller initContextModel() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ContextModel.class);
		return context.createUnmarshaller();
	}
}
