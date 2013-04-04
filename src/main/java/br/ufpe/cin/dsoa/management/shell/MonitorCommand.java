package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.management.service.ManagedService;
import br.ufpe.cin.dsoa.management.service.ManagedServiceCatalog;
import br.ufpe.cin.dsoa.metric.MetricCatalog;

public class MonitorCommand implements Command {

	private static final String ACTION_NAME = "monitor";

	private ManagedServiceCatalog serviceCatalog;
	private MetricCatalog metricCatalog;

	public String getName() {
		return ACTION_NAME;
	}

	public String getShortDescription() {
		return "*";
	}

	public String getUsage() {
		return "monitor";
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		
	}

}
