package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.management.ManagementService;
import br.ufpe.cin.dsoa.monitor.MonitoredServiceMetadata;

public class ServiceOperationsCommand implements Command {

	private static final String ACTION_NAME = "service-operations";

	private ManagementService managementService;

	public String getName() {
		return ACTION_NAME;
	}

	public String getShortDescription() {
		return "Show service operations for every interface that service provider implements";
	}

	public String getUsage() {
		return "dsoa service-operations [service_pid]";
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		if (line.split(" ").length > 1) {
			try {
				String servicePid = line.split(" ")[1];
				MonitoredServiceMetadata metadata = this.managementService
						.getManagedServiceMetadata(servicePid + "");
				
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
			} catch (NumberFormatException e) {
				err.println("Parameter error: You should give a number of service id");
			}
		} else {
			err.println("Incorrect number of parameters");
		}
	}

}