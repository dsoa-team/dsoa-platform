package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.osgi.framework.Constants;

import br.ufpe.cin.dsoa.monitor.MonitoredServiceMetadata;

public class ServiceListCommand extends AbstractDsoaCommand {

	public String getName() {
		return  "service-list";
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
