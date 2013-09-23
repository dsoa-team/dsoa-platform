package br.ufpe.cin.dsoa.platform.management.shell;

import java.io.PrintStream;
import java.util.List;

import br.ufpe.cin.dsoa.platform.management.PlatformManagementService;

public class ListAgentsCommand extends DsoaBaseCommand {

		protected PlatformManagementService managementService;
		
		private static final String COMMAND = "lsagt";

		private static final String DESCRIPTION = "List all agents running on dsoa platform";

		public final String getName() {
			return COMMAND;
		}
		
		public String getShortDescription() {
			return DESCRIPTION;
		}

		
		public void execute(String line, PrintStream out, PrintStream err) {
			List<String> agents = this.managementService.getAgentList(); 
			for(String agent : agents){
				out.println(" - " + agent);
			}
			out.println("Total: " + agents.size());
		}


		@Override
		public List<String> getParameters() {
			return null;
		}

}
