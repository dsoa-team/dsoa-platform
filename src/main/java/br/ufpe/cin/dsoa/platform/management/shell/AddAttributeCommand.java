package br.ufpe.cin.dsoa.platform.management.shell;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.platform.management.PlatformManagementService;

public class AddAttributeCommand extends DsoaBaseCommand {

	protected PlatformManagementService platformManagementService;

	private static final String COMMAND		 	= "addatt";
	private static final String DESCRIPTION 	= "Add a new QoS attribute definition";

	public final String getName() {
		return COMMAND;
	}
	
	public String getShortDescription() {
		return DESCRIPTION;
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		
	}

	@Override
	public List<String> getParameters() {
		List<String> params = new ArrayList<String>();
		params.add("component-id");
		params.add("event-type");
		params.add("attribute-name");
		params.add("service-interface");
		params.add("[operation]");
		return params;
	}

}
