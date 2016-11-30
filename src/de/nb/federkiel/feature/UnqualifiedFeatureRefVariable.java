package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.Variable;

/**
 * A variable in first-order logic that corresponds
 * to an UNQUALIFIED feature name like <code>kasus</code>(typically, this variable will be
 * assigned to the feature value).
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class UnqualifiedFeatureRefVariable extends Variable<IFeatureValue, FeatureAssignment> {
	private final String featureName;

	public UnqualifiedFeatureRefVariable(final String featureName) {
		super();
		this.featureName = featureName;
	}

	public String getFeatureName() {
		return featureName;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}

		final UnqualifiedFeatureRefVariable other = (UnqualifiedFeatureRefVariable) obj;

		if (! featureName.equals(other.featureName)) {
			return false;
		}

		return true;
	}

	@Override
	public int compareTo(final ITerm<? extends Object, ? extends IAssignment> o) {
		final int classNameCompared =
			this.getClass().getCanonicalName().compareTo(
					o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final UnqualifiedFeatureRefVariable other = (UnqualifiedFeatureRefVariable) o;

		return featureName.compareTo(other.featureName);
	}


	@Override
	public int hashCode() {
		return featureName.hashCode();
	}

	@Override
	public String toString() {
		return this.toString(false);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		// brackets are not applicable
		return featureName;
	}

}
