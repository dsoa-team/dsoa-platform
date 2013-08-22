package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;
import org.osgi.framework.Constants;

import br.ufpe.cin.dsoa.management.ManagementService;
import br.ufpe.cin.dsoa.monitor.MonitoredServiceMetadata;

public class ListServicesCommand implements Command {
	private static final String ACTION_NAME = "service-list";
	protected ManagementService managementService;

	public String getUsage() {
		return "dsoa " + getName();
	}
	
	public String getName() {
		return ACTION_NAME;
	}
	
	public String getShortDescription() {
		return "List all provided services on plaform";
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		for (MonitoredServiceMetadata metadata : this.managementService
				.getManagedServicesMetadata()) {
			out.println(String.format(" - Pid: %s :: %s", metadata.getPid(),
					metadata.getProperty(Constants.SERVICE_DESCRIPTION)));
		}
		out.println("Total: "
				+ this.managementService.getManagedServicesMetadata().size());
	}

}
