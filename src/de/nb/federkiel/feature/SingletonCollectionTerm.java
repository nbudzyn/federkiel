package de.nb.federkiel.feature;

import java.util.Collection;
import java.util.Collections;

import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.UnaryCompoundTerm;
import de.nb.federkiel.logic.UnassignedVariableException;

/**
 * This is a kind of <i>general singleton operator. This term has a value-typed argument
 * and returns a collection of this argument.
 *
 * @author nbudzyn 2009
 */
@SuppressWarnings("ucd")
public class SingletonCollectionTerm<S extends Object, A extends IAssignment> extends
UnaryCompoundTerm<Collection<S>, S, A> {

	public SingletonCollectionTerm(
			final ITerm<S, A> subTerm) {
		super(subTerm);
	}

	@Override
	public Collection<S> evaluate(final A assignment)
	throws UnassignedVariableException {
		final S subValue =
			getSubTerm().evaluate(assignment); // UnassignedVariableException

		return Collections.singletonList(subValue);
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
