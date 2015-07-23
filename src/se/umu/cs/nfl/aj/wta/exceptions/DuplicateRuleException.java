package se.umu.cs.nfl.aj.wta.exceptions;

public class DuplicateRuleException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateRuleException() {
		super();
	}

	public DuplicateRuleException(String message) {
		super(message);
	}

	public DuplicateRuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateRuleException(Throwable cause) {
		super(cause);
	}
	
}
