package br.ufpe.cin.dsoa.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.agent.channel.OutputChannel;
import br.ufpe.cin.dsoa.event.Attribute;
import br.ufpe.cin.dsoa.event.Context;
import br.ufpe.cin.dsoa.event.Event;
import br.ufpe.cin.dsoa.event.EventType;

public abstract class TransformerAgent {
	private List<EventType> inputEventType;
	private EventType outputEventType;

	private Map<String, String> transformationMaps;

	private OutputChannel outputChannel;
	private OutputChannel errorChannel;

	public TransformerAgent(List<EventType> inputEventType,
			EventType outputEventType, OutputChannel outputChannel,
			OutputChannel errorChannel) {
		super();
		this.inputEventType = inputEventType;
		this.outputEventType = outputEventType;
		this.outputChannel = outputChannel;
		this.errorChannel = errorChannel;
	}

	public void process(List<Event> events, Context context) throws Exception {
		Event outputEvent;
		Map<String, Object> stateMap = this.buildState(events, context);
		try {
			outputEvent = transform(stateMap);
			outputChannel.publish(outputEvent);
		} catch (Exception e) {
			e.printStackTrace();
			errorChannel.publish(null);
		}
	}

	private Map<String, Object> buildState(List<Event> events, Context context) {
		Map<String, Object> attMap = new HashMap<String, Object>();
		for (Event event : events) {
			String eventTypeId = event.getEventType().getId();
			List<Attribute> attLst = event.getApplicationAttributes();
			for (Attribute att : attLst) {
				attMap.put(
						eventTypeId + "." + att.getAttributeType().getName(),
						att.getValue());
			}
		}
		for (String key : context.keySet()) {
			attMap.put(key, context.get(key));
		}
		return attMap;
	}

	public Event transform(Map<String, Object> stateMap) throws Exception {
		String attExpression = null;
		Object value = null;
		List<Attribute> attLst = new ArrayList<Attribute>();
/*		for (String attName : transformationMaps.keySet()) {
			if (outputEventType.getApplicationAttributeTypeList())
			attExpression = transformationMaps.get(attName);
			if (attExpression != null) {
				value = stateMap.get(attExpression);
			}
			attLst.add(new Attribute<>)
		}
		Object event.getAttribute(name).*/
		return null;
	}
}
