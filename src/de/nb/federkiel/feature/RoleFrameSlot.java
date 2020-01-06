package de.nb.federkiel.feature;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.interfaces.IFeatureValue;

/**
 * An slot in a <code>RoleFrame</code> - e.g. the slot for the <i>subject</i> in
 * the role frame of a verb. Can be empty or filled.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public final class RoleFrameSlot implements IFeatureValue, Comparable<IFeatureValue> {
	/**
	 * All generated values shall be cached - to minimize memory use. The cache
	 * consists of weak references, so it will be cleared automatically, when a
	 * value is no longer (strongly) referenced.
	 */
	final private static WeakCache<RoleFrameSlot> cache = new WeakCache<>();

	/**
	 * Requirements to an element that could fill this slot. These are alternatives:
	 * The element only needs to fulfill <i>one</i> of these.
	 * <p>
	 * This collection has to be immutable!
	 */
	private final ImmutableCollection<SlotRequirements> alternativeRequirements;

	/**
	 * The minimal number of fillings, that this slot needs. Would be 0 for an
	 * optional slot, 1 for a mandatory slot.
	 */
	private final int minFillings;

	/**
	 * The maximal number of fillings, that this slot accepts, or -1, if there is no
	 * maximum.
	 */
	private final int maxFillings;

	/**
	 * The fillings of the slot. Can be empty (<i>empty slot</i>).
	 * <p>
	 * Fillings are values
	 */
	private final ImmutableSet<IFillingInSlot> fillings;

	/**
	 * caching the hashCode
	 */
	private final int hashCode;

	public static RoleFrameSlot of(final boolean optional, final boolean multiple,
			final SlotRequirements... requirementAlternatives) {
		return cache.findOrInsert(new RoleFrameSlot(optional, multiple, requirementAlternatives));
	}

	public static RoleFrameSlot of(final int minFillings, final int maxFillings,
			final SlotRequirements... requirementAlternatives) {
		return cache.findOrInsert(new RoleFrameSlot(minFillings, maxFillings, requirementAlternatives));
	}

	public static RoleFrameSlot of(final SlotRequirements... requirementAlternatives) {
		return cache.findOrInsert(new RoleFrameSlot(requirementAlternatives));
	}

	public static RoleFrameSlot of(final IFillingInSlot filling) {
		return cache.findOrInsert(new RoleFrameSlot(filling));
	}

	public static RoleFrameSlot of(final ImmutableCollection<SlotRequirements> alternativeRequirements,
			final ImmutableSet<IFillingInSlot> fillings, final int minFillings, final int maxFillings) {
		return cache.findOrInsert(new RoleFrameSlot(alternativeRequirements, fillings, minFillings, maxFillings));
	}

	/**
	 * Creates a frame slot with one filling, that only accepts one filling.
	 */
	private RoleFrameSlot(final IFillingInSlot filling,
			final SlotRequirements... requirementAlternatives) {
		this(ImmutableList.<SlotRequirements>copyOf(requirementAlternatives),
				ImmutableSet.<IFillingInSlot>of(filling), 1, 1);
	}

	/**
	 * Creates a mandatory frame slot, that only accepts one filling.
	 */
	private RoleFrameSlot(final SlotRequirements... requirementAlternatives) {
		this(ImmutableList.<SlotRequirements>copyOf(requirementAlternatives), ImmutableSet.<IFillingInSlot>of(), 1,
				1);
	}

	/**
	 * Creates a role frame slot.
	 *
	 * @param optional
	 *          whether the slot is optional
	 * @param multiple
	 *          whether the slot accepts more than one filling
	 */
	private RoleFrameSlot(final boolean optional, final boolean multiple,
			final SlotRequirements... requirementAlternatives) {
		this(optional ? 0 : 1, // min
				multiple ? -1 : 1, // max
				requirementAlternatives);
	}

	/**
	 * Creates a role frame slot.
	 */
	private RoleFrameSlot(final int minFillings, final int maxFillings,
			final SlotRequirements... requirementAlternatives) {
		this(ImmutableList.<SlotRequirements>copyOf(requirementAlternatives), ImmutableSet.<IFillingInSlot>of(),
				minFillings, maxFillings);
	}

	private RoleFrameSlot(final ImmutableCollection<SlotRequirements> alternativeRequirements,
			final ImmutableSet<IFillingInSlot> fillings, final int minFillings, final int maxFillings) {
		this.alternativeRequirements = alternativeRequirements;
		this.fillings = fillings;
		this.minFillings = minFillings;
		this.maxFillings = maxFillings;
		hashCode = calcHash();
	}

	/**
	 * @return an copy of this which is empty (does not have any fillings)
	 */
	public RoleFrameSlot emptyCopy() {
		return of(alternativeRequirements, ImmutableSet.<IFillingInSlot>of(), minFillings, maxFillings);
	}

	/**
	 * @return <code>true</code>, if the realization matches the requirements, that
	 *         is, it matches any of the requirements alternatives
	 *
	 *         public boolean matchesRequirements( final String
	 *         actualGrammarSymbolName, final List<ParseAlternatives>
	 *         actualSymbolRealizations, final IFeatureProvider actualFeatures) {
	 *         for (final SlotRequirements slotRequirementsAlternative :
	 *         this.alternativeRequirements) { if
	 *         (slotRequirementsAlternative.match( actualGrammarSymbolName,
	 *         actualSymbolRealizations, actualFeatures)) { return true; } }
	 *
	 *         return false; }
	 */

	/**
	 * Checks whether this (additional) filling would be acceptable for this slot.
	 * If the filling would be acceptable, the methode returns a copy of this slot
	 * with this filling added. Otherwise, the method returns <code>null</code>.
	 */
	RoleFrameSlot addFillingIfAccepted(final IHomogeneousConstituentAlternatives freeFilling,
			int keepPlaceFreeForHowManyFillings) {
		if (maxFillings != -1
				&& fillings.size() + 1 + keepPlaceFreeForHowManyFillings > maxFillings) {
			return null;
		}

		return addFillingIfMatchesRequirements(freeFilling);
	}

	/**
	 * Checks whether this (additional) filling matches the requirements for this
	 * slot (maxFillings is NOT checked!). If the filling mathches the requirements,
	 * the methode returns a copy of this slot with this filling added. Otherwise,
	 * the method returns <code>null</code>.
	 */
	private RoleFrameSlot addFillingIfMatchesRequirements(final IHomogeneousConstituentAlternatives freeFilling) {
		if (!matchesRequirements(freeFilling)) {
			return null;
		}

		// accept filling and return new Role Frame Slot
		return addFilling(freeFilling.toFillingInSlot());
	}

	/**
	 * @return a copy of this slot with this filling added (nothing is checked!)
	 */
	private RoleFrameSlot addFilling(final IFillingInSlot filling) {
		final ImmutableSet.Builder<IFillingInSlot> resFillingsBuilder = ImmutableSet.<IFillingInSlot>builder();
		resFillingsBuilder.addAll(fillings);
		resFillingsBuilder.add(filling);

		return of(alternativeRequirements, resFillingsBuilder.build(), minFillings, maxFillings);
	}

	/**
	 * @return wether the free filling matches the slot requirements
	 */
	private boolean matchesRequirements(final IHomogeneousConstituentAlternatives freeFilling) {
		for (final SlotRequirements slotRequirementsAlternative : alternativeRequirements) {
			if (slotRequirementsAlternative.match(freeFilling)) {
				return true;
			}
		}

		return false;
	}

	public Collection<IFillingInSlot> getFillings() {
		return Collections.unmodifiableCollection(fillings);
	}

	public boolean isEmpty() {
		return fillings.isEmpty();
	}

	public int getMinFillings() {
		return minFillings;
	}

	public int getMaxFillings() {
		return maxFillings;
	}

	/**
	 * Returns whether the slot is completed.
	 */
	@Override
	public boolean isCompleted() {
		return fillings.size() >= minFillings;
	}

	/**
	 * @return <code>true</code>, iff the slot contains any filling that is equal to
	 *         a filling of the other slot
	 */
	boolean hasOneEqualFillingAs(final RoleFrameSlot other) {
		for (final IFillingInSlot filling : fillings) {
			if (other.containsFilling(filling)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return <code>true</code>, iff the slot contains this filling - false, if it
	 *         does not. This method does an equality check.
	 */
	boolean containsFilling(final IFillingInSlot fillingToCheckFor) {
		for (final IFillingInSlot fillingContained : fillings) {
			if (fillingContained.equals(fillingToCheckFor)) {
				return true; // =>
			}
		}

		return false;
	}

	private int calcHash() {
		final int prime = 31;
		int result = 1;
		result = prime * result + alternativeRequirements.hashCode();
		result = prime * result + fillings.hashCode();
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
		final RoleFrameSlot other = (RoleFrameSlot) obj;

		if (hashCode != other.hashCode) {
			return false;
		}

		if (!fillings.equals(other.fillings)) {
			return false;
		}
		if (!alternativeRequirements.equals(other.alternativeRequirements)) {
			return false;
		}
		if (maxFillings != other.maxFillings) {
			return false;
		}
		if (minFillings != other.minFillings) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final IFeatureValue o) {
		final int classNameCompared = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final RoleFrameSlot other = (RoleFrameSlot) o;

		final int reqsCompared = CollectionUtil.compareCollections(alternativeRequirements, other.alternativeRequirements);
		if (reqsCompared != 0) {
			return reqsCompared;
		}

		final int fillingsCompared = CollectionUtil.compareCollections(fillings, other.fillings);
		if (fillingsCompared != 0) {
			return fillingsCompared;
		}

		if (minFillings < other.minFillings) {
			return -1;
		}

		if (minFillings > other.minFillings) {
			return 1;
		}

		if (maxFillings < other.maxFillings) {
			return -1;
		}

		if (maxFillings > other.maxFillings) {
			return 1;
		}

		return 0;
	}

	@Override
	public String toString() {
		return toString(false, false);
	}

	/**
	 * @param neverShowRequirements
	 *          if <code>true</code>, requirements are never shown, even not if the
	 *          slot is empty
	 * @param forceShowRequirements
	 *          if <code>true</code>, slot requirements are shown, even if the slot
	 *          is filled.
	 */
	public String toString(final boolean neverShowRequirements, final boolean forceShowRequirements) {
		if (neverShowRequirements && forceShowRequirements) {
			throw new IllegalArgumentException("Unsensible combination! Never show and force show??");
		}

		final StringBuilder res = new StringBuilder();

		// (0..1), (1..*), ...
		if (minFillings != 1 || maxFillings != 1) {
			res.append("(");
			res.append(formatFillingNumSpec(minFillings));
			res.append("..");
			res.append(formatFillingNumSpec(maxFillings));
			res.append(") ");
		}

		if (!fillings.isEmpty()) {
			res.append("[");
			boolean first = true;
			for (final IFillingInSlot filling : fillings) {
				if (first == true) {
					first = false;
				} else {
					res.append(" ,");
				}
				res.append(filling);
			}
			res.append("] ");
		} else if (minFillings == 0) {
			res.append("[]");
		} else {
			res.append(" ?");
		}

		if (!neverShowRequirements && (fillings.isEmpty() || forceShowRequirements)) {
			boolean first = true;
			for (final SlotRequirements requirements : alternativeRequirements) {
				if (first == true) {
					res.append(" (");
					first = false;
				} else {
					res.append(" | ");
				}
				res.append(requirements.toString());
			}

			if (first != true) {
				res.append(")");
			}
		}
		return res.toString();
	}

	private static String formatFillingNumSpec(final int num) {
		return num >= 0 ? Integer.toString(num) : "*";
	}

	/**
	 * @return How many fillings are missing, until this slot is completed?
	 */
	int howManyFillingsAreMissingUntilCompletion() {
		return Math.max(0, minFillings - fillings.size());
	}

	/**
	 * @return How many <i>additional</i> fillings are allowed? - <i>-1</i>, if
	 *         there is <i>no upper bound</i>.
	 */
	int howManyAdditionalFillingsAreAllowed() {
		if (maxFillings == -1) {
			return -1;
		}

		return maxFillings - fillings.size();
	}

	/*
	 * public boolean areUppermostAtomicFeaturesEqual(final RoleFrameSlot other) {
	 * if (this.alternativeRequirements.equals(other.alternativeRequirements)) { //
	 * "war", das 1. Person erwartetn != "war", das 3. Person erwartet return false;
	 * }
	 *
	 * final int numMyFillings = this.fillings.size(); if (numMyFillings !=
	 * other.fillings.size()) { return false; }
	 *
	 * // they have the same number of role frames
	 *
	 * if (numMyFillings == 0) { // they both are empty return true; }
	 *
	 *
	 * if (numMyFillings != 1) { // I test for full equality - not sure how to make
	 * it better return this.fillings.equals(other.fillings); }
	 *
	 * // they both have exactly one role frame final
	 * IHomogeneousConstituentAlternatives myFilling =
	 * this.fillings.iterator().next();
	 *
	 * final IHomogeneousConstituentAlternatives othersFilling =
	 * other.fillings.iterator().next();
	 *
	 * try { final ParseAlternativesDifference singleDiff = myFilling.getFeatures().
	 * findSinglePointOfDifferenceForRoleFrameCollFeaturesAndCheckAtomicFeatures(
	 * othersFilling.getFeatures()); if (singleDiff != null) { throw new
	 * TooManyDifferencesException("There was a difference between " +
	 * "the feature structures"); }
	 *
	 * return true; } catch (final TooManyDifferencesException e) { return false; }
	 * }
	 */

}
