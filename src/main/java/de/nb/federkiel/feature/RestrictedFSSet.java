package de.nb.federkiel.feature;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A set of feature structures. Only a certain number of feature structures is
 * allowed, and they might have to confirm to restrictions.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public final class RestrictedFSSet implements IFeatureValue, Iterable<FeatureStructure>, IFillingUsageRestrictor {
	/**
	 * All generated values shall be cached - to minimize memory use. The cache
	 * consists of weak references, so it will be cleared automatically, when a
	 * value is no longer (strongly) referenced.
	 */
	final private static WeakCache<RestrictedFSSet> cache = new WeakCache<>();

	public static final RestrictedFSSet EMPTY_WITHOUT_REQUIREMENTS = of();

	final private static int MIN_NUM_FREE_FILLINGS_FOR_RESTRICTION_CHECK = 2; // "Daumenwert"

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
	 */
	private final ImmutableSet<FeatureStructure> fillings;

	/**
	 * caching the hashCode
	 */
	private final int hashCode;

	public static RestrictedFSSet of(final boolean optional, final boolean multiple,
			final SlotRequirements... requirementAlternatives) {
		return cache.findOrInsert(new RestrictedFSSet(optional, multiple, requirementAlternatives));
	}

	public static RestrictedFSSet of(final int minFillings, final int maxFillings,
			final SlotRequirements... requirementAlternatives) {
		return cache.findOrInsert(new RestrictedFSSet(minFillings, maxFillings, requirementAlternatives));
	}

	/**
	 * Creates a mandatory frame slot, that only accepts one filling.
	 */
	public static RestrictedFSSet of(final SlotRequirements... requirementAlternatives) {
		return cache.findOrInsert(new RestrictedFSSet(requirementAlternatives));
	}

	/**
	 * Creates a frame slot with these fillings.
	 */
	public static RestrictedFSSet of(final int minFillings, final int maxFillings, final FeatureStructure... fillings) {
		return of(minFillings, maxFillings, ImmutableSet.copyOf(fillings));
	}

	/**
	 * Creates a frame slot with these fillings.
	 */
	public static RestrictedFSSet of(final int minFillings, final int maxFillings,
			final ImmutableSet<FeatureStructure> fillings) {
		return cache.findOrInsert(new RestrictedFSSet(ImmutableList.<SlotRequirements>of(), ImmutableSet.copyOf(fillings),
				minFillings, maxFillings));
	}

	public static RestrictedFSSet of(final ImmutableCollection<SlotRequirements> alternativeRequirements,
			final ImmutableSet<FeatureStructure> fillings, final int minFillings, final int maxFillings) {
		return cache.findOrInsert(new RestrictedFSSet(alternativeRequirements, fillings, minFillings, maxFillings));
	}

	/**
	 * Creates a frame slot with one filling, that only accepts one filling.
	 */
	private RestrictedFSSet(final FeatureStructure filling, final SlotRequirements... requirementAlternatives) {
		this(ImmutableList.<SlotRequirements>copyOf(requirementAlternatives), ImmutableSet.<FeatureStructure>of(filling), 1,
				1);
	}

	/**
	 * Creates a mandatory frame slot, that only accepts one filling.
	 */
	private RestrictedFSSet(final SlotRequirements... requirementAlternatives) {
		this(ImmutableList.<SlotRequirements>copyOf(requirementAlternatives), ImmutableSet.<FeatureStructure>of(), 1, 1);
	}

	/**
	 * Creates a role frame slot.
	 *
	 * @param optional whether the slot is optional
	 * @param multiple whether the slot accepts more than one filling
	 */
	private RestrictedFSSet(final boolean optional, final boolean multiple,
			final SlotRequirements... requirementAlternatives) {
		this(optional ? 0 : 1, // min
				multiple ? -1 : 1, // max
				requirementAlternatives);
	}

	/**
	 * Creates a role frame slot.
	 */
	private RestrictedFSSet(final int minFillings, final int maxFillings,
			final SlotRequirements... requirementAlternatives) {
		this(ImmutableList.<SlotRequirements>copyOf(requirementAlternatives), ImmutableSet.<FeatureStructure>of(),
				minFillings, maxFillings);
	}

	private RestrictedFSSet(final ImmutableCollection<SlotRequirements> alternativeRequirements,
			final ImmutableSet<FeatureStructure> fillings, final int minFillings, final int maxFillings) {
		this.alternativeRequirements = alternativeRequirements;
		this.fillings = fillings;
		this.minFillings = minFillings;
		this.maxFillings = maxFillings;
		hashCode = calcHash();
	}

	/**
	 * @return an copy of this which is empty (does not have any fillings)
	 */
	public RestrictedFSSet emptyCopy() {
		return of(alternativeRequirements, ImmutableSet.<FeatureStructure>of(), minFillings, maxFillings);
	}

	/**
	 * Builds a new role frame slot by merging this one with another one.
	 * <p>
	 * Merging two <i>feature structures</i> means adding slots and filling slots
	 * with free fillings.
	 */
	protected Plurival<RestrictedFSSet> mergeWithoutSemantics(final RestrictedFSSet other) {
		Collection<RestrictedFSSet> resAlternatives = new LinkedList<>();
		resAlternatives.add(of(ImmutableSet.of(), ImmutableSet.of(), 0, -1));
		// one result: empty role frame slot
		// (for a start)

		/*
		 * FIXME besteht hier noch ein Problem? Adam war Komponist und Dichter. Adam war
		 * Komponist und heiratete Eva.
		 *
		 * "Adam" wird in jedem Rollen-Rahmen der Role Frame Collection ENTWEDER als
		 * Subjekt ODER als Prädikatsnomen belegt.
		 *
		 * Vielleicht ist der Haken aber auch: Adam belegt einen Slot, der im anderen
		 * anderen Role Frame Slot schon belegt ist?? (Das geht gar nicht mehr oder?)
		 * Adam muss in allen Rollen-Rahmen des Role Frame Slots dieselbe Rolle
		 * ausfüllen (oder gar keine!)
		 */

		for (final FeatureStructure myFeatures : fillings) {
			for (final FeatureStructure othersFeatures : other.fillings) {
				// NOTE: Merging of the two role frames depends on the earlier retrieved
				// results,
				// BECAUSE: The same free filling MUST BE FILLED INTO THE SAME SLOT
				// for all role-frame-combinations!
				// (And condition must hold for each result alternative!)
				// So we iterate over all alternatives, that have already been found.

				final Collection<RestrictedFSSet> oldResAlternatives = resAlternatives;

				resAlternatives = new LinkedList<>();
				for (final RestrictedFSSet oldResAlternative : oldResAlternatives) {
					// we take the alternative we already have and
					// add the new possible merge result(s) to the alternative!
					final Plurival<FeatureStructure> possibleMerges = myFeatures.mergeWithoutSemantics(othersFeatures,
							oldResAlternative);

					for (final FeatureStructure possibleMerge : possibleMerges) {
						resAlternatives.add(oldResAlternative.addFilling(possibleMerge));
					}
				}

				if (resAlternatives.isEmpty()) {
					// No result alternatives found for merging these two role frames!!
					// That means, that merging these
					// role frame SLOTS is not possible EITHER!
					return Plurival.empty();
				}
			}
		}

		// Achtung: NICHT DURCHEINANDERBRINGEN: Als Ergebnis werden ALTERNATIVEN
		// erwartet,
		// jede ALTERNATIVE enthält dann wieder einen RoleFrameSlot mit allen
		// merge-Paaren...
		return Plurival.of(ImmutableList.copyOf(resAlternatives));
	}

	/**
	 * Builds a new role frame slot by using this as the base for filling an ellipse
	 * role frame slot.
	 * <p>
	 * Filling <i>role frame</i> ellipse means copying some filled slots from the
	 * base and filling other slots (taken from the base) with free filling from the
	 * ellipse.
	 */
	protected Plurival<RestrictedFSSet> fillEllipseWithoutSemantics(final RestrictedFSSet ellipse) {
		Collection<RestrictedFSSet> resAlternatives = new LinkedList<>();
		resAlternatives.add(of(ImmutableSet.of(), ImmutableSet.of(), 0, -1));
		// one result: empty role frame slot
		// (for a start)

		// FIXME Hier besteht bestimmt ein Problem wie bei merge()??
		// FIXME use getRestrictedRoleFrameName
		// FIXME Note, that this method will not work properly, if there are several
		// role frame slots
		// <i>with different slot names</i>, that contain the filling!

		for (final FeatureStructure basefeatureStructure : fillings) {
			for (final FeatureStructure ellipseFeatureStructure : ellipse.fillings) {
				final Collection<RestrictedFSSet> oldResAlternatives = resAlternatives;

				resAlternatives = new LinkedList<>();
				for (final RestrictedFSSet oldRoleFrameSlotResAlternative : oldResAlternatives) {

					final Plurival<FeatureStructure> possibleEllipseFillings = basefeatureStructure
							.fillEllipseWithoutSemantics(ellipseFeatureStructure, oldRoleFrameSlotResAlternative);

					// We take all alternatives we already have and
					// add the new possible ellipse-filling result(s) to each alternative!
					for (final FeatureStructure possibleEllipseFilling : possibleEllipseFillings) {
						resAlternatives.add(oldRoleFrameSlotResAlternative.addFilling(possibleEllipseFilling));
					}
				}

				if (resAlternatives.isEmpty()) {
					// No result alternatives found for ellipse-filling of these two role frames!!
					// That means, that the ellipse-filling
					// of the role frame SLOT based on this base role frame COLLECTION
					// is not possible EITHER!
					return Plurival.empty();
				}
			}
		}

		// Achtung: NICHT DURCHEINANDERBRINGEN: Als Ergebnis werden ALTERNATIVEN
		// erwartet,
		// jede ALTERNATIVE enthält dann wieder einen RoleFrameSlot mit allen
		// ellipse-Fillings...
		return Plurival.of(ImmutableList.copyOf(resAlternatives));
	}

	/**
	 * This method is used to check, whether a role frame slot confirms to some
	 * feature restrictions. For example, a feature restriction could say, that
	 * there must at most be one <i>Subjekt</i> in a role frame. So the system can
	 * see, how many <i>Nominativ</i>s there are as <i>free fillings</i> in this
	 * slots's role frames, and if there is more than one, the system could tell the
	 * parser, that there is no use working on this role frame collection any more.
	 * <p>
	 * Effectively, this method does a merge (of this role frame slot and the
	 * restriction), but stops, when the first result is found.
	 * <p>
	 * NOTE THAT this method MIGHT return <code>true</code>, even if the
	 * restrictions are not met! (It guesses, whether checking would be worthwhile
	 * or not.)
	 *
	 * @return Returns true, iff at least one of the following is true
	 *         <ul>
	 *         <li>This role frame slot contains a feature structure, that does not
	 *         have any free fillings at all. So the restrictions will not be
	 *         checked.
	 *         <li>Each feature structure in this role frame slot confirms to the
	 *         restrictions. (Even if you take into accoung, that the same free
	 *         filling MUST BE FILLED INTO THE SAME SLOT for each role frame!)
	 *         </ul>
	 */
	public boolean hasNoFreeFillingsAtAllOrHasFreeFillingsThatConfirmTo(final FeatureStructure restriction) {
		// This procedure is very much like merge().

		Collection<RestrictedFSSet> mergeAlternatives = new LinkedList<>();

		mergeAlternatives.add(of(ImmutableSet.of(), ImmutableSet.of(), 0, -1));
		// empty role frame Collection (for a start)

		int roleFrameCount = 0;
		for (final FeatureStructure myFeatureStructure : fillings) {
			if (!myFeatureStructure.hasFreeFillings()) {
				// This feature structure does not have any free fillings at all. So
				// the restrictions will not be checked. I guess, that this
				// applies to all role frames of the collection.
				return true;
			}

			if (size() > 1 ||
			// If the role frame slot contains only one role frame
					myFeatureStructure.numberOfFreeFillings() >= MIN_NUM_FREE_FILLINGS_FOR_RESTRICTION_CHECK) {
				// and less than a special number of free fillings - we do not
				// check it. I do not think,
				// that a role frame with less fillings would be invalid!

				// NOTE: Merging of the two role frames depends on the earlier
				// retrieved results,
				// BECAUSE: The same free filling MUST BE FILLED INTO THE SAME
				// SLOT
				// for all role-frame-combinations!
				// (And condition must hold for each result alternative!)
				// So we iterate over all alternatives, that have already been
				// found.

				final Collection<RestrictedFSSet> oldMergeAlternatives = mergeAlternatives;

				mergeAlternatives = new LinkedList<>();
				for (final RestrictedFSSet oldMergeAlternative : oldMergeAlternatives) {
					// we take the alternative we already have and
					// add the new possible merge result(s) to the alternative!

					// Special case: if myRoleFrame is THE LAST ONE (and note,
					// that many role frame slots have only one role frame!)...
					if (roleFrameCount == size() - 1) {
						// ..., then we can
						// stop succesfully, if we have found any merge
						// alternative (based on the
						// oldMergeAlternatives) - we DO NOT NEED ALL merge
						// alternative
						if (myFeatureStructure.canMerge(restriction, oldMergeAlternative)) {
							return true;
						}
						// else try the next oldMergeAlternative -- the
						// mergeAlternatives stay empty!
					} else {
						// it is not the last role frame of the collection - we
						// need
						// all alternatives!
						final Plurival<FeatureStructure> possibleMerges = myFeatureStructure.mergeWithoutSemantics(restriction,
								oldMergeAlternative);

						for (final FeatureStructure possibleMerge : possibleMerges) {
							mergeAlternatives.add(oldMergeAlternative.addFilling(possibleMerge));
						}
					}
				}

				if (mergeAlternatives.isEmpty()) {
					// No result alternatives found for merging these two role
					// frames!!
					// That means, that merging this role frame COLLECTION with
					// the
					// restriction role frame is not possible EITHER!
					return false;
				}
			}

			roleFrameCount++;
		}

		// ! mergeAlternatives.isEmpty() || numSlots <= 1
		return true;
	}

	/**
	 * Necessary, when this object is used as an
	 * <code>IFillingUsageRestrictor</code> - returns the only feature name which is
	 * allowed for the given homogeneous filling (because the role frame collection
	 * already contains the free filling in a slot with this name!), or
	 * <code>null</code>, if the slot name is <i>not</i> restricted for the free
	 * filling (because the role frame collection does NOT contain the free filling
	 * in any slot in any role frame)
	 * <p>
	 * Note, that this method will not work properly, if there are several role
	 * frame slots <i>with different slot names</i>, that contain the filling!
	 *
	 * @see IFillingUsageRestrictor
	 */
	@Override
	public String getRestrictedNameFor(final IHomogeneousConstituentAlternatives homogeneousFilling) {
		final FeatureStructure lookedFor = homogeneousFilling.getFeatures();

		for (final FeatureStructure featureStructure : fillings) {
			final String featureName = featureStructure.findFeatureNameContaining(lookedFor);
			if (featureName != null) {
				return featureName; // ==>
			}
		}

		// homogeneousFilling not found in any slot of my role frames
		// -> no restriction on the slot name for the free filling
		return null;
	}

	/**
	 * Builds the union of the two RoleFrameSlots - or returns <code>null</code>,
	 * iff
	 * <ul>
	 * <li>the two RoleFrameSlots do contain role frames which have <i>the same
	 * filling</i> filled into <i>slots with different names</i>
	 * <li>other's feature structures do no match my requirements
	 * <li>the overall number exceeds the maximum number of fillings
	 */
	protected RestrictedFSSet addFillingsIfAccepted(final RestrictedFSSet other) {
		for (final FeatureStructure oneFeatureStructure : fillings) {
			for (final FeatureStructure otherFeatureStructure : other.fillings) {
				if (oneFeatureStructure.containsTheSameRoleFrameSlotFillingInADifferentFeature(otherFeatureStructure)) {
					return null;
				}
			}
		}

		for (FeatureStructure otherFilling : other.fillings) {
			if (!matchesRequirements(otherFilling)) {
				return null;
			}
		}

		ImmutableSet<FeatureStructure> resFeatureStructures = ImmutableSet.<FeatureStructure>builder().addAll(fillings)
				.addAll(other.fillings).build();

		if (maxFillings != -1 && resFeatureStructures.size() > maxFillings) {
			return null;
		}

		return of(alternativeRequirements, resFeatureStructures, minFillings, maxFillings);
	}

	/**
	 * Checks whether this (additional) filling would be acceptable for this slot.
	 * If the filling would be acceptable, the methode returns a copy of this slot
	 * with this filling added. Otherwise, the method returns <code>null</code>.
	 */
	public RestrictedFSSet addFillingIfAccepted(final IHomogeneousConstituentAlternatives freeFilling,
			int keepPlaceFreeForHowManyFillings) {
		if (maxFillings != -1 && fillings.size() + 1 + keepPlaceFreeForHowManyFillings > maxFillings) {
			return null;
		}

		return addFillingIfMatchesRequirements(freeFilling.getFeatures());
	}

	/**
	 * Checks whether this (additional) feature structure would be acceptable for
	 * this slot. If the feature structure would be acceptable, the methode returns
	 * a copy of this slot with this feature structure added. Otherwise, the method
	 * returns <code>null</code>.
	 */
	public RestrictedFSSet addFillingIfAccepted(final FeatureStructure featureStructure) {
		if (maxFillings != -1 && fillings.size() + 1 > maxFillings) {
			return null;
		}

		return addFillingIfMatchesRequirements(featureStructure);
	}

	/**
	 * Checks whether this (additional) feature structure matches the requirements
	 * for this slot (maxFillings is NOT checked!). If the feature structure
	 * mathches the requirements, the methode returns a copy of this slot with this
	 * filling added. Otherwise, the method returns <code>null</code>.
	 */
	private RestrictedFSSet addFillingIfMatchesRequirements(final FeatureStructure featureStructure) {
		if (!matchesRequirements(featureStructure)) {
			return null;
		}

		// accept filling and return new Role Frame Slot
		return addFilling(featureStructure);
	}

	/**
	 * @return a copy of this slot with this filling added (nothing is checked!)
	 */
	private RestrictedFSSet addFilling(final FeatureStructure filling) {
		final ImmutableSet.Builder<FeatureStructure> resFillingsBuilder = ImmutableSet.<FeatureStructure>builder();
		resFillingsBuilder.addAll(fillings);
		resFillingsBuilder.add(filling);

		return of(alternativeRequirements, resFillingsBuilder.build(), minFillings, maxFillings);
	}

	/**
	 * @return wether the feature structure matches the slot requirements
	 */
	private boolean matchesRequirements(final FeatureStructure featureStructure) {
		for (final SlotRequirements slotRequirementsAlternative : alternativeRequirements) {
			if (slotRequirementsAlternative.match(featureStructure)) {
				return true;
			}
		}

		return false;
	}

	public Collection<FeatureStructure> getFillings() {
		return Collections.unmodifiableCollection(fillings);
	}

	public Plurival<FeatureStructure> toFillingInSlot() {
		final ImmutableSet.Builder<FeatureStructure> res = ImmutableSet.builder();

		for (final FeatureStructure featuresStructure : fillings) {
			@Nullable
			final FeatureStructure fillingInSlot = featuresStructure.toFillingInSlot();
			if (fillingInSlot != null) {
				res.add(fillingInSlot);
			}
		}

		return Plurival.of(res.build());
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

	public boolean hasEnoughFillings() {
		return fillings.size() >= minFillings;
	}

	/**
	 * @return <code>true</code>, iff all slots are satisfied and there are no free
	 *         fillings.
	 */
	public boolean noFreeFillingsAndAllSlotsHaveEnoughFillings() {
		for (final FeatureStructure roleFrame : fillings) {
			if (!roleFrame.noFreeFillingsAndAllSlotsHaveEnoughFillings()) {
				return false;
				// FIXME Könnte es sein, dass nur EINIGE meiner featureStructures completed
				// sind?
				// Macht es Sinn, dann die anderen zu löschen oder so??
			}
		}

		return true;
	}

	public boolean containsAFillingAlsoContainedIn(final RestrictedFSSet other) {
		for (final FeatureStructure myFilling : fillings) {
			for (FeatureStructure othersFilling : other.fillings) {
				if (myFilling.equals(othersFilling)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean containsFilling(FeatureStructure filling) {
		for (FeatureStructure candidate : fillings) {
			if (filling.equals(candidate)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return an Iterator over my feature structures.
	 */
	@Override
	public Iterator<FeatureStructure> iterator() {
		return fillings.iterator();
	}

	public int size() {
		return fillings.size();
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
		final RestrictedFSSet other = (RestrictedFSSet) obj;

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

		final RestrictedFSSet other = (RestrictedFSSet) o;

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

	@Override
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
			for (final FeatureStructure filling : fillings) {
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

	public int howManyFillingsAreMissingUntilCompletion() {
		return Math.max(0, minFillings - fillings.size());
	}

	/**
	 * @return How many <i>additional</i> fillings are allowed? - <i>-1</i>, if
	 *         there is <i>no upper bound</i>.
	 */
	public int howManyAdditionalFillingsAreAllowed() {
		if (maxFillings == -1) {
			return -1;
		}

		return maxFillings - fillings.size();

		// oder 0 ??
	}

	@Override
	public int keepPlaceFreeForHowManyFillings(final String name) {
		return howManyFillingsAreMissingUntilCompletion(name);
	}

	@Override
	public int howManyAdditionalFillingsAreAllowed(final String name) {
		int res = -1;

		for (final FeatureStructure featureStructure : fillings) {
			final int howManyAdditionalFillingsAreAllowedForThisFeatureStructureAndSlotName = featureStructure
					.howManyAdditionalFillingsAreAllowed(name);

			if (howManyAdditionalFillingsAreAllowedForThisFeatureStructureAndSlotName != -1) {
				if (res == -1) {
					res = howManyAdditionalFillingsAreAllowedForThisFeatureStructureAndSlotName;
				} else {
					res = Math.min(res, howManyAdditionalFillingsAreAllowedForThisFeatureStructureAndSlotName);
				}
			}
		}

		return res;

	}

	/**
	 * @return How many fillings for features with this name are missing, until all
	 *         slots with this name are completed?
	 */
	private int howManyFillingsAreMissingUntilCompletion(final String name) {
		int res = 0;

		for (final FeatureStructure roleFrame : fillings) {
			res = Math.max(res, roleFrame.howManyFillingsAreMissingUntilCompletion(name));
		}

		return res;
	}

	@Override
	public SurfacePart getSurfacePart() {
		SurfacePart res = null;

		for (FeatureStructure filling : fillings) {
			res = SurfacePart.join(res, filling.getSurfacePart());
		}

		return res;
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
