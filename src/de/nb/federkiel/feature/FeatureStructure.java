package de.nb.federkiel.feature;

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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

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
   * Die (grammatischen) Merkmale, jeweils mit Name und Wert, die für diese Wortform spezifisch sind
   * (also über die Features des <i>Lexems</i> hinausgehen)
   * <p>
   * Die features des parents stehen hier NICHT nochmal (außer sie würden überschrieben), sondern
   * sie gelten implizit!
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

    final Builder<String, IFeatureValue> builder = ImmutableMap.<String, IFeatureValue>builder();

    final FeatureStructure oneFeatureStructure = one;
    final FeatureStructure otherFeatureStructure = other;

    builder.putAll(oneFeatureStructure.features);
    builder.putAll(otherFeatureStructure.features);

    return fromValues(builder.build());
  }


  /**
   * @param stringFeatures The key - string-value- pairs for the feature structure; an
   *        <code>UnspecifiedFeatureValue.UNSPECIFIED_STRING</code> leads to an
   *        <code>UnspecifiedFeatureValue</code>.
   * @return
   */
  public static FeatureStructure fromStringValues(
      final ImmutableMap<String, String> stringFeatures) {
    return cache.findOrInsert(new FeatureStructure(ImmutableMap.copyOf(
        Maps.transformValues(stringFeatures, stringValue -> toFeatureValue(stringValue)))));
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

    final ImmutableMap.Builder<String, IFeatureValue> res = ImmutableMap.builder();

    // @formatter:off
    features.entrySet().stream()
      .filter(featureEntry -> !namesToBeRemoved.contains(featureEntry.getKey()))
      .forEach(featureEntry -> res.put(featureEntry.getKey(), featureEntry.getValue()));
    // @formatter:on
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

  public int numberOfFeatures() { // NO_UCD
    return features.size();
  }

  public boolean contains(final String featureName) { // NO_UCD
    return this.getFeatureValue(featureName) != null;
  }

  public boolean isEmpty() {
    final boolean res = features.isEmpty();

    return res;
  }

  public void forEach(final BiConsumer<? super String, ? super IFeatureValue> action) {
    features.forEach(action);
  }

  public Iterator<String> featureNameIterator() {
    return new FeatureNameIterator(features);
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

    for (final Entry<String, IFeatureValue> featureEntry : features.entrySet()) {
      @Nullable
      final IFeatureValue otherFeature = other.features.get(featureEntry.getKey());
      if (otherFeature == null) {
        return false;
      }

      if (!featureEntry.getValue().equals(otherFeature)
          && !featureEntry.getValue().equals(UnspecifiedFeatureValue.INSTANCE)) {
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
    return features.hashCode();
    // IDEA: Teuer? könnte man per entrySet /iterator auf einige wenige Einträge beschränken!
    // (die HashMap-Sets sind clever!)
  }

  @Override
  public int compareTo(final FeatureStructure o) {
    return CollectionUtil.compareMaps(features, o.features);
  }

  @Override
  public String toString() {
    final StringBuilder res = new StringBuilder();

    boolean first = true;

    for (final Iterator<String> featureNameIt = featureNameIterator(); featureNameIt.hasNext();) {
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
   * Used for the {@link FeatureStructure#featureNameIterator()} method.
   */
  private static class FeatureNameIterator implements Iterator<String> {
    private final Iterator<String> mapIterator;

    private FeatureNameIterator(final Map<String, IFeatureValue> featureMap) {
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
