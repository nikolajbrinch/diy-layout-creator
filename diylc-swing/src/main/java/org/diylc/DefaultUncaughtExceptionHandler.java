package org.diylc;

public class DefaultUncaughtExceptionHandler implements
		Thread.UncaughtExceptionHandler {

	public void handle(Throwable thrown) {
		handleException(Thread.currentThread().getName(), thrown);
	}

	public void uncaughtException(Thread thread, Throwable thrown) {
		handleException(thread.getName(), thrown);
	}

	protected void handleException(String tname, Throwable thrown) {
		System.err.println("Exception on " + tname);
		thrown.printStackTrace();
	}
}