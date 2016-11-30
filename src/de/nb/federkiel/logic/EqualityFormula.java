package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;


/**
 * An equaliy (t1 = t2) in firstTerm-order logic.<p>
 * T is the type of the both terms.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class EqualityFormula<T extends Object, A extends IAssignment>
extends BinaryPredicateFormula<T, T, A> {
	public EqualityFormula(final ITerm <T, A>firstTerm, final ITerm<T, A> secondTerm) {
		super(firstTerm, secondTerm);
	}

	@Override
	public boolean evaluate(final A variableAssignment)
	throws UnassignedVariableException {
		return getFirstTerm().evaluate(variableAssignment).equals( // UnassignedVariableException
				getSecondTerm().evaluate(variableAssignment)); // UnassignedVariableException
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		// brackets are not applicable here
		final StringBuilder res = new StringBuilder();
		res.append(getFirstTerm().toString());
		res.append("==");
		res.append(getSecondTerm().toString());
		return res.toString();

	}
}
