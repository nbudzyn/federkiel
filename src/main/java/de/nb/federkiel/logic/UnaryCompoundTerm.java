package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;


/**
 * A term, that is build up from one sub-term (could be something like an operator working on a
 * term).
 * <p>
 * T is the type of the term, S is the type of the sub-term.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public abstract class UnaryCompoundTerm<T extends Object, S extends Object, A extends IAssignment>
    implements ITerm<T, A> {

  private final ITerm<S, A> subTerm;

  public UnaryCompoundTerm(final ITerm<S, A> subTerm) {
    super();
    this.subTerm = subTerm;
  }

  public ITerm<S, A> getSubTerm() {
    return this.subTerm;
  }

  @Override
  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return subTerm.getAllVariables();
  }

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
    final UnaryCompoundTerm<? extends Object, ? extends Object, ? extends IAssignment> other =
        (UnaryCompoundTerm<? extends Object, ? extends Object, ? extends IAssignment>) obj;
    if (!this.subTerm.equals(other.subTerm)) {
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

    final UnaryCompoundTerm<?, ?, ? extends IAssignment> other =
        (UnaryCompoundTerm<?, ?, ? extends IAssignment>) o;

    return this.subTerm.compareTo(other.subTerm);
  }


  @Override
  public String toString() {
    return this.toString(false);
  }
}
