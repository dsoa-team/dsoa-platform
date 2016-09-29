package br.ufpe.cin.dsoa.platform.resource.mbean;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.event.agent.InputEvent;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingMapping;

public class ManagedAgent implements ManagedAgentMBean {

	private EventProcessingAgent agent;
	private EventProcessingService epService;

	public ManagedAgent(EventProcessingAgent agent, EventProcessingService epService) {
		if (!(agent.getProcessing() instanceof ProcessingMapping)) {
			throw new IllegalArgumentException();
		}
		this.agent = agent;
		this.epService = epService;
	}

	public void stop() throws MalformedObjectNameException, NullPointerException,
			MBeanRegistrationException, InstanceNotFoundException {
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName(this.getObjectName());
		mbeanServer.unregisterMBean(name);

	}

	public void start() throws MalformedObjectNameException, NullPointerException,
			InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {

		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName(this.getObjectName());
		mbeanServer.registerMBean(this, name);

	}

	public void setWindowSize(int size) {
		this.getInputEvent().getWindow().setSize(size);
	}

	public int getWindowSize() {
		int windowSize = this.getInputEvent().getWindow().getSize();

		return windowSize;
	}

	private InputEvent getInputEvent() {
		InputEvent inputEvent = ((ProcessingMapping) agent.getProcessing()).getInputEvent();
		return inputEvent;
	}

	public String getId() {
		String id = this.agent.getId();

		return id;
	}

	public String getDescription() {
		String description = this.agent.getDescription();

		return description;
	}

	public void update() {
		epService.unregisterAgent(this.getId());
		epService.registerAgent(this.agent);
	}

	public String getObjectName() {
		String objectName = String.format("dsoa:type=agent, name=%s", this.agent.getId());

		return objectName;
	}

	public String getWindowType() {
		String windowType = this.getInputEvent().getWindow().getType();
		
		return windowType;
	}

	public void setWindowType(String windowType) {
		this.getInputEvent().getWindow().setType(windowType);
	}

}