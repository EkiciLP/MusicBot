package net.tomatentum.musicbot.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ReturnException extends RuntimeException {

	public ReturnException() {
		super(" ");
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
