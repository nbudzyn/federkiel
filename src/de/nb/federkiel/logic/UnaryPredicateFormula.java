package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;


/**
 * A formula, that is build up from a single term. Typically, the formula tells, whether this term
 * has some kind of &quot;quality&quot; (whether a <i>role frame collection</i> is <i>completed</i>,
 * e.g.)
 * <p>
 * T ist the type of the term.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public abstract class UnaryPredicateFormula<T extends Object, A extends IAssignment>
    implements IFormula<A> {

  private final ITerm<T, A> term;

  public UnaryPredicateFormula(final ITerm<T, A> term) {
    super();
    this.term = term;
  }

  public ITerm<T, A> getTerm() {
    return this.term;
  }

  @Override
  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return term.getAllVariables();
  }

  @Override
  public int hashCode() {
    return this.term.hashCode();
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
    final UnaryPredicateFormula<? extends Object, ? extends IAssignment> other =
        (UnaryPredicateFormula<? extends Object, ? extends IAssignment>) obj;
    if (!this.term.equals(other.term)) {
      return false;
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(final IFormula<? extends IAssignment> obj) {
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(obj.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    final UnaryPredicateFormula<?, ? extends IAssignment> other =
        (UnaryPredicateFormula<?, ? extends IAssignment>) obj;

    return this.term.compareTo(other.term);
  }

  @Override
  public String toString() {
    return this.toString(false);
  }
}
