package de.nb.federkiel.feature;

import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.UnaryCompoundTerm;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.YieldsNoResultException;

/**
 * A term, that takes a {@link IHomogeneousConstituentAlternatives} value and
 * extracts its features, to, from and semantics
 *
 * @author nbudzyn 2009
 */
public class ToFillingInSlotTerm
		extends UnaryCompoundTerm<IFillingInSlot, IHomogeneousConstituentAlternatives, FeatureAssignment> {
	public ToFillingInSlotTerm(final ITerm<IHomogeneousConstituentAlternatives, FeatureAssignment> subTerm) {
		super(subTerm);
	}

	@Override
	public IFillingInSlot evaluate(final FeatureAssignment variableAssignment)
			throws UnassignedVariableException, YieldsNoResultException {
		final IHomogeneousConstituentAlternatives subValue = getSubTerm().evaluate(variableAssignment);
		// UnassignedVariableException, YieldsNoResultException

		return subValue.toFillingInSlot();
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		return getSubTerm().toString(true);
	}
}
