package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.feature.ITermBounds;

/**
 * A class that contains:
 * <ul>
 * some bounds for the possible values of a term (for example a role frame
 * value, that contains all slots, that some role frame collection term might
 * contain at most, after being fully evaluated) - or <code>null</code>, if
 * there are no bounds known
 * <li>some bounds for a variable (for example restrictions for a role frame
 * variable, that say, that this variable, IF it does NOT have any slots (but
 * free fillings, possibly), there may be at most one free filing, matching a
 * <code>Subjekt</code> slot, but any number of free fillings matching a
 * <code>AdverbialeAngabe</code> slot) - or <code>null</code>, if there are no
 * such bounds known
 * <li>
 * </ul>
 * <p>
 *
 * @author nbudzyn 2010
 */
@Immutable
@ThreadSafe
public class BoundsForTermAndBoundsForVariable {
	final private IDataFlowElement boundsForTerm;

	final private ITermBounds boundsForVariable;

	public static final BoundsForTermAndBoundsForVariable NONE = new BoundsForTermAndBoundsForVariable(
			null, null);

	public BoundsForTermAndBoundsForVariable(
			final IDataFlowElement boundsForTerm,
			final ITermBounds boundsForVariable) {
		super();
		this.boundsForTerm = boundsForTerm;
		this.boundsForVariable = boundsForVariable;
	}

	public IDataFlowElement getBoundsForTerm() {
		return boundsForTerm;
	}

	public ITermBounds getBoundsForVariable() {
		return boundsForVariable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime
		* result
		+ ((boundsForVariable == null) ? 0
				: boundsForVariable.hashCode());

		result = prime
		* result
		+ ((boundsForTerm == null) ? 0
				: boundsForTerm.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BoundsForTermAndBoundsForVariable other = (BoundsForTermAndBoundsForVariable) obj;

		if (boundsForVariable == null) {
			if (other.boundsForVariable != null) {
				return false;
			}
		} else if (!boundsForVariable
				.equals(other.boundsForVariable)) {
			return false;
		}

		if (boundsForTerm == null) {
			if (other.boundsForTerm != null) {
				return false;
			}
		} else if (!boundsForTerm.equals(other.boundsForTerm)) {
			return false;
		}

		return true;
	}

}
