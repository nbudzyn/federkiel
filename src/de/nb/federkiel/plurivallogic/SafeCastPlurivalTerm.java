package de.nb.federkiel.plurivallogic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.Variable;

/**
 * This term is necessary, when a term with a more specific type is used (in another term or a
 * formula), where a term with a more generell type is expected.
 * <p>
 * This type of cast is safe! We only need the term to conform to the necessities of the generics.
 * <p>
 * ST is the sub-type, UT is the upper type. - SA is the sub-variable assignment type, UA is the
 * upper variable assignment type.
 *
 * @author nbudzyn 2009
 */
public final class SafeCastPlurivalTerm<UT extends Object, ST extends UT, UA extends IAssignment, SA extends UA>
    implements IPlurivalTerm<UT, SA> {

  private final IPlurivalTerm<ST, UA> term;

  private SafeCastPlurivalTerm(final IPlurivalTerm<ST, UA> term) {
    this.term = term;
  }

  public static <UT extends Object, ST extends UT, UA extends IAssignment, SA extends UA> SafeCastPlurivalTerm<UT, ST, UA, SA> of(
      final IPlurivalTerm<ST, UA> term) {
    return new SafeCastPlurivalTerm<>(term);
  }

  @Override
  public Plurival<UT> evaluate(final SA variableAssignment) throws UnassignedVariableException {
    final ImmutableList.Builder<UT> castedAlternatives = ImmutableList.<UT>builder();

    final Plurival<ST> uncastedAlternatives = this.term.evaluate(variableAssignment); // UnassignedVariableException
    for (final ST value : uncastedAlternatives) {
      castedAlternatives.add(value);
    }

    return Plurival.of(castedAlternatives.build());
  }

  @Override
  @SuppressWarnings("unchecked")
  public ImmutableSet<Variable<?, SA>> getAllVariables() {
    final ImmutableSet.Builder<Variable<?, SA>> res = ImmutableSet.<Variable<?, SA>>builder();

    for (final Variable<?, UA> variable : term.getAllVariables()) {
      // This cast is unsafe and can lead to an exception at runtime!
      // IDEA: Have some kind of TermEvalutationException?
      res.add((Variable<?, SA>) variable);
    }

    return res.build();
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

    final SafeCastPlurivalTerm<?, ?, ?, ?> other = (SafeCastPlurivalTerm<?, ?, ?, ?>) obj;

    return this.term.equals(other.term);
  }

  @Override
  public int compareTo(final IPlurivalTerm<?, ? extends IAssignment> o) {
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    final SafeCastPlurivalTerm<?, ?, ?, ? extends IAssignment> other =
        (SafeCastPlurivalTerm<?, ?, ?, ? extends IAssignment>) o;

    return this.term.compareTo(other.term);
  }
}
