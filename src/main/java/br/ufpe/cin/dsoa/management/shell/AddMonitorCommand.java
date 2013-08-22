package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.management.ManagementService;

public class AddMonitorCommand implements Command {

	protected ManagementService managementService;

	private static final int PID = 0;
	private static final int CATEGORY = 1;
	private static final int ATTRIBUTE = 2;
	private static final int OPERATION = 3;
	
	public final String getName() {
		return "monitor";
	}

	public String getShortDescription() {
		return "Configures monitoring configuration for the provided service";
	}

	public String getUsage() {
		return "dsoa " + getName() + " [service-pid] [attribute-category] [attribute-name] <operation>";
	}

	//dsoa monitor 
	public void execute(String line, PrintStream out, PrintStream err) {
		String[] params = line.split(" ");
		if (params.length == 4) { //operation monitor
			String pid = params[PID];
			String category = params[CATEGORY];
			String attribute = params[ATTRIBUTE];
			String operation = params[OPERATION];
			this.managementService.addMetricMonitor(pid, attribute, category, operation);
			
		} else if(params.length == 3) {//service monitor
			String pid = params[PID];
			String category = params[CATEGORY];
			String attribute = params[ATTRIBUTE];
			this.managementService.addMetricMonitor(pid, attribute, category, null);
		}
	}

}
