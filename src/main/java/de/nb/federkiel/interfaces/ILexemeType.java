package de.nb.federkiel.interfaces;

/**
 * Type of a lexeme (noun, adjective, ...)
 *
 * @author nbudzyn 2011
 */
public interface ILexemeType {
	/**
	 * @return a unique key for this lexeme type
	 */
	String getKey();

	/**
	 * @return a user-friendly description
	 */
	String getDescription();
}
