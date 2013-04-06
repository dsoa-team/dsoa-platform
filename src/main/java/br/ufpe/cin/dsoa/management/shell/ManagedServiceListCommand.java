package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;
import org.osgi.framework.Constants;

import br.ufpe.cin.dsoa.management.ManagedServiceMetadata;
import br.ufpe.cin.dsoa.management.ManagementService;

public class ManagedServiceListCommand implements Command {

	private static final String ACTION_NAME = "service-list";

	private ManagementService managementService;

	public String getName() {
		return ACTION_NAME;
	}

	public String getShortDescription() {
		return "*";
	}

	public String getUsage() {
		return ACTION_NAME + "";
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		for (ManagedServiceMetadata metadata : this.managementService
				.getManagedServicesMetadata()) {
			out.println(String.format(" - Id: %s :: %s", metadata.getId(),
					metadata.getProperty(Constants.SERVICE_DESCRIPTION)));
		}
		out.println("Total: "
				+ this.managementService.getManagedServicesMetadata().size());
	}

}
