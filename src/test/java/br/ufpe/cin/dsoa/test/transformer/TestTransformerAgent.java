package br.ufpe.cin.dsoa.test.transformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.junit.Before;

import br.ufpe.cin.dsoa.agent.TransformerAgent;
import br.ufpe.cin.dsoa.agent.channel.OutputChannel;
import br.ufpe.cin.dsoa.event.AttributeType;
import br.ufpe.cin.dsoa.event.EventType;


public class TestTransformerAgent {
	
	/*
	 * private String consumerId;
	private String providerId;
	private String operationName;
	private Class<?>[] parameterTypes;
	private Object[] parameterValues;
	private Class<?> returnType;
	private Object returnValue;
	private boolean success;
	private Exception exception;
	private long resquestTimestamp;
	private long responseTimestamp;
	 */
	
	@Before
	public void setUp() {
		AttributeType consumerIdType = new AttributeType("consumerIdType",  String.class, true);
		AttributeType providerIdType = new AttributeType("providerIdType", String.class, true);
		AttributeType operationNameType = new AttributeType("operationNameType", String.class, true);
		
		Class<?>[] clazz = {};
		/*AttributeType<new Class<?>[]{}.getClass()> parameterTypes = new AttributeType<new Class<?>[]{}.getClass()>("consumerId", true, clazz.getClass());
		AttributeType<String> consumerIdType = new AttributeType("consumerId", true);
		AttributeType<String> consumerIdType = new AttributeType("consumerId", true);*/
		
		//EventTypeRegister.registerEventType(new EventType("ResponseTimeEventType",false,))
	}
	
	public void testTransformerAgent() {
		//TransformerAgent agent = new ResponseTimeTransformerAgent();
	}
	
	static class ResponseTimeTransformerAgent extends TransformerAgent {

		public ResponseTimeTransformerAgent(List<EventType> inputEventType,
				EventType outputEventType, OutputChannel outputChannel,
				OutputChannel errorChannel) {
			super(inputEventType, outputEventType, outputChannel, errorChannel);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	static class Proxy implements InvocationHandler {

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			EventType rtEventType = EventTypeRegister.getEventType("ResponseTimeEventType");
			return null;
		}
		
	}
	
	static class EventTypeRegister {
		private static Map<String, EventType> eventTypes;
		
		public static void registerEventType(EventType eventType) {
			eventTypes.put(eventType.getId(), eventType);
		}
		
		public static EventType getEventType(String id) {
			return eventTypes.get(id);
		}
	}
}
