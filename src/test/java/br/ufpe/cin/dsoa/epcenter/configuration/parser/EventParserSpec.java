package br.ufpe.cin.dsoa.epcenter.configuration.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.dsoa.event.Event;
import br.ufpe.cin.dsoa.event.EventList;

public class EventParserSpec {

	private JAXBContext context;
	private Unmarshaller u;
	private EventList list;

	@Before
	public void setUp() throws JAXBException, FileNotFoundException {
		context = JAXBContext.newInstance(EventList.class);
		u = context.createUnmarshaller();

		list = this.getList();
	}

	private EventList getList() throws FileNotFoundException, JAXBException {
		EventList list = (EventList) u.unmarshal(new FileInputStream(
				"src/test/resources/epcenter/configuration/event.xml"));
		return list;
	}

	@Test
	public void testConfigPath() {
		assertEquals(EventList.CONFIG, "DSOA-INF/event.xml");
	}

	@Test
	public void testDefaultEventType() {
		Event e = list.getEvents().get(0);
		assertEquals("xml has modified", "DsoaEvent", e.getType());
	}

	@Test
	public void testEventList() throws JAXBException, FileNotFoundException {
		assertNotNull(list.getEvents());
	}

	@Test
	public void testEvetProperties() throws FileNotFoundException,
			JAXBException, ClassNotFoundException {
		Map<String, Object> props = list.getEvents().get(0).getProperties();
		assertNotNull(props);
	}

	@Test
	public void testEventProperty() throws FileNotFoundException,
			JAXBException, ClassNotFoundException {

		Event e = list.getEvents().get(0);
		assertEquals(2, e.getProperties().size());

		Set<String> set = e.getProperties().keySet();
		for (Object id : set) {
			assertNotNull(e.getProperties().get(id));
		}
	}

}
