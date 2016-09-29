package br.ufpe.cin.dsoa.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class DsoaSimpleLogger {
	
	
	public static Logger getDsoaLogger(String loggerName, final boolean prefix, final boolean newLine) {
		return getDsoaLogger(loggerName, null, prefix, newLine);
	}
	
	public static Logger getDsoaLogger(String loggerName, String filename, final boolean prefix, final boolean newLine) {
		final Logger logger = Logger.getLogger(loggerName);
		logger.setUseParentHandlers(false);
		java.util.logging.Formatter formatter = getFormatter(prefix,newLine);
		Handler handler = getHandler(filename, formatter);
		logger.addHandler(handler);
		return logger;
	}

	private static Handler getHandler(String filename, Formatter formatter) {
		Handler handler = null;
		if (null == filename) {
			handler = new ConsoleHandler();
		} else {
			try {
				handler = new FileHandler(filename);
			} catch (Exception e) {
				handler = new ConsoleHandler();
			}
		}
		handler.setFormatter(formatter);
		return handler;
	}

	private static java.util.logging.Formatter getFormatter(final boolean prefix, final boolean newLine) {
		java.util.logging.Formatter formatter = new java.util.logging.Formatter() {
			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				if (prefix) {
					builder.append(Constants.DSOA_PREFIX).append(": ");
				}
				builder.append(formatMessage(record));
				if (newLine) {
					builder.append("\n");
				}
				return builder.toString();
			}
		};
		return formatter;
	}

}
