package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;

import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class MonitorCommand  implements Command {

	private static final String ACTION_NAME = "monitor";

	private BundleContext bc = null;

	public MonitorCommand(BundleContext bc) {
		this.bc = bc;
	}

	public void execute(String s, PrintStream out, PrintStream err) {

		String[] st = s.split(" ");
		String[] args;

		if (st.length > 1 && st.length <= 5) {

			args = new String[st.length - 1];
			System.arraycopy(st, 1, args, 0, args.length);

			long l = Long.valueOf(args[0]).longValue();
			Bundle bundle = bc.getBundle(l);

			if (args.length == 3) {

				try {
					if (bundle != null) {

						//MonitorConfigurationAgent.addMetric(bc, args[1],
						//		args[2], args[0], null);

					} else {
						err.println("Bundle ID " + args[0] + " is invalid.");
					}
				} catch (NumberFormatException ex) {
					err.println("Unable to parse id '" + args[0] + "'.");
				} catch (Exception ex) {
					err.println(ex.toString());
				}
			} else {

				//MonitorConfigurationAgent.addMetric(bc, args[1], args[2],
				//		args[0], args[3]);
			}
		} else {
			err.println("Incorrect number of arguments");
		}
	}

	public String getName() {
		return ACTION_NAME;
	}

	public String getShortDescription() {
		return "Monitor QoS Attribute of Services.";
	}

	public String getUsage() {
		return "monitor <id> <category> <attribute> [operation]";
	}
}
