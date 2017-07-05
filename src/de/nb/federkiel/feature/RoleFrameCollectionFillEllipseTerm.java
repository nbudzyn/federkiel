package de.nb.federkiel.feature;

import de.nb.federkiel.plurivallogic.BinaryCompoundPlurivalTerm;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A term that joins two role frame collections, in which the left one acts as the BASE for filling
 * some &quot;blanks&quot; in the right one, the ELLIPSE.
 * <p>
 * When X and Y are role frame collections, the resulting role frame collection contains all
 * ellipse-fillings of each role frame of X with each role frame of Y.
 * <p>
 * Filling <i>role frame</i> ellipse means copying some filled slots from the base and filling other
 * slots (taken from the base) with free filling from the ellipse.
 * <p>
 * In some cases, this operation is not possible, in other cases there are several possibilities
 * (filling A fills slot X, filling B fills Slot Y; or the other way round). So, there are several
 * result alternatives.
 *
 * @author nbudzyn 2009
 */
public class RoleFrameCollectionFillEllipseTerm extends
BinaryCompoundPlurivalTerm<RoleFrameCollection,
RoleFrameCollection, RoleFrameCollection, FeatureAssignment> {

	public RoleFrameCollectionFillEllipseTerm(
			final IPlurivalTerm<RoleFrameCollection, FeatureAssignment> firstSubTerm,
			final IPlurivalTerm<RoleFrameCollection, FeatureAssignment> secondSubTerm) {
		super(firstSubTerm, secondSubTerm);
	}

	@Override
	public Plurival<RoleFrameCollection> calculate(
			final RoleFrameCollection base, final RoleFrameCollection ellipse) {
		/* FIXME Test
		if (!ellipse.isEmpty()) {
			System.out.println("Test");
		}
		 */

		return base.fillEllipse(ellipse);
	}


	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		final StringBuilder res = new StringBuilder();
		if (surroundWithBracketsIfApplicable) {
			res.append("(");
		}
		res.append(getFirstSubTerm().toString(true));
		res.append(" FILL_ELLIPSE ");
		res.append(getSecondSubTerm().toString(true));
		if (surroundWithBracketsIfApplicable) {
			res.append(")");
		}
		return res.toString();

	}
}
