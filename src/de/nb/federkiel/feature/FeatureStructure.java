package de.nb.federkiel.feature;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.UnmodifiableIterator;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.interfaces.IFeatureValue;

/**
 * A structure of (grammatical) features. Features could be:
 * <ol>
 * <li>Strings
 * <li>Role frame collections
 * </ol>
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class FeatureStructure implements Comparable<FeatureStructure> {
  /**
   * All generated feature structures shall be cached - to minimize memory use. The cache consists
   * of weak references, so it will be cleared automatically, when a feature structure is no longer
   * (strongly) referenced.
   */
  final private static WeakCache<FeatureStructure> cache = new WeakCache<>();

  final public static FeatureStructure EMPTY = cache.findOrInsert(new FeatureStructure());

  /*
   * FeatureStructures could be organized in hierarchies: Each feature structure could have a
   * parent. It could inherits the parents' values, but also could override them.
   *
   * private final FeatureStructure parent;
   */

  /**
   * Die (grammatischen) Merkmale, jeweils mit Name und Wert
   */
  private final ImmutableMap<String, IFeatureValue> features;

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
        "Not a String feature - and no UnspecifiedFeatureValue either: "
            + booleanStringOrUnpecifiedFeatureValue);

    final StringFeatureValue booleanStringFeatureValue =
        (StringFeatureValue) booleanStringOrUnpecifiedFeatureValue;
    return StringFeatureLogicUtil.stringToBoolean(booleanStringFeatureValue.getString());
  }

  /**
   * Checks two feature values for equality, also recognizing STRING_FEATURE_UNSPECIFIED values -
   * these are equal to everything!
   * <p>
   * USE THIS METHOD WHENEVER YOU COMPARE FEATURE VALUES!
   */
  protected static boolean doFeatureValuesMatch(final IFeatureValue oneValue,
      final IFeatureValue otherValue) {
    if (oneValue.equals(UnspecifiedFeatureValue.INSTANCE)
        || otherValue.equals(UnspecifiedFeatureValue.INSTANCE)) {
      return true;
    }

    return oneValue.equals(otherValue);
  }

  /**
   * When calling this, always use <code>cache.findOrInsert(...)</code>, to minimize memory use!
   */
  private FeatureStructure() {
    this(ImmutableMap.<String, IFeatureValue>of());
  }

  /**
   * When calling this, always use <code>cache.findOrInsert(...)</code>, to minimize memory use!
   */
  private FeatureStructure(final ImmutableMap<String, IFeatureValue> features) {
    this.features = features;

    hashCode = calcHashCode(this.features);
  }

  /**
   * @param a feature provider
   * @param another feature provider - the features must NOT overlap!
   */
  public static FeatureStructure disjunctUnion(final FeatureStructure one,
      final FeatureStructure other) {
    if (one.isEmpty()) {
      return other;
    }

    if (other.isEmpty()) {
      return one;
    }

    final FeatureStructure oneFeatureStructure = one;
    final FeatureStructure otherFeatureStructure = other;

    final Builder<String, IFeatureValue> valueBuilder =
        ImmutableMap.<String, IFeatureValue>builder();

    valueBuilder.putAll(oneFeatureStructure.features);
    valueBuilder.putAll(otherFeatureStructure.features);

    return fromValues(valueBuilder.build());
  }


  /**
   * @param stringFeatures The key - string-value- pairs for the feature structure; an
   *        <code>UnspecifiedFeatureValue.UNSPECIFIED_STRING</code> leads to an
   *        <code>UnspecifiedFeatureValue</code>.
   */
  public static FeatureStructure fromStringValues(
      final ImmutableMap<String, String> stringFeatures) {
    // @formatter:off
    return cache.findOrInsert(new FeatureStructure(
        ImmutableMap.copyOf(
            Maps.transformValues(stringFeatures, stringValue -> toFeatureValue(stringValue)))));
    // @formatter:off
  }

  public static FeatureStructure fromValues(final String key, final IFeatureValue value) {
    return fromValues(ImmutableMap.<String, IFeatureValue>of(key, value));
  }

  public static FeatureStructure fromValues(final String key1, final IFeatureValue value1,
      final String key2, final IFeatureValue value2) {
    return fromValues(ImmutableMap.<String, IFeatureValue>of(key1, value1, key2, value2));
  }

  public static FeatureStructure fromValues(final String key1, final IFeatureValue value1,
      final String key2, final IFeatureValue value2, final String key3,
      final IFeatureValue value3) {
    return fromValues(
        ImmutableMap.<String, IFeatureValue>of(key1, value1, key2, value2, key3, value3));
  }

  public static FeatureStructure fromValues(final String key1, final IFeatureValue value1,
      final String key2, final IFeatureValue value2, final String key3, final IFeatureValue value3,
      final String key4, final IFeatureValue value4) {
    return fromValues(ImmutableMap.<String, IFeatureValue>of(key1, value1, key2, value2, key3,
        value3, key4, value4));
  }

  public static FeatureStructure fromValues(final ImmutableMap<String, IFeatureValue> features) {
    return cache.findOrInsert(new FeatureStructure(features));
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

    return fromValues(newFeatures.build());
  }

  /**
   * Returns a new feature structure from with this feature generalized (from its original value to
   * JOKER).
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

    return fromValues(res.build());
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

  /**
   * For some role frame types, this method takes <i>several</i> role frames per type, and it
   * generates all combination of them as role frame structures. Each role frame structure will
   * contain exactly one role frame per type.
   * <p>
   * So, if this method gets 2 types, with 3 role frames per type, it will return 2*3=6 role frame
   * structures, each of which contains exactly one role frame per type.
   *
   * public static final Collection<RoleFeatureStructure> buildCombinations ( final
   * Map<RoleFrameType, Collection<RoleFrame>> roleFramesToCombineByType) { // TEST!!!
   *
   * Collection<RoleFeatureStructure> res = new LinkedList<RoleFeatureStructure>();
   * res.add(RoleFeatureStructure.EMPTY); // starting with only ONE role frame structure, that is
   * empty
   *
   * for (final Map.Entry<RoleFrameType, Collection<RoleFrame>> mapEntry :
   * roleFramesToCombineByType.entrySet()) { final Collection<RoleFeatureStructure>
   * roleFrameStructureAlternativesWithoutThisType = res;
   *
   * // iterate through all role frame structures, that have already been // generated and, for each
   * of them, combine them with each // of the alternatives for this type
   *
   * final Collection<RoleFeatureStructure> roleFrameStructureAlternativesWithThisType = new
   * LinkedList<RoleFeatureStructure>();
   *
   * for (final RoleFeatureStructure roleFrameStructureWithoutThisType :
   * roleFrameStructureAlternativesWithoutThisType) { for (final RoleFrame roleFrameAlternative :
   * mapEntry.getValue()) { final RoleFeatureStructure roleFrameStructureAlternativeWithThisType =
   * new RoleFeatureStructure (); // copy... for (final Map.Entry<RoleFrameType, RoleFrame>
   * roleFrameMapEntry : roleFrameStructureWithoutThisType.byType.entrySet()) {
   * roleFrameStructureAlternativeWithThisType.byType.put( roleFrameMapEntry.getKey(),
   * roleFrameMapEntry.getValue()); } // add new role frame for this type:
   * roleFrameStructureAlternativeWithThisType.byType.put( mapEntry.getKey(), roleFrameAlternative);
   *
   * roleFrameStructureAlternativesWithThisType.add(roleFrameStructureAlternativeWithThisType); } }
   *
   * res = roleFrameStructureAlternativesWithThisType; }
   *
   * return res; }
   */

  /**
   * Creates a new <code>RoleFeatureStructure</code> by merging the given role frame structures.
   *
   * @param roleFrameStructures may not contain more than one role frame for each role frame types
   *        (must have disjoint role frame types)!
   *        <p>
   *        Must be {@link RoleFeatureStructure}s.
   *
   *        public static final RoleFeatureStructure merge ( final Iterable<RoleFeatureStructure>
   *        roleFrameStructures) { final Map<RoleFrameType, RoleFrame> roleFrameMap = new
   *        HashMap<RoleFrameType, RoleFrame>();
   *
   *        for (final RoleFeatureStructure roleFrameStructure : roleFrameStructures) { for (final
   *        Map.Entry<RoleFrameType, RoleFrame> entry : roleFrameStructure.byType.entrySet()) {
   *        final boolean multipleRoleFramesOfSameType = roleFrameMap.put(entry.getKey(),
   *        entry.getValue()) != null;
   *
   *        if (multipleRoleFramesOfSameType) { throw new IllegalArgumentException ("Multiple role
   *        frames of type " + entry.getKey() + " role frame Structures " + roleFrameStructures); }
   *        } }
   *
   *        return new RoleFeatureStructure(roleFrameMap.values().toArray(new RoleFrame[] {})); }
   */

  public boolean areAllFeaturesCompleted() {
    for (final IFeatureValue value : features.values()) {
      if (!value.isCompleted()) {
        return false;
      }
    }

    return true;
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

    if (!features.equals(other.features)) {
      return false;
    }

    return features.equals(other.features);
  }

  /**
   * Returns true, if this edge subsumes the <code>other</code> feature structure. This means, they
   * are equal, or the other is just a special case of this one. For example: JOKER would subsume
   * "n", and "JOKER" would as well subsume "nom" and "gen".
   */
  public boolean subsumes(final FeatureStructure other) {
    if (features.size() != other.features.size()) {
      return false;
    }

    return subsumesExcluding(other, ImmutableList.of());
  }

  /**
   * Returns true, if this edge subsumes the <code>other</code> feature structure, excluding the
   * given features. This means, the feature structures are equal (excluding the given features), or
   * the other is just a special case of this one. For example: JOKER would subsume "n", and "JOKER"
   * would as well subsume "nom" and "gen".
   */
  public boolean subsumesExcluding(final FeatureStructure other, final String... excludedNames) {
    return subsumesExcluding(other, Arrays.asList(excludedNames));
  }

  /**
   * Returns true, if this edge subsumes the <code>other</code> feature structure, excluding the
   * given features. This means, the feature structures are equal (excluding the given features), or
   * the other is just a special case of this one. For example: JOKER would subsume "n", and "JOKER"
   * would as well subsume "nom" and "gen".
   */
  public boolean subsumesExcluding(final FeatureStructure other,
      final Collection<String> excludedNames) {

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
   * Returns true, if the feature structures are equal, excluding the given features from the check.
   */
  public boolean equalsExcluding(final FeatureStructure other, final String... excludedNames) {
    return equalsExcluding(other, Arrays.asList(excludedNames));
  }

  /**
   * Returns true, if the feature structures are equal, excluding the given features from the check.
   */
  public boolean equalsExcluding(final FeatureStructure other,
      final Collection<String> excludedNames) {
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

  private final static int calcHashCode(final ImmutableMap<String, IFeatureValue> features) {
    return features.hashCode() * 32;
    // IDEA: Teuer? könnte man per entrySet /iterator auf einige wenige Einträge beschränken!
    // (die HashMap-Sets sind clever!)
    // Feature structure type nicht berücksichtigen. Wenn die features gleich sind, wird in den
    // allermeisten
    // Fällen der feature structure type gleich sein.
  }

  @Override
  public int compareTo(final FeatureStructure o) {
    return CollectionUtil.compareMaps(features, o.features);
  }

  @Override
  public String toString() {
    final StringBuilder res = new StringBuilder();

    boolean first = true;

    for (final Iterator<String> featureNameIt =
        orderedFeatureNameIterator(); featureNameIt.hasNext();) {
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
  public static @Nullable IFeatureValue unifyStrings(final IFeatureValue first,
      final IFeatureValue second) {
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
   *        UnspecifiedFeatureValue.UNSPECIFIED_STRING (as a marker for an unspecified feature
   *        value).
   */
  public static IFeatureValue toFeatureValue(
      final String stringFeatureValueOrMarkerForUnspecified) {
    if (stringFeatureValueOrMarkerForUnspecified.equals(
        UnspecifiedFeatureValue.UNSPECIFIED_STRING)) {
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

  /**
   * Tries to find <i>the single point of difference<i> between the elements, that are referenced by
   * the role frame collection features of <code>this</code> and the otherFeatures - it will also be
   * fine, if there is no essential difference - and also checks the atomic features for equality.
   * <p>
   * (This method is used to check wether features and semantics are sufficiently equal to think
   * about inserting information from the <code>otherEdge</code> into <code>this</code>, instead of
   * adding the other edge to the chart.)
   *
   * @return the <i>single point of difference<i> between the elements, that are referenced by the
   *         role frame collection features of <code>this</code> and the otherFeatures - or
   *         <code>null</code>, if there was no SINGLE difference found
   * @throws TooManyDifferencesException if there is more than one &quot;point if difference&quot;
   *         between the <code>this</code> and the <code>otherFeatures</code> (for the role frame
   *         collection features) -- or if the <i>atomic</i> are NOT equal.
   *
   *         public ParseAlternativesDifference
   *         findSinglePointOfDifferenceForRoleFrameCollFeaturesAndCheckAtomicFeatures ( final
   *         FeatureStructure otherFeatures) throws TooManyDifferencesException {
   *         ParseAlternativesDifference singleDiff = null; // no difference initially
   *
   *         for (final Map.Entry<String, IFeatureValue> entry : this.features.entrySet()) { final
   *         IFeatureValue otherValue = otherFeatures.getFeatureValue(entry.getKey(), null); if
   *         (otherValue == null) { throw new TooManyDifferencesException( "Features not equal:
   *         Feature missing in other features structure!" ); }
   *
   *         singleDiff = ParseAlternativesDifference.join( singleDiff, entry.getValue().
   *         findSinglePointOfDifferenceForRoleFrameCollFeaturesAndCheckAtomicFeatures (
   *         otherValue)); // TooManyDifferencesException }
   *
   *         return singleDiff; }
   */
}
