package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * A formula, that is build up from two terms (could be something like an equality, e.g.).
 * <p>
 * T1 and T2 are the types of the two terms.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public abstract class BinaryPredicateFormula<T1 extends Object, T2 extends Object, A extends IAssignment>
    implements IFormula<A> {

  private final ITerm<T1, A> firstTerm;
  private final ITerm<T2, A> secondTerm;

  public BinaryPredicateFormula(final ITerm<T1, A> firstTerm, final ITerm<T2, A> secondTerm) {
    super();
    this.firstTerm = firstTerm;
    this.secondTerm = secondTerm;
  }

  public ITerm<T1, A> getFirstTerm() {
    return this.firstTerm;
  }

  public ITerm<T2, A> getSecondTerm() {
    return this.secondTerm;
  }

  @Override
  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return Sets.union(firstTerm.getAllVariables(), secondTerm.getAllVariables()).immutableCopy();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.firstTerm.hashCode();
    result = prime * result + this.secondTerm.hashCode();
    return result;
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
    final BinaryPredicateFormula<? extends Object, ? extends Object, ? extends IAssignment> other =
        (BinaryPredicateFormula<?, ?, ?>) obj;
    if (!this.firstTerm.equals(other.firstTerm)) {
      return false;
    }
    if (!this.secondTerm.equals(other.secondTerm)) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(final IFormula<? extends IAssignment> obj) {
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(obj.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    final BinaryPredicateFormula<?, ?, ? extends IAssignment> other =
        (BinaryPredicateFormula<?, ?, ?>) obj;

    final int firstTermsCompared = this.firstTerm.compareTo(other.firstTerm);
    if (firstTermsCompared != 0) {
      return firstTermsCompared;
    }

    return this.secondTerm.compareTo(other.secondTerm);
  }


  @Override
  public String toString() {
    return this.toString(false);
  }
}
