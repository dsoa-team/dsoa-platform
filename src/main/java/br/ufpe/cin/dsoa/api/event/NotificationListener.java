package br.ufpe.cin.dsoa.api.event;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;


public interface NotificationListener  {
	
	public void receive(AttributeValue value);
	
}
