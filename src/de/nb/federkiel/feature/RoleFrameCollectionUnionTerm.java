package de.nb.federkiel.feature;

import de.nb.federkiel.plurivallogic.BinaryCompoundPlurivalTerm;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A union term, that joins two role frame collections.<p>
 * A union is NOT POSSIBLE (no result alternative), iff the two role frame collections
 * do contain role frames which have <i>the same filling</i> filled into
 * <i>slots with different names</i>.
 *
 * @author nbudzyn 2009
 */
public class RoleFrameCollectionUnionTerm extends
BinaryCompoundPlurivalTerm<RoleFrameCollection,
RoleFrameCollection, RoleFrameCollection, FeatureAssignment> {

	public RoleFrameCollectionUnionTerm(
			final IPlurivalTerm<RoleFrameCollection, FeatureAssignment> firstSubTerm,
			final IPlurivalTerm<RoleFrameCollection, FeatureAssignment> secondSubTerm) {
		super(firstSubTerm, secondSubTerm);
	}

	/* not used
	public static ITerm<RoleFrameCollection, FeatureAssignment> union
		(final Collection<ITerm<RoleFrame, FeatureAssignment>> terms) {
		// A U B U C U D -> ((((A U B) U C ) U D)
		final Iterator<ITerm<RoleFrame, FeatureAssignment>> iter = terms.iterator();

		if (! iter.hasNext()) {
			return Constant.of(RoleFrameCollection.EMPTY);
		}

		ITerm<RoleFrameCollection, FeatureAssignment> res = iter.next();
		while (iter.hasNext()) {
			res = new RoleFrameCollectionUnionTerm (res, iter.next());
		}

		return res;
	}
	 */

	@Override
	public Plurival<RoleFrameCollection> calculate(
			final RoleFrameCollection first, final RoleFrameCollection second) {
		final RoleFrameCollection union = first.union(second);
		if (union == null) {
			// union was not possible (the same filling in both role frames, but in different slots, e.g.)
			return Plurival.empty(); // ==>
		}

		return Plurival.of(union);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		final StringBuilder res = new StringBuilder();
		if (surroundWithBracketsIfApplicable) {
			res.append("(");
		}
		res.append(getFirstSubTerm().toString(true));
		res.append(" UNION ");
		res.append(getSecondSubTerm().toString(true));
		if (surroundWithBracketsIfApplicable) {
			res.append(")");
		}
		return res.toString();

	}
}
