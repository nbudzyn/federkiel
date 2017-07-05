package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * A term, that is build up from two (sub-)terms (could be something like two terms, joined by a
 * union operator, e.g.).
 * <p>
 * T is the type of the term, S1 and S2 are the types of the two sub-terms.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public abstract class BinaryCompoundTerm<T extends Object, S1 extends Object, S2 extends Object, A extends IAssignment>
    implements ITerm<T, A> {

  private final ITerm<S1, A> firstSubTerm;
  private final ITerm<S2, A> secondSubTerm;

  public BinaryCompoundTerm(final ITerm<S1, A> firstSubTerm, final ITerm<S2, A> secondSubTerm) {
    super();
    this.firstSubTerm = firstSubTerm;
    this.secondSubTerm = secondSubTerm;
  }

  public ITerm<S1, A> getFirstSubTerm() {
    return this.firstSubTerm;
  }

  public ITerm<S2, A> getSecondSubTerm() {
    return this.secondSubTerm;
  }

  @Override
  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return Sets.union(firstSubTerm.getAllVariables(),
        secondSubTerm.getAllVariables()).immutableCopy();
  }

  @Override
  public final T evaluate(final A assignment)
      throws UnassignedVariableException, YieldsNoResultException {
    final S1 firstSubResult = this.firstSubTerm.evaluate(assignment); // UnassignedVariableException,
                                                                      // YieldsNoResultException
    final S2 secondSubResult = this.secondSubTerm.evaluate(assignment); // UnassignedVariableException,
                                                                        // YieldsNoResultException

    return calculate(firstSubResult, secondSubResult); // YieldsNoResultException
  }

  /**
   * Calculates the value of the term for these to input values.
   * <p>
   * This method is called by evaluate().
   */
  abstract public T calculate(S1 first, S2 second) throws YieldsNoResultException;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.firstSubTerm.hashCode();
    result = prime * result + this.secondSubTerm.hashCode();
    return result;
  }

  @SuppressWarnings("unchecked")
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
    final BinaryCompoundTerm<? extends Object, ? extends Object, ? extends Object, ? extends IAssignment> other =
        (BinaryCompoundTerm<? extends Object, ? extends Object, ? extends Object, ? extends IAssignment>) obj; // (unechecked
                                                                                                               // warning)
    if (!this.firstSubTerm.equals(other.firstSubTerm)) {
      return false;
    }
    if (!this.secondSubTerm.equals(other.secondSubTerm)) {
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

    final BinaryCompoundTerm<?, ?, ?, ? extends IAssignment> other =
        (BinaryCompoundTerm<?, ?, ?, ? extends IAssignment>) o;

    final int firstSubTermsCompared = this.firstSubTerm.compareTo(other.firstSubTerm);
    if (firstSubTermsCompared != 0) {
      return firstSubTermsCompared;
    }

    return this.secondSubTerm.compareTo(other.secondSubTerm);

  }

  @Override
  public String toString() {
    return this.toString(false);
  }
}
