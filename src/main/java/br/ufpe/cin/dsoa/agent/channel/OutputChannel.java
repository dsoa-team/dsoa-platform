package br.ufpe.cin.dsoa.agent.channel;

import br.ufpe.cin.dsoa.event.Event;

public interface OutputChannel {

	void publish(Event event);

}
