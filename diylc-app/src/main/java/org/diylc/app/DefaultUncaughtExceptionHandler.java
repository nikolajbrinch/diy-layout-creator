package org.diylc.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nikolajbrinch@gmail.com
 */
public class DefaultUncaughtExceptionHandler implements
		Thread.UncaughtExceptionHandler {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultUncaughtExceptionHandler.class);

	public void handle(Throwable thrown) {
		handleException(Thread.currentThread().getName(), thrown);
	}

	public void uncaughtException(Thread thread, Throwable thrown) {
		handleException(thread.getName(), thrown);
	}

	protected void handleException(String tname, Throwable thrown) {
		LOG.error("Uncaught exception in thread " + tname, thrown);
		thrown.printStackTrace();
	}
}