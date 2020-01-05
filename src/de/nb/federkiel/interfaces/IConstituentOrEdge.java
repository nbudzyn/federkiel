package de.nb.federkiel.interfaces;

import java.util.Set;

import javax.annotation.Nonnull;

import de.nb.federkiel.feature.SurfacePart;

/**
 * A constituent (consisting of passive edges) or an active edge.
 *
 * @author nbudzyn 2010
 */
public interface IConstituentOrEdge extends Comparable<IConstituentOrEdge> {
	/**
	 * Returns the part of the service this edge covers (taken directly from the
	 * input)
	 */
	@Nonnull
	public SurfacePart getSurfacePart();

	/**
	 * The grammar symbol that is (partially still, in case of an active edge)
	 * realized by this word sequence.
	 */
	public String getGrammarSymbol();

	public void visitWordForms(IWordFormVisitor visitor);

	public Set<IGuessedWordForm> getGuessedWordForms();
}
