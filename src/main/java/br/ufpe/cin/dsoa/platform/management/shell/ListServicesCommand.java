package br.ufpe.cin.dsoa.platform.management.shell;

import java.io.PrintStream;
import java.util.List;

import org.osgi.framework.Constants;

import br.ufpe.cin.dsoa.platform.management.PlatformManagementService;
import br.ufpe.cin.dsoa.platform.monitor.ServiceMetadata;

public class ListServicesCommand extends DsoaBaseCommand {
	private static final String DESCRIPTION = "List all provided services on plaform";
	private static final String COMMAND = "lssrv";
	protected PlatformManagementService managementService;

	public String getName() {
		return COMMAND;
	}
	
	public String getShortDescription() {
		return DESCRIPTION;
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		for (ServiceMetadata metadata : this.managementService
				.getManagedServicesMetadata()) {
			
			out.println(String.format(" - [ComponentId]: %s", metadata.getId()));
			out.println(String.format(" - [Description]: %s", metadata.getProperty(Constants.SERVICE_DESCRIPTION)));
			out.println(String.format(" - [Interface]: %s", metadata.getClassName()));
			out.println();
		}
		out.println("Total: "
				+ this.managementService.getManagedServicesMetadata().size());
	}

	@Override
	public List<String> getParameters() {
		return null;
	}

}
