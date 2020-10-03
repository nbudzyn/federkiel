package de.nb.federkiel.semantik;

import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.interfaces.ISemantics;

/**
 * Semantical instance for "means nothing in particular".
 * <p>
 * Immutable.
 *
 * @author nbudzyn 2009
 */
@Immutable
public final class NothingInParticularSemantics implements ISemantics
{
	final public static NothingInParticularSemantics INSTANCE = new NothingInParticularSemantics();

	private NothingInParticularSemantics() {
		super();
	}

	@Override
	public String toString() {
		return "(ohne besondere Bedeutung)";
	}

	@Override
	public int hashCode() {
		return 0;
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

		return true;
	}

	@Override
	public int compareTo(final ISemantics o) {
		// This method shall be consistent with equals().
		final int classNameCompared = this.getClass().getCanonicalName()
				.compareTo(o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		return 0;
	}
}
