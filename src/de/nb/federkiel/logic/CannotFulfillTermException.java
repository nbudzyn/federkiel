package de.nb.federkiel.logic;


/**
 * Signals, that a term cannot be fulfilled. Used, when the System tries to
 * build bounds for terms.
 *
 * @author nbudzyn 2010
 */
public class CannotFulfillTermException extends Exception {
	private static final long serialVersionUID = 1L;

	public CannotFulfillTermException() {
		super();
	}
}
