package de.nb.federkiel.feature;

import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.UnaryCompoundTerm;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.YieldsNoResultException;

/**
 * A term, of which the value is a role frame COLLECTION, but which has only one
 * argument (a role frame term). So this is a kind of <i>singleton operator</i>
 * for role frame terms.
 *
 * @author nbudzyn 2009
 */
public final class RoleFrameSingletonTerm
		extends UnaryCompoundTerm<RoleFrameCollection, FeatureStructure, FeatureAssignment> {

	/**
	 * This class is currently not used.
	 *
	 * @param roleFrameTerm
	 */
	private RoleFrameSingletonTerm(final ITerm<FeatureStructure, FeatureAssignment> roleFrameTerm) {
		super(roleFrameTerm);
	}

	@Override
	public RoleFrameCollection evaluate(final FeatureAssignment variableAssignment)
			throws UnassignedVariableException, YieldsNoResultException {
		final FeatureStructure roleFrame = getSubTerm().evaluate(variableAssignment); // UnassignedVariableException,
																																						// YieldsNoResultException

		return RoleFrameCollection.of(roleFrame);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		// no (additional) brackets applicable
		final StringBuilder res = new StringBuilder();

		res.append("{ ");
		res.append(getSubTerm().toString(false));
		res.append(" }");

		return res.toString();
	}
}
