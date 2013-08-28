package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.event.InvocationEvent;

import com.espertech.esper.client.EventBean;

public final class EventIdGenerator {
	
	private static Map<String, Long> keyMap = new HashMap<String, Long>();
	
	public static String nextId(InvocationEvent eventBean) {
		//String eventType = eventBean.getEventType().getName();
		String eventType = eventBean.getClass().getSimpleName();
		Long key = null;
		synchronized (keyMap) {
			key = keyMap.containsKey(eventType) ? ((Long)keyMap.get(eventType)+1) : 1;
			keyMap.put(eventType, key);
		}
		return eventType + "#" + key;
	}
}
