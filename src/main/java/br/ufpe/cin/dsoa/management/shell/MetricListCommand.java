package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.metric.MetricCatalog;

public class MetricListCommand implements Command {

	private MetricCatalog catalog;
	
	private static final String ACTION_NAME = "metric-list";
	
	
	public String getName() {
		return ACTION_NAME;
	}

	
	public String getUsage() {
		return "metric-list";
	}

	
	public String getShortDescription() {
		return "*";
	}

	
	public void execute(String line, PrintStream out, PrintStream err) {
		for(Metric metric : catalog.getMetrics()){
			out.println(" - " + metric.toString());
		}
	}

}
