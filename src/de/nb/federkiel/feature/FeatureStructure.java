package de.nb.federkiel.feature;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A structure for (grammatical) features (for the genus, the subject,
 * objects, e.g.).
 * <p>
 * There can also be <i>Free Fillings</i>, that could be used to 
 * fill (not-yet existing) empty feature slots.
 * Each free filling is a set of realization alternatives.
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
		this(surfacePart, features, ImmutableSet.of());
	}

	/**
	 * When calling this, always use <code>cache.findOrInsert(...)</code>, to
	 * minimize memory use!
	 */
	private FeatureStructure(@Nullable final SurfacePart surfacePart, final ImmutableMap<String, IFeatureValue> features,
			ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		// There can only be free fillings, if there are NO features at all!
		if (!features.isEmpty()) {
			if (!freeFillings.isEmpty()) {
				throw new IllegalArgumentException("There can only be free fillings, if there are NO features at all! "
						+ "Features: " + features + ", free fillings: " + freeFillings);
			}
		}

		this.surfacePart = surfacePart;
		this.features = features;
		this.freeFillings = freeFillings;

		hashCode = calcHashCode(this.surfacePart, this.features, this.freeFillings);
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
	Plurival<FeatureStructure> merge(final FeatureStructure other,
			final IFillingUsageRestrictor fillingUsageRestrictor)
			throws IllegalArgumentException {
		if (features.isEmpty() && other.features.isEmpty()) {
			// Case 1: this: no features (maybe some free fillings),
			// other: no features (maybe more fillings)

			// No features at all --> all free fillings stay unconsumed!

			return Plurival.of(FeatureStructure.fromFreeFillings(
					SurfacePart.join(surfacePart, other.surfacePart),
					mergeFreeFillings(freeFillings, other.freeFillings))); // ==>
		}

		if (features.isEmpty() && !other.features.isEmpty()) {
			// Case 2: this: no features (maybe some free fillings),
			// other: some features (no free fillings)!

			return buildFeatureStructurePlurivalFromAlternatives(
					SurfacePart.join(surfacePart, other.surfacePart),
					fillFeaturesConsumingAllFillings(other.features, freeFillings, fillingUsageRestrictor));
			// the results have features (all my free fillings are used to fill them),
			// but the results don't have free fillings
		}

		if (!features.isEmpty() && other.features.isEmpty()) {
			// Case 3: this: some features (no free fillings),
			// other: no features (maybe some free fillings)!

			return buildFeatureStructurePlurivalFromAlternatives(
					SurfacePart.join(surfacePart, other.surfacePart),
					fillFeaturesConsumingAllFillings(features, other.freeFillings, fillingUsageRestrictor));
			// the results have features (all of the other's free fillings are used to fill
			// them),
			// but the results have no free fillings
		}

		// ELSE:
		// Case 4: this: some features (no free fillings)!
		// other: some features (no free fillings)

		final FeatureStructure featureUnion = disjunctUnionWithoutFreeFillings(other);
		if (featureUnion == null) {
			// (there might habe been the same filling in both features, or the same feature
			// name...
			return Plurival.empty();
		}

		return Plurival.of(featureUnion);
	}

	/**
	 * Builds a FeatureStructure Plurival from the alternatives
	 */
	private static Plurival<FeatureStructure> buildFeatureStructurePlurivalFromAlternatives(SurfacePart surfacePart,
			final Collection<ImmutableMap<String, IFeatureValue>> alternatives) {
		// @formatter:off
	  return Plurival.of(
	      alternatives.stream()
						.map(features -> FeatureStructure.fromValues(surfacePart, features))
	        .collect(toImmutableList())
	      );
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

		return filterAlternativesWhereAllFillingsMissingForCompletionCanBeAddedLater(alternatives,
				fillingUsageRestrictor);
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
			final IHomogeneousConstituentAlternatives freeFilling,
			final IFillingUsageRestrictor fillingUsageRestrictor) {
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
					final IFeatureValue filledFeature = addFillingIfAccepted(onlyAllowedFeatureName, onlyAllowedFeature, freeFilling,
							fillingUsageRestrictor);
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
		return feature.addFillingIfAccepted(freeFilling, fillingUsageRestrictor.keepPlaceFreeForHowManyFillings(name));
	}

	/**
	 * @return only those alternatives, for which all fillings, that are still
	 *         missing for completion, can be added in some later parsing step
	 */
	private static Collection<ImmutableMap<String, IFeatureValue>> filterAlternativesWhereAllFillingsMissingForCompletionCanBeAddedLater(
			final ImmutableCollection<ImmutableMap<String, IFeatureValue>> alternatives,
			final IFillingUsageRestrictor fillingUsageRestrictor) {
		// @formatter:off
		return alternatives.stream()
				.filter(
						a -> allFillingsMissingForCompletionCanBeAddedLater(a, fillingUsageRestrictor))
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

		return feature.howManyFillingsAreMissingUntilCompletion() <= howManyAdditionalFillingsAllowed;
	}

	public FeatureStructure disjunctUnionWithoutFreeFillings(final FeatureStructure other) {
		if (freeFillings.isEmpty() && Objects.equal(surfacePart, other.surfacePart)) {
			if (isEmpty()) {
				return other;
			}

			if (other.isEmpty()) {
				return this;
			}
		}

		if (hasOneEqualFillingInSlotAs(other)) {
			// this.features and other.features contain
			// the SAME FillingInSlot, you cannot build a union!
			// The problem is: It would be a verbotene Doppelbelegung, wenn
			// dasselbe FillingInSlot in BEIDEN FeatureStructures unter verschiedenen Namen
			// vorkommt!!
			return null; // ==>
		}

		final Builder<String, IFeatureValue> valueBuilder = ImmutableMap.<String, IFeatureValue>builder();

		valueBuilder.putAll(features);
		valueBuilder.putAll(other.features);

		try {
			return fromValues(SurfacePart.join(surfacePart, other.surfacePart), valueBuilder.build());
			// fails, if duplicate keys were added
		} catch (final IllegalArgumentException e) {
			// Cannot merge role frames: Both share the same slot name.
			return null;
		}
	}

	@Override
	public boolean hasOneEqualFillingInSlotAs(IFeatureValue other) {
		for (final IFeatureValue someFeature : features.values()) {
			if (other.hasOneEqualFillingInSlotAs(someFeature)) {
				// this.features and other.features contain
				// the SAME FillingInSlot, you cannot build a union!
				// The problem is: It would be a verbotene Doppelbelegung, wenn
				// dasselbe FillingInSlot in BEIDEN FeatureStructures unter verschiedenen Namen
				// vorkommt!!
				return true; // ==>
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
	 * @param stringFeatures
	 *          The key - string-value- pairs for the feature structure; an
	 *          <code>UnspecifiedFeatureValue.UNSPECIFIED_STRING</code> leads to an
	 *          <code>UnspecifiedFeatureValue</code>.
	 */
	public static FeatureStructure fromStringValues(@Nullable final SurfacePart surfacePart,
			final ImmutableMap<String, String> stringFeatures) {
		// @formatter:off
    return cache.findOrInsert(new FeatureStructure(
    		surfacePart,
        ImmutableMap.copyOf(
            Maps.transformValues(stringFeatures, stringValue -> toFeatureValue(stringValue)))));
    // @formatter:off
  }

  public static FeatureStructure fromValues(
  		@Nullable final SurfacePart surfacePart,
  		final String key, final IFeatureValue value) {
    return fromValues(surfacePart, ImmutableMap.<String, IFeatureValue>of(key, value));
  }

  public static FeatureStructure fromValues(
  		@Nullable final SurfacePart surfacePart,
  		final String key1, final IFeatureValue value1,
      final String key2, final IFeatureValue value2) {
    return fromValues(surfacePart, ImmutableMap.<String, IFeatureValue>of(key1, value1, key2, value2));
  }

  public static FeatureStructure fromValues(
  		@Nullable final SurfacePart surfacePart,
  		final String key1, final IFeatureValue value1,
      final String key2, final IFeatureValue value2, final String key3,
      final IFeatureValue value3) {
    return fromValues(
    		surfacePart,
        ImmutableMap.<String, IFeatureValue>of(key1, value1, key2, value2, key3, value3));
  }

  public static FeatureStructure fromValues(
  		@Nullable final SurfacePart surfacePart,
  		final String key1, final IFeatureValue value1,
      final String key2, final IFeatureValue value2, final String key3, final IFeatureValue value3,
      final String key4, final IFeatureValue value4) {
    return fromValues(
    		surfacePart,
    		ImmutableMap.<String, IFeatureValue>of(key1, value1, key2, value2, key3,
        value3, key4, value4));
  }

  public static FeatureStructure fromValues(@Nullable final SurfacePart surfacePart, final ImmutableMap<String, IFeatureValue> features) {
    return cache.findOrInsert(new FeatureStructure(surfacePart, features));
  }

	public static FeatureStructure fromFreeFillings(
			@Nullable final SurfacePart surfacePart,
			final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		return cache
				.findOrInsert(new FeatureStructure(surfacePart, ImmutableMap.<String, IFeatureValue>of(), freeFillings));
	}

	public FeatureStructure sameValuesFor(final SurfacePart otherSurfacePart) {
		return cache.findOrInsert(new FeatureStructure(otherSurfacePart, features));
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
    features.entrySet().stream()
      .filter(featureEntry -> !namesToBeRemoved.contains(featureEntry.getKey()))
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

		return fromValues(surfacePart, res.build());
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

	public boolean contains(final String featureName) { // NO_UCD
		return this.getFeatureValue(featureName) != null;
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

	@Override
	public int howManyFillingsAreMissingUntilCompletion() {
		int res = 0;

		for (final IFeatureValue value : features.values()) {
			res += value.howManyFillingsAreMissingUntilCompletion();
		}

		return res;
	}

	/**
	 * Whether this feature value is completed. In this case, all features of this
	 * feature structure have to be completed.
	 */
	@Override
	public boolean isCompleted() {
		for (final IFeatureValue value : features.values()) {
			if (!value.isCompleted()) {
				return false;
			}
		}

		return true;
	}

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

		return freeFillings.equals(other.freeFillings);
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
			final ImmutableMap<String, IFeatureValue> features,
			ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings) {
		final int prime = 31;
		int result = 1;
		result = prime * result + features.hashCode();
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
		final StringBuilder res = new StringBuilder();

		if (getSurfacePart() != null) {
			res.append("\"");
			res.append(getSurfacePart());
			res.append("\": ");
		}

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
			res.append(features.get(name));
		}

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

	@Override
	public IFeatureValue addFillingIfAccepted(IHomogeneousConstituentAlternatives freeFilling,
			int keepPlaceFreeForHowManyFillings) {
		return null;
	}

	/**
	 * @param stringFeatureValueOrMarkerForUnspecified
	 *          String feature value - or UnspecifiedFeatureValue.UNSPECIFIED_STRING
	 *          (as a marker for an unspecified feature value).
	 */
	public static IFeatureValue toFeatureValue(final String stringFeatureValueOrMarkerForUnspecified) {
		if (stringFeatureValueOrMarkerForUnspecified.equals(UnspecifiedFeatureValue.UNSPECIFIED_STRING)) {
			return UnspecifiedFeatureValue.INSTANCE;
		}

		return StringFeatureValue.of(stringFeatureValueOrMarkerForUnspecified);
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
