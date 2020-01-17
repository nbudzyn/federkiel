package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.UnaryPredicateFormula;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.YieldsNoResultException;


/**
 * A predicate, that tells, whether a role frame collection is <i>completed</i>.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class RoleFrameCollectionCompletedFormula extends UnaryPredicateFormula<RoleFrameSlot, FeatureAssignment> {
	public RoleFrameCollectionCompletedFormula(final ITerm<RoleFrameSlot, FeatureAssignment> term) {
    super(term);
  }

  @Override
  public boolean evaluate(final FeatureAssignment variableAssignment)
      throws UnassignedVariableException, YieldsNoResultException {
		return getTerm().evaluate(variableAssignment).noFreeFillingsAndAllSlotsHaveEnoughFillings(); // UnassignedVariableException,
                                                                 // YieldsNoResultException
  }

  @Override
  public String toString(final boolean surroundWithBracketsIfApplicable) {
    // brackets are not applicable here
    final StringBuilder res = new StringBuilder();
    res.append("COMPLETED (");
    res.append(getTerm().toString());
    res.append(")");
    return res.toString();
  }
}
