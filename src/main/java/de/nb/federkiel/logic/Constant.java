package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * A constant (Individuenkonstante) in firstTerm-order logic. Denotes one element of the domain of
 * discourse.
 *
 * @author nbudzyn 2009
 * @param <T> the type of the element that is denoted.
 */
@Immutable
@ThreadSafe
public class Constant<T extends Object, A extends IAssignment> implements ITerm<T, A> {
  /**
   * <code>null</code> allowed
   */
  private final T value;

  /**
   * @param value <code>null</code> allowed
   */
  public Constant(final T value) {
    super();
    this.value = value;
  }

  public static final <T extends Object, A extends IAssignment> Constant<T, A> of(final T value) {
    return new Constant<>(value);
  }

  @Override
  public T evaluate(final A variableAssignment) {
    return this.value;
  }

  @Override
  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return ImmutableSet.of();
  }

  /*
   * public BoundsForTermAndBoundsForVariable retrieveImplicitBoundsForTermAndBoundsForVariable(
   * final Variable<T, A> variable, final A assignment) { if (this.value instanceof RoleFrame) {
   * return new BoundsForTermAndBoundsForVariable( (RoleFrame) this.value, null); }
   *
   * if (this.value instanceof RoleFrameCollection) { try { final RoleFrame resBounds =
   * ((RoleFrameCollection) this.value).combineBoundsGivenAsRoleFrameCollection();
   *
   * return new BoundsForTermAndBoundsForVariable(resBounds, null); } catch (final
   * CannotFulfillTermException e) { // FIXME Not clear what to do! It seems to be impossible to fit
   * this term... throw new RuntimeException("FIXME - Not clear what to do here."); } }
   *
   * return BoundsForTermAndBoundsForVariable.NONE; }
   */

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }

    if (this == obj) {
      return true;
    }

    if (!this.getClass().equals(obj.getClass())) {
      return false;
    }

    final Constant<? extends Object, ? extends IAssignment> other =
        (Constant<? extends Object, ? extends IAssignment>) obj;

    if (!Objects.equal(this.value, other.value)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.value);
  }

  /**
   * This method is not absolutely consistent with equals - if the values are not
   * Comparables-that-can-be-compared.
   */
  @Override
  public int compareTo(final ITerm<? extends Object, ? extends IAssignment> o) {
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    final Constant<?, ?> other = (Constant<?, ?>) o;

    final Object myValue = this.value;
    final Object otherValue = other.value;
    try {
      @SuppressWarnings({"rawtypes", "unchecked"})
      final int valuesCompared = ((Comparable) myValue).compareTo(otherValue);
      if (valuesCompared != 0) {
        return valuesCompared;
      }
    } catch (final RuntimeException e) {
      // Could not cast to Comparable - or they were not compatible...
      // try and go on!
    }

    if (myValue.equals(otherValue)) {
      return 0;
    }

    if (myValue.hashCode() < otherValue.hashCode()) {
      return -1;
    }

    if (myValue.hashCode() > otherValue.hashCode()) {
      return 1;
    }

    return 0;
    // This is not absolutely consistent with equals - but I don't have any other chance!
  }


  @Override
  public String toString() {
    return toString(false);
  }

  @Override
  public String toString(final boolean surroundWithBracketsIfApplicable) {
    // brackets are not applicable
    if (this.value == null) {
      return null;
    }

    return this.value.toString();
  }
}
