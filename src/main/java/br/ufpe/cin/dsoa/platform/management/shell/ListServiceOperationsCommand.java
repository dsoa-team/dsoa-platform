package br.ufpe.cin.dsoa.platform.management.shell;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.platform.management.PlatformManagementService;
import br.ufpe.cin.dsoa.platform.monitor.ServiceMetadata;

public class ListServiceOperationsCommand extends DsoaBaseCommand {

	protected PlatformManagementService managementService;
	private static final String COMMAND 		= "lssrvop";
	private static final String DESCRIPTION 	= "List monitored service operations";
	private static final String PARAMETER 		= "service-pid";
	
	public void execute(String line, PrintStream out, PrintStream err) {
		if (line.split(" ").length > 1) {
			String servicePid = line.split(" ")[1];
			ServiceMetadata metadata = this.managementService
					.getManagedServiceMetadata(servicePid);
			
			if (null != metadata) {
				out.println("Service: ");
				out.println("Service Pid: " + metadata.getId());
				out.println("Service interface: " + metadata.getClassName());
				out.println("Operations: ");
				for (String operation : metadata.getOperations()) {
					out.println(" - " + operation);
				}
				out.println("Attribute constraints:");
				List<AttributeConstraint> constraints = metadata.getAttributeConstraints();
				Iterator<AttributeConstraint> itrConstraints = constraints.iterator();
				while (itrConstraints.hasNext()) {
					out.println(" - " + itrConstraints.next());
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
