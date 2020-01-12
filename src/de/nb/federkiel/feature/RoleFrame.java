package de.nb.federkiel.feature;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.ISemantics;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A frame (expectations) set up by some part of the input (a verb, e.g.) that
 * needs to be filled by other parts of the input (subject, objects etc.).
 * <p>
 * The frame has several <i>slots</i> (one for the subject, one for the direct
 * object, e.g.).
 * <p>
 * In Detail, there are two kinds of elements in a role frame:
 * <ul>
 * <li>slots, with a name and some Conditions for to be filled - some of them
 * may be empty, others may be filled
 * <li>Free Fillings, that could be used to fill (not-yet existing) empty Slots.
 * Each filling is a set of realization alternatives.
 * </ul>
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class RoleFrame
		// extends AbstractTermBounds
		implements Comparable<RoleFrame>
// , ITermBounds
{
	/**
	 * All generated values shall be cached - to minimize memory use. The cache
	 * consists of weak references, so it will be cleared automatically, when a
	 * value is no longer (strongly) referenced.
	 */
	final private static WeakCache<RoleFrame> cache = new WeakCache<>();

	public static final RoleFrame EMPTY = of(FeatureStructure.empty(null));

	/**
	 * The slots with name and value.
	 */
	private final ImmutableMap<String, RoleFrameSlot> slots;

	/**
	 * Free filling values. There can only be free fillings, if there are NO slots
	 * at all!
	 */
	private final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings;

	/**
	 * caching the hashCode
	 */
	private final int hashCode;

	public static RoleFrame of(final FeatureStructure slots) {
		return cache.findOrInsert(new RoleFrame(slots));
	}

	public static RoleFrame of(final ImmutableMap<String, RoleFrameSlot> slots) {
		return cache.findOrInsert(new RoleFrame(slots));
	}

	public static RoleFrame of(final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		return cache.findOrInsert(new RoleFrame(ImmutableMap.<String, RoleFrameSlot>of(), freeFillings));
	}

	public static RoleFrame of(final ImmutableMap<String, RoleFrameSlot> slots,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		return cache.findOrInsert(new RoleFrame(slots, freeFillings));
	}

	private RoleFrame(final FeatureStructure slots) {
		final ImmutableMap.Builder<String, RoleFrameSlot> slotMapBuilder = ImmutableMap.<String, RoleFrameSlot>builder();

		slots.forEach((n, v) -> slotMapBuilder.put(n, (RoleFrameSlot) v));

		this.slots = slotMapBuilder.build();
		freeFillings = ImmutableSet.of();
		hashCode = calcHashCode();
	}

	private RoleFrame(final ImmutableMap<String, RoleFrameSlot> slots) {
		this(slots, ImmutableSet.<IHomogeneousConstituentAlternatives>of());
	}

	private RoleFrame(final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		this(ImmutableMap.<String, RoleFrameSlot>of(), freeFillings);
	}

	private RoleFrame(final ImmutableMap<String, RoleFrameSlot> slots,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		check(slots, freeFillings);

		this.slots = slots;
		this.freeFillings = freeFillings;
		hashCode = calcHashCode();
	}

	/**
	 * Merges the two role frames, which means:
	 * <ul>
	 * <li>free fillings are added
	 * <li>if there are any slots at all, the slots are added, and <i>ALL</i> free
	 * fillings are used to fill the slots. (<i>ALL</i> symbols are
	 * &quot;consumed&quot; in this case! - There can only be free fillings, if
	 * there are NO slots at all!)
	 * </ul>
	 * <p>
	 * The free fillings of this role frame collection and of the other role frame
	 * collection may contain the same free filling (resulting from the same parts
	 * of the input, based on a filled ellipse, for example).
	 * <p>
	 * In some cases, merging is not possible (not all free fillings can be consumed
	 * by the slots, e.g.), in other cases there are several possibilities for a
	 * merge (filling A fills slot X, filling B fills Slot Y; or the other way
	 * round). So, the merge result is a Plurival, containing all possible
	 * (alternative) merges.
	 * <p>
	 * If the merge is not possible (due to a all-has-to-be-consumed-condition,
	 * e.g.), the result will be empty.
	 * <p>
	 * The two role frames MUST NOT share the same role slot name!
	 *
	 * @param fillingUsageRestrictor
	 *          a free filling might be restricted, so that it can only fill a slot
	 *          with a specified name - this is the restrictor. (This is necessary
	 *          to ensure that in a ellipse-like sentence like <i>Paul war Komponist
	 *          und ab 1924 Dirigent.<i>, Paul is the <i>Subjekt<i> in both parts -
	 *          not the <i>Subjekt</i> in one part and the <i>Praedikatsnomen</i> in
	 *          the other!)
	 */
	protected Plurival<RoleFrame> merge(final RoleFrame other, final IFillingUsageRestrictor fillingUsageRestrictor)
			throws IllegalArgumentException {
		if (slots.isEmpty() && other.slots.isEmpty()) {
			// Case 1: this: no slots (maybe free fillings),
			// other: no slots (maybe more fillings)

			// No slots at all --> all free fillings stay unconsumed!

			return Plurival.of(RoleFrame.of(mergeFreeFillings(freeFillings, other.freeFillings))); // ==>
		}

		if (slots.isEmpty() && !other.slots.isEmpty()) {
			// Case 2: this: no slots (maybe free fillings),
			// other: some slots (no free fillings)!

			return buildRoleFramesFromeSlotAlternatives(
					fillSlotsConsumingAllFillings(other.slots, freeFillings, fillingUsageRestrictor));
			// the results have slots (all my free fillings are used to fill them),
			// but the results have no free fillings
		}

		if (!slots.isEmpty() && other.slots.isEmpty()) {
			// Case 3: this: some slots (no free fillings),
			// other: no slots (maybe free fillings)!

			return buildRoleFramesFromeSlotAlternatives(
					fillSlotsConsumingAllFillings(slots, other.freeFillings, fillingUsageRestrictor));
			// the results have slots (all of the others free fillings are used to fill
			// them),
			// but the results have no free fillings
		}

		// ELSE:
		// Case 4: this: some slots (no free fillings)!
		// other: some slots (no free fillings)

		final ImmutableMap<String, RoleFrameSlot> slotUnion = slotUnion(slots, other.slots);
		if (slotUnion == null) {
			// (there might habe been the same filling in both role frames, or the same slot
			// name...
			return Plurival.empty();
		}

		return buildRoleFramesFromeSlotAlternatives(fillSlotsConsumingAllFillings(slotUnion,
				mergeFreeFillings(freeFillings, other.freeFillings), fillingUsageRestrictor));
	}

	/**
	 * Tries to merge this and the other role frame, according to the
	 * fillingUsageRestrictor.
	 *
	 * @return <code>true</code>, iff a merge is possible (at least one alternative
	 *         result)
	 */
	protected boolean canMerge(final RoleFrame other, final RoleFrameCollection fillingUsageRestrictor) {
		if (slots.isEmpty() && other.slots.isEmpty()) {
			// Case 1: this: no slots (maybe free fillings),
			// other: no slots (maybe more fillings)

			// No slots at all --> all free fillings stay unconsumed!

			return true; // ==>
		}

		if (slots.isEmpty() && !other.slots.isEmpty()) {
			// Case 2: this: no slots (maybe free fillings),
			// other: some slots (no free fillings)!

			return !fillSlotsConsumingAllFillings(other.slots, freeFillings, fillingUsageRestrictor).isEmpty();
			// the results have slots (all my free fillings are used to fill
			// them),
			// but the results have no free fillings
		}

		if (!slots.isEmpty() && other.slots.isEmpty()) {
			// Case 3: this: some slots (no free fillings),
			// other: no slots (maybe free fillings)!

			return !fillSlotsConsumingAllFillings(slots, other.freeFillings, fillingUsageRestrictor).isEmpty();
			// the results have slots (all of the others free fillings are used
			// to fill them),
			// but the results have no free fillings
		}

		// ELSE:
		// Case 4: this: some slots (no free fillings)!
		// other: some slots (no free fillings)

		final ImmutableMap<String, RoleFrameSlot> slotUnion = slotUnion(slots, other.slots);
		if (slotUnion == null) {
			// (there might habe been the same filling in both role frames, or
			// the same slot name...
			return false;
		}

		return !fillSlotsConsumingAllFillings(slotUnion, mergeFreeFillings(freeFillings, other.freeFillings),
				fillingUsageRestrictor).isEmpty();
	}

	/**
	 * Uses this role frame as the base for filling an ellipse role frame.
	 * <ul>
	 * <li>the ellipse MUST NOT contain any slots (only free filings)
	 * <li>if there are any slots in this (the base) at all, then <i>ALL</i> free
	 * fillings IN THE ELLIPSE are used to fill the slots)
	 * </ul>
	 * <p>
	 * The free fillings of this role frame collection (the base) and of the ellipse
	 * role frame collection may contain the same elements (resulting from earlier
	 * ellipse-filling, for example)!
	 * <p>
	 * In some cases, filling the ellipse is not possible (not all free fillings in
	 * the ellipse can be consumed by the slots, e.g.), in other cases there are
	 * several possibilities (filling A fills slot X, filling B fills Slot Y; or the
	 * other way round). So, the result is a Plurival, containing all possible
	 * (alternative) ellipse fillings.
	 *
	 * @param fillingUsageRestrictor
	 *          a free filling (e.g. from <code>this</code>) might be restricted, so
	 *          that it can only fill a slot (e.g. from the <code>ellipse</code>)
	 *          with a specified name - this is the restrictor. (This is necessary
	 *          to ensure that in a ellipse-like sentence like <i>Paul war Komponist
	 *          und ab 1924 Dirigent.<i>, Paul is the <i>Subjekt<i> in both parts -
	 *          not the <i>Subjekt</i> in one part and the <i>Praedikatsnomen</i> in
	 *          the other!)
	 */
	protected Plurival<RoleFrame> fillEllipse(final RoleFrame ellipse,
			final IFillingUsageRestrictor fillingUsageRestrictor) throws IllegalArgumentException {
		if (slots.isEmpty() && ellipse.slots.isEmpty()) {
			// Case 1: this: no slots (maybe free fillings),
			// ellipse: no slots (maybe more fillings)

			// No slots at all --> all free fillings stay unconsumed!
			return buildRoleFramesFromFreeFillingAlternatives(buildAllSubSets(freeFillings, ellipse.freeFillings)); // ==>
		}

		if (slots.isEmpty() && !ellipse.slots.isEmpty()) {
			// Case 2: this: no slots (maybe free fillings),
			// ellipse: some slots (no free fillings)!

			return buildRoleFramesFromeSlotAlternatives(
					fillSlotsUsingFillingsOrNotUsingThem(ellipse.slots, freeFillings, fillingUsageRestrictor)); // ==>
			// the results have slots (some of my free fillings may be used to fill them),
			// but the result has no free fillings
		}

		if (!slots.isEmpty() && ellipse.slots.isEmpty()) {
			// Case 3: this: some slots (no free fillings)!
			// ellipse: no slots (maybe free fillings)

			throw new RuntimeException("Strange use of fill-ellipse: Ellipse has no slots and" + // NOPMD by nbudzyn on
																																														// 29.06.10 19:46
					" base has slots." + "\nBase: " + toString() + "\nEllipse: " + toString()); // ==>

			/*
			 * FIXME (or would this be ok?) Actually this would work like a merge, see
			 * merge()
			 */
		}

		// ELSE:
		// Case 4: this: some slots (no free fillings)!
		// ellipse: some slots (no free fillings)

		throw new RuntimeException("Strange use of fill-ellipse: Ellipse has slots, but" + // NOPMD by nbudzyn on 29.06.10
																																												// 19:46
				" base also has slots, so there cannot be any free fillings in the base." + "\nBase: " + toString()
				+ "\nEllipse: " + toString()); // ==>

		/*
		 * FIXME (or would this be ok?) Actually this would work like a merge, see
		 * merge()!
		 */
	}

	/*
	 * Fills the slots in each of the slotAlternatives with the free fillings. Also
	 * generates alternatives - in each alternative, <i>all</i> free fillings must
	 * be consumed.
	 *
	 * THIS METHOD IS BUGGY!
	 *
	 * private static Collection<ImmutableMap<String, RoleFrameSlot>>
	 * fillSlotsConsumingAllFillings( final Collection<ImmutableMap<String,
	 * RoleFrameSlot>> slotsAlternatives, final
	 * Collection<ImmutableSet<ParseAlternatives>> freeFillingsAlternatives) {
	 *
	 * Collection<ImmutableMap<String, RoleFrameSlot>> res = new
	 * LinkedList<ImmutableMap<String, RoleFrameSlot>>(slotsAlternatives);
	 *
	 * for (final ImmutableSet<ParseAlternatives> freeFillings :
	 * freeFillingsAlternatives) { for (final ParseAlternatives freeFilling :
	 * freeFillings) { final Collection<ImmutableMap<String, RoleFrameSlot>> oldRes
	 * = res;
	 *
	 * res = fillSlotsConsumingFilling(oldRes, freeFilling); } }
	 *
	 * return res; }
	 */

	/**
	 * Fills the free fillings into the slots. Also generates alternatives - in each
	 * alternative, <i>all</i> free fillings must be consumed.
	 *
	 * @param fillingUsageRestrictor
	 *          a free filling might be restricted, so that it can only fill a slot
	 *          with a specified name - this is the restrictor. (This is necessary
	 *          to ensure that in a ellipse-like sentence like <i>Paul war Komponist
	 *          und ab 1924 Dirigent.<i>, Paul is the <i>Subjekt<i> in both parts -
	 *          not the <i>Subjekt</i> in one part and the <i>Praedikatsnomen</i> in
	 *          the other!)
	 */
	private static Collection<ImmutableMap<String, RoleFrameSlot>> fillSlotsConsumingAllFillings(
			final ImmutableMap<String, RoleFrameSlot> slots,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings,
			final IFillingUsageRestrictor fillingUsageRestrictor) {

		// There could be several different slot fillings. We start with exactly one
		// slot-map,
		// and then consume the free fillings, one after the other.
		// (ALL fillings have to be consumed!)

		ImmutableCollection<ImmutableMap<String, RoleFrameSlot>> slotAlternatives = ImmutableList
				.<ImmutableMap<String, RoleFrameSlot>>of(slots);

		for (final IHomogeneousConstituentAlternatives freeFilling : freeFillings) {
			slotAlternatives = ImmutableList
					.copyOf(fillSlotsConsumingFilling(slotAlternatives, freeFilling, fillingUsageRestrictor));

			if (slotAlternatives.isEmpty()) {
				// the filling could not be consumed - no result at all
				return ImmutableList.of(); // ==>
			}
		}

		return filterSlotAlternativesWhereAllFillingsMissingForCompletionCanBeAddedLater(slotAlternatives,
				fillingUsageRestrictor);
	}

	/**
	 * @return only those slot alternatives, for which all fillings, that are still
	 *         missing for completion, can be added in some later parsing step
	 */
	private static Collection<ImmutableMap<String, RoleFrameSlot>> filterSlotAlternativesWhereAllFillingsMissingForCompletionCanBeAddedLater(
			final ImmutableCollection<ImmutableMap<String, RoleFrameSlot>> slotAlternatives,
			final IFillingUsageRestrictor fillingUsageRestrictor) {
		// @formatter:off
	  return slotAlternatives.stream()
	      .filter(slotAlternative -> allFillingsMissingForCompletionCanBeAddedLater(slotAlternative,
                    fillingUsageRestrictor))
	      .collect(toImmutableList());
      // @formatter:on
	}

	/**
	 * @return whether for these slots all fillings, that are still missing for
	 *         completion, can be added in some later parsing step
	 */
	private static boolean allFillingsMissingForCompletionCanBeAddedLater(final ImmutableMap<String, RoleFrameSlot> slots,
			final IFillingUsageRestrictor fillingUsageRestrictor) {
		for (final Entry<String, RoleFrameSlot> entry : slots.entrySet()) {
			if (!allFillingsMissingForCompletionCanBeAddedLater(entry.getKey(), entry.getValue(), fillingUsageRestrictor)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return whether all fillings, that are still missing for completion, can be
	 *         added in some later parsing step
	 */
	private static boolean allFillingsMissingForCompletionCanBeAddedLater(String slotName, RoleFrameSlot slot,
			final IFillingUsageRestrictor fillingUsageRestrictor) {
		final int howManyAdditionalFillingsAllowed = fillingUsageRestrictor.howManyAdditionalFillingsAreAllowed(slotName);

		if (howManyAdditionalFillingsAllowed == -1) {
			return true;
		}

		return slot.howManyFillingsAreMissingUntilCompletion() <= howManyAdditionalFillingsAllowed;
	}

	/**
	 * Tries to fill the free fillings into the slots. If a filling does not fit
	 * into any slot, it is simply left out! Also generates alternatives - in each
	 * alternative, <i>some</i> free fillings may be consumed - others may not.
	 * <p>
	 * The result always contains at least one element: The slots from the input,
	 * left unchanged without any fillings filled in.
	 *
	 * @param fillingUsageRestrictor
	 *          a free filling might be restricted, so that it can only fill a slot
	 *          with a specified name - this is the restrictor. (This is necessary
	 *          to ensure that in a ellipse-like sentence like <i>Paul war Komponist
	 *          und ab 1924 Dirigent.<i>, Paul is the <i>Subjekt<i> in both parts -
	 *          not the <i>Subjekt</i> in one part and the <i>Praedikatsnomen</i> in
	 *          the other!)
	 */
	private static Collection<ImmutableMap<String, RoleFrameSlot>> fillSlotsUsingFillingsOrNotUsingThem(
			final ImmutableMap<String, RoleFrameSlot> slots,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings,
			final IFillingUsageRestrictor fillingUsageRestrictor) {

		// starting with all slots not filled
		final Collection<ImmutableMap<String, RoleFrameSlot>> res = new LinkedList<>();
		res.add(slots);

		for (final IHomogeneousConstituentAlternatives freeFilling : freeFillings) {
			// Tries to fill in this free filling into the already-generated
			// alternatives -- and then adds the results to the alternatives.
			res.addAll(fillSlotsConsumingFilling(res, freeFilling, fillingUsageRestrictor));
		}

		return res;
	}

	/**
	 * Takes the free filling and fills it (exactly once) into each of the slot
	 * alternatives. If in the slot alternative, there is no matching slot for the
	 * filling, the alternative is skipped (and not included in the result). If
	 * there is more than one possibility for filling the free filling into a slot
	 * alternative, all possibilities are generated.
	 * <p>
	 * The result is a collection of slot alternatives, each of which contains the
	 * (formerly) free filling exactly once. If the free filling does not match any
	 * of the slots, the result will be empty.
	 *
	 * @param fillingUsageRestrictor
	 *          the filling might be restricted, so that the
	 *          <code>freeFilling</code> can only fill a slot with a specified name
	 *          - this is the restrictor. (This is necessary to ensure that in a
	 *          ellipse-like sentence like <i>Paul war Komponist und ab 1924
	 *          Dirigent.<i>, Paul is the <i>Subjekt<i> in both parts - not the
	 *          <i>Subjekt</i> in one part and the <i>Praedikatsnomen</i> in the
	 *          other!)
	 */
	private static ImmutableCollection<ImmutableMap<String, RoleFrameSlot>> fillSlotsConsumingFilling(
			final Collection<ImmutableMap<String, RoleFrameSlot>> slotAlternatives,
			final IHomogeneousConstituentAlternatives homogenousFilling,
			final IFillingUsageRestrictor fillingUsageRestrictor) {
		final String onlyAllowedSlotName = fillingUsageRestrictor.getRestrictedNameFor(homogenousFilling);

		final ImmutableList.Builder<ImmutableMap<String, RoleFrameSlot>> res = ImmutableList
				.<ImmutableMap<String, RoleFrameSlot>>builder();

		// Iterate over all slot alternatives we already have
		for (final Map<String, RoleFrameSlot> oldSlots : slotAlternatives) {
			// FIXME Note, that the method
			// fillingUsageRestrictor.getRestrictedRoleFrameName() will not work
			// properly, if there are several role frame slots
			// <i>with different slot names</i>, that contain the filling!
			// Is this a problem? How to prevent this?
			if (onlyAllowedSlotName != null) {
				final RoleFrameSlot onlyAllowedSlot = oldSlots.get(onlyAllowedSlotName);
				if (onlyAllowedSlot != null) {
					final RoleFrameSlot filledSlot = addFillingIfAccepted(onlyAllowedSlotName, onlyAllowedSlot, homogenousFilling,
							fillingUsageRestrictor);
					if (filledSlot != null) {
						// Filling was accepted -> so we have a new filling alternative
						res.add(replaceOneSlot(oldSlots, onlyAllowedSlotName, filledSlot));
					}
					// else: filling - based on these oldSlots - is impossible -> try the next slot
					// alternative
				}
				// else: filling - based on these oldSlots - is impossible -> try the next slot
				// alternative
			} else {
				// No Filling restriction -> iterate over all slots in this alternative
				for (final Entry<String, RoleFrameSlot> entry : oldSlots.entrySet()) {
					final RoleFrameSlot filledSlot = addFillingIfAccepted(entry.getKey(), entry.getValue(),
							homogenousFilling, fillingUsageRestrictor);
					if (filledSlot != null) {
						// Filling was accepted -> so we have a new filling alternative
						res.add(replaceOneSlot(oldSlots, entry.getKey(), filledSlot));
					}
				}
			}
		}
		return res.build();
	}

	/**
	 * Checks whether this (additional) filling would be acceptable for this slot.
	 * If the filling would be acceptable, the methode returns a copy of this slot
	 * with this filling added. Otherwise, the method returns <code>null</code>.
	 */
	private static RoleFrameSlot addFillingIfAccepted(String slotName, RoleFrameSlot slot,
			final IHomogeneousConstituentAlternatives freeFilling,
			final IFillingUsageRestrictor fillingUsageRestrictor) {
		return slot.addFillingIfAccepted(freeFilling,
				fillingUsageRestrictor.keepPlaceFreeForHowManyFillings(slotName));
	}

	/**
	 * Build RoleFrame alternatives from the slot filling alternatives
	 */
	private static Plurival<RoleFrame> buildRoleFramesFromeSlotAlternatives(
			final Collection<ImmutableMap<String, RoleFrameSlot>> slotsAlternatives) {
		// @formatter:off
	  return Plurival.of(
	      slotsAlternatives.stream()
	        .map(newSlots -> RoleFrame.of(newSlots))
	        .collect(toImmutableList())
	      );
      // @formatter:on
	}

	/**
	 * Build RoleFrame alterantives from the free fillings alternatives
	 */
	private static Plurival<RoleFrame> buildRoleFramesFromFreeFillingAlternatives(
			final Collection<ImmutableSet<IHomogeneousConstituentAlternatives>> freeFillingAlternatives) {
		//  @formatter:off
	  return Plurival.of(
	      freeFillingAlternatives.stream()
	        .map(RoleFrame::of) // all free fillings consumed
	        .collect(toImmutableList())
	      );
      //  @formatter:on
	}

	/**
	 * Returns an immutable copy of the map with one slot replaced.
	 */
	private static ImmutableMap<String, RoleFrameSlot> replaceOneSlot(final Map<String, RoleFrameSlot> oldSlots,
			String nameToReplace,
			final RoleFrameSlot newSlot) {
		final Builder<String, RoleFrameSlot> res = ImmutableMap.<String, RoleFrameSlot>builder();

		for (final Entry<String, RoleFrameSlot> oldEntry : oldSlots.entrySet()) {
			final String oldSlotName = oldEntry.getKey();

			if (oldSlotName.equals(nameToReplace)) {
				res.put(oldSlotName, newSlot);
			} else {
				res.put(oldSlotName, oldEntry.getValue());
			}
		}

		return res.build();
	}

	/**
	 * @return <code>true</code>, iff all slots are completed and there are no free
	 *         fillings.
	 */
	public boolean isCompleted() {
		if (!freeFillings.isEmpty()) {
			return false;
		}

		for (final RoleFrameSlot slot : slots.values()) {
			if (!slot.isCompleted()) {
				return false;
			}
		}

		return true;
	}

	public boolean hasOneEqualFillingInSlotAs(IFeatureValue other) {
		for (final RoleFrameSlot slot : slots.values()) {
			if (slot.containsAFillingInASlotEqualTo(other)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param name
	 *          the name of an existing slot
	 * @return The slot with this name
	 */
	public RoleFrameSlot getSlot(final String name) { // NO_UCD
		if (!slots.containsKey(name)) {
			throw new IllegalArgumentException("No slot with this name: " + name);
		}

		return slots.get(name);
	}

	protected boolean containsTheSameFillingInADifferentSlot(final RoleFrame other) {
		for (final Entry<String, RoleFrameSlot> someEntry : slots.entrySet()) {
			for (final Entry<String, RoleFrameSlot> otherEntry : other.slots.entrySet()) {
				if (someEntry.getKey().equals(otherEntry.getKey())) { // NOPMD by nbudzyn on 29.06.10 21:37
					// all fine!
				} else {
					// slot names are different!
					if (someEntry.getValue().containsAFillingInASlotEqualTo(otherEntry.getValue())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * @return the name of a slot that contains the <code>filling</code> - or
	 *         <code>null</code>, if there is no such slot. (The method does an
	 *         equality check.)
	 */
	protected String findSlotNameContaining(final FillingInSlot filling) {
		for (final Entry<String, RoleFrameSlot> entry : slots.entrySet()) {
			if (entry.getValue().containsAFillingInASlotEqualTo(filling)) {
				return entry.getKey();
			}
		}

		// no slot contains this filling
		return null;
	}

	public boolean hasSlot(final String name) { // NO_UCD
		return slots.containsKey(name);
	}

	public Iterator<Map.Entry<String, RoleFrameSlot>> slotIterator() {
		return slots.entrySet().iterator();
	}

	public int numSlots() { // NO_UCD
		return slots.size();
	}

	public Iterator<IHomogeneousConstituentAlternatives> freeFillingIterator() {
		return freeFillings.iterator();
	}

	protected boolean hasFreeFillings() {
		return !freeFillings.isEmpty();
	}

	protected int numFreeFillings() {
		return freeFillings.size();
	}

	/*
	 * @return a union of the slots of the two role frames - or <code>null</code>,
	 * iff their slots both contain <i>the same filling</i> or <i>the same slot
	 * name</i>
	 *
	 * public static ImmutableMap<String, RoleFrameSlot> slotUnion( final RoleFrame
	 * oneRoleFrame, final RoleFrame otherRoleFrame) { return
	 * slotUnion(oneRoleFrame.slots, otherRoleFrame.slots); }
	 */

	/*
	 * Merges the free fillings of the two role frames by simply building a union.
	 * The free fillings MUST BE DISJOINT!
	 *
	 * public static ImmutableSet<IHomogeneousConstituentAlternatives>
	 * mergeFreeFillings( final RoleFrame oneRoleFrame, final RoleFrame
	 * otherRoleFrame) { return mergeFreeFillings(oneRoleFrame.freeFillings,
	 * otherRoleFrame.freeFillings); }
	 */

	/**
	 * @return a union of the slot maps - or <code>null</code>, iff they both
	 *         contain <i>the same filling</i> or <i>the same slot name</i>
	 */
	private static ImmutableMap<String, RoleFrameSlot> slotUnion(final Map<String, RoleFrameSlot> someSlots,
			final Map<String, RoleFrameSlot> moreSlots) {
		for (final RoleFrameSlot someSlot : someSlots.values()) {
			for (final RoleFrameSlot otherSlot : moreSlots.values()) {
				if (someSlot.containsAFillingInASlotEqualTo(otherSlot)) {
					// someSlots and moreSlots contain the same
					// FILLING, you cannot build a union!
					// The problem is: It would be a verbotene Doppelbelegung, wenn
					// dasselbe Filling in BEIDEN ROLEFRAMES in einem Slot verschiedenen Namens
					// vorkommt!!
					return null; // ==>
				}
			}
		}

		final ImmutableMap.Builder<String, RoleFrameSlot> res = ImmutableMap.<String, RoleFrameSlot>builder();
		res.putAll(someSlots);
		res.putAll(moreSlots); // duplicates will cause build() to fail

		try {
			return res.build(); // fails, if duplicate keys were added
		} catch (final IllegalArgumentException e) {
			// Cannot merge role frames: Both share the same slot name.
			return null;
		}
	}

	/**
	 * Takes a map and builds all partially empty sub slot maps.
	 * <p>
	 * Example: The sub-maps of { a -> 1, b -> 2 } are:
	 * <ul>
	 * <li>{ a -> empty slot, b -> empty slot}
	 * <li>{ a -> 1 b -> empty slot }
	 * <li>{ a -> empty slot , b -> 2 }
	 * <li>{ a -> 1, b -> 2 }
	 * </ul>
	 *
	 * @param additionMap
	 *          all these slots will also be in each result
	 *
	 *          private static Collection<ImmutableMap<String, RoleFrameSlot>>
	 *          buildAllPartiallyEmptySubSlotMaps( final Map<String, RoleFrameSlot>
	 *          slotMap, final Map<String, RoleFrameSlot> additionMap) { // start
	 *          with empty slot map Collection<Map<String, RoleFrameSlot>> res = new
	 *          LinkedList<Map<String, RoleFrameSlot>>(); res.add(new
	 *          HashMap<String, RoleFrameSlot>(additionMap));
	 *
	 *          // iterate over all slot map entries for (final Map.Entry<String,
	 *          RoleFrameSlot> slotEntry : slotMap.entrySet()) { final
	 *          Collection<Map<String, RoleFrameSlot>> oldRes = res;
	 *
	 *          res = new LinkedList<Map<String, RoleFrameSlot>>();
	 *
	 *          // Iterate over all slot alternatives we already have for (final
	 *          Map<String, RoleFrameSlot> oldSlots : oldRes) { if (!
	 *          slotEntry.getValue().isEmpty()) { final HashMap<String,
	 *          RoleFrameSlot> newSlotsWithEmptySlot = new HashMap<String,
	 *          RoleFrameSlot> (oldSlots); newSlotsWithEmptySlot.put(
	 *          slotEntry.getKey(), slotEntry.getValue().emptyCopy());
	 *          res.add(newSlotsWithEmptySlot); }
	 *
	 *          oldSlots.put(slotEntry.getKey(), slotEntry.getValue());
	 *          res.add(oldSlots); } }
	 *
	 *          final Collection<ImmutableMap<String, RoleFrameSlot>>
	 *          immutableMapRes = new LinkedList<ImmutableMap<String,
	 *          RoleFrameSlot>>(); for (final Map<String, RoleFrameSlot> map : res)
	 *          { immutableMapRes.add(ImmutableMap.copyOf(map)); }
	 *
	 *          return immutableMapRes; }
	 */

	/**
	 * Takes a Set and builds all subsets.
	 * <p>
	 * Example: The subsets of { a, b } are:
	 * <ul>
	 * <li>{ }
	 * <li>{ a }
	 * <li>{ b }
	 * <li>{ a, b }
	 * </ul>
	 *
	 * @param additionSet
	 *          all these elements will also be in each result
	 */
	private static <T extends Object> Set<ImmutableSet<T>> buildAllSubSets(final Set<T> set, final Set<T> additionSet) {
		// start with empty set
		ImmutableSet.Builder<ImmutableSet<T>> res = ImmutableSet.builder();
		// usings a result Set (instead of a list) allows for values, that
		// are contained in set as well as in additionSet
		res.add(ImmutableSet.<T>copyOf(additionSet));

		// iterate over all set elements
		for (final T element : set) {
			final ImmutableSet<ImmutableSet<T>> oldRes = res.build();

			res = ImmutableSet.<ImmutableSet<T>>builder();

			// Iterate over all sub-set "alternatives" we already have
			for (final ImmutableSet<T> oldSet : oldRes) {
				// without the element
				res.add(oldSet);

				// with the element
				final ImmutableSet<T> withElement = ImmutableSet.<T>builder().addAll(oldSet).add(element).build();
				res.add(withElement);
			}
		}

		return res.build();
	}

	/*
	 * public boolean containsFilling(final IParsingEdge fillingAlternative) { for
	 * (final RoleFrameSlot slot : this.slots.values()) { if
	 * (fillingAlternative.equals(slot.getFilling())) { // This works, because two
	 * // IParsingEdge objects are only equal, // if they mean the same position in
	 * the sentence!! return true; } }
	 *
	 * return false; }
	 */

	/*
	 * Checks each of the {@code baseRoleFrameAlternatives} and each of the {@code
	 * fillingAlternatives}: If the filling is NOT YET USED to fill any other of the
	 * slots, the role frame is copied and filled with the given filling. All
	 * derived (FILLED!) role frames are returned.<p> The slot has to exist and MUST
	 * NOT BE FILLED in the base role frames!
	 *
	 * public static Collection<RoleFrame> possiblyCopyAndFill( final
	 * Collection<RoleFrame> baseRoleFrameAlternatives, final RoleFrameSlot
	 * emptySlot, final Collection<IParsingEdge> fillingAlternatives) { final
	 * Collection<RoleFrame> res = new LinkedList<RoleFrame>();
	 *
	 * for (final RoleFrame baseRoleFrameAlternative : baseRoleFrameAlternatives) {
	 * for (final IParsingEdge fillingAlternative : fillingAlternatives) { if (!
	 * baseRoleFrameAlternative.containsFilling(fillingAlternative)) {
	 * res.add(baseRoleFrameAlternative. copyAndFill(emptySlot,
	 * fillingAlternative)); } } }
	 *
	 * return res; }
	 */

	/*
	 * Returns a copy of this role frame, in which the given slot (that has to be
	 * empty in the original), is filled with the given realization.
	 *
	 * public RoleFrame copyAndFill(final RoleFrameSlot emptySlot, final
	 * IParsingEdge realization) { if (emptySlot.isFilled()) { throw new
	 * IllegalArgumentException("Slot already filled: " + emptySlot); }
	 *
	 * final RoleFrame res = new RoleFrame(this.type);
	 *
	 * // copy all slots for (final RoleFrameSlot slot : this.slots.values()) {
	 * res.slots.put(slot.getName(), slot); }
	 *
	 * // override filled slot final RoleFrameSlot oldSlot = res.slots.put(
	 * emptySlot.getName(), emptySlot.copyEmptySlotAndFill(realization));
	 *
	 * if (oldSlot == null) { throw new
	 * IllegalArgumentException("There was no such slot in the frame before!\n" +
	 * "Slot: " + emptySlot + "\nFrame: " + this); }
	 *
	 * return res; }
	 */

	/**
	 * Merges two collections of free fillings by simply building a union. The free
	 * fillings need not be disjoint.
	 */
	private static ImmutableSet<IHomogeneousConstituentAlternatives> mergeFreeFillings(
			final ImmutableSet<IHomogeneousConstituentAlternatives> someFreeFillings,
			final ImmutableSet<IHomogeneousConstituentAlternatives> moreFreeFillings) {
		if (someFreeFillings.isEmpty()) {
			return moreFreeFillings;
		}
		// else : someFreeFilling is not empty

		if (moreFreeFillings.isEmpty()) {
			return someFreeFillings;
		}

		// else: none of them is empty

		return ImmutableSet.<IHomogeneousConstituentAlternatives>builder().
		// duplicates have no effect :-)
				addAll(someFreeFillings).
				// duplicates have no effect :-)
				addAll(moreFreeFillings).build();
	}

	/**
	 * Convenience method.
	 *
	 * @param slotName
	 *          of a slot that exists
	 * @return the filling of the slot, or <code>null</code>, if the slot is not
	 *         filled
	 *
	 *         public IParsingEdge getSlotFilling(final String slotName) { final
	 *         RoleFrameSlot roleFrameSlot = this.slots.get(slotName); return
	 *         roleFrameSlot.getFilling(); }
	 */

	private final int calcHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + slots.hashCode();
		result = prime * result + freeFillings.hashCode();
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
		final RoleFrame other = (RoleFrame) obj;

		if (hashCode != other.hashCode) {
			return false;
		}

		if (!slots.equals(other.slots)) {
			return false;
		}
		if (!freeFillings.equals(other.freeFillings)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final RoleFrame other) {
		final int slotsCompared = CollectionUtil.compareMaps(slots, other.slots);
		if (slotsCompared != 0) {
			return slotsCompared;
		}

		return CollectionUtil.compareCollections(freeFillings, other.freeFillings);

	}

	@Override
	public String toString() {
		return toString(true, false);
	}

	/**
	 * @return a string representation of <code>this</code>
	 */
	public String toString(boolean neverShowRequirements, boolean forceShowRequirements) {
		final StringBuilder res = new StringBuilder();

		res.append("{");

		boolean first = true;

		for (final Entry<String, RoleFrameSlot> entry : slots.entrySet()) {
			if (first) {
				first = false;
			} else {
				res.append(", ");
			}

			res.append(entry.getKey());

			res.append(" : ");

			res.append(entry.getValue().toString(neverShowRequirements,
					forceShowRequirements));
		}

		for (final IConstituentAlternatives freeFilling : freeFillings) {
			if (first) {
				first = false;
			} else {
				res.append(", ");
			}

			res.append("? : ");
			res.append(freeFilling.toString());
		}

		res.append("}");
		return res.toString();
	}

	/**
	 * Checks wether these values would make up a consistent role frame.
	 */
	private static void check(final Map<String, RoleFrameSlot> slots,
			final Collection<IHomogeneousConstituentAlternatives> freeFillings) throws IllegalArgumentException {
		// There can only be free fillings, if there are NO slots AT ALL!
		if (!freeFillings.isEmpty()) {
			if (!slots.isEmpty()) {
				throw new IllegalArgumentException("There can only be free fillings, if "
						+ "there are NO empty slots and NO filled slots. But parameters were:\n" + " - Free fillings: "
						+ freeFillings + "\n - Slots: " + slots);
			}
		}
	}

	public int howManyFillingsAreMissingUntilCompletion() {
		int res = 0;
		for (String slotName : slots.keySet()) {
			res +=
					// TODO Bug?? howManyFillingsAreMissingUntilCompletion(slotName??)
					howManyAdditionalFillingsAreAllowed(slotName);
		}

		return res;
	}

	/**
	 * @return How many fillings are missing, until a slot with this name is
	 *         completed (if such a slot exists)
	 */
	int howManyFillingsAreMissingUntilCompletion(final String slotName) {
		final RoleFrameSlot slot = slots.get(slotName);

		if (slot == null) {
			return 0;
		}

		return slot.howManyFillingsAreMissingUntilCompletion();
	}

	/**
	 * @return How many <i>additional</i> fillings are allowed for a slot with this
	 *         name? - <i>-1</i>, if there is <i>no upper bound</i>.
	 */
	int howManyAdditionalFillingsAreAllowed(final String slotName) {
		final RoleFrameSlot slot = slots.get(slotName);

		if (slot == null) {
			return -1;
		}

		return slot.howManyAdditionalFillingsAreAllowed();
	}

	/**
	 * Returns this role frame as {@link IFillingInSlot} - unless the role frame has
	 * more than one filling in a slot.
	 */
	@Nullable
	public FillingInSlot toFillingInSlot() {
		SurfacePart surfacePart = null;
		final ImmutableMap.Builder<String, IFeatureValue> features = ImmutableMap.builder();
		boolean semanticsAmbivalent = false;
		ISemantics semantics = null;

		for (final Iterator<Entry<String, RoleFrameSlot>> slotIter = slotIterator(); slotIter.hasNext();) {
			final Entry<String, RoleFrameSlot> entry = slotIter.next();

			final Collection<FillingInSlot> slotFillings = entry.getValue().getFillings();
			if (slotFillings.size() > 1) {
				return null;
			}

			if (!slotFillings.isEmpty()) {
				final FillingInSlot slotFilling = slotFillings.iterator().next();

				surfacePart = SurfacePart.join(surfacePart, slotFilling.getFeatures().getSurfacePart());

				features.put(entry.getKey(), slotFilling);

				if (!semanticsAmbivalent) {
					if (semantics == null) {
						semantics = slotFilling.getSemantics();
					} else if (slotFilling.getSemantics() != null && !slotFilling.getSemantics().equals(semantics)) {
						semantics = null;
						semanticsAmbivalent = true;
					}
				}
			}
		}

		return new FillingInSlot(FeatureStructure.fromValues(surfacePart, features.build()), semantics);
	}
}
