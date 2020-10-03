package de.nb.federkiel.logic;

import de.nb.federkiel.feature.ITermBounds;

/**
 * Abstract superclass for some bounds for the possible values of a term.
 *
 * @author nbudzyn 2010
 */
@Deprecated
abstract public class AbstractTermBounds implements ITermBounds
{

	@Override
	public final ITermBounds combineBounds(final ITermBounds moreBounds)
	throws CannotFulfillTermException {
		/*
		if (moreBounds instanceof OnlyValueBound<?>) {
			return combineBounds(moreBounds);
		}
		if (moreBounds instanceof RoleFrame) {
			final RoleFrame roleFrameBounds = (RoleFrame) moreBounds;

			// does this.onlyValue fulfills the restrictions, given by the other
			// role frame?
			return combineBounds(roleFrameBounds);
		}

		 */
		throw new IllegalStateException("Unkwown type of ITermBounds: "
				+ moreBounds);
	}
}
