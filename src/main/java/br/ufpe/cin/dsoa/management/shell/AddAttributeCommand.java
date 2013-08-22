package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;
import br.ufpe.cin.dsoa.management.ManagementService;

public class AddAttributeCommand implements Command {

	protected ManagementService managementService;

	public String getUsage() {
		return "dsoa " + getName();
	}
	
	public final String getName() {
		return "metric-add";
	}
	
	public String getShortDescription() {
		return "*";
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		// TODO Auto-generated method stub
		
	}

}
