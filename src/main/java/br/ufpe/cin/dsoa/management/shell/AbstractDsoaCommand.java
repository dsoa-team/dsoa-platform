package br.ufpe.cin.dsoa.management.shell;

import org.apache.felix.shell.Command;

import br.ufpe.cin.dsoa.management.ManagementService;

public abstract class AbstractDsoaCommand implements Command {
	
	protected ManagementService managementService;

	public String getUsage() {
		return "dsoa " + getName();
	}
	
}
