package net.tomatentum.musicbot.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ReturnException extends RuntimeException {

	private static final long serialVersionId = 0;


	public ReturnException() {
		super("", null, true, false);

	}

	@Override
	public void printStackTrace() {
	}

	@Override
	public void printStackTrace(PrintStream s) {

	}

	@Override
	public void printStackTrace(PrintWriter s) {
	}

	
}
