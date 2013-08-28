package br.ufpe.cin.dsoa.platform.management.shell;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.shell.ShellService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * This class is the root of DSOA's command shell. Every DSOA command should be enacted through 'dsoa' prefix,
 * so that this class is called to handle the command. In fact, it just delegate the resposibility of executing
 * the command itself to the OSGI's ShellCommand.
 * 
 * @author dsoa-team
 *
 */
public class DsoaShell extends DsoaBaseCommand implements ServiceTrackerCustomizer  {

	private static final String DESCRIPTION = "Dsoa shell";
	private static final String OR = " | ";
	
	private ShellService shellService;
	private ServiceTracker tracker;
	private Map<String, DsoaCommand> commands = new HashMap<String, DsoaCommand>();
	private BundleContext ctx;

	public DsoaShell(BundleContext context) {
		this.ctx = context;
		this.tracker = new ServiceTracker(context, DsoaCommand.class.getName(),this);
	}
	
	public void execute(String s, PrintStream out, PrintStream err) {

		String subcomand = s.replaceAll(DSOA, "").trim();

		if (!subcomand.trim().equalsIgnoreCase("")) {
			try {
				shellService.executeCommand(subcomand, out, err);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			err.println(this.getUsage());
		}
	}

	public void start() {
		this.tracker.open();
	}
	
	public void stop() {
		this.tracker.close();
	}
	
	public String getName() {
		return DSOA;
	}

	public String getShortDescription() {
		return DESCRIPTION;
	}
	
	@Override
	public String getUsage() {
		StringBuffer buf = new StringBuffer(getName()).append(TOKEN);
		List<String> params = this.getParameters();
		if (params != null) {
			Iterator<String> itr = params.iterator();
			while(itr.hasNext()) {
				buf.append(TOKEN).append(itr.next());
			}
		}
		return buf.toString();
	}
	
	@Override
	public List<String> getParameters() {
		List<String> params = new ArrayList<String>();
		Set<String> commands = this.commands.keySet();
		Iterator<String> cmdItr = commands.iterator();
		boolean first = true;
		while (cmdItr.hasNext()) {
			if (!first) {
				params.add(OR);
			} else {
				first = false;
			}
			params.add(cmdItr.next());
		}
		return params;
	}

	public Object addingService(ServiceReference reference) {
		DsoaCommand command = (DsoaCommand) this.ctx.getService(reference);
		if (!command.getName().equals(DSOA)) {
			this.commands.put(command.getName(), command);
			return command;
		} else {
			return null;
		}
	}

	public void modifiedService(ServiceReference reference, Object service) {
		
	}

	public void removedService(ServiceReference reference, Object service) {
		DsoaCommand command = (DsoaCommand)service;
		this.commands.remove(command.getName());
	}

}
