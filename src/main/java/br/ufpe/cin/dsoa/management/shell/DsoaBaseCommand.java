package br.ufpe.cin.dsoa.management.shell;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public abstract class DsoaBaseCommand implements DsoaCommand {
	protected static final String DSOA = "dsoa";
	protected static final String TOKEN = " ";
	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.shell.DsoaCommand#getUsage()
	 */
	public String getUsage() {
		StringBuffer buf = new StringBuffer(DSOA).append(TOKEN).append(getName()).append(TOKEN);
		List<String> params = this.getParameters();
		if (params != null) {
			Iterator<String> itr = params.iterator();
			while(itr.hasNext()) {
				buf.append(TOKEN).append(itr.next());
			}
		}
		return buf.toString();
	}
	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.shell.DsoaCommand#getParameters()
	 */
	public abstract List<String> getParameters();

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.shell.DsoaCommand#getName()
	 */
	public abstract String getName();

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.shell.DsoaCommand#getShortDescription()
	 */
	public abstract String getShortDescription();

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.shell.DsoaCommand#execute(java.lang.String, java.io.PrintStream, java.io.PrintStream)
	 */
	public abstract void execute(String line, PrintStream out, PrintStream err);

}
