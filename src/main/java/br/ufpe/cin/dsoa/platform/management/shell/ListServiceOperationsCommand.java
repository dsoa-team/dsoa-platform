package br.ufpe.cin.dsoa.platform.management.shell;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.platform.management.PlatformManagementService;
import br.ufpe.cin.dsoa.platform.monitor.ServiceMetadata;

public class ListServiceOperationsCommand extends DsoaBaseCommand {

	protected PlatformManagementService managementService;
	private static final String COMMAND 		= "lstsrvop";
	private static final String DESCRIPTION 	= "List monitored service operations";
	private static final String PARAMETER 		= "service-pid";
	
	public void execute(String line, PrintStream out, PrintStream err) {
		if (line.split(" ").length > 1) {
			String servicePid = line.split(" ")[1];
			ServiceMetadata metadata = this.managementService
					.getManagedServiceMetadata(servicePid);
			
			if (null != metadata) {
				out.println("Operations: ");
				out.println("Service Pid: " + metadata.getPid());
				for (String it : metadata.getOperationsMap().keySet()) {
					out.println(" * " + it);
					for (String operation : metadata.getOperationsMap()
							.get(it)) {
						out.println(" - " + operation);
					}
				}

			}
		} else {
			err.println("You should inform a service-pid");
		}
	}

	public String getName() {
		return  COMMAND;
	}

	public String getShortDescription() {
		return DESCRIPTION;
	}

	@Override
	public List<String> getParameters() {
		List<String> params = new ArrayList<String>();
		params.add(PARAMETER);
		return params;
	}

}
