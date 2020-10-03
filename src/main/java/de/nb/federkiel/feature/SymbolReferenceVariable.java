package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.Variable;

/**
 * A variable in first-order logic that corresponds
 * to a(nother) symbol (in a grammar rule).
 * (Typically, this variable will be
 *  assigned to the realization alternatives of this other symbol.)
 *
 * @author nbudzyn 2009
 *
 * @param T The type of the variable term, could by {@link IConstituentAlternatives} or
 * {@link IHomogeneousConstituentAlternatives}, e.g.
 */
@Immutable
@ThreadSafe
public class SymbolReferenceVariable<T extends IConstituentAlternatives>
extends Variable<T, FeatureAssignment> {
	/**
	 * The position-in-the-rule of the symbol, that is referenced.
	 */
	private final int symbolRefPosition;

	/**
	 * The string by which the symbol is referenced - only for <code>toString</code> output.
	 */
	private final String symbolRefString;

	public SymbolReferenceVariable(
			final int symbolRefPosition, final String symbolRefString) {
		super();
		this.symbolRefPosition = symbolRefPosition;
		this.symbolRefString = symbolRefString;
	}

	public int getSymbolRefPosition() {
		return this.symbolRefPosition;
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

		final SymbolReferenceVariable<?> other = (SymbolReferenceVariable<?>) obj;

		if (this.symbolRefPosition != other.symbolRefPosition) {
			return false;
		}

		if (! this.symbolRefString.equals(other.symbolRefString)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return this.symbolRefPosition;
		// String will typically be the same for the same position.
	}

	@Override
	public int compareTo(final ITerm<? extends Object, ? extends IAssignment> o) {
		final int classNameCompared =
			this.getClass().getCanonicalName().compareTo(
					o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final SymbolReferenceVariable<?> other = (SymbolReferenceVariable<?>) o;

		if (this.symbolRefPosition < other.getSymbolRefPosition()) {
			return -1;
		}

		if (this.symbolRefPosition > other.getSymbolRefPosition()) {
			return 1;
		}

		return this.symbolRefString.compareTo(other.symbolRefString);
	}

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		// brackets are not applicable
		final StringBuilder res = new StringBuilder(40);

		res.append(this.symbolRefString);

		return res.toString();
	}
}
