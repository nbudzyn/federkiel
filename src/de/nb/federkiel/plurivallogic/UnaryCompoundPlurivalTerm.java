package de.nb.federkiel.plurivallogic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.UnassignedVariableException;


/**
 * A plurivalent term, that is build up from one sub-term (could be something like an operator
 * working on a term).
 * <p>
 * T is the type of the term, S is the type of the sub-term.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public abstract class UnaryCompoundPlurivalTerm<T extends Object, S extends Object, A extends IAssignment>
    implements IPlurivalTerm<T, A> {

  private final IPlurivalTerm<S, A> subTerm;

  public UnaryCompoundPlurivalTerm(final IPlurivalTerm<S, A> subTerm) {
    super();
    this.subTerm = subTerm;
  }

  public IPlurivalTerm<S, A> getSubTerm() {
    return this.subTerm;
  }

  @Override
  public final Plurival<T> evaluate(final A assignment) throws UnassignedVariableException {
    Plurival<S> subResults = null;

    subResults = this.subTerm.evaluate(assignment); // UnassignedVariableException

    // evaluate all possible combinations
    // @formatter:off
		return Plurival.of(subResults.stream()
		    .flatMap(subResult -> calculate(subResult).stream())
		    .collect(ImmutableList.toImmutableList()));
        // @formatter:on
  }

  /**
   * Calculates the alternative values of the term for these to input value.
   * <p>
   * This method is called by evaluate().
   */
  abstract public Plurival<T> calculate(S input);

  @Override
  public int hashCode() {
    return this.subTerm.hashCode();
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
    final UnaryCompoundPlurivalTerm<? extends Object, ? extends Object, ? extends IAssignment> other =
        (UnaryCompoundPlurivalTerm<? extends Object, ? extends Object, ? extends IAssignment>) obj;
    if (!this.subTerm.equals(other.subTerm)) {
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

    final UnaryCompoundPlurivalTerm<?, ?, ? extends IAssignment> other =
        (UnaryCompoundPlurivalTerm<?, ?, ? extends IAssignment>) o;

    return this.subTerm.compareTo(other.subTerm);

  }


  @Override
  public String toString() {
    return this.toString(false);
  }
}
