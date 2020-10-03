/**
 * 
 */
package de.nb.federkiel.logic;

/**
 * Signals that (during evalution of some formula), a
 * variable, that was needed, had no assignment
 * 
 * @author nbudzyn 2009
 */
public class UnassignedVariableException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnassignedVariableException() {
		super();
	}

	public UnassignedVariableException(final String msg) {
		super(msg);
	}
}
