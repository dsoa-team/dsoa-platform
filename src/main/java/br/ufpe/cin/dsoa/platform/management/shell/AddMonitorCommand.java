package br.ufpe.cin.dsoa.platform.management.shell;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ufpe.cin.dsoa.platform.management.PlatformManagementService;

public class AddMonitorCommand  extends DsoaBaseCommand {

	private static final String DESCRIPTION = "Configures monitoring configuration for the provided service";

	private static final String COMMAND = "addmon";

	protected PlatformManagementService managementService;

	private static final int PID = 0;
	private static final int CATEGORY = 1;
	private static final int ATTRIBUTE = 2;
	private static final int SPECIFICATION = 3;
	private static final int OPERATION = 4;

	
	public final String getName() {
		return COMMAND;
	}

	public String getShortDescription() {
		return DESCRIPTION;
	}

	//dsoa monitor 
	public void execute(String line, PrintStream out, PrintStream err) {
		
		String[] params = line.split(" ");
		params = Arrays.copyOfRange(params, 1, params.length);
		if (params.length == 5) { //operation monitor
			String pid = params[PID];
			String category = params[CATEGORY];
			String attribute = params[ATTRIBUTE];
			String specification = params[SPECIFICATION];
			String operation = params[OPERATION];
			this.managementService.addAttributeMonitor(pid, attribute, category, specification, operation);
			
		} else if(params.length == 4) {//service monitor
			String pid = params[PID];
			String category = params[CATEGORY];
			String attribute = params[ATTRIBUTE];
			String specification = params[SPECIFICATION];
			this.managementService.addAttributeMonitor(pid, attribute, category,specification, null);
		}
	}

	@Override
	public List<String> getParameters() {
		List<String> params = new ArrayList<String>();
		params.add("component-id");
		params.add("attribute-category");
		params.add("attribute-name");
		params.add("service-interface");
		params.add("[operation]");
		return params;
	}

}
