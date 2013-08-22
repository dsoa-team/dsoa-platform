package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.management.ManagementService;

public class ListAttributesCommand implements Command  {

	protected ManagementService managementService;

	public String getUsage() {
		return "dsoa " + getName();
	}
	
	
	public final String getName() {
		return "metric-list";
	}
	
	public String getShortDescription() {
		return "List all monitorable metrics on platform";
	}

	
	public void execute(String line, PrintStream out, PrintStream err) {
		for(String metric : this.managementService.getMetricList()){
			out.println(" - " + metric.toString());
		}
		out.println("Total: " + this.managementService.getMetricList().size());
	}

}
