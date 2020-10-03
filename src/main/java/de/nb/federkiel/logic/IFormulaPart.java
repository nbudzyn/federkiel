package de.nb.federkiel.logic;

/**
 * A part in a formula. Might need brackets, when displayed as
 * part of a displayed formula.
 *
 * @author nbudzyn 2009
 */
public interface IFormulaPart {
	/**
	 * @param surroundWithBracketsIfApplicable
	 * if this parameter is set to <code>true</code>,
	 * the String will be surrounded with brackets -
	 * unless no brackets are used for
	 * this kind of term
	 */
	public String toString(final boolean surroundWithBracketsIfApplicable);
}