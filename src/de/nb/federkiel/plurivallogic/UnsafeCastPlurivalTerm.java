package de.nb.federkiel.plurivallogic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.Variable;

/**
 * This term is necessary, when a term with a LESS specific type is used (in another term or a
 * formula), where a term with a MORE SPECIFIC type is expected.
 * <p>
 * This type of cast is NOT SAFE! We need the term to conform to the necessities of the generics,
 * but it CAN LEAD TO AN ERROR AT RUNTIME!
 * <p>
 * ST is the sub-type, UT is the upper type. - A is the variable assignment type.
 *
 * @author nbudzyn 2009
 */
public final class UnsafeCastPlurivalTerm<UT extends Object, ST extends UT, A extends IAssignment>
    implements IPlurivalTerm<ST, A> {
  private final IPlurivalTerm<UT, A> term;

  private UnsafeCastPlurivalTerm(final IPlurivalTerm<UT, A> term) {
    this.term = term;
  }

  public static <UT extends Object, ST extends UT, A extends IAssignment> UnsafeCastPlurivalTerm<UT, ST, A> of(
      final IPlurivalTerm<UT, A> term) {
    return new UnsafeCastPlurivalTerm<>(term);
  }

  @Override
  public Plurival<ST> evaluate(final A variableAssignment) throws UnassignedVariableException {
    final ImmutableList.Builder<ST> castedAlternatives = ImmutableList.<ST>builder();

    final Plurival<UT> uncastedAlternatives = this.term.evaluate(variableAssignment); // UnassignedVariableException
    for (final UT value : uncastedAlternatives) {
      // This cast is unsafe and can lead to an exception at runtime!
      // IDEA: Have some kind of TermEvalutationException?
      castedAlternatives.add((ST) value);
    }

    return Plurival.of(castedAlternatives.build());
  }

  @Override
  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return term.getAllVariables();
  }

  @Override
  public String toString(final boolean surroundWithBracketsIfApplicable) {
    return this.term.toString(surroundWithBracketsIfApplicable);
  }

  @Override
  public String toString() {
    return this.term.toString();
  }

  @Override
  public int hashCode() {
    return this.term.hashCode();
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

    final UnsafeCastPlurivalTerm<?, ?, ?> other = (UnsafeCastPlurivalTerm<?, ?, ?>) obj;

    return this.term.equals(other.term);
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(final IPlurivalTerm<?, ? extends IAssignment> o) {
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    final UnsafeCastPlurivalTerm<?, ?, ? extends IAssignment> other =
        (UnsafeCastPlurivalTerm<?, ?, ? extends IAssignment>) o;

    return this.term.compareTo(other.term);
  }
}
