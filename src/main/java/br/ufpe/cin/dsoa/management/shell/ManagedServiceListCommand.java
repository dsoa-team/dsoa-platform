package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.management.service.ManagedService;
import br.ufpe.cin.dsoa.management.service.ManagedServiceCatalog;

public class ManagedServiceListCommand implements Command{
	
	private static final String ACTION_NAME = "service-list";
	
	private ManagedServiceCatalog catalog;

	public String getName() {
		return ACTION_NAME;
	}

	public String getShortDescription() {
		return "*";
	}

	public String getUsage() {
		return "service-list";
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		for(ManagedService service : catalog.getServices()) {
			out.println(" - " + service.toString());
		}
		out.println("Total: " + catalog.getServices().size());
	}

}
