package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;

/**
 * A (well-formed) formula (in propositional calculus or predicate logic)
 * <p>
 * A is the specific type of Assignment that is needed to evaluate this formula - determined by the
 * <i>Domain of discourse</i>.
 * <p>
 * Each implementation of <code>IFormula</code> MUST BE IMMUTABLE.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public interface IFormula<A extends IAssignment>
    extends IFormulaPart, Comparable<IFormula<? extends IAssignment>> {
  /**
   * @return <code>true</code>, iff the formula evalutes to true with this variable assignment
   */
  public boolean evaluate(final A variableAssignment)
      throws UnassignedVariableException, YieldsNoResultException;

  public ImmutableSet<Variable<?, A>> getAllVariables();
}
