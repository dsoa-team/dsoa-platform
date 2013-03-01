package br.ufpe.cin.dsoa.epcenter.configuration.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.dsoa.epcenter.configurator.parser.agent.Agent;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.agent.AgentList;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.agent.Transformer;

public class AgentParserSpec {

	private JAXBContext context;
	private Unmarshaller u;
	
	private AgentList list;
	
	@Before
	public void setUp() throws JAXBException, FileNotFoundException{
		context = JAXBContext.newInstance(AgentList.class);
		u = context.createUnmarshaller();
		this.list = this.getList();
	}
	
	private AgentList getList() throws FileNotFoundException, JAXBException {
		AgentList list = (AgentList) u.unmarshal(new FileInputStream(
				"src/test/resources/epcenter/configuration/agent.xml"));
		return list;
	}
	
	@Test
	public void testConfigPath() {
		assertEquals(AgentList.CONFIG, "DSOA-INF/agent.xml");
	}
	
	@Test
	public void testAgentList() throws FileNotFoundException, JAXBException {
		assertNotNull(list.getAgents());
	}
	
	@Test
	public void testAgentName() {
		String name = "ResponseTime";
		assertEquals("Xml has modified", name, list.getAgents().get(0).getName());
	}
	
	@Test
	public void testAgentDescription() {
		String desc = "Response time from invocation";
		assertEquals("Xml has modified", desc, list.getAgents().get(0).getDescription());
	}
	
	@Test
	public void testAgentTransformer(){
		for(Agent a : list.getAgents()){
			assertNotNull(a.getTransformer());
		}
	}
	
	@Test
	public void testTransformerType() {
		List<String> types = new ArrayList<String>(Arrays.asList(Transformer.TYPES)) ;
		for(Agent a : list.getAgents()){
			assertTrue(types.contains(a.getTransformer().getType()));
		}
	}
	
	@Test
	public void testTransformerQuery(){
		Transformer t = list.getAgents().get(2).getTransformer();
		String query = t.getQuery();
		assertNotNull(query);
		
	}
	
}
