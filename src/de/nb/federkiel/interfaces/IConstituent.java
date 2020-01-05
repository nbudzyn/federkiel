package de.nb.federkiel.interfaces;

import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.feature.FeatureStructure;

/**
 * A constituent: A Result of successful parsing, a sub-sequence from the input,
 * that carries some grammatical features and a meaning, and that might
 * recursively contain other successful parsings for its parts.
 * <p>
 * The surface, from-and-to, grammar symbol, features and semantics do NOT
 * change (that means, they always remain equal). Other details might change
 * over time.
 * <p>
 * All implementations shall be thread-safe.
 *
 * @author nbudzyn 2009
 */
@ThreadSafe
public interface IConstituent extends IConstituentOrEdge {
	public FeatureStructure getFeatures();

	public ISemantics getSemantics();

	/**
	 * Returns the number of all parsings represented by this grammar symbol
	 * realization, multiplying all possibilities (from the alternativity sets,
	 * e.g.) with each other.
	 * <p>
	 * So this returns a number that tells how many <i>meanings</i> this grammar
	 * symbolRealization might have.
	 */
	public long getNumParsesEffectively();

	/**
	 * A pretty (easy-to read, informative) String representation.
	 *
	 * @param includeMultilineRecursiveDetails
	 *          Whether it should recursively contain details - might span multiple
	 *          lines.
	 */
	public String toPrettyString(boolean includeMultilineRecursiveDetails);
}
