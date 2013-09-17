package br.ufpe.cin.dsoa.epcenter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.epcenter.helper.HelperEpCenterTest;
import br.ufpe.cin.dsoa.platform.event.impl.EsperProcessingService;
import br.ufpe.cin.dsoa.platform.event.impl.Query;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;

public class TestAgentBuilder {

	private List<EventProcessingAgent> agents;
	private EsperProcessingService epService;
	private List<EventType> eventTypes;

	@Before
	public void setUp() throws Exception {
		this.epService = new EsperProcessingService();
		this.epService.start();
		
		this.eventTypes = HelperEpCenterTest.handleEventDefinitions(epService).getEvents();
		this.agents = HelperEpCenterTest.handleAgentDefinitions(epService).getAgents();
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
				stmt.addListener(HelperEpCenterTest.getEventListener(this.epService));
			}
		}
		HelperEpCenterTest.publishSampleInvocationEvent(epService);
	}
}
