package se.umu.cs.nfl.aj.wta.exceptions;

public class SymbolUsageException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SymbolUsageException() {
		super();
	}

	public SymbolUsageException(String message) {
		super(message);
	}

	public SymbolUsageException(String message, Throwable cause) {
		super(message, cause);
	}

	public SymbolUsageException(Throwable cause) {
		super(cause);
	}

}
