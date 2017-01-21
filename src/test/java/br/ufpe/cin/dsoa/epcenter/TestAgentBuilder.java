package br.ufpe.cin.dsoa.epcenter;

import org.junit.Before;
import org.junit.Test;

import antlr.collections.List;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.api.service.Expression;
import br.ufpe.cin.dsoa.epcenter.helper.HelperEpCenterTest;
import br.ufpe.cin.dsoa.platform.event.impl.EsperProcessingService;
import br.ufpe.cin.dsoa.platform.event.impl.EventTypeCatalogImpl;
import br.ufpe.cin.dsoa.platform.event.impl.Query;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;

public class TestAgentBuilder {

	private List<EventProcessingAgent> agents;
	private EsperProcessingService epService;
	private List<EventType> eventTypes;
	private EventTypeCatalog eventTypeCatalog;

	@Before
	public void setUp() throws Exception {
		//this.epService = new EsperProcessingService();//XXX: this test will fail
		this.epService.start();
		this.eventTypeCatalog = new EventTypeCatalogImpl();
		
		this.eventTypes = HelperEpCenterTest.handleEventDefinitions(HelperEpCenterTest.EVENT_DEFINITION_FILE, epService, eventTypeCatalog).getEvents();
		this.agents = HelperEpCenterTest.handleAgentDefinitions(HelperEpCenterTest.AGENT_DEFINITION_FILE, epService).getAgents();
	}

	@Test
	public void testAgentHandling() {
		assertFalse(agents.isEmpty());
	}
	
	@Test
	public void testEventHandling(){
		assertFalse(eventTypes.isEmpty());
	}

	@Test
	public void testQueryBuild() {
		for (EventProcessingAgent agent : this.agents) {
			if (agent.getProcessing() instanceof ProcessingMapping) {
				Query query = HelperEpCenterTest.getQuery(agent);
				assertNotNull(query);
				
				EPServiceProvider provider = this.epService.getEpProvider();
				EPStatement stmt = provider.getEPAdministrator().createEPL(query.getQueryString());
				stmt.addListener(HelperEpCenterTest.getEventListener(this.epService, eventTypeCatalog));
			}
		}
		HelperEpCenterTest.publishSampleInvocationEvent(epService);
	}
	
	
	@Test
	public void testSubscription(){
		
		String source = "service.operation";
		final EventType invocationEventType = HelperEpCenterTest.getInvocationEventType();
		PropertyType propertyType = invocationEventType.getMetadataPropertyType("source");
		EventFilter filter = HelperEpCenterTest.getEventFilter(propertyType, source, Expression.EQ);
		
		epService.subscribe(new EventConsumer() {
			
			@Override
			public void handleEvent(Event event) {
				System.out.println("===== CONSUMER: ======");
				System.out.println(event);
				org.junit.Assert.assertEquals(event.getEventType().getName(), invocationEventType.getName());
			}
			
			@Override
			public String getId() {
				return "consumer-01";
			}
		},new Subscription(invocationEventType , filter), false);
		
		epService.publish(HelperEpCenterTest.getSampleInvocationEvent(source));
		
	}
}
