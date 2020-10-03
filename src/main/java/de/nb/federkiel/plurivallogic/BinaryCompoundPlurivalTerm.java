package de.nb.federkiel.plurivallogic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.Variable;


/**
 * A plurivalent term, that is build up from two (sub-)terms (could be something like two terms,
 * joined by a union operator, e.g.).
 * <p>
 * T is the type of the term, S1 and S2 are the types of the two sub-terms.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public abstract class BinaryCompoundPlurivalTerm<T extends Object, S1 extends Object, S2 extends Object, A extends IAssignment>
    implements IPlurivalTerm<T, A> {

  private final IPlurivalTerm<S1, A> firstSubTerm;
  private final IPlurivalTerm<S2, A> secondSubTerm;

  public BinaryCompoundPlurivalTerm(final IPlurivalTerm<S1, A> firstSubTerm,
      final IPlurivalTerm<S2, A> secondSubTerm) {
    super();
    this.firstSubTerm = firstSubTerm;
    this.secondSubTerm = secondSubTerm;
  }

  public IPlurivalTerm<S1, A> getFirstSubTerm() {
    return this.firstSubTerm;
  }

  public IPlurivalTerm<S2, A> getSecondSubTerm() {
    return this.secondSubTerm;
  }

  @Override
  public final Plurival<T> evaluate(final A assignment) throws UnassignedVariableException {
    Plurival<S1> firstAlternativeSubResults = null;

    try {
      firstAlternativeSubResults = this.firstSubTerm.evaluate(assignment); // UnassignedVariableException

      if (firstAlternativeSubResults.isEmpty()) {
        // We do not need to evaluate the second term!!
        return Plurival.empty();
      }
    } catch (final UnassignedVariableException e) {
      // Try the second sub term!
      final Plurival<S2> secondAlternativeSubResults = this.secondSubTerm.evaluate(assignment); // UnassignedVariableException

      if (secondAlternativeSubResults.isEmpty()) {
        // The first term does not matter!!!
        return Plurival.empty();
      }

      // We DO need both subresults :-(
      throw e;
    }

    // NO UnassignedVariableException with the first sub term, and
    // more than 0 values in the first Plurival -> go on!

    final Plurival<S2> secondAlternativeSubResults = this.secondSubTerm.evaluate(assignment); // UnassignedVariableException

    // evaluate all possible combinations

    // @formatter:off
    return Plurival.of(firstAlternativeSubResults.stream()
        .flatMap(firstSubResult ->
          secondAlternativeSubResults.stream().flatMap(secondSubResult -> calculate(firstSubResult, secondSubResult).stream()))
        .collect(ImmutableList.toImmutableList()));

//	final ImmutableList.Builder<T> res = ImmutableList.<T>builder();
//
//	for (final S1 firstSubResult : firstAlternativeSubResults) {
//      res.addAll(secondAlternativeSubResults.stream()
//          .flatMap(secondSubResult -> calculate(firstSubResult, secondSubResult).stream())
//          ::iterator);
//    }
//
//	return Plurival.of(res.build());
    // @formatter:on
  }

  @Override
  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return Sets.union(firstSubTerm.getAllVariables(),
        secondSubTerm.getAllVariables()).immutableCopy();
  }

  /**
   * Calculates the alternative values of the term for these to input values.
   * <p>
   * This method is called by evaluate().
   */
  abstract public Plurival<T> calculate(S1 first, S2 second);

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.firstSubTerm.hashCode();
    result = prime * result + this.secondSubTerm.hashCode();
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
    @SuppressWarnings("unchecked")
    final BinaryCompoundPlurivalTerm<? extends Object, ? extends Object, ? extends Object, ? extends IAssignment> other =
        (BinaryCompoundPlurivalTerm<? extends Object, ? extends Object, ? extends Object, ? extends IAssignment>) obj;
    if (!this.firstSubTerm.equals(other.firstSubTerm)) {
      return false;
    }
    if (!this.secondSubTerm.equals(other.secondSubTerm)) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(final IPlurivalTerm<? extends Object, ? extends IAssignment> o) {
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    @SuppressWarnings("unchecked")
    final BinaryCompoundPlurivalTerm<?, ?, ?, ? extends IAssignment> other =
        (BinaryCompoundPlurivalTerm<?, ?, ?, ? extends IAssignment>) o;

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
