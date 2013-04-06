package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.management.ManagementService;

public class MetricListCommand implements Command {

	private ManagementService managementService;
	
	private static final String ACTION_NAME = "metric-list";
	
	
	public String getName() {
		return ACTION_NAME;
	}

	
	public String getUsage() {
		return ACTION_NAME + "";
	}

	
	public String getShortDescription() {
		return "*";
	}

	
	public void execute(String line, PrintStream out, PrintStream err) {
		for(String metric : this.managementService.getMetricList()){
			out.println(" - " + metric.toString());
		}
		out.println("Total: " + this.managementService.getMetricList().size());
	}

}
