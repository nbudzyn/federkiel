package de.nb.federkiel.feature;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;
import de.nb.federkiel.plurivallogic.UnaryCompoundPlurivalTerm;

/**
 * Takes a {@link RoleFrameSlot} or an {@link IFillingInSlot} and guesses all
 * information that is needed when used as a slot filling.
 *
 * @author nbudzyn 2019
 */
public class GuessImpliedInformationForFillingInSlotPlurivalTerm
		extends UnaryCompoundPlurivalTerm<FeatureStructure, IFeatureValue, FeatureAssignment> {

	public GuessImpliedInformationForFillingInSlotPlurivalTerm(
			final IPlurivalTerm<IFeatureValue, FeatureAssignment> subTerm) {
		super(subTerm);
	}

	@Override
	public Plurival<FeatureStructure> calculate(final IFeatureValue input) {
		if (input instanceof FeatureStructure) {
			return Plurival.of((FeatureStructure) input);
		}

		final RoleFrameSlot roleFrameCollection = (RoleFrameSlot) input;

		return roleFrameCollection.toFillingInSlot();
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		return getSubTerm().toString(true);
	}
}
