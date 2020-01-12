package de.nb.federkiel.feature;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.interfaces.IFeatureValue;


/**
 * A feature value that means: Value is not specified (that could mean: does not apply...). See
 * ThreeStateFeatureEqualityFormula! (Could be some-kind-of-equal to everything!)
 * <p>
 * (Example: The genus of a german plural pronoun could be <i>unspecified</i>.)
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public final class UnspecifiedFeatureValue implements IFeatureValue {
  public static final String UNSPECIFIED_STRING = "***UNSPECIFIED***";

  public static final UnspecifiedFeatureValue INSTANCE = new UnspecifiedFeatureValue();

  public static boolean notNullAndNotUnspecified(final String value) {
    return value != null && !UnspecifiedFeatureValue.UNSPECIFIED_STRING.equals(value);
  }

  private UnspecifiedFeatureValue() {
    super();
  }

	@Override
	public SurfacePart getSurfacePart() {
		return null;
	}

	@Override
	public Collection<FillingInSlot> getFillings() {
		return ImmutableList.of();
	}

	@Override
	public IFeatureValue addFillingIfAccepted(IHomogeneousConstituentAlternatives freeFilling,
			int keepPlaceFreeForHowManyFillings) {
		return null;
	}

  /**
   * Returns true, if one feature value subsumes the other. This means, the feature values are
   * equal, or the other is just a special case of the first one. For example: JOKER would subsume
   * "n", and "JOKER" would as well subsume "nom" and "gen".
   */
  public static boolean subsumes(final IFeatureValue one, final IFeatureValue other) {
    if (one.equals(UnspecifiedFeatureValue.INSTANCE)) {
      return true;
    }

    return one.equals(other);
  }


  @Override
  public int hashCode() {
    return 1;
  }

	@Override
	public String toString() {
		return toString(true, false);
	}

	@Override
	public String toString(boolean neverShowRequirements, boolean forceShowRequirements) {
    return "(unspecified)";
  }

	@Override
	public int howManyFillingsAreMissingUntilCompletion() {
		// UnspecifiedFeatureValues are NEVER completed AT ALL.
		return 1;
		// TODO good idea?
	}

	@Override
	public int howManyAdditionalFillingsAreAllowed() {
		// TODO good idea?
		return -1;
	}

  /**
   * @return <code>true</code>, iff all slots are satisfied and there are no free fillings.
   *         UnspecifiedFeatureValues NEVER completed AT ALL.
   */
  @Override
  public boolean isCompleted() {
    return false;
    // TODO good idea?
  }

	@Override
	public boolean containsAFillingInASlotEqualTo(IFeatureValue other) {
		return false;
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

    return 0;
  }

  /*
   * public ParseAlternativesDifference
   * findSinglePointOfDifferenceForRoleFrameCollFeaturesAndCheckAtomicFeatures( final IFeatureValue
   * otherValue) throws TooManyDifferencesException { if (! (otherValue instanceof
   * UnspecifiedFeatureValue)) { throw new TooManyDifferencesException(
   * "Values not equal: Other value is no unspecified feature value!"); }
   *
   * return null; }
   */

}
