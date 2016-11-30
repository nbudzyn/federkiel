package de.nb.federkiel.feature;

import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.UnaryCompoundTerm;
import de.nb.federkiel.logic.UnassignedVariableException;

/**
 * A term, that is build up from a (sub-)term, that should lead to values like
 * &quot;j&quot; or &quot;n&quot;. This term does a logical NOT.
 *
 * @author nbudzyn 2009
 */
public class StringFeatureNotTerm extends
UnaryCompoundTerm<StringFeatureValue,
StringFeatureValue, FeatureAssignment> {

	public StringFeatureNotTerm(
			final ITerm<StringFeatureValue, FeatureAssignment> subTerm) {
		super(subTerm);
	}

	@Override
	public StringFeatureValue evaluate(final FeatureAssignment variableAssignment)
	throws UnassignedVariableException {
		return StringFeatureValue.of(
				StringFeatureLogicUtil.booleanToString(
						! StringFeatureLogicUtil.stringToBoolean(
								getSubTerm().evaluate(variableAssignment).getString())));
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		final StringBuilder res = new StringBuilder();
		if (surroundWithBracketsIfApplicable) {
			res.append("(");
		}
		res.append("NOT ");
		res.append(getSubTerm().toString(true));
		if (surroundWithBracketsIfApplicable) {
			res.append(")");
		}
		return res.toString();
	}
}
