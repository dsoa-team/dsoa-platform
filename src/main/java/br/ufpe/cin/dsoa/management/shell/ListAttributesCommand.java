package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;
import java.util.List;

import br.ufpe.cin.dsoa.management.ManagementService;

public class ListAttributesCommand  extends DsoaBaseCommand {

	protected ManagementService managementService;
	
	private static final String COMMAND = "lstatt";

	private static final String DESCRIPTION = "List all monitorable metrics on platform";

	public final String getName() {
		return COMMAND;
	}
	
	public String getShortDescription() {
		return DESCRIPTION;
	}

	
	public void execute(String line, PrintStream out, PrintStream err) {
		for(String attribute : this.managementService.getAttributeList()){
			out.println(" - " + attribute.toString());
		}
		out.println("Total: " + this.managementService.getAttributeList().size());
	}


	@Override
	public List<String> getParameters() {
		return null;
	}

}
