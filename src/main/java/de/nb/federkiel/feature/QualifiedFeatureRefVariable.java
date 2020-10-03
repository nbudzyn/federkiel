package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.Variable;

/**
 * A variable in first-order logic that corresponds
 * to a QUALIFIED feature name like <code>x.kasus</code>,
 * that means, a feature in a(nother) symbol reference (in a grammar rule).
 * (Typically, this variable will be
 * assigned to the feature value of this other symbol.)
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class QualifiedFeatureRefVariable extends Variable<IFeatureValue, FeatureAssignment> {
	/**
	 * The position-in-the-rule of the symbol, that is referenced.
	 */
	private final int symbolRefPosition;

	/**
	 * The string by which the symbol is referenced - only for <code>toString</code> output.
	 */
	private final String symbolRefString;

	/**
	 * The name of the feature, that is referenced.
	 */
	private final String featureName;

	public QualifiedFeatureRefVariable(
			final int symbolRefPosition, final String symbolRefString, final String featureName) {
		super();
		this.symbolRefPosition = symbolRefPosition;
		this.symbolRefString = symbolRefString;
		this.featureName = featureName;
	}

	public String getFeatureName() {
		return featureName;
	}

	public int getSymbolRefPosition() {
		return symbolRefPosition;
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

		final QualifiedFeatureRefVariable other = (QualifiedFeatureRefVariable) obj;

		if (! featureName.equals(other.featureName)) {
			return false;
		}

		if (symbolRefPosition != other.symbolRefPosition) {
			return false;
		}

		if (! symbolRefString.equals(other.symbolRefString)) {
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

		final QualifiedFeatureRefVariable other = (QualifiedFeatureRefVariable) o;

		if (symbolRefPosition < other.getSymbolRefPosition()) {
			return -1;
		}

		if (symbolRefPosition > other.getSymbolRefPosition()) {
			return 1;
		}

		final int featNamesCompared = featureName.compareTo(other.featureName);
		if (featNamesCompared != 0) {
			return featNamesCompared;
		}

		return symbolRefString.compareTo(other.symbolRefString);
	}


	@Override
	public int hashCode() {
		return featureName.hashCode() * 31 + symbolRefPosition;
		// String will typically be the same for the same position.
	}

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		// brackets are not applicable
		final StringBuilder res = new StringBuilder(40);

		res.append(symbolRefString);
		res.append(".");
		res.append(featureName);

		return res.toString();
	}
}
