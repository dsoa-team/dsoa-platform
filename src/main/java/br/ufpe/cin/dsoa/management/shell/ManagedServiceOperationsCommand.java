package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.management.ManagedService;
import br.ufpe.cin.dsoa.management.ManagedServiceRegistry;

public class ManagedServiceOperationsCommand implements Command {

	private static final String ACTION_NAME = "service-operations";

	private ManagedServiceRegistry serviceRegistry;

	public String getName() {
		return ACTION_NAME;
	}

	public String getShortDescription() {
		return "*";
	}

	public String getUsage() {
		return "service-operations";
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		if (line.split(" ").length > 1) {
			try {
				int serviceId = Integer.parseInt(line.split(" ")[1]);
				ManagedService service = this.serviceRegistry
						.getService(serviceId + "");
				if (null != service) {
					out.println("Operations: ");
					out.println("Service id: " + service.getId());
					
				}
			} catch (NumberFormatException e) {
				err.println("Parameter error: You should give a number of service id");
			}
		} else {
			err.println("Incorrect number of parameters");
		}
	}

}
