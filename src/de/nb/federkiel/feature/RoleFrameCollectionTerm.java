package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.Variable;
import de.nb.federkiel.logic.YieldsNoResultException;


/**
 * A term that takes several role frame terms and builds a collection from them.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class RoleFrameCollectionTerm implements ITerm<RoleFrameCollection, FeatureAssignment> {
  /**
   * All generated values shall be cached - to minimize memory use. The cache consists of weak
   * references, so it will be cleared automatically, when a value is no longer (strongly)
   * referenced.
   */
  final private static WeakCache<RoleFrameCollectionTerm> cache = new WeakCache<>();

  final private ImmutableSet<ITerm<RoleFrame, FeatureAssignment>> roleFrameTerms;

  /**
   * caching the hashCode
   */
  private final int hashCode;

  private RoleFrameCollectionTerm(final ITerm<RoleFrame, FeatureAssignment>... roleFrameTerms) {
    super();
    this.roleFrameTerms = ImmutableSet.copyOf(roleFrameTerms);
    hashCode = calcHashCode();
  }

  private RoleFrameCollectionTerm(
      final ImmutableSet<ITerm<RoleFrame, FeatureAssignment>> roleFrameTerms) {
    super();
    this.roleFrameTerms = roleFrameTerms;
    hashCode = calcHashCode();
  }

  public static RoleFrameCollectionTerm of(
      final ImmutableSet<ITerm<RoleFrame, FeatureAssignment>> roleFrameTerms) {
    return cache.findOrInsert(new RoleFrameCollectionTerm(roleFrameTerms));
  }

  /**
   * Builds the union of the two role frame collection terms.
   */
  public RoleFrameCollectionTerm union(final RoleFrameCollectionTerm other) {
    return of(
        ImmutableSet.<ITerm<RoleFrame, FeatureAssignment>>builder().addAll(roleFrameTerms).addAll(
            other.roleFrameTerms).build());
  }

  /**
   * @return The result is a role frame collection.
   */
  @Override
  public RoleFrameCollection evaluate(final FeatureAssignment variableAssignment)
      throws UnassignedVariableException, YieldsNoResultException {
    final ImmutableSet.Builder<RoleFrame> roleFrameValues = ImmutableSet.<RoleFrame>builder();
    for (final ITerm<RoleFrame, FeatureAssignment> roleFrameTerm : roleFrameTerms) {
      roleFrameValues.add(roleFrameTerm.evaluate(variableAssignment)); // UnassignedVariableException,
                                                                       // YieldsNoResultException
    }

    return RoleFrameCollection.of(roleFrameValues.build());
  }

  @Override
  public ImmutableSet<Variable<?, FeatureAssignment>> getAllVariables() {
    // @formatter:off
    return roleFrameTerms.stream()
        .flatMap(roleFrameTerm -> roleFrameTerm.getAllVariables().stream())
        .collect(ImmutableSet.toImmutableSet());
    // @formatter:on
  }

  private final int calcHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + roleFrameTerms.hashCode();
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
    final RoleFrameCollectionTerm other = (RoleFrameCollectionTerm) obj;

    if (hashCode != other.hashCode) {
      return false;
    }

    if (!roleFrameTerms.equals(other.roleFrameTerms)) {
      return false;
    }

    return true;
  }

  @Override
  public int compareTo(final ITerm<? extends Object, ? extends IAssignment> o) {
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    final RoleFrameCollectionTerm other = (RoleFrameCollectionTerm) o;

    return CollectionUtil.compareCollections(roleFrameTerms, other.roleFrameTerms);
  }


  @Override
  public String toString() {
    return this.toString(false);
  }

  @Override
  public String toString(final boolean surroundWithBracketsIfApplicable) {
    // never show brackets!
    final StringBuilder res = new StringBuilder();

    res.append("[");

    boolean first = true;
    for (final ITerm<RoleFrame, FeatureAssignment> roleFrameTerm : roleFrameTerms) {
      if (first) {
        first = false;
      } else {
        res.append(", ");
      }
      res.append(roleFrameTerm.toString(true));
    }

    res.append("]");

    return res.toString();
  }
}
