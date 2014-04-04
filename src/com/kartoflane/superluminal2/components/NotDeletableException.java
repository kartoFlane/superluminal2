package com.kartoflane.superluminal2.components;

public class NotDeletableException extends RuntimeException {
	private static final long serialVersionUID = 3428108685925727460L;

	public NotDeletableException() {
		super();
	}

	public NotDeletableException(String s) {
		super(s);
	}

	public NotDeletableException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotDeletableException(Throwable cause) {
		super(cause);
	}
}
