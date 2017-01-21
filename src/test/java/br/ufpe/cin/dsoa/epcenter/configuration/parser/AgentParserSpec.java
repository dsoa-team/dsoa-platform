package br.ufpe.cin.dsoa.epcenter.configuration.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.agent.AgentList;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.platform.event.impl.EsperProcessingService;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

public class AgentParserSpec {

	private AgentList agentList;
	private EventTypeList eventList;
	private EsperProcessingService epService;
	private EPServiceProvider esperProvider;
	
	@Before
	public void setUp() throws JAXBException, FileNotFoundException{
		JAXBContext context = JAXBContext.newInstance(AgentList.class);
		Unmarshaller u = context.createUnmarshaller();
		this.agentList = (AgentList) u.unmarshal(new FileInputStream(
		"src/test/resources/epcenter/configuration/agent.xml"));
		
		context = JAXBContext.newInstance(EventTypeList.class);
		u = context.createUnmarshaller();
		this.eventList = (EventTypeList) u.unmarshal(new FileInputStream(
		"src/test/resources/epcenter/configuration/event.xml"));
		
		this.esperProvider = EPServiceProviderManager.getProvider("Dsoa-EsperEngine", new Configuration());
		this.epService = new EsperProcessingService();
		this.epService.start();
		this.registerEvents();
	}
	
	@Test
	public void testConfigPath() {
		assertEquals(AgentList.CONFIG, "DSOA-INF/agent.xml");
	}
	
	public void registerEvents() {
		List<EventType> list = eventList.getEvents();
		if (list != null && !list.isEmpty()) {
			List<EventType> subtypes = new ArrayList<EventType>();
			List<EventType> types = new ArrayList<EventType>();
			for (EventType eventType : eventList.getEvents()) {
				if (eventType.getSuperTypeName() != null) {
					subtypes.add(eventType);
				} else {
					types.add(eventType);
				}
			}
			
			if (!types.isEmpty()) {
				for (EventType type : types) {
					epService.registerEventType(type);
				}
			}
			
			if (!subtypes.isEmpty()) {
				for (EventType subtype : subtypes) {
					EventType superType = epService.getEventType(subtype.getSuperTypeName());
					if (superType != null) {
						Map<String, PropertyType> superMetaProps = superType.getMetadataMap();
						Map<String, PropertyType> subMetadataProps = subtype.getMetadataMap();
						copyProperties(superMetaProps, subMetadataProps);
						
						Map<String, PropertyType> superDataProps = superType.getDataMap();
						Map<String, PropertyType> subDataProps = subtype.getDataMap();
						copyProperties(superDataProps, subDataProps);
					}
					epService.registerEventType(subtype);
				}
			}
		}
	}
	
	private void copyProperties(Map<String, PropertyType> superProps, Map<String, PropertyType> subProps) {
		if (!superProps.isEmpty()) {
			Set<String> keys = superProps.keySet();
			for (String key : keys) {
				subProps.put(key, superProps.get(key));
			}
		}
	}
	
	@Test
	public void testAgentList() throws FileNotFoundException, JAXBException {
		List<EventProcessingAgent> eventProcessingAgents = agentList.getAgents();
		if (eventProcessingAgents != null && !eventProcessingAgents.isEmpty()) {
			for (EventProcessingAgent eventProcessingAgent : eventProcessingAgents) {
				epService.registerAgent(eventProcessingAgent);
			}
		}
		assertNotNull(agentList.getAgents());
	}
	
	@Test
	public void testAgentName() {
		String name = "ResponseTimeAgent";
		assertEquals("Xml has modified", name, agentList.getAgents().get(0).getId());
	}
	
	@Test
	public void testAgentDescription() {
		String desc = "Response time from invocation";
		assertEquals("Xml has modified", desc, agentList.getAgents().get(0).getDescription());
	}
	
	
	@Test
	public void testAgentTransformer(){
		for(EventProcessingAgent a : agentList.getAgents()){
			assertNotNull(a.getTransformer());
		}
	}
	
	@Test
	public void testTransformerType() {
		List<String> types = new ArrayList<String>(Arrays.asList(Processing.TYPES)) ;
		for(EventProcessingAgent a : agentList.getAgents()){
			assertTrue(types.contains(a.getTransformer().getType()));
		}
	}
	
	@Test
	public void testTransformerQuery(){
		for (EventProcessingAgent agent : agentList.getAgents()) {
			Processing t = agent.getTransformer();
			List<String> query = t.getQueries();
			assertNotNull(query);
		}
	}
	
}
