package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.util.Constants;

public final class EventIdGenerator {

	private static Map<String, Long> keyMap = new HashMap<String, Long>();

	public static String nextId(Map<String, Object> eventBean) {
		String eventType = (String) eventBean.get(Constants.EVENT_TYPE);
		Long key = null;
		synchronized (keyMap) {
			key = keyMap.containsKey(eventType) ? ((Long) keyMap.get(eventType) + 1)
					: 1;
			keyMap.put(eventType, key);
		}
		return eventType + "#" + key;
	}
}
