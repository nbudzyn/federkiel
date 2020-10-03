package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;



/**
 * An first-order logic formula, that consists of two formulas, connected with the and operator.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class AndFormula<A extends IAssignment> extends BinaryLogicalOperatorFormula<A> {
  public AndFormula(final IFormula<A> firstFormula, final IFormula<A> secondFormula) {
    super(firstFormula, secondFormula, "AND");
  }

  @Override
  public boolean evaluate(final A variableAssignment)
      throws UnassignedVariableException, YieldsNoResultException {
    return getFirstFormula().evaluate(variableAssignment) && // UnassignedVariableException,
                                                             // YieldsNoResultException
        getSecondFormula().evaluate(variableAssignment); // UnassignedVariableException,
                                                         // YieldsNoResultException
  }
}
