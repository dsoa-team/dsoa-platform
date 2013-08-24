package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;
import java.util.List;

import org.osgi.framework.Constants;

import br.ufpe.cin.dsoa.management.ManagementService;
import br.ufpe.cin.dsoa.monitor.MonitoredServiceMetadata;

public class ListServicesCommand extends DsoaBaseCommand {
	private static final String DESCRIPTION = "List all provided services on plaform";
	private static final String COMMAND = "lstsrv";
	protected ManagementService managementService;

	public String getName() {
		return COMMAND;
	}
	
	public String getShortDescription() {
		return DESCRIPTION;
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

	@Override
	public List<String> getParameters() {
		return null;
	}

}
