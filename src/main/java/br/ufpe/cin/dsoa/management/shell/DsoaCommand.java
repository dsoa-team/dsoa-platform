package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;
import org.apache.felix.shell.ShellService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

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

		if (subcomand.split(" ").length >= 1) {
			try {
				shellService.executeCommand(subcomand, out, err);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getName() {
		return ACTION_NAME;
	}

	public String getShortDescription() {
		return "dsoa *";
	}

	public String getUsage() {
		return "dsoa *";
	}

	@Override
	public Object addingService(ServiceReference reference) {
		this.shellService = (ShellService) ctx.getService(reference);
		return shellService;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		this.shellService = (ShellService) ctx.getService(reference);
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		this.shellService = null;
	}
}
