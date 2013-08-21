package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

public class MetricListCommand extends AbstractDsoaCommand {

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
