package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

public class MetricAddCommand implements Command {

	private static final String ACTION_NAME = "metric-add";
	
	
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
		// TODO Auto-generated method stub
		
	}

}
