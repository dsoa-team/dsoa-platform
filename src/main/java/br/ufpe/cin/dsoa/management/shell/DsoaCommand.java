package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;
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
public class DsoaCommand implements Command, ServiceTrackerCustomizer {

	private static final String ACTION_NAME = "dsoa";
	private BundleContext ctx = null;
	private ShellService shellService;

	public DsoaCommand(BundleContext ctx) {
		this.ctx = ctx;
		new ServiceTracker(ctx, ShellService.class.getName(), this).open();
	}

	public void execute(String s, PrintStream out, PrintStream err) {

		String subcomand = s.replaceAll(ACTION_NAME, "");

		if (subcomand.split(" ").length >= 1 && !subcomand.trim().equalsIgnoreCase("")) {
			try {
				shellService.executeCommand(subcomand, out, err);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			err.println("Incorrect number of parameters");
		}
	}

	public String getName() {
		return ACTION_NAME;
	}

	public String getShortDescription() {
		return "Main command of dsoa platform";
	}

	public String getUsage() {
		return "dsoa [cmd] <params>" +
				"\n" +
				"metric-list" +
				"service-operations" +
				"monitor" +
				"service-list";
	}

	
	public Object addingService(ServiceReference reference) {
		this.shellService = (ShellService) ctx.getService(reference);
		return shellService;
	}


	public void modifiedService(ServiceReference reference, Object service) {
		this.shellService = (ShellService) ctx.getService(reference);
	}

	public void removedService(ServiceReference reference, Object service) {
		this.shellService = null;
	}
}
