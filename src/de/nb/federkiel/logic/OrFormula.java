package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;


/**
 * An first-order logic formula, that consists of two formulas, connected with
 * the OR operator.
 *
 * @author nbudzyn 2009
 */
@Immutable
class OrFormula<A extends IAssignment>
extends BinaryLogicalOperatorFormula<A> {
	protected OrFormula(final IFormula<A> firstFormula,
			final IFormula<A> secondFormula) {
		super(firstFormula, secondFormula, "OR");
	}

	@Override
	public boolean evaluate(final A variableAssignment) throws UnassignedVariableException {
		try {
			final boolean firstValue = getFirstFormula().evaluate(variableAssignment);
			// it was possible to evaluate the first formula :-)
			if (firstValue) {
				return true;
			}
		} catch (final UnassignedVariableException e) {
			// it was NOT possible to evaluate the first formula... ->
			// try the second term! (we need only one result!!)
			final boolean secondValue = getSecondFormula().evaluate(variableAssignment);
			// UnassignedVariableException

			if (secondValue) {
				return true;
			} else {
				throw e;
			}
		}

		// it was able to evaluate the first formula, but firstValue was false!
		final boolean secondValue = getSecondFormula().evaluate(variableAssignment);
		// UnassignedVariableException

		return secondValue; // UnassignedVariableException
	}
}
