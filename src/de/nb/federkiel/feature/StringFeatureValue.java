package de.nb.federkiel.feature;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.interfaces.IFeatureValue;


/**
 * A String feature value
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class StringFeatureValue implements IFeatureValue {
  /**
   * All generated String feature values shall be cached - to minimize memory use. The cache
   * consists of weak references, so it will be cleared automatically, when a value is no longer
   * (strongly) referenced.
   */
  final private static WeakCache<StringFeatureValue> cache = new WeakCache<>();

  public static final StringFeatureValue J = of(StringFeatureLogicUtil.TRUE);
  public static final StringFeatureValue N = of(StringFeatureLogicUtil.FALSE);

  private final String string;

  /**
   * Do not forget to cache all created values!
   */
  private StringFeatureValue(final String string) {
    super();

    if (string == null) {
      throw new IllegalArgumentException("String for string feature " + "was null - not allowed.");
    }

    this.string = string;
  }

  public static StringFeatureValue of(final boolean b) {
    return b ? J : N;
  }

  public static StringFeatureValue of(final String string) {
    return cache.findOrInsert(new StringFeatureValue(string));
  }

	@Override
	public IFeatureValue addFillingIfAccepted(IHomogeneousConstituentAlternatives freeFilling,
			int keepPlaceFreeForHowManyFillings) {
		return null;
	}

	@Override
	public int howManyFillingsAreMissingUntilCompletion() {
		return 0;
	}

	@Override
	public int howManyAdditionalFillingsAreAllowed() {
		return 0;
	}

  /**
   * @return <code>true</code>, iff all slots are satisfied and there are no free fillings. String
   *         features are always completed (even if there value were empty!)
   */
  @Override
  public boolean isCompleted() {
    return true;
  }

	@Override
	public boolean containsAFillingInASlotEqualTo(IFeatureValue other) {
		return false;
	}

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + string.hashCode();
    return result;
  }

  /*
   * public ParseAlternativesDifference
   * findSinglePointOfDifferenceForRoleFrameCollFeaturesAndCheckAtomicFeatures( final IFeatureValue
   * otherValue) throws TooManyDifferencesException { if (! (otherValue instanceof
   * StringFeatureValue)) { throw new TooManyDifferencesException(
   * "Values not equal: Other value is no string feature value!"); }
   *
   * final StringFeatureValue otherStringFeatureValue = (StringFeatureValue) otherValue;
   *
   * if (!this.string.equals(otherStringFeatureValue.string)) { throw new
   * TooManyDifferencesException( "Values not equal: Other string feature value is different!"); }
   *
   * return null; }
   */

	@Override
	public Collection<FillingInSlot> getFillings() {
		return ImmutableList.of();
	}

  public String getString() {
    return string;
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
    final StringFeatureValue other = (StringFeatureValue) obj;
    if (!string.equals(other.string)) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(final IFeatureValue o) {
    // This method shall be consistent with equals().
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    final StringFeatureValue other = (StringFeatureValue) o;

    return string.compareTo(other.string);
  }

	@Override
	public String toString() {
		return toString(true, false);
	}

  @Override
	public String toString(boolean neverShowRequirements, boolean forceShowRequirements) {
    return "\"" + string.toString() + "\"";
  }

}
