package de.nb.federkiel.feature;

import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;
import de.nb.federkiel.plurivallogic.UnaryCompoundPlurivalTerm;

/**
 * A term, that is build up from a (sub-)term, that should lead to values like
 * &quot;j&quot; or &quot;n&quot;. This term does a logical NOT.
 *
 * @author nbudzyn 2009
 */
public class StringFeatureNotPlurivalTerm extends
UnaryCompoundPlurivalTerm<StringFeatureValue,
StringFeatureValue, FeatureAssignment> {

	public StringFeatureNotPlurivalTerm(
			final IPlurivalTerm<StringFeatureValue, FeatureAssignment> subTerm) {
		super(subTerm);
	}

	@Override
	public Plurival<StringFeatureValue> calculate(final StringFeatureValue input) {
		return Plurival.of(
				StringFeatureValue.of(
						StringFeatureLogicUtil.booleanToString(
								! StringFeatureLogicUtil.stringToBoolean(input.getString()))));
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
