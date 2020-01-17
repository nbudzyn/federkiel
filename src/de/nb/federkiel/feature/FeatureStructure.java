package de.nb.federkiel.feature;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.UnmodifiableIterator;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.ISemantics;
import de.nb.federkiel.plurivallogic.Plurival;
import de.nb.federkiel.semantik.NothingInParticularSemantics;

/**
 * A structure for (grammatical) features (for the genus, the subject, objects,
 * e.g.).
 * <p>
 * A feature structure also carries semantics.
 * <p>
 * There can also be <i>Free Fillings</i>, that could be used to fill (not-yet
 * existing) empty feature slots. Each free filling is a set of realization
 * alternatives.
 *
 * @author nbudzyn 2009
 */
@Immutable
public class FeatureStructure implements IFeatureValue {
	/**
	 * All generated feature structures shall be cached - to minimize memory use.
	 * The cache consists of weak references, so it will be cleared automatically,
	 * when a feature structure is no longer (strongly) referenced.
	 */
	final private static WeakCache<FeatureStructure> cache = new WeakCache<>();

	/**
	 * The (slotted) features with name and value.
	 */
	private final ImmutableMap<String, IFeatureValue> features;

	/**
	 * The semantics (might be a {@link NothingInParticularSemantics})
	 */
	private final ISemantics semantics;

	/**
	 * Free filling values. There can only be free fillings, if there are NO
	 * features at all!
	 */
	private final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings;

	/**
	 * The part of the surface - if any
	 */
	@Nullable
	private SurfacePart surfacePart;

	/**
	 * caching the hashCode
	 */
	private final int hashCode;

	/**
	 * Test a boolean StringFeatureValue (expecting the value "j" or "n") - or an
	 * UnspecifiedFeatureValue, which for this method counts as true.
	 */
	public static boolean toBoolean(final IFeatureValue booleanStringOrUnpecifiedFeatureValue) {
		if (booleanStringOrUnpecifiedFeatureValue.equals(UnspecifiedFeatureValue.INSTANCE)) {
			return true;
		}

		Preconditions.checkArgument(booleanStringOrUnpecifiedFeatureValue instanceof StringFeatureValue,
				"Not a String feature - and no UnspecifiedFeatureValue either: " + booleanStringOrUnpecifiedFeatureValue);

		final StringFeatureValue booleanStringFeatureValue = (StringFeatureValue) booleanStringOrUnpecifiedFeatureValue;
		return StringFeatureLogicUtil.stringToBoolean(booleanStringFeatureValue.getString());
	}

	/**
	 * Checks two feature values for equality, also recognizing
	 * STRING_FEATURE_UNSPECIFIED values - these are equal to everything!
	 * <p>
	 * USE THIS METHOD WHENEVER YOU COMPARE FEATURE VALUES!
	 */
	protected static boolean doFeatureValuesMatch(final IFeatureValue oneValue, final IFeatureValue otherValue) {
		if (oneValue.equals(UnspecifiedFeatureValue.INSTANCE) || otherValue.equals(UnspecifiedFeatureValue.INSTANCE)) {
			return true;
		}

		return oneValue.equals(otherValue);
	}

	/**
	 * When calling this, always use <code>cache.findOrInsert(...)</code>, to
	 * minimize memory use!
	 */
	private FeatureStructure(@Nullable final SurfacePart surfacePart) {
		this(surfacePart, ImmutableMap.<String, IFeatureValue>of());
	}

	/**
	 * When calling this, always use <code>cache.findOrInsert(...)</code>, to
	 * minimize memory use!
	 */
	private FeatureStructure(@Nullable final SurfacePart surfacePart,
			final ImmutableMap<String, IFeatureValue> features) {
		this(surfacePart, features, NothingInParticularSemantics.INSTANCE, ImmutableSet.of());
	}

	/**
	 * When calling this, always use <code>cache.findOrInsert(...)</code>, to
	 * minimize memory use!
	 */
	private FeatureStructure(@Nullable final SurfacePart surfacePart, final ImmutableMap<String, IFeatureValue> features,
			ISemantics semantics) {
		this(surfacePart, features, semantics, ImmutableSet.of());
	}

	/**
	 * When calling this, always use <code>cache.findOrInsert(...)</code>, to
	 * minimize memory use!
	 */
	private FeatureStructure(@Nullable final SurfacePart surfacePart, final ImmutableMap<String, IFeatureValue> features,
			ISemantics semantics, ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		// There can only be free fillings, if there are NO features at all!
		if (!features.isEmpty()) {
			if (!freeFillings.isEmpty()) {
				throw new IllegalArgumentException("There can only be free fillings, if there are NO features at all! "
						+ "Features: " + features + ", free fillings: " + freeFillings);
			}
		}

		this.surfacePart = surfacePart;
		this.features = features;
		this.semantics = semantics;
		this.freeFillings = freeFillings;

		hashCode = calcHashCode(this.surfacePart, this.features, this.semantics, this.freeFillings);
	}

	/**
	 * Merges the two feature structures, which means:
	 * <ul>
	 * <li>free fillings are added
	 * <li>if there are any features at all, the features are added, and <i>ALL</i>
	 * free fillings are used to fill the slotted features. (<i>ALL</i> symbols are
	 * &quot;consumed&quot; in this case! - There can only be free fillings, if
	 * there are NO features at all!)
	 * </ul>
	 * <p>
	 * The free fillings of this feature structure and of the other feature
	 * structure may contain the same free filling (resulting from the same parts of
	 * the input, based on a filled ellipse, for example).
	 * <p>
	 * In some cases, merging is not possible (not all free fillings can be consumed
	 * by the slotted freatures, e.g.), in other cases there are several
	 * possibilities for a merge (filling A fills feature X, filling B fills feature
	 * Y; or the other way round). So, the merge result is a Plurival, containing
	 * all possible (alternative) merges.
	 * <p>
	 * If the merge is not possible (due to a all-has-to-be-consumed-condition,
	 * e.g.), the result will be empty.
	 * <p>
	 * The two feature structures MUST NOT share the feature name!
	 *
	 * @param fillingUsageRestrictor a free filling might be restricted, so that it
	 *                               can only fill a slotted feature with a
	 *                               specified name - this is the restrictor. (This
	 *                               is necessary to ensure that in a ellipse-like
	 *                               sentence like <i>Paul war Komponist und ab 1924
	 *                               Dirigent.<i>, Paul is the <i>Subjekt<i> in both
	 *                               parts - not the <i>Subjekt</i> in one part and
	 *                               the <i>Praedikatsnomen</i> in the other!)
	 */
	Plurival<FeatureStructure> mergeWithoutSemantics(final FeatureStructure other,
			final IFillingUsageRestrictor fillingUsageRestrictor) throws IllegalArgumentException {
		if (features.isEmpty() && other.features.isEmpty()) {
			// Case 1: this: no features (maybe some free fillings),
			// other: no features (maybe more fillings)

			// No features at all --> all free fillings stay unconsumed!

			return Plurival.of(FeatureStructure.fromFreeFillings(SurfacePart.join(surfacePart, other.surfacePart),
					mergeFreeFillings(freeFillings, other.freeFillings))); // ==>
		}

		if (features.isEmpty() && !other.features.isEmpty()) {
			// Case 2: this: no features (maybe some free fillings),
			// other: some features (no free fillings)!

			return buildFeatureStructurePlurivalFromFeatureAlternativesWithoutSemantics(
					SurfacePart.join(surfacePart, other.surfacePart),
					fillFeaturesConsumingAllFillings(other.features, freeFillings, fillingUsageRestrictor));
			// the results have features (all my free fillings are used to fill them),
			// but the results don't have free fillings
		}

		if (!features.isEmpty() && other.features.isEmpty()) {
			// Case 3: this: some features (no free fillings),
			// other: no features (maybe some free fillings)!

			return buildFeatureStructurePlurivalFromFeatureAlternativesWithoutSemantics(
					SurfacePart.join(surfacePart, other.surfacePart),
					fillFeaturesConsumingAllFillings(features, other.freeFillings, fillingUsageRestrictor));
			// the results have features (all of the other's free fillings are used to fill
			// them),
			// but the results have no free fillings
		}

		// ELSE:
		// Case 4: this: some features (no free fillings)!
		// other: some features (no free fillings)

		final FeatureStructure featureUnion = disjunctUnionWithoutFreeFillings(other,
				NothingInParticularSemantics.INSTANCE);
		if (featureUnion == null) {
			// (there might habe been the same filling in both features, or the same feature
			// name...
			return Plurival.empty();
		}

		return buildFeatureStructurePlurivalFromFeatureAlternativesWithoutSemantics(
				SurfacePart.join(surfacePart, other.surfacePart), fillFeaturesConsumingAllFillings(featureUnion.features,
						mergeFreeFillings(freeFillings, other.freeFillings), fillingUsageRestrictor));
	}

	/**
	 * Tries to merge this and the other feature structure, according to the
	 * fillingUsageRestrictor.
	 *
	 * @return <code>true</code>, iff a merge is possible (at least one alternative
	 *         result)
	 */
	protected boolean canMerge(final FeatureStructure other, final IFillingUsageRestrictor fillingUsageRestrictor) {
		if (features.isEmpty() && other.features.isEmpty()) {
			// Case 1: this: no features (maybe some free fillings),
			// other: no features (maybe more fillings)

			// No slots at all --> all free fillings stay unconsumed!

			return true; // ==>
		}

		if (features.isEmpty() && !other.features.isEmpty()) {
			// Case 2: this: no features (maybe some free fillings),
			// other: some features (no free fillings)!

			return !fillFeaturesConsumingAllFillings(other.features, freeFillings, fillingUsageRestrictor).isEmpty();
			// the results have slots (all my free fillings are used to fill
			// them), but the results have no free fillings
		}

		if (!features.isEmpty() && other.features.isEmpty()) {
			// Case 3: this: some slots (no free fillings),
			// other: no slots (maybe free fillings)!

			return !fillFeaturesConsumingAllFillings(features, other.freeFillings, fillingUsageRestrictor).isEmpty();
			// the results have features (all of the other's free fillings are used
			// to fill them), but the results have no free fillings
		}

		// ELSE:
		// Case 4: this: some features (no free fillings)!
		// other: some features (no free fillings)

		final FeatureStructure featureUnion = disjunctUnionWithoutFreeFillings(other,
				NothingInParticularSemantics.INSTANCE);
		if (featureUnion == null) {
			// (there might habe been the same filling in both features, or the same feature
			// name...
			return false;
		}

		return !fillFeaturesConsumingAllFillings(featureUnion.features, mergeFreeFillings(freeFillings, other.freeFillings),
				fillingUsageRestrictor).isEmpty();
	}

	/**
	 * Uses this feature structure as the base for filling an ellipse feature
	 * structure.
	 * <ul>
	 * <li>the ellipse MUST NOT contain any features (only free filings)
	 * <li>if there are any features in this (the base) at all, then <i>ALL</i> free
	 * fillings IN THE ELLIPSE are used to fill the slots)
	 * </ul>
	 * <p>
	 * The free fillings of this feature structure (the base) and of the ellipse
	 * feature structure may contain the same elements (resulting from earlier
	 * ellipse-filling, for example)!
	 * <p>
	 * In some cases, filling the ellipse is not possible (not all free fillings in
	 * the ellipse can be consumed by the slotted features, e.g.), in other cases
	 * there are several possibilities (filling A fills slotted feature X, filling B
	 * fills slotted feature Y; or the other way round). So, the result is a
	 * Plurival, containing all possible (alternative) ellipse fillings.
	 *
	 * @param fillingUsageRestrictor a free filling (e.g. from <code>this</code>)
	 *                               might be restricted, so that it can only fill a
	 *                               feature (e.g. from the <code>ellipse</code>)
	 *                               with a specified name - this is the restrictor.
	 *                               (This is necessary to ensure that in a
	 *                               ellipse-like sentence like <i>Paul war
	 *                               Komponist und ab 1924 Dirigent.<i>, Paul is the
	 *                               <i>Subjekt<i> in both parts - not the
	 *                               <i>Subjekt</i> in one part and the
	 *                               <i>Praedikatsnomen</i> in the other!)
	 */
	protected Plurival<FeatureStructure> fillEllipseWithoutSemantics(final FeatureStructure ellipse,
			final IFillingUsageRestrictor fillingUsageRestrictor) throws IllegalArgumentException {
		if (features.isEmpty() && ellipse.features.isEmpty()) {
			// Case 1: this: no features (maybe free fillings),
			// ellipse: no features (maybe more fillings)

			// No features at all --> all free fillings stay unconsumed!
			return buildRoleFramesFromFreeFillingAlternatives(SurfacePart.join(surfacePart, ellipse.surfacePart),
					buildAllSubSets(freeFillings, ellipse.freeFillings)); // ==>
		}

		if (features.isEmpty() && !ellipse.features.isEmpty()) {
			// Case 2: this: no features (maybe free fillings),
			// ellipse: some features (no free fillings)!

			return buildFeatureStructurePlurivalFromFeatureAlternativesWithoutSemantics(
					SurfacePart.join(surfacePart, ellipse.surfacePart),
					fillFeaturesUsingFillingsOrNotUsingThem(ellipse.features, freeFillings, fillingUsageRestrictor)); // ==>
			// the results have features (some of my free fillings may be used to fill
			// them),
			// but the result has no free fillings
		}

		if (!features.isEmpty() && ellipse.features.isEmpty()) {
			// Case 3: this: some features (no free fillings)!
			// ellipse: no features (maybe free fillings)

			throw new RuntimeException("Strange use of fill-ellipse: Ellipse has no features and" + // NOPMD by nbudzyn on
			// 29.06.10 19:46
					" base has features." + "\nBase: " + toString() + "\nEllipse: " + toString()); // ==>

			/*
			 * FIXME (or would this be ok?) Actually this would work like a merge, see
			 * merge()
			 */
		}

		// ELSE:
		// Case 4: this: some features (no free fillings)!
		// ellipse: some features (no free fillings)

		throw new RuntimeException("Strange use of fill-ellipse: Ellipse has features, but" + // NOPMD by nbudzyn on
																																													// 29.06.10
		// 19:46
				" base also has features, so there cannot be any free fillings in the base." + "\nBase: " + toString()
				+ "\nEllipse: " + toString()); // ==>

		/*
		 * FIXME (or would this be ok?) Actually this would work like a merge, see
		 * merge()!
		 */
	}

	/**
	 * Builds a FeatureStructure Plurival from the alternatives
	 */
	private static Plurival<FeatureStructure> buildFeatureStructurePlurivalFromFeatureAlternativesWithoutSemantics(
			SurfacePart surfacePart, final Collection<ImmutableMap<String, IFeatureValue>> alternatives) {
		// @formatter:off
		return Plurival.of(alternatives.stream().map(features -> FeatureStructure.fromValues(surfacePart, features))
				.collect(toImmutableList()));
		// @formatter:on
	}

	/**
	 * Build a feature structure plurival from the free fillings alternatives
	 */
	private static Plurival<FeatureStructure> buildRoleFramesFromFreeFillingAlternatives(SurfacePart surfacePart,
			final Collection<ImmutableSet<IHomogeneousConstituentAlternatives>> freeFillingAlternatives) {
		// @formatter:off
		return Plurival.of(freeFillingAlternatives.stream().map(f -> FeatureStructure.fromFreeFillings(surfacePart, f)) // all
																																																										// free
																																																										// fillings
																																																										// consumed
				.collect(toImmutableList()));
		// @formatter:on
	}

	/**
	 * Fills the free fillings into the slotted features. Also generates
	 * alternatives - in each alternative, <i>all</i> free fillings will be
	 * consumed.
	 *
	 * @param fillingUsageRestrictor a free filling might be restricted, so that it
	 *                               can only fill a slot with a specified name -
	 *                               this is the restrictor. (This is necessary to
	 *                               ensure that in a ellipse-like sentence like
	 *                               <i>Paul war Komponist und ab 1924 Dirigent.<i>,
	 *                               Paul is the <i>Subjekt<i> in both parts - not
	 *                               the <i>Subjekt</i> in one part and the
	 *                               <i>Praedikatsnomen</i> in the other!)
	 */
	private static Collection<ImmutableMap<String, IFeatureValue>> fillFeaturesConsumingAllFillings(
			ImmutableMap<String, IFeatureValue> features,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings,
			final IFillingUsageRestrictor fillingUsageRestrictor) {

		// There could be several different possibilites to fill the slotted features.
		// We start with exactly one of them,
		// and then consume the free fillings, one after the other.
		// (ALL fillings have to be consumed!)

		ImmutableCollection<ImmutableMap<String, IFeatureValue>> alternatives = ImmutableList
				.<ImmutableMap<String, IFeatureValue>>of(features);

		for (final IHomogeneousConstituentAlternatives freeFilling : freeFillings) {
			alternatives = ImmutableList
					.copyOf(fillFeaturesConsumingFilling(alternatives, freeFilling, fillingUsageRestrictor));

			if (alternatives.isEmpty()) {
				// the filling could not be consumed -> no result at all
				return ImmutableList.of(); // ==>
			}
		}

		return filterAlternativesWhereAllFillingsMissingForCompletionCanBeAddedLater(alternatives, fillingUsageRestrictor);
	}

	/**
	 * Takes the free filling and fills it (exactly once) into each of the
	 * alternatives. If in the alternative, there is no matching slotted feature for
	 * the filling, the alternative is skipped (and not included in the result). If
	 * there is more than one possibility for filling the free filling into an
	 * alternative, all possibilities are generated.
	 * <p>
	 * The result is a collection of alternatives, each of which contains the
	 * (formerly) free filling exactly once. If the free filling does not match any
	 * of the slotted features, the result will be empty.
	 *
	 * @param fillingUsageRestrictor the filling might be restricted, so that the
	 *                               <code>freeFilling</code> can only fill a
	 *                               slotted feature with a specified name - this is
	 *                               the restrictor. (This is necessary to ensure
	 *                               that in a ellipse-like sentence like <i>Paul
	 *                               war Komponist und ab 1924 Dirigent.<i>, Paul is
	 *                               the <i>Subjekt<i> in both parts - not the
	 *                               <i>Subjekt</i> in one part and the
	 *                               <i>Praedikatsnomen</i> in the other!)
	 */
	private static ImmutableCollection<ImmutableMap<String, IFeatureValue>> fillFeaturesConsumingFilling(
			final Collection<ImmutableMap<String, IFeatureValue>> alternatives,
			final IHomogeneousConstituentAlternatives freeFilling, final IFillingUsageRestrictor fillingUsageRestrictor) {
		final String onlyAllowedFeatureName = fillingUsageRestrictor.getRestrictedNameFor(freeFilling);

		final ImmutableList.Builder<ImmutableMap<String, IFeatureValue>> res = ImmutableList
				.<ImmutableMap<String, IFeatureValue>>builder();

		// Iterate over all alternatives we already have
		for (final ImmutableMap<String, IFeatureValue> oldFeatures : alternatives) {
			// FIXME Note, that the method
			// fillingUsageRestrictor.getRestrictedNameFor() will not work
			// properly, if there are several features
			// <i>with different names</i>, that contain the filling!
			// Is this a problem? How to prevent this?
			if (onlyAllowedFeatureName != null) {
				final IFeatureValue onlyAllowedFeature = oldFeatures.get(onlyAllowedFeatureName);
				if (onlyAllowedFeature != null) {
					final IFeatureValue filledFeature = addFillingIfAccepted(onlyAllowedFeatureName, onlyAllowedFeature,
							freeFilling, fillingUsageRestrictor);
					if (filledFeature != null) {
						// Filling was accepted -> so we have a new alternative
						res.add(replaceOneFeature(oldFeatures, onlyAllowedFeatureName, filledFeature));
					}
					// else: filling - based on these oldFeatures - is impossible -> try the next
					// alternative
				}
				// else: filling - based on these oldFeatures - is impossible -> try the next
				// alternative
			} else {
				// No Filling restriction -> iterate over all features in this alternative
				for (final Entry<String, IFeatureValue> entry : oldFeatures.entrySet()) {
					final IFeatureValue filledFeature = addFillingIfAccepted(entry.getKey(), entry.getValue(), freeFilling,
							fillingUsageRestrictor);
					if (filledFeature != null) {
						// Filling was accepted -> so we have a new alternative
						res.add(replaceOneFeature(oldFeatures, entry.getKey(), filledFeature));
					}
				}
			}
		}
		return res.build();
	}

	/**
	 * Checks whether this (additional) filling would be acceptable for this
	 * feature. If the filling would be acceptable, the methode returns a copy of
	 * this feature with this filling added. Otherwise, the method returns
	 * <code>null</code>.
	 */
	private static IFeatureValue addFillingIfAccepted(String name, IFeatureValue feature,
			final IHomogeneousConstituentAlternatives freeFilling, final IFillingUsageRestrictor fillingUsageRestrictor) {
		if (!(feature instanceof RoleFrameSlot)) {
			return feature;
		}

		return ((RoleFrameSlot) feature).addFillingIfAccepted(freeFilling,
				fillingUsageRestrictor.keepPlaceFreeForHowManyFillings(name));
	}

	/**
	 * @return only those alternatives, for which all fillings, that are still
	 *         missing for completion, can be added in some later parsing step
	 */
	private static Collection<ImmutableMap<String, IFeatureValue>> filterAlternativesWhereAllFillingsMissingForCompletionCanBeAddedLater(
			final ImmutableCollection<ImmutableMap<String, IFeatureValue>> alternatives,
			final IFillingUsageRestrictor fillingUsageRestrictor) {
		// @formatter:off
		return alternatives.stream().filter(a -> allFillingsMissingForCompletionCanBeAddedLater(a, fillingUsageRestrictor))
				.collect(toImmutableList());
		// @formatter:on
	}

	/**
	 * @return whether for these features all fillings, that are still missing for
	 *         completion, can be added in some later parsing step
	 */
	private static boolean allFillingsMissingForCompletionCanBeAddedLater(
			final ImmutableMap<String, IFeatureValue> features, final IFillingUsageRestrictor fillingUsageRestrictor) {
		for (final Entry<String, IFeatureValue> entry : features.entrySet()) {
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
	private static boolean allFillingsMissingForCompletionCanBeAddedLater(String name, IFeatureValue feature,
			final IFillingUsageRestrictor fillingUsageRestrictor) {
		final int howManyAdditionalFillingsAllowed = fillingUsageRestrictor.howManyAdditionalFillingsAreAllowed(name);

		if (howManyAdditionalFillingsAllowed == -1) {
			return true;
		}

		if (feature instanceof RoleFrameSlot) {
			return ((RoleFrameSlot) feature).howManyFillingsAreMissingUntilCompletion() <= howManyAdditionalFillingsAllowed;
		}
		
		return true;
	}

	/**
	 * Tries to fill the free fillings into the features. If a filling does not fit
	 * into any feature, it is simply left out! Also generates alternatives - in
	 * each alternative, <i>some</i> free fillings may be consumed - others may not.
	 * <p>
	 * The result always contains at least one element: The slots from the input,
	 * left unchanged without any fillings filled in.
	 *
	 * @param fillingUsageRestrictor a free filling might be restricted, so that it
	 *                               can only fill a (slotted) feature with a
	 *                               specified name - this is the restrictor. (This
	 *                               is necessary to ensure that in a ellipse-like
	 *                               sentence like <i>Paul war Komponist und ab 1924
	 *                               Dirigent.<i>, Paul is the <i>Subjekt<i> in both
	 *                               parts - not the <i>Subjekt</i> in one part and
	 *                               the <i>Praedikatsnomen</i> in the other!)
	 */
	private static Collection<ImmutableMap<String, IFeatureValue>> fillFeaturesUsingFillingsOrNotUsingThem(
			final ImmutableMap<String, IFeatureValue> features,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings,
			final IFillingUsageRestrictor fillingUsageRestrictor) {

		// starting without any feature filled
		final Collection<ImmutableMap<String, IFeatureValue>> res = new LinkedList<>();
		res.add(features);

		for (final IHomogeneousConstituentAlternatives freeFilling : freeFillings) {
			// Tries to fill in this free filling into the already-generated
			// alternatives -- and then adds the results to the alternatives.
			res.addAll(fillFeaturesConsumingFilling(res, freeFilling, fillingUsageRestrictor));
		}

		return res;
	}

	public FeatureStructure disjunctUnionWithoutFreeFillings(final FeatureStructure other, ISemantics newSemantics) {
		if (freeFillings.isEmpty() && Objects.equal(surfacePart, other.surfacePart)) {
			if (isEmpty() && newSemantics.equals(other.semantics)) {
				return other;
			}

			if (other.isEmpty() && newSemantics.equals(semantics)) {
				return this;
			}
		}

		if (containsARoleFrameSlotWithAFillingAlsoContainedIn(other)) {
			// this.features and other.features contain
			// the SAME FeatureStructure, you cannot build a union!
			// The problem is: It would be a verbotene Doppelbelegung, wenn
			// dieselbe FeatureStructure in BEIDEN FeatureStructures unter verschiedenen
			// Namen vorkommt!!
			return null; // ==>
		}

		final Builder<String, IFeatureValue> valueBuilder = ImmutableMap.<String, IFeatureValue>builder();

		valueBuilder.putAll(features);
		valueBuilder.putAll(other.features);

		try {
			return fromValues(SurfacePart.join(surfacePart, other.surfacePart), valueBuilder.build(), newSemantics);
			// fails, if duplicate keys were added
		} catch (final IllegalArgumentException e) {
			// Cannot merge role frames: Both share the same slot name.
			return null;
		}
	}

	public boolean containsARoleFrameSlotWithAFillingAlsoContainedIn(FeatureStructure other) {
		for (final IFeatureValue myFeature : features.values()) {
			if (myFeature instanceof RoleFrameSlot) {
				RoleFrameSlot mySlot = (RoleFrameSlot) myFeature;

				for (IFeatureValue othersFeature : other.features.values()) {
					if (othersFeature instanceof RoleFrameSlot) {
						RoleFrameSlot otherSlot = (RoleFrameSlot) othersFeature;

						if (mySlot.containsAFillingAlsoContainedIn(otherSlot)) {
							// this.features and other.features contain
							// the SAME feature value in a slot, you cannot build a union!
							// The problem is: It would be a verbotene Doppelbelegung, wenn
							// dasselbe Feature in RoleFrameSlots in den beiden FeatureStructures unter
							// verschiedenen Namen
							// vorkommt!!
							return true; // ==>
						}
					}
				}
			}
		}

		return false;
	}

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
	 * @param stringFeatures The key - string-value- pairs for the feature
	 *                       structure; an
	 *                       <code>UnspecifiedFeatureValue.UNSPECIFIED_STRING</code>
	 *                       leads to an <code>UnspecifiedFeatureValue</code>.
	 */
	public static FeatureStructure fromStringValues(@Nullable final SurfacePart surfacePart,
			final ImmutableMap<String, String> stringFeatures) {
		// @formatter:off
		return cache.findOrInsert(new FeatureStructure(surfacePart,
				ImmutableMap.copyOf(Maps.transformValues(stringFeatures, stringValue -> toFeatureValue(stringValue)))));
		// @formatter:off
	}

	public static FeatureStructure fromValues(@Nullable final SurfacePart surfacePart, final String key,
			final IFeatureValue value) {
		return fromValues(surfacePart, ImmutableMap.<String, IFeatureValue>of(key, value));
	}

	public static FeatureStructure fromValues(@Nullable final SurfacePart surfacePart, final String key1,
			final IFeatureValue value1, final String key2, final IFeatureValue value2) {
		return fromValues(surfacePart, ImmutableMap.<String, IFeatureValue>of(key1, value1, key2, value2));
	}

	public static FeatureStructure fromValues(@Nullable final SurfacePart surfacePart, final String key1,
			final IFeatureValue value1, final String key2, final IFeatureValue value2, final String key3,
			final IFeatureValue value3) {
		return fromValues(surfacePart, ImmutableMap.<String, IFeatureValue>of(key1, value1, key2, value2, key3, value3));
	}

	public static FeatureStructure fromValues(@Nullable final SurfacePart surfacePart, final String key1,
			final IFeatureValue value1, final String key2, final IFeatureValue value2, final String key3,
			final IFeatureValue value3, final String key4, final IFeatureValue value4) {
		return fromValues(surfacePart,
				ImmutableMap.<String, IFeatureValue>of(key1, value1, key2, value2, key3, value3, key4, value4));
	}

	public static FeatureStructure fromValues(@Nullable final SurfacePart surfacePart,
			final ImmutableMap<String, IFeatureValue> features) {
		return fromValues(surfacePart, features, NothingInParticularSemantics.INSTANCE);
	}

	public static FeatureStructure fromValues(@Nullable final SurfacePart surfacePart,
			final ImmutableMap<String, IFeatureValue> features, ISemantics semantics) {
		return cache.findOrInsert(new FeatureStructure(surfacePart, features, semantics));
	}

	public static FeatureStructure fromValuesAndFreeFillings(@Nullable final SurfacePart surfacePart,
			final ImmutableMap<String, IFeatureValue> features,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		return fromValuesSemanticsAndFreeFillings(surfacePart, features, NothingInParticularSemantics.INSTANCE,
				freeFillings);
	}

	public static FeatureStructure fromValuesSemanticsAndFreeFillings(@Nullable final SurfacePart surfacePart,
			final ImmutableMap<String, IFeatureValue> features, final ISemantics semantics,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		return cache.findOrInsert(new FeatureStructure(surfacePart, features, semantics, freeFillings));
	}

	public static FeatureStructure fromFreeFillings(
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		return fromFreeFillings(joinSurfaceParts(freeFillings), freeFillings);
	}

	public static FeatureStructure fromFreeFillings(@Nullable final SurfacePart surfacePart,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		return cache.findOrInsert(new FeatureStructure(surfacePart, ImmutableMap.<String, IFeatureValue>of(),
				NothingInParticularSemantics.INSTANCE, freeFillings));
	}

	private static SurfacePart joinSurfaceParts(ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		SurfacePart res = null;

		for (IHomogeneousConstituentAlternatives freeFilling : freeFillings) {
			res = SurfacePart.join(res, freeFilling.getSurfacePart());
		}

		return res;
	}

	public FeatureStructure sameValuesFor(final SurfacePart otherSurfacePart) {
		return cache.findOrInsert(new FeatureStructure(otherSurfacePart, features, semantics, freeFillings));
	}

	public FeatureStructure with(ISemantics otherSemantics) {
		return cache.findOrInsert(new FeatureStructure(surfacePart, features, otherSemantics, freeFillings));
	}

	public static FeatureStructure empty(@Nullable final SurfacePart surfacePart) {
		return cache.findOrInsert(new FeatureStructure(surfacePart));
	}

	public FeatureStructure removeNames(final Collection<String> namesToBeRemoved) {
		if (namesToBeRemoved.isEmpty()) {
			return this;
		}

		final ImmutableMap.Builder<String, IFeatureValue> newFeatures = ImmutableMap.builder();

		// @formatter:off
		features.entrySet().stream().filter(featureEntry -> !namesToBeRemoved.contains(featureEntry.getKey()))
				.forEach(featureEntry -> newFeatures.put(featureEntry.getKey(), featureEntry.getValue()));
		// @formatter:on

		return fromValues(surfacePart, newFeatures.build());
	}

	/**
	 * Returns an immutable copy of the map with one feature replaced.
	 */
	private static ImmutableMap<String, IFeatureValue> replaceOneFeature(final Map<String, IFeatureValue> oldFeatures,
			String nameToReplace, final IFeatureValue newFeature) {
		final Builder<String, IFeatureValue> res = ImmutableMap.<String, IFeatureValue>builder();

		for (final Entry<String, IFeatureValue> oldEntry : oldFeatures.entrySet()) {
			final String oldFeatureName = oldEntry.getKey();

			if (oldFeatureName.equals(nameToReplace)) {
				res.put(oldFeatureName, newFeature);
			} else {
				res.put(oldFeatureName, oldEntry.getValue());
			}
		}

		return res.build();
	}

	/**
	 * Returns a new feature structure from this with this feature generalized (from
	 * its original value to JOKER).
	 */
	public FeatureStructure generalizeFeature(final String featureName) {
		final ImmutableMap.Builder<String, IFeatureValue> res = ImmutableMap.builder();

		for (final Entry<String, IFeatureValue> originalEntry : features.entrySet()) {
			if (originalEntry.getKey().equals(featureName)) {
				res.put(featureName, UnspecifiedFeatureValue.INSTANCE);
			} else {
				res.put(originalEntry);
			}
		}

		return fromValues(surfacePart, res.build(), semantics);
	}

	protected boolean containsTheSameRoleFrameSlotFillingInADifferentFeature(final FeatureStructure other) {
		for (final Entry<String, IFeatureValue> myEntry : features.entrySet()) {
			if (myEntry.getValue() instanceof RoleFrameSlot) {
				for (final Entry<String, IFeatureValue> othersEntry : other.features.entrySet()) {
					if (othersEntry.getValue() instanceof RoleFrameSlot) {
						if (myEntry.getKey().equals(othersEntry.getKey())) { // NOPMD by nbudzyn on 29.06.10 21:37
							// all fine!
						} else {
							// feature names are different!
							RoleFrameSlot mySlot = (RoleFrameSlot) myEntry.getValue();
							RoleFrameSlot othersSlot = (RoleFrameSlot) othersEntry.getValue();
							if (mySlot.containsAFillingAlsoContainedIn(othersSlot)) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * @return the name of a feature that contains the <code>filling</code> - or
	 *         <code>null</code>, if there is no such feature. (The method does an
	 *         equality check.)
	 */
	protected String findFeatureNameContaining(final FeatureStructure filling) {
		for (final Entry<String, IFeatureValue> entry : features.entrySet()) {
			if (entry.getValue() instanceof RoleFrameSlot) {
				RoleFrameSlot slot = (RoleFrameSlot) entry.getValue();
				if (slot.containsFilling(filling)) {
				return entry.getKey();
			}
			}
		}

		// no feature contains this FillingInSlot
		return null;
	}

	public IFeatureValue getFeatureValue(final String name) {
		final IFeatureValue res = features.get(name);

		if (res == null) {
			throw new IllegalArgumentException("No feature with name " + name + " in " + this + ".");
		}

		return res;
	}

	public IFeatureValue getFeatureValue(final String name, final IFeatureValue defaultValue) {
		final IFeatureValue res = features.get(name);

		if (res != null) {
			return res;
		}

		return defaultValue;
	}

	public int numberOfFeatures() {
		return features.size();
	}

	public boolean hasFeature(final String featureName) { // NO_UCD
		return this.getFeatureValue(featureName) != null;
	}

	public ISemantics getSemantics() {
		return semantics;
	}

	public boolean isEmpty() {
		return features.isEmpty();
	}

	public void forEach(final BiConsumer<? super String, ? super IFeatureValue> action) {
		features.forEach(action);
	}

	public UnmodifiableIterator<Entry<String, IFeatureValue>> unorderedFeatureIterator() {
		return features.entrySet().iterator();
	}

	public Iterator<String> orderedFeatureNameIterator() {
		return new OrderedFeatureNameIterator(features);
	}

	public Iterator<IHomogeneousConstituentAlternatives> freeFillingIterator() {
		return freeFillings.iterator();
	}

	protected boolean hasFreeFillings() {
		return !freeFillings.isEmpty();
	}

	protected int numberOfFreeFillings() {
		return freeFillings.size();
	}

	public int howManyFillingsAreMissingUntilCompletion(final String name) {
		final IFeatureValue feature = features.get(name);

		if (feature == null) {
			return 0;
		}

		if (feature instanceof RoleFrameSlot) {
			return ((RoleFrameSlot) feature).howManyFillingsAreMissingUntilCompletion();
		}

		return 0;
	}

	/**
	 * @return How many <i>additional</i> fillings are allowed for a feature with
	 *         this name? - <i>-1</i>, if there is <i>no upper bound</i>.
	 */
	int howManyAdditionalFillingsAreAllowed(final String name) {
		final IFeatureValue feature = features.get(name);

		if (feature == null) {
			return -1;
		}
		
		if (feature instanceof RoleFrameSlot) {
			return ((RoleFrameSlot) feature).howManyAdditionalFillingsAreAllowed();
		}

		return -1;
	}

	public boolean noFreeFillingsAndAllSlotsHaveEnoughFillings() {
		if (!freeFillings.isEmpty()) {
			return false;
		}

		for (final IFeatureValue value : features.values()) {
			if (value instanceof RoleFrameSlot) {
				if (!((RoleFrameSlot) value).hasEnoughFillings()) {
				return false;
			}
			}
		}

		return true;
	}

	/**
	 * Returns this feature structure as a {@link IFillingInSlot} - unless the
	 * feature structure has more than one filling in slotted feature
	 */
	@Nullable
	public FeatureStructure toFillingInSlot() {
		SurfacePart resSurfacePart = null;
		final ImmutableMap.Builder<String, IFeatureValue> resFeatures = ImmutableMap.builder();
		boolean semanticsAmbivalent = false;
		ISemantics semantics = NothingInParticularSemantics.INSTANCE;

		for (Entry<String, IFeatureValue> entry : features.entrySet()) {
			if (entry.getValue() instanceof RoleFrameSlot) {
				RoleFrameSlot slot = (RoleFrameSlot) entry.getValue();
				final Collection<FeatureStructure> slotFillings = slot.getFillings();
				if (slotFillings.size() > 1) {
					return null;
				}

				if (!slotFillings.isEmpty()) {
					final FeatureStructure slotFilling = slotFillings.iterator().next();

					resSurfacePart = SurfacePart.join(resSurfacePart, slotFilling.getSurfacePart());

					resFeatures.put(entry.getKey(), slotFilling);

					if (!semanticsAmbivalent) {
						if (semantics == null) {
							semantics = slotFilling.getSemantics();
						} else if (slotFilling.getSemantics() != null && !slotFilling.getSemantics().equals(semantics)) {
							semantics = NothingInParticularSemantics.INSTANCE;
							semanticsAmbivalent = true;
						}
					}
				}
			}
		}

		return FeatureStructure.fromValues(resSurfacePart, resFeatures.build(), semantics);
	}

	@Override
	@Nullable
	public SurfacePart getSurfacePart() {
		return surfacePart;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (hashCode != obj.hashCode()) {
			// Short cut
			return false;
		}

		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}

		final FeatureStructure other = (FeatureStructure) obj;

		if (!surfacePartsEqual(other)) {
			return false;
		}

		if (!features.equals(other.features)) {
			return false;
		}

		if (!freeFillings.equals(other.freeFillings)) {
			return false;
		}

		if (!semantics.equals(other.semantics)) {
			return false;
		}

		return true;
	}

	/**
	 * Returns true, if this edge subsumes the <code>other</code> feature structure.
	 * This means, they are equal, or the other is just a special case of this one.
	 * For example: JOKER would subsume "n", and "JOKER" would as well subsume "nom"
	 * and "gen".
	 * <p>
	 * The surface parts have to be equal - otherwise there is no subsumption.
	 */
	public boolean subsumes(final FeatureStructure other) {
		if (!surfacePartsEqual(other)) {
			return false;
		}

		if (features.size() != other.features.size()) {
			return false;
		}

		return subsumesExcluding(other, ImmutableList.of());
	}

	private boolean surfacePartsEqual(final FeatureStructure other) {
		return Objects.equal(surfacePart, other.surfacePart);
	}

	/**
	 * Returns true, if this edge subsumes the <code>other</code> feature structure,
	 * excluding the given features. This means, the feature structures are equal
	 * (excluding the given features), or the other is just a special case of this
	 * one. For example: JOKER would subsume "n", and "JOKER" would as well subsume
	 * "nom" and "gen".
	 * <p>
	 * The surface parts have to be equal - otherwise there is no subsumption.
	 */
	public boolean subsumesExcluding(final FeatureStructure other, final String... excludedNames) {
		return subsumesExcluding(other, Arrays.asList(excludedNames));
	}

	/**
	 * Returns true, if this edge subsumes the <code>other</code> feature structure,
	 * excluding the given features. This means, the feature structures are equal
	 * (excluding the given features), or the other is just a special case of this
	 * one. For example: JOKER would subsume "n", and "JOKER" would as well subsume
	 * "nom" and "gen".
	 * <p>
	 * The surface parts have to be equal - otherwise there is no subsumption.
	 */
	public boolean subsumesExcluding(final FeatureStructure other, final Collection<String> excludedNames) {
		if (!surfacePartsEqual(other)) {
			return false;
		}

		if (!freeFillings.equals(other.freeFillings)) {
			return false;
		}

		if (!semantics.equals(other.semantics)) {
			return false;
		}

		for (final Entry<String, IFeatureValue> featureEntry : features.entrySet()) {
			if (excludedNames.contains(featureEntry.getKey())) {
				continue;

			}
			final @Nullable IFeatureValue otherFeatureValue = other.features.get(featureEntry.getKey());
			if (otherFeatureValue == null) {
				return false;
			}

			if (!UnspecifiedFeatureValue.subsumes(featureEntry.getValue(), otherFeatureValue)) {
				return false;
			}
		}

		// Check the other way round!
		for (final Entry<String, IFeatureValue> featureEntry : other.features.entrySet()) {
			if (excludedNames.contains(featureEntry.getKey())) {
				continue;
			}

			final @Nullable IFeatureValue oneFeature = features.get(featureEntry.getKey());

			if (oneFeature == null) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns true, if the feature structures are equal, excluding the given
	 * features from the check.
	 */
	public boolean equalsExcluding(final FeatureStructure other, final String... excludedNames) {
		return equalsExcluding(other, Arrays.asList(excludedNames));
	}

	/**
	 * Returns true, if the feature structures are equal, excluding the given
	 * features from the check.
	 */
	public boolean equalsExcluding(final FeatureStructure other, final Collection<String> excludedNames) {
		if (!surfacePartsEqual(other)) {
			return false;
		}

		if (!freeFillings.equals(other.freeFillings)) {
			return false;
		}

		if (!semantics.equals(other.semantics)) {
			return false;
		}

		for (final Entry<String, IFeatureValue> featureEntry : features.entrySet()) {
			if (excludedNames.contains(featureEntry.getKey())) {
				continue;
			}

			final @Nullable IFeatureValue otherFeatureValue = other.features.get(featureEntry.getKey());
			if (otherFeatureValue == null) {
				return false;
			}
			if (!featureEntry.getValue().equals(otherFeatureValue)) {
				return false;
			}
		}

		// Check the other way round!
		for (final Entry<String, IFeatureValue> featureEntry : other.features.entrySet()) {
			if (excludedNames.contains(featureEntry.getKey())) {
				continue;
			}

			final @Nullable IFeatureValue oneFeature = features.get(featureEntry.getKey());

			if (oneFeature == null) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	private final static int calcHashCode(@Nullable final SurfacePart surfacePart,
			final ImmutableMap<String, IFeatureValue> features, final ISemantics semantics,
			ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		final int prime = 31;
		int result = 1;
		result = prime * result + features.hashCode();
		result = prime * result + semantics.hashCode();
		result = prime * result + freeFillings.hashCode();

		// IDEA: Teuer? könnte man per entrySet /iterator auf einige wenige Einträge
		// beschränken!
		// (die HashMap-Sets sind clever!)

		result = prime * result + (surfacePart == null ? 0 : surfacePart.hashCode());
		return result;
	}

	@Override
	public int compareTo(final IFeatureValue o) {
		final int classNameCompared = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final FeatureStructure other = (FeatureStructure) o;

		final int surfacePartsCompared = compareSurfacePart(other.getSurfacePart());
		if (surfacePartsCompared != 0) {
			return surfacePartsCompared;
		}

		final int featuresCompared = CollectionUtil.compareMaps(features, other.features);
		if (featuresCompared != 0) {
			return featuresCompared;
		}

		final int semanticsCompared = semantics.compareTo(other.semantics);
		if (semanticsCompared != 0) {
			return semanticsCompared;
		}

		return CollectionUtil.compareCollections(freeFillings, other.freeFillings);
	}

	private int compareSurfacePart(@Nullable final SurfacePart otherSurfacePart) {
		if (surfacePart == null) {
			if (otherSurfacePart == null) {
				return 0;
			} else {
				return -1;
			}
		}

		// surfacePart != null
		if (otherSurfacePart == null) {
			return 1;
		}

		return surfacePart.compareTo(otherSurfacePart);
	}

	@Override
	public String toString() {
		return toString(true, false);
	}

	@Override
	public String toString(boolean neverShowRequirements, boolean forceShowRequirements) {
		final StringBuilder res = new StringBuilder();

		if (getSurfacePart() != null) {
			res.append("\"");
			res.append(getSurfacePart());
			res.append("\": ");
		}

		res.append("{");

		boolean first = true;

		for (final Iterator<String> featureNameIt = orderedFeatureNameIterator(); featureNameIt.hasNext();) {
			if (first) {
				first = false;
			} else {
				res.append(", ");
			}

			final String name = featureNameIt.next();

			res.append(name);
			res.append("=");
			res.append(features.get(name).toString(neverShowRequirements, forceShowRequirements));
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
	 * Unifies to values, that are supposed to be {@link StringFeatureValue}s or
	 * {@link UnspecifiedFeatureValue}s.
	 * <p>
	 * Examples:
	 * <ul>
	 * <li>"sg" unified with "sg" yields "sg"
	 * <li>"sg" unified with UNSPECIFIED yields "sg"
	 * <li>UNSPECIFIED unified with UNSPECIFIED yields UNSPECIFIED
	 * <li>"sg" unified with "pl" yields null (no result)
	 * </ul>
	 */
	public static @Nullable IFeatureValue unifyStrings(final IFeatureValue first, final IFeatureValue second) {
		if (first.equals(UnspecifiedFeatureValue.INSTANCE)) {
			return second;
		}

		if (second.equals(UnspecifiedFeatureValue.INSTANCE)) {
			return first;
		}

		checkArgument(first instanceof StringFeatureValue);
		checkArgument(second instanceof StringFeatureValue);

		if (first.equals(second)) {
			return first;
		}

		return null;
	}

	/**
	 * @param stringFeatureValueOrMarkerForUnspecified String feature value - or
	 *                                                 UnspecifiedFeatureValue.UNSPECIFIED_STRING
	 *                                                 (as a marker for an
	 *                                                 unspecified feature value).
	 */
	public static IFeatureValue toFeatureValue(final String stringFeatureValueOrMarkerForUnspecified) {
		if (stringFeatureValueOrMarkerForUnspecified.equals(UnspecifiedFeatureValue.UNSPECIFIED_STRING)) {
			return UnspecifiedFeatureValue.INSTANCE;
		}

		return StringFeatureValue.of(stringFeatureValueOrMarkerForUnspecified);
	}

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
	 * @param additionSet all these elements will also be in each result
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

	/**
	 * Used for the {@link FeatureStructure#orderedFeatureNameIterator()} method.
	 */
	private static class OrderedFeatureNameIterator implements Iterator<String> {
		private final Iterator<String> mapIterator;

		private OrderedFeatureNameIterator(final Map<String, IFeatureValue> featureMap) {
			final List<String> featureNamesSorted = Ordering.natural().sortedCopy(featureMap.keySet());

			mapIterator = featureNamesSorted.iterator();
		}

		@Override
		public boolean hasNext() {
			return mapIterator.hasNext();
		}

		@Override
		public String next() {
			return mapIterator.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
