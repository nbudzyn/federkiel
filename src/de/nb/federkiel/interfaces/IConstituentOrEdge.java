package de.nb.federkiel.interfaces;

import java.util.Set;

/**
 * A constituent (consisting of passive edges) or an active edge.
 *
 * @author nbudzyn 2010
 */
public interface IConstituentOrEdge extends
Comparable<IConstituentOrEdge> {
	/**
	 * @return the String taken directly from the input
	 */
	public String getSurface();

	/**
	 * @return TOKEN index (in the input), where this sequence starts (0 is
	 *         before the firstTerm word)
	 */
	public int getFrom();

	/**
	 * @return TOKEN index (in the input), where this sequence ends (0 is before
	 *         the firstTerm word)
	 */
	public int getTo();

	/**
	 * @return the CHARACTER index (in the input), where this sequence starts (0
	 *         is the first character)
	 */
	public int getCharacterIndexFrom();

	/**
	 * @return the CHARACTER index (in the input), where this sequence ends
	 *         MINUS 1. (So an empty edge could be starting at 5 and ending at
	 *         4).
	 */
	public int getCharacterIndexTo();

	/**
	 * The grammar symbol that is (partially still, in case of an active edge)
	 * realized by this word sequence.
	 */
	public String getGrammarSymbol();

	public void visitWordForms(IWordFormVisitor visitor);

	public Set<IGuessedWordForm> getGuessedWordForms();
}
