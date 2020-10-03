package de.nb.federkiel.feature;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.plurivallogic.BinaryCompoundPlurivalTerm;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A term, that is build up from two (sub-)terms. This term checks for (String)
 * equalitiy and allows undefined values.
 *
 * @author nbudzyn 2011
 */
public class ThreeStateFeatureEqualsPlurivalTerm extends
BinaryCompoundPlurivalTerm<StringFeatureValue,
		IFeatureValue, IFeatureValue, FeatureAssignment> {

	public ThreeStateFeatureEqualsPlurivalTerm(
			final IPlurivalTerm<IFeatureValue, FeatureAssignment> firstSubTerm,
			final IPlurivalTerm<IFeatureValue, FeatureAssignment> secondSubTerm) {
		super(firstSubTerm, secondSubTerm);
	}

	@Override
	public Plurival<StringFeatureValue> calculate(
			final IFeatureValue first, final IFeatureValue second) {
		if (FeatureStructure.doFeatureValuesMatch(first, second)) {
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
		res.append(" EQUALS ");
		res.append(getSecondSubTerm().toString(true));
		if (surroundWithBracketsIfApplicable) {
			res.append(")");
		}
		return res.toString();
	}
}
