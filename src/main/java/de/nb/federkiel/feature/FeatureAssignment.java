package de.nb.federkiel.feature;

import java.util.List;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.Variable;

/**
 * A special firstTerm-order logic variable assignment, useful for dealing with a symbol
 * when parsing.<p>
 * The variables are:
 * <ol>
 * <li>Some feature carriers, (for example the preceding grammar symbols in a grammar rule,
 *     assigned to their realization alternatives)
 * <li>the String feature names (of the symbol currently parsed, e.g.), assigned to their values
 * </ol>
 *
 * in which
 * the variables are assigned
 *
 * @author nbudzyn 2009
 */
public class FeatureAssignment implements IAssignment {
	/**
	 * All generated values shall be cached - to minimize memory use. The cache
	 * consists of weak references, so it will be cleared automatically, when a
	 * value is no longer (strongly) referenced.
	 */
	final private static WeakCache<FeatureAssignment> cache =
			new WeakCache<>();

	private final List<? extends IFeatureStructure> parseAlternatives;

	private final FeatureStructure features;

	/**
	 * caching the hashCode
	 */
	private final int hashCode;


	/**
	 * @param parseAlternatives
	 *            TODO hand-over -- nicht schön
	 * @param featuresOfThisSymbol
	 */
	public static FeatureAssignment of(
			final List<? extends IFeatureStructure> parseAlternatives,
			final FeatureStructure featuresOfThisSymbol) {
		return cache.findOrInsert(
				new FeatureAssignment(parseAlternatives, featuresOfThisSymbol));
	}

	/**
	 * @param parseAlternatives TODO hand-over -- nicht schön
	 * @param featuresOfThisSymbol
	 */
	private FeatureAssignment(
			final List<? extends IFeatureStructure> parseAlternatives,
			final FeatureStructure featuresOfThisSymbol) {
		super();
		this.parseAlternatives = parseAlternatives;
		features = featuresOfThisSymbol;
		hashCode = calcHash();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> T getValue(final Variable<T, ? extends IAssignment> variable)
	throws UnassignedVariableException {
		T res = null;

		if (variable instanceof UnqualifiedFeatureRefVariable) {
			// T == String
			final UnqualifiedFeatureRefVariable stringFeatureVariable = (UnqualifiedFeatureRefVariable) variable;
			try {
				res = (T) features.getFeatureValue(
						stringFeatureVariable.getFeatureName(), null);
			} catch (final IllegalArgumentException e) {
				throw new IllegalArgumentException("There was a problem with features of " +
						stringFeatureVariable.toString(), e);
			}
		} else if (variable instanceof QualifiedFeatureRefVariable) {
			// T == String
			final QualifiedFeatureRefVariable stringFeatureReferenceVariable =
				(QualifiedFeatureRefVariable) variable;

			final int symbolRefPosition = stringFeatureReferenceVariable.getSymbolRefPosition();

			if (symbolRefPosition < parseAlternatives.size()) {
				final IFeatureStructure parsesForRefencedSymbol =
					parseAlternatives.get(symbolRefPosition);
				try {
					res = (T) parsesForRefencedSymbol.getFeatureValue(
							stringFeatureReferenceVariable.getFeatureName());
				} catch (final IllegalArgumentException e) {
					throw new IllegalArgumentException(
							"There was a problem with feature " +
							stringFeatureReferenceVariable.toString(), e);
				}
			}
		} else if (variable instanceof SymbolReferenceVariable) {
			// T == ParseAlternatives
			final SymbolReferenceVariable<?> symbolReferenceVariable =
					(SymbolReferenceVariable<?>) variable;

			final int symbolRefPosition = symbolReferenceVariable.getSymbolRefPosition();

			if (symbolRefPosition < parseAlternatives.size()) {
				try {
					res = (T) parseAlternatives.get(symbolRefPosition);
				} catch (final IllegalArgumentException e) {
					throw new IllegalArgumentException("There was a problem with " +
							symbolReferenceVariable.toString(), e);
				}
			}
		}

		if (res == null) {
			throw new UnassignedVariableException();
			// FIXME
			// This is a very typical case - as many variables will be
			// evaluated, as they
			// do not have any value yet.
			//
			// So I do not use String building here.
			// throw new UnassignedVariableException("Variable not assigned: " +
			// variable +
			// " (Class: " + variable.getClass() + ")");
		}

		return res;
	}

	private final int calcHash() {
		final int prime = 31;
		int result = 1;
		result = prime
		* result
		+ parseAlternatives.hashCode();
		result = prime
		* result
		+ features.hashCode();
		return result;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FeatureAssignment other = (FeatureAssignment) obj;
		if (hashCode != other.hashCode) {
			return false;
		}

		if (!features
				.equals(other.features)) {
			return false;
		}
		if (!parseAlternatives
				.equals(other.parseAlternatives)) {
			return false;
		}
		return true;
	}


}
