package de.nb.federkiel.plurivallogic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.IFormulaPart;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.Variable;

/**
 * Like a term in first-order logic, but this one can have several <i>alternative</i> values (I call
 * this a <i>plurivalent term</i>).
 * <p>
 * T is the type - the type of things in the universe, the term could be evaluate to.
 * <p>
 * A is the specific type of Assignment that is needed to evaluate this term - determined by the
 * <i>Domain of discourse</i>.
 * <p>
 * Each Implementation of IPlurivalTerm MUST BE IMMUTABLE.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public interface IPlurivalTerm<T extends Object, A extends IAssignment>
    extends IFormulaPart, Comparable<IPlurivalTerm<?, ? extends IAssignment>> {

  Plurival<T> evaluate(final A variableAssignment) throws UnassignedVariableException;

  ImmutableSet<Variable<?, A>> getAllVariables();

  /*
   * NOT USED
   *
   * Calculates the implicit restrictions, that this Term places on a Variable, given this (partial)
   * assignment. For example, this term may force a variable to habe at most a value of <i>5</i> -
   * or the term will have no result at all. <p> This is aimed at Role Frame Collection terms.
   *
   * @return a (partial) assignment. It shall NOT contain an assignment for the
   * <code>variable</code>! (Otherwise, looking for bounds would be unsensible - and the algorithm
   * used might not work)
   *
   * public Plurival<BoundsForTermAndBoundsForVariable>
   * retrieveImplicitBoundsForTermAndBoundsForVariable( final Variable<T, A> variable, final A
   * assignment);
   */
}
