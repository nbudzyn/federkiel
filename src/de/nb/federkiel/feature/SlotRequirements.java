package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.logic.AndFormula;
import de.nb.federkiel.logic.BooleanConstantTrue;
import de.nb.federkiel.logic.IFormula;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.YieldsNoResultException;

/**
 * The requirements to an element to fill a slot in role frame. (The subject of
 * a verb might need to be a NP in nominative and in singular, e.g.)
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class SlotRequirements implements Comparable<SlotRequirements> {
	/**
	 * All generated values shall be cached - to minimize memory use. The cache
	 * consists of weak references, so it will be cleared automatically, when a
	 * value is no longer (strongly) referenced.
	 */
	final private static WeakCache<SlotRequirements> cache =
			new WeakCache<>();

	/**
	 * The required grammar symbol name (NP, or art, e.g.).
	 */
	private final String grammarSymbolName;

	/**
	 * Requirements to features of the element, that is supposed to fill the
	 * slot. The required features ARE REQUIRED to exist - but the value
	 * <code>FeatureStructure.STRING_FEATURE_UNSPECIFIED</code> will also do!
	 */
	private final IFormula<FeatureAssignment> featureCondition;

	/**
	 * caching the hashCode
	 */
	private final int hashCode;

	public static SlotRequirements of(final String grammarSymbolName) {
		return cache.findOrInsert(new SlotRequirements(grammarSymbolName));
	}

	public static SlotRequirements of(
			final String grammarSymbolName,
			final IFormula<FeatureAssignment> featureCondition) {
		return cache.findOrInsert(new SlotRequirements(grammarSymbolName,
				featureCondition));
	}

	private SlotRequirements(final String grammarSymbolName) {
		this(grammarSymbolName, BooleanConstantTrue
				.<FeatureAssignment> getInstance());
	}

	private SlotRequirements(
			final String grammarSymbolName,
			final IFormula<FeatureAssignment> featureCondition) {
		super();
		this.grammarSymbolName = grammarSymbolName;
		this.featureCondition = featureCondition;
		hashCode = calcHash();
	}

	/**
	 * Whether these requirements are fulfilled.
	 *
	 * @param toBeChecked
	 *            the element that shall be checked, whether the slot
	 *            requirements are fulfilled
	 */
	protected boolean match(
			final IHomogeneousConstituentAlternatives toBeChecked) {
		if (!toBeChecked.getGrammarSymbol().equals(
				grammarSymbolName)) {
			return false;
		}

		try {
			final FeatureAssignment variableAssignment =
					FeatureAssignment
							.of(
							ImmutableList.<IHomogeneousConstituentAlternatives> of(),
							toBeChecked.getFeatures());

			if (!featureCondition.evaluate(variableAssignment)) {
				return false;
			}

			return true;
    } catch (final YieldsNoResultException e) {
      return false;
		} catch (final UnassignedVariableException e) {
			throw new IllegalStateException("Feature missing in parse "
					+ toBeChecked + "?",
					e);
		}
	}

	private int calcHash() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ featureCondition.hashCode();
		result = prime
				* result
				+ grammarSymbolName.hashCode();
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
		final SlotRequirements other = (SlotRequirements) obj;
		if (hashCode() != other.hashCode) {
			return false;
		}

		if (!featureCondition.equals(other.featureCondition)) {
			return false;
		}
		if (!grammarSymbolName.equals(other.grammarSymbolName)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final SlotRequirements other) {
		// In case, other where a subclass!
		final int classNameCompared =
				this.getClass().getCanonicalName().compareTo(
						other.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final int symbolNamesCompared =
				grammarSymbolName.compareTo(other.grammarSymbolName);
		if (symbolNamesCompared != 0) {
			return symbolNamesCompared;
		}

		return featureCondition.compareTo(other.featureCondition);
	}

	@Override
	public String toString() {
		final StringBuilder res = new StringBuilder();
		res.append(grammarSymbolName);

		if (!(featureCondition instanceof BooleanConstantTrue)) {
			final String conditionString =
					formulaCommaSepString(featureCondition);

			if (!conditionString.isEmpty()) {
				res.append("(");
				res.append(conditionString);
				res.append(")");
			}
		}

		return res.toString();
	}

	private String formulaCommaSepString(
			final IFormula<FeatureAssignment> formula) {
		if (formula instanceof AndFormula) {
			final AndFormula<FeatureAssignment> andFormula =
					(AndFormula<FeatureAssignment>) formula;
			return formulaCommaSepString(andFormula.getFirstFormula()) +
					", " +
					formulaCommaSepString(andFormula.getSecondFormula());
		}

		return formula.toString();
	}
}
