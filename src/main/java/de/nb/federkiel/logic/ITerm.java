package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;

/**
 * A Term in firstTerm-order logic
 * <p>
 * T is the Type of the term - the type of things in the universe, it could be evaluated to.
 * <p>
 * A is the specific type of Assignment that is needed to evaluate this term - determined by the
 * <i>Domain of discourse</i>.
 * <p>
 * Each Implementation of ITerm MUST BE IMMUTABLE.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public interface ITerm<T extends Object, A extends IAssignment>
    extends IFormulaPart, Comparable<ITerm<? extends Object, ? extends IAssignment>> {

  /**
   * Evaluates the term under this variable assignment.
   *
   * @param variableAssignment
   * @return
   * @throws UnassignedVariableException
   */
  public T evaluate(final A variableAssignment)
      throws YieldsNoResultException, UnassignedVariableException;

  public ImmutableSet<Variable<?, A>> getAllVariables();

  /*
   * Calculates the implicit restrictions, that this Term places on a Variable, given this (partial)
   * assignment. For example, this term may force a variable to habe at most a value of <i>5</i> -
   * or the term will have no result at all. <p> If the <code>this</code> <i>is</i> the variable, as
   * <i>bounds for term</i>, the variable itself is returned. <p> This is aimed at Role Frame
   * Collection terms.
   *
   * @return a (partial) assignment. It shall NOT contain an assignment for the
   * <code>variable</code>! (Otherwise, looking for bounds would be unsensible - and the algorithm
   * used might not work)
   *
   * public BoundsForTermAndBoundsForVariable retrieveImplicitBoundsForTermAndBoundsForVariable(
   * final Variable<T, A> variable, final A assignment);
   */
}
