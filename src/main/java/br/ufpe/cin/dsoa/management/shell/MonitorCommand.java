package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.management.ManagementService;

public class MonitorCommand implements Command {

	private static final String ACTION_NAME = "monitor";

	private ManagementService managementService;

	public String getName() {
		return ACTION_NAME;
	}

	public String getShortDescription() {
		return "*";
	}

	public String getUsage() {
		return ACTION_NAME + "";
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		
	}

}
