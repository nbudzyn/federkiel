package de.nb.federkiel.feature;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.logic.BinaryCompoundTerm;
import de.nb.federkiel.logic.ITerm;

/**
 * A term, that is build up from two (sub-)terms. This term checks for (String)
 * equalitiy and allows undefined values.
 *
 * @author nbudzyn 2011
 */
public class ThreeStateFeatureEqualsTerm extends
		BinaryCompoundTerm<StringFeatureValue,
		IFeatureValue, IFeatureValue, FeatureAssignment> {

	public ThreeStateFeatureEqualsTerm(
			final ITerm<IFeatureValue, FeatureAssignment> firstSubTerm,
			final ITerm<IFeatureValue, FeatureAssignment> secondSubTerm) {
		super(firstSubTerm, secondSubTerm);
	}

	@Override
	public StringFeatureValue calculate(
			final IFeatureValue first, final IFeatureValue second) {
		if (FeatureStructure.doFeatureValuesMatch(first, second)) {
			return StringFeatureValue.J;
		}

		return StringFeatureValue.N;
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
