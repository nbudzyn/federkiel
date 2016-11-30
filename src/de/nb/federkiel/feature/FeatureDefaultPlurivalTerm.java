package de.nb.federkiel.feature;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.plurivallogic.BinaryCompoundPlurivalTerm;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A term, that is build up from two (sub-)terms. If the first sub-term is
 * specified, then this is the value of the term - otherwise the value of the
 * second sub-term is the value of the term (functioning as a "default value").
 *
 * @author nbudzyn 2011
 */
public class FeatureDefaultPlurivalTerm extends
		BinaryCompoundPlurivalTerm<IFeatureValue,
		IFeatureValue, IFeatureValue, FeatureAssignment> {

	public FeatureDefaultPlurivalTerm(
			final IPlurivalTerm<IFeatureValue, FeatureAssignment> firstSubTerm,
			final IPlurivalTerm<IFeatureValue, FeatureAssignment> secondSubTerm) {
		super(firstSubTerm, secondSubTerm);
	}

	@Override
	public Plurival<IFeatureValue> calculate(
			final IFeatureValue first, final IFeatureValue second) {
		if (!UnspecifiedFeatureValue.INSTANCE.equals(first)) {
			return Plurival.of(first);
		}

		return Plurival.of(second);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		final StringBuilder res = new StringBuilder();
		if (surroundWithBracketsIfApplicable) {
			res.append("(");
		}
		res.append(getFirstSubTerm().toString(true));
		res.append(" JOKER_DEFAULTS_TO ");
		res.append(getSecondSubTerm().toString(true));
		if (surroundWithBracketsIfApplicable) {
			res.append(")");
		}
		return res.toString();
	}
}
