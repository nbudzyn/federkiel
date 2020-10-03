package de.nb.federkiel.logic;

import com.google.common.collect.ImmutableSet;

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
public final class SafeCastTerm<UT extends Object, ST extends UT, UA extends IAssignment, SA extends UA>
    implements ITerm<UT, SA> {
  private final ITerm<ST, UA> term;

  private SafeCastTerm(final ITerm<ST, UA> term) {
    this.term = term;
  }

  public static <UT extends Object, ST extends UT, UA extends IAssignment, SA extends UA> SafeCastTerm<UT, ST, UA, SA> of(
      final ITerm<ST, UA> term) {
    return new SafeCastTerm<>(term);
  }

  @Override
  public UT evaluate(final SA variableAssignment)
      throws UnassignedVariableException, YieldsNoResultException {
    return this.term.evaluate(variableAssignment); // UnassignedVariableException,
                                                   // YieldsNoResultException
  }

  @Override
  public ImmutableSet<Variable<?, SA>> getAllVariables() {
    final ImmutableSet.Builder<Variable<?, SA>> res = ImmutableSet.builder();

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

    final SafeCastTerm<?, ?, ?, ?> other = (SafeCastTerm<?, ?, ?, ?>) obj;

    return this.term.equals(other.term);
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(final ITerm<? extends Object, ? extends IAssignment> o) {
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    final SafeCastTerm<?, ?, ?, ? extends IAssignment> other =
        (SafeCastTerm<?, ?, ?, ? extends IAssignment>) o;

    return this.term.compareTo(other.term);
  }
}
