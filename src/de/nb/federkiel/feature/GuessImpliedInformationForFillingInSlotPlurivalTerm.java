package de.nb.federkiel.feature;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;
import de.nb.federkiel.plurivallogic.UnaryCompoundPlurivalTerm;

/**
 * Takes a {@link RoleFrameCollection} or an {@link IFillingInSlot} and guesses
 * all information that is needed when used as a slot filling.
 *
 * @author nbudzyn 2019
 */
public class GuessImpliedInformationForFillingInSlotPlurivalTerm
		extends UnaryCompoundPlurivalTerm<IFillingInSlot, IFeatureValue, FeatureAssignment> {

	public GuessImpliedInformationForFillingInSlotPlurivalTerm(
			final IPlurivalTerm<IFeatureValue, FeatureAssignment> subTerm) {
		super(subTerm);
	}

	@Override
	public Plurival<IFillingInSlot> calculate(final IFeatureValue input) {
		if (input instanceof IFillingInSlot) {
			return Plurival.of((IFillingInSlot) input);
		}

		final RoleFrameCollection roleFrameCollection = (RoleFrameCollection) input;

		return roleFrameCollection.toFillingInSlot();
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		return getSubTerm().toString(true);
	}
}
