package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;

/**
 * A variable in firstTerm-order logic
 * <p>
 * T is the Type of the variable - the sort of things in the universe, that could be assigned to
 * this variable.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public abstract class Variable<T extends Object, A extends IAssignment>
    implements ITerm<T, A>, IDataFlowElement {
  public Variable() {
    super();
  }

  @Override
  public T evaluate(final A variableAssignment) throws UnassignedVariableException {
    return variableAssignment.<T>getValue(this);
  }

  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return ImmutableSet.of(this);
  }
}
