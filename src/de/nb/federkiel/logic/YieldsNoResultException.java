/**
 *
 */
package de.nb.federkiel.logic;

/**
 * Signals that a term or formula (not plurival!) has no result.
 * <p>
 * For example, the unification might yield a result - or it has no result.
 *
 * @author nbudzyn 2017
 */
public class YieldsNoResultException extends Exception {
	private static final long serialVersionUID = 1L;

	public YieldsNoResultException() {
		super();
	}

	public YieldsNoResultException(final String msg) {
		super(msg);
	}
}
