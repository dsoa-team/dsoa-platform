package br.ufpe.cin.dsoa.agent;

import br.ufpe.cin.dsoa.agent.channel.OutputChannel;

public class TransformerAgent<I,O> {
	
	private Class<O> outputEventType;
	private Derivator<I> derivator;
	private OutputChannel outputChannel;
	private OutputChannel errorChannel;
	
	public void process(I event) {
		O outputEvent;
		try {
			outputEvent = transform(event, derivator.derive(event));
			outputChannel.publish(outputEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorChannel.publish(event);
		}
		
	}

	private O transform(Object event, Object derive) throws Exception {
		return outputEventType.newInstance();
	}
}
