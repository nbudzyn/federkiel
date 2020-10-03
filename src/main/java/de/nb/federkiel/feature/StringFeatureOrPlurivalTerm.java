package de.nb.federkiel.feature;

import de.nb.federkiel.plurivallogic.BinaryCompoundPlurivalTerm;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A term, that is build up from two (sub-)terms, that should lead to values like
 * &quot;j&quot; or &quot;n&quot;. This term does a logical OR.
 *
 * @author nbudzyn 2009
 */
public class StringFeatureOrPlurivalTerm extends
BinaryCompoundPlurivalTerm<StringFeatureValue,
StringFeatureValue, StringFeatureValue, FeatureAssignment> {

	public StringFeatureOrPlurivalTerm(
			final IPlurivalTerm<StringFeatureValue, FeatureAssignment> firstSubTerm,
			final IPlurivalTerm<StringFeatureValue, FeatureAssignment> secondSubTerm) {
		super(firstSubTerm, secondSubTerm);
	}

	@Override
	public Plurival<StringFeatureValue> calculate(
			final StringFeatureValue first, final StringFeatureValue second) {
		// There might be an optimization not to evaluate the second term
		// in the super class - but in typical cases, this won't buy
		// us anything.

    if (FeatureStructure.toBoolean(first) || FeatureStructure.toBoolean(second)) {
			return Plurival.of(StringFeatureValue.J);
		}

		return Plurival.of(StringFeatureValue.N);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		final StringBuilder res = new StringBuilder();
		if (surroundWithBracketsIfApplicable) {
			res.append("(");
		}
		res.append(getFirstSubTerm().toString(true));
		res.append(" OR ");
		res.append(getSecondSubTerm().toString(true));
		if (surroundWithBracketsIfApplicable) {
			res.append(")");
		}
		return res.toString();

	}
}
