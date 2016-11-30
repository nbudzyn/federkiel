package de.nb.federkiel.feature;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.plurivallogic.Plurival;


/**
 * A Collection of role frames.<p>
 * (This is a value, not a term.)
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class RoleFrameCollection
implements IFeatureValue, Iterable<RoleFrame>, IFillingUsageRestrictor {
	final private static WeakCache<RoleFrameCollection> cache =
			new WeakCache<>();

	public static final RoleFrameCollection EMPTY = of();

	final private static int MIN_NUM_FREE_FILLINGS_FOR_RESTRICTION_CHECK = 2; // "Daumenwert"

	final private ImmutableSet<RoleFrame> roleFrames;

	/**
	 * caching the hashCode
	 */
	private final int hashCode;

	public static RoleFrameCollection of(final RoleFrame... roleFrames) {
		return cache.findOrInsert(new RoleFrameCollection(roleFrames));
	}

	public static RoleFrameCollection of(
			final ImmutableSet<RoleFrame> roleFrames) {
		return cache.findOrInsert(new RoleFrameCollection(roleFrames));
	}

	private RoleFrameCollection(final RoleFrame... roleFrames) {
		this(ImmutableSet.copyOf(roleFrames));
	}

	private RoleFrameCollection(final ImmutableSet<RoleFrame> roleFrames) {
		super();
		this.roleFrames = roleFrames;
		hashCode = calcHashCode(roleFrames);
	}

	/**
	 * Builds the union of the two role frame collections - or returns <code>null</code>,
	 * iff  the two role frame collections
	 * do contain role frames which have <i>the same filling</i> filled into
	 * <i>slots with different names</i>.
	 */
	protected RoleFrameCollection union(
			final RoleFrameCollection other) {
		for (final RoleFrame oneRoleFrame : roleFrames) {
			for (final RoleFrame otherRoleFrame : other.roleFrames) {
				if (oneRoleFrame.containsTheSameFillingInADifferentSlot(otherRoleFrame)) {
					return null;
				}
			}
		}

		return of(
				ImmutableSet.<RoleFrame>builder().
				addAll(roleFrames).
				addAll(other.roleFrames).
				build());

	}

	private RoleFrameCollection add(final RoleFrame roleFrame) {
		return of(
				ImmutableSet.<RoleFrame>builder().
				addAll(roleFrames).
				add(roleFrame).
				build());
	}

	/**
	 * Builds a new role frame collection by merging this one with another one.
	 * <p>
	 * Merging two <i>role frames</i> means adding slots and filling slots
	 * with free fillings.
	 */
	protected Plurival<RoleFrameCollection> merge(
			final RoleFrameCollection other) {
		Collection<RoleFrameCollection> resAlternatives =
			new LinkedList<>();
		resAlternatives.add(of()); // one result: empty role frame Collection
									// (for a start)

		/* FIXME besteht hier noch ein Problem?
		 * Adam war Komponist und Dichter.
		 * Adam war Komponist und heiratete Eva.
		 *
		"Adam" wird in jedem Rollen-Rahmen der
		Role Frame Collection ENTWEDER als Subjekt ODER als Prädikatsnomen belegt.

		Vielleicht ist der Haken aber auch: Adam belegt einen Slot, der in der anderen Role Frame Collection
		schon belegt ist?? (Das geht gar nicht mehr oder?)
		Adam muss in allen Rollen-Rahmen der Collection dieselbe Rolle ausfüllen (oder gar keine!)
		 */

		for (final RoleFrame myRoleFrame : roleFrames) {
			for (final RoleFrame otherRoleFrame : other.roleFrames) {
				// NOTE: Merging of the two role frames depends on the earlier retrieved results,
				// BECAUSE: The same free filling MUST BE FILLED INTO THE SAME SLOT
				// for all role-frame-combinations!
				// (And condition must hold for each result alternative!)
				// So we iterate over all alternatives, that have already been found.

				final Collection<RoleFrameCollection> oldResAlternatives = resAlternatives;

				resAlternatives = new LinkedList<>();
				for (final RoleFrameCollection oldResAlternative : oldResAlternatives) {
					// we take the alternative we already have and
					// add the new possible merge result(s) to the alternative!
					final Plurival<RoleFrame> possibleMerges =
						myRoleFrame.merge(otherRoleFrame, oldResAlternative);

					for (final RoleFrame possibleMerge : possibleMerges) {
						resAlternatives.add(oldResAlternative.add(possibleMerge));
					}
				}

				if (resAlternatives.isEmpty()) {
					// No result alternatives found for merging these two role frames!!
					// That means, that merging these
					// role frame COLLECTIONS is not possible EITHER!
					return Plurival.empty();
				}
			}
		}

		// Achtung: NICHT DURCHEINANDERBRINGEN: Als Ergebnis werden ALTERNATIVEN erwartet,
		// jede ALTERNATIVE enthält dann wieder eine RoleFrameCollection mit allen merge-Paaren...
		return Plurival.of(
				ImmutableList.copyOf(resAlternatives));
	}

	/**
	 * Builds a new role frame collection by using this as the base for filling
	 * an ellipse role frame collection.
	 * <p>
	 * Filling <i>role frame</i> ellipse means copying some filled slots from the base and
	 * filling other slots (taken from the base) with free filling from the ellipse.
	 */
	protected Plurival<RoleFrameCollection> fillEllipse(
			final RoleFrameCollection ellipse) {
		Collection<RoleFrameCollection> resAlternatives =
			new LinkedList<>();
		resAlternatives.add(of()); // one result: empty role frame Collection
									// (for a start)

		// FIXME Hier besteht bestimmt ein Problem wie bei merge()??
		// FIXME use getRestrictedRoleFrameName
		// FIXME Note, that this method will not work properly, if there are several role frame slots
		// <i>with different slot names</i>, that contain the filling!


		for (final RoleFrame baseRoleFrame : roleFrames) {
			for (final RoleFrame ellipseRoleFrame : ellipse.roleFrames) {
				final Collection<RoleFrameCollection> oldResAlternatives = resAlternatives;

				resAlternatives = new LinkedList<>();
				for (final RoleFrameCollection oldRoleResAlternative : oldResAlternatives) {

					final Plurival<RoleFrame> possibleEllipseFillings =
						baseRoleFrame.fillEllipse(ellipseRoleFrame, oldRoleResAlternative);

					// We take all alternatives we already have and
					// add the new possible ellipse-filling result(s) to each alternative!
					for (final RoleFrame possibleEllipseFilling : possibleEllipseFillings) {
						resAlternatives.add(oldRoleResAlternative.add(possibleEllipseFilling));
					}
				}

				if (resAlternatives.isEmpty()) {
					// No result alternatives found for ellipse-filling of these two role frames!!
					// That means, that the ellipse-filling
					// of the role frame COLLECTION based on this base role frame COLLECTION
					// is not possible EITHER!
					return Plurival.empty();
				}
			}
		}

		// Achtung: NICHT DURCHEINANDERBRINGEN: Als Ergebnis werden ALTERNATIVEN erwartet,
		// jede ALTERNATIVE enthält dann wieder eine RoleFrameCollection mit allen ellipse-Fillings...
		return Plurival.of(ImmutableList.copyOf(resAlternatives));
	}

	/**
	 * This method is used to check, whether a role frame collection confirms to
	 * some feature restrictions. For example, a feature restriction could say,
	 * that there must at most be one <i>Subjekt</i> in a role frame. So the
	 * system can see, how many <i>Nominativ</i>s there are as <i>free
	 * fillings</i> in this collection's role frames, and if there is more than
	 * one, the system could tell the parser, that there is no use working on
	 * this role frame collection any more.
	 * <p>
	 * Effectively, this method does a merge (of this role frame collection and
	 * the restriction), but stops, when the first result is found.
	 * <p>
	 * NOTE, THAT this method might return <code>true</code>, even if the
	 * restrictions are not met! (It guesses, whether checking would be
	 * worthwhile or not.)
	 *
	 * @return Returns true, iff at least one of the following is true
	 *         <ul>
	 *         <li>This role frame collection contains a role frame, that does
	 *         not have any free fillings at all. So the restrictions will not
	 *         be checked. <li>Each role frame in this role frame collection
	 *         confirms to the restrictions. (Even if you take into accoung,
	 *         that the same free filling MUST BE FILLED INTO THE SAME SLOT for
	 *         each role frame!)
	 *         </ul>
	 */
	public boolean hasNoFreeFillingsAtAllOrHasFreeFillingsThatConfirmTo(
			final RoleFrame restriction) {
		// This procedure is very much like merge().

		Collection<RoleFrameCollection> mergeAlternatives = new LinkedList<>();
		mergeAlternatives.add(of()); // one result: empty
		// role frame Collection (for a start)

		int roleFrameCount = 0;
		for (final RoleFrame myRoleFrame : roleFrames) {
			if (!myRoleFrame.hasFreeFillings()) {
				// This role frame does not have any free fillings at all. So
				// the restrictions will not be checked. I guess, that this
				// applies
				// to all role frames of the collection.
				return true;
			}

			if (size() > 1 ||
					// If the role frame collection contains only one role frame
					myRoleFrame.numFreeFillings() >= MIN_NUM_FREE_FILLINGS_FOR_RESTRICTION_CHECK) {
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

				final Collection<RoleFrameCollection> oldMergeAlternatives = mergeAlternatives;

				mergeAlternatives = new LinkedList<>();
				for (final RoleFrameCollection oldMergeAlternative : oldMergeAlternatives) {
					// we take the alternative we already have and
					// add the new possible merge result(s) to the alternative!

					// Special case: if myRoleFrame is THE LAST ONE (and note,
					// that
					// many
					// role frame collections have only one role frame!)...
					if (roleFrameCount == size() - 1) {
						// ..., then we can
						// stop succesfully, if we have found any merge
						// alternative
						// (based on the
						// oldMergeAlternatives) - we DO NOT NEED ALL merge
						// alternative
						if (myRoleFrame.canMerge(restriction,
								oldMergeAlternative)) {
							return true;
						}
						// else try the next oldMergeAlternative -- the
						// mergeAlternatives stay empty!
					} else {
						// it is not the last role frame of the collection - we
						// need
						// all alternatives!
						final Plurival<RoleFrame> possibleMerges = myRoleFrame
						.merge(restriction, oldMergeAlternative);

						for (final RoleFrame possibleMerge : possibleMerges) {
							mergeAlternatives.add(oldMergeAlternative
									.add(possibleMerge));
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


	/*
	/**
	 * Builds a new role frame collection by merging this one with another one.
	 * <p>
	 * Merging two <i>role frames</i> means adding slots and filling slots
	 * with free fillings.
	 *
	public IPlurival<RoleFrameCollection> mergeNew(final RoleFrameCollection other) {
		Plurival<RoleFrameCollection> res =
			new Plurival<RoleFrameCollection>();
		res.add(new RoleFrameCollection()); // one result: empty role frame Collection (for a start)

		/*
	 * Adam war Komponist und Dichter.
	 * Adam war Komponist und heiratete Eva.
	 * Peter bzw. Frank waren Komponist heirateten Eva.
	 *
		Das Problem ist eine Ebene höher: "Adam" wird in einem Rollen-Rahmen der
		Role Frame Collection als Subjekt und zugleich im anderen als Prädikatsnomen belegt --
		das darf nicht sein!!!
		Vielleicht ist der Haken auch: Adam belegt einen Slot, der in der anderen Role Frame Collection
		schon belegt ist!
		Adam muss in allen Rollen-Rahmen der Collection dieselbe Rolle ausfüllen (oder gar keine!)

		Das freeFilling muss also gegen die gleichnamigen Slots ALLER ROLE FRAMES geprüft werden -
		nicht nur gegen einen! Das gilt natürlich SPIEGELBILDLICH!
		Das Merging muss also etwa so passieren wie beim Role-Frame-Merge, aber auf Ebene der Collections,
		wobei die GLEICHNAMIGEN SLOTS beim FILLING immer GEMEINSAM betrachtet werden müssen.
		Oder aber die GLEICHEN FILLINGS (gleiche von-bis-Wortfolgen) müssen gleich betrachtet
		werden??

		Bedenke aber auch: Adam und Eva waren Mann und Frau. (Wohl irrelevant. "und" ~ "bzw.")
	 */

	/*
	 * Merges the two role frames, which means:
	 * <ul>
	 * <li>free fillings are added
	 * <li>if there are any slots at all, the slots are added, and
	 *     <i>ALL</i> free fillings are used to fill the slots.
	 * (<i>ALL</i> symbols are &quot;consumed&quot; in this case! -
	 * There can only be free fillings, if there are NO slots at all!)
	 * </ul>
	 * <p>
	 * The free fillings of this role frame collection and of the other role frame collection
	 * MUST BE DISJOINT (resulting from different parts of the input, for example)!
	 * <p>
	 * In some cases, merging is not possible (not all free fillings can be consumed by the
	 * slots, e.g.), in other cases there are several possibilities for a merge
	 * (filling A fills slot X, filling B fills Slot Y; or the other way round). So, the
	 * merge result is a COLLECTION, containing all possible (alternative) merges.
	 * <p>
	 * If the merge is not possible (due to a all-has-to-be-consumed-condition, e.g.),
	 * the result will be empty.
	 * <p>
	 * The two role frames MUST NOT share the same role frame name!<p>
	 *

		final Plurival<Map<String, RoleFrameSlot>> slotsAlternatives =
			this.slotUnionAlternatives(other);


		for (final RoleFrame myRoleFrame : this.roleFrames) {
			for (final RoleFrame otherRoleFrame : other.roleFrames) {

				final Set<ParseAlternatives> mergedFreeFillings =
					RoleFrame.mergeFreeFillings(myRoleFrame, otherRoleFrame);

				IPlurival<RoleFrame> possibleMerges;

				// Hier gibt es ein Problem! EIGENTLICH könnte hier sowas stehen wie
				// slotsAlternatives.areAllEmpty(), oder ich muss das eine Ebene oder mehr
				// tiefer schieben - was auch schwierig ist, weil da die for-Schleifen anders
				// herum geschachtelt sind - was auch gut ist! (Und ich möchte nicht so gern
				// bei jedem freien Filling einzeln prüfen: Ach nee, bei diesen Slots, da will
				// ich ja gar nicht die Fillings einfügen....
				// Statt slotsAlternatives.areAllEmpty() könnte man vorher schon
				// this.roleFramesDontHaveSlots() && other.slots.roleFramesDontHaveSlots()
				// prüfen...
				// Das würde aber irgendwie "voraussetzen", dass IMMER ALLE Role Frames einer
				// Role Frame Collection Slots haben - ODER GAR KEINE haben Slots...
				// (Das könnte wiederum sein - wenn auch die Slots in einer VERB Role Frame Collection
				// schon sehr verschieden sein könnten...)
				// Denkbar wäre auch: TYPEN von Role Frame Collections (die, die Slot-Filling
				// verlangen und die anderen, die nur Container für free fillings sind)

				if (slotUnion.isEmpty()) {
					// No slots at all --> free fillings stay unconsumed!
					possibleMerges =
						Plurival.singleton(new RoleFrame(slotUnion, mergedFreeFillings)); // ==>
				} else {

					// ELSE: There could be several different slot fillings. We start the
					// slot alternatives we already know
					// and then consume the free fillings, one after the other.
					// (ALL fillings have to be consumed - for each slot alternative, that
					// has any slots at all!)

					for (final ParseAlternatives freeFilling : mergedFreeFillings) {
						final Plurival<Map<String, RoleFrameSlot>> oldSlotsAlternatives =
							slotsAlternatives;

						slotsAlternatives = new Plurival<Map<String, RoleFrameSlot>>();

						// Iterate over all slot alternatives we already have
						for (final Map<String, RoleFrameSlot> oldSlots : oldSlotsAlternatives) {
							// iterate over all slots in this alternative
							for (final RoleFrameSlot slot : oldSlots.values()) {
								final RoleFrameSlot filledSlot = slot.addFillingIfAccepted(freeFilling);
								if (filledSlot != null) {
									// Filling was accepted -> so we have a new filling alternative

									final Map<String, RoleFrameSlot> newSlots =
										new HashMap<String, RoleFrameSlot>(oldSlots);
									newSlots.put(slot.getName(), filledSlot); // (override old value)
									slotsAlternatives.add(newSlots);
								}
							}
						}
					}

					// Build resulting RoleFrames from the slot filling alternatives
					final Plurival<RoleFrame> possibleMergesBuilder = new Plurival<RoleFrame>();
					for (final Map<String, RoleFrameSlot> newSlots : slotsAlternatives) {
						possibleMergesBuilder.add(
								new RoleFrame(
										newSlots,
										Collections.<ParseAlternatives>emptySet())); // all free fillings consumed
					}

					possibleMerges = possibleMergesBuilder.build();
				}

				if (!possibleMerges.isEmpty()) {
					// merging these two role frames was possible.
					// So we take all alternatives we already have and
					// add the new possible merge result(s) to each alternative!
					final Plurival<RoleFrameCollection> oldRes = res;

					res = new Plurival<RoleFrameCollection>();
					for (final RoleFrameCollection oldRoleFrameCollection : oldRes) {
						for (final RoleFrame possibleMerge : possibleMerges) {
							res.add(oldRoleFrameCollection.add(possibleMerge));
						}
					}
				} else {
					// possibleMerges is empty!! That means, that merging of these two role frames
					// is not at all possible! That means, that merging these
					// role frame COLLECTIONS is not possible EITHER!
					return Plurival.empty();
				}
			}
		}

		// Achtung: NICHT DURCHEINANDERBRINGEN: Als Ergebnis werden ALTERNATIVEN erwartet,
		// jede ALTERNATIVE enthält dann wieder eine RoleFrameCollection mit allen merge-Paaren...
		return res.build();
	}
	 */

	/**
	 * Necessary, when this object is used as an <code>IFillingUsageRestrictor</code> -
	 * returns the only slot name which is allowed for the given homogeneous filling
	 * (because the role frame collection already contains the free filling in a slot
	 * with this name!),
	 * or <code>null</code>, if the slot name is <i>not</i> restricted for
	 * the free filling (because the role frame collection does NOT contain the free filling
	 * in any slot in any role frame)
	 * <p>
	 * Note, that this method will not work properly, if there are several role frame slots
	 * <i>with different slot names</i>, that contain the filling!
	 *
	 * @see IFillingUsageRestrictor
	 */
	@Override
	public String getRestrictedSlotNameFor (final IHomogeneousConstituentAlternatives homogeneousFilling) {
		for (final RoleFrame roleFrame : roleFrames) {
			final RoleFrameSlot slot = roleFrame.findSlotContaining(homogeneousFilling);
			if (slot != null) {
				return slot.getName(); // ==>
			}
		}

		// homogeneousFilling not found in any slot of my role frames
		// -> no restriction on the slot name for the free filling
		return null;
	}

	@Override
	public int keepPlaceFreeForHowManyFillings(final String slotName) {
		return howManyFillingsAreMissingUntilCompletion(slotName);
	}

	@Override
	public int howManyAdditionalFillingsAreAllowed(final String slotName) {
		int res = -1;

		for (final RoleFrame roleFrame : roleFrames) {
			final int howManyAdditionalFillingsAreAllowedForThisRoleFrameAndSlotName =
					roleFrame.howManyAdditionalFillingsAreAllowed(slotName);

			if (howManyAdditionalFillingsAreAllowedForThisRoleFrameAndSlotName != -1) {
				if (res == -1) {
					res =
							howManyAdditionalFillingsAreAllowedForThisRoleFrameAndSlotName;
				} else {
					res =
							Math.min(res,
									howManyAdditionalFillingsAreAllowedForThisRoleFrameAndSlotName);
				}
			}
		}

		return res;

	}

	/**
	 * @return How many fillings for slots with this name are missing, until all
	 *         slots with this name are completed?
	 */
	private int howManyFillingsAreMissingUntilCompletion(final String slotName) {
		int res = 0;

		for (final RoleFrame roleFrame : roleFrames) {
			res =
					Math.max(res, roleFrame
							.howManyFillingsAreMissingUntilCompletion(slotName));
		}

		return res;
	}

	/**
	 * @return <code>true</code>, iff all slots are satisfied and there are no free fillings.
	 */
	@Override
	public boolean isCompleted() {
		for (final RoleFrame roleFrame : roleFrames) {
			if (! roleFrame.isCompleted()) {
				return false;
				// FIXME Könnte es sein, dass nur EINIGE meiner roleFrames completed sind?
				// Macht es Sinn, dann die anderen zu löschen oder so??
			}
		}

		return true;
	}

	public boolean isEmpty() {
		return roleFrames.isEmpty();
	}

	/**
	 * @return the number of role frames this contains
	 */
	public int size() {
		return roleFrames.size();
	}

	/**
	 * @return an Iterator over my role frames.
	 */
	@Override
	public Iterator<RoleFrame> iterator() {
		return roleFrames.iterator();
	}

	/*
	public ParseAlternativesDifference
	findSinglePointOfDifferenceForRoleFrameCollFeaturesAndCheckAtomicFeatures(
			final IFeatureValue otherValue) throws TooManyDifferencesException {
		if (! (otherValue instanceof RoleFrameCollection)) {
			throw new TooManyDifferencesException(
			"Values not equal: Other value is no role frame collection!");
		}

		final RoleFrameCollection otherRoleFrameCollection = (RoleFrameCollection) otherValue;

		final int numMyRoleFrames = this.roleFrames.size();
		if (numMyRoleFrames != otherRoleFrameCollection.roleFrames.size()) {
			throw new TooManyDifferencesException(
			"Role frame collections not equal: Different size!");
		}

		// they have the same number of role frames

		if (numMyRoleFrames == 0) {
			// they both are empty
			return null; // ==> fine
		}


		if (numMyRoleFrames != 1) {
			// I test for full equality - not sure how to make it better
			// TODO Have a better idea and check the role frames for
			// respective equality / findSinglePointOfDifference().
			if (this.roleFrames.equals(otherRoleFrameCollection.roleFrames)) {
				return null; // ==> fine
			} else {
				throw new TooManyDifferencesException(
				"Role frame collections not equal: Role frames not equal, respectively.");
			}
		}

		// they both have exactly one role frame
		final RoleFrame myRoleFrame = this.roleFrames.iterator().next();

		final RoleFrame othersRoleFrame = otherRoleFrameCollection.roleFrames.iterator().next();

		return myRoleFrame.findSinglePointOfDifference(othersRoleFrame);
	}
	 */

	/*
	 * If we have two terms with two term bounds, what will be the resulting
	 * term bound for the term, that results from merging the two terms?
	 * <p>
	 * This method expects, that the term bounds are given as role frame
	 * collections (<code>this</code> and
	 * <code>otherBoundsGivenAsRoleFrameCollection</code>).
	 *
	public Plurival<RoleFrame> mergeBoundsGivenAsRoleFrameCollections(
			final RoleFrameCollection otherBoundsGivenAsRoleFrameCollection) {
		final Plurival<RoleFrameCollection> boundAlternativesAsRoleFrameCollections = merge(otherBoundsGivenAsRoleFrameCollection);

		return combineAlternativeBoundsGivenAsAlternativeRoleFrameCollection(boundAlternativesAsRoleFrameCollections);
	}

	private static Plurival<RoleFrame> combineAlternativeBoundsGivenAsAlternativeRoleFrameCollection(
			final Plurival<RoleFrameCollection> boundAlternativesAsRoleFrameCollections) {
		final Builder<RoleFrame> resBoundAlternatives = ImmutableList
		.<RoleFrame> builder();

		for (final RoleFrameCollection boundsAlternativeAsRoleFrameCollection : boundAlternativesAsRoleFrameCollections) {
			// Each role frame in the collection might bring some
			// bounds
			// with it - they must all be combined! (Remember, that
			// when (in a later step) any role
			// frame from a collection cannot be merged, the whole
			// collection cannot be merged!)
			try {
				resBoundAlternatives
				.add(boundsAlternativeAsRoleFrameCollection
						.combineBoundsGivenAsRoleFrameCollection());
			} catch (final CannotFulfillTermException e) {
				// bad luck, the ristriction from this role frame
				// collection cannot even be combined.
				// Try the next alternative.
			}
		}

		return new Plurival<RoleFrame>(resBoundAlternatives.build());
	}

	 * Combines all role frame bounds from the given role frame collection.
	 *
	 * @throws CannotFulfillTermException
	 *             iff the bounds cannot be combined
	 *
	public RoleFrame combineBoundsGivenAsRoleFrameCollection()
	throws CannotFulfillTermException {
		RoleFrame resBounds = null;

		for (final RoleFrame roleFrameFromBoundsAlternative : this) {
			if (resBounds == null) {
				resBounds = roleFrameFromBoundsAlternative;
			} else {
				final ITermBounds newResBoundsAsTermBounds = resBounds
				.combineBounds(roleFrameFromBoundsAlternative); // CannotFulfillTermException

				if (!(newResBoundsAsTermBounds instanceof RoleFrame)) {
					throw new IllegalStateException(
							"Combining to role frame bounds "
							+ "must lead to a role frame bound (or to an exception)!");
				}

				resBounds = (RoleFrame) newResBoundsAsTermBounds;
			}
		}
		return resBounds;
	}
	 */

	private static int calcHashCode(final ImmutableSet<RoleFrame> roleFrames) {
		final int prime = 31;
		int result = 1;
		result = prime * result + roleFrames.hashCode();
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
		final RoleFrameCollection other = (RoleFrameCollection) obj;

		if (hashCode != other.hashCode) {
			return false;
		}

		if (!roleFrames.equals(other.roleFrames)) {
			return false;
		}

		return true;
	}

	@Override
	public int compareTo(final IFeatureValue o) {
		final int classNameCompared =
			this.getClass().getCanonicalName().compareTo(
					o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final RoleFrameCollection other = (RoleFrameCollection) o;

		return CollectionUtil.compareCollections(
				roleFrames, other.roleFrames);
	}

	/**
	 * @param full
	 *            wether to return a <i>full</i> string representation
	 * @return a string representation of <code>this</code>
	 */
	public String toString(final boolean full) {
		final StringBuilder res = new StringBuilder();

		res.append("[");

		boolean first = true;
		for (final RoleFrame roleFrame : roleFrames) {
			if (first) {
				first = false;
			} else {
				res.append(", ");
			}
			res.append(roleFrame.toString(full));
		}

		res.append("]");

		return res.toString();

	}

	@Override
	public String toString() {
		return toString(false);
	}


	// -- for PRIVATE use --

	/**
	 * Iterates through all my slots and all slot of the other role frame collection
	 * and builds for each pair the union of their slots
	 *
	 * not used Currently
	private Plurival<Map<String, RoleFrameSlot>> slotUnionAlternatives (
			final RoleFrameCollection other) {
		final ImmutableList.Builder<Map<String, RoleFrameSlot>> res =
			ImmutableList.<Map<String,RoleFrameSlot>>builder();

		for (final RoleFrame myRoleFrame : this.roleFrames) {
			for (final RoleFrame otherRoleFrame : other.roleFrames) {
				res.add(RoleFrame.slotUnion(myRoleFrame, otherRoleFrame));
			}
		}

		return new Plurival<Map<String,RoleFrameSlot>>(res.build());
	}
	 */
}
