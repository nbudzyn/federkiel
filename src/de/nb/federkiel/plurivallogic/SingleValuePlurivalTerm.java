package de.nb.federkiel.plurivallogic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.UnassignedVariableException;


/**
 * Special case of an term, that could have several results: This one only has one result!<p>
 * Useful for joining single-and-multiple-result-terms.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class SingleValuePlurivalTerm<T extends Object, A extends IAssignment>
implements IPlurivalTerm<T, A> {
	private final ITerm<T, A> subTerm;

	private SingleValuePlurivalTerm(final ITerm<T, A> subTerm) {
		super();
		this.subTerm = subTerm;
	}

	public ITerm<T, A> getSubTerm() {
		return this.subTerm;
	}

	public static <T extends Object, A extends IAssignment> IPlurivalTerm<T, A> of(
			final ITerm<T,A> term) {
		return new SingleValuePlurivalTerm<>(term);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Plurival<T> evaluate(final A variableAssignment) throws UnassignedVariableException {
		return Plurival.of(
				this.subTerm.evaluate(variableAssignment) // UnassignedVariableException
		);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.subTerm.hashCode();
		return result;
	}

	@SuppressWarnings("unchecked")
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
		final SingleValuePlurivalTerm<? extends Object,
				? extends IAssignment> other =
					(SingleValuePlurivalTerm<? extends Object,
							? extends IAssignment>) obj; // unchecked cast
		if (!this.subTerm.equals(other.subTerm)) {
			return false;
		}

		return true;
	}

	@Override
	public int compareTo(final IPlurivalTerm<?, ? extends IAssignment> o) {
		final int classNameCompared =
			this.getClass().getCanonicalName().compareTo(
					o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final SingleValuePlurivalTerm<?, ? extends IAssignment> other =
			(SingleValuePlurivalTerm<?, ? extends IAssignment>) o;

		return this.subTerm.compareTo(other.subTerm);
	}

	@Override
	public String toString() {
		return this.toString(false);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		return this.subTerm.toString(surroundWithBracketsIfApplicable);
	}

}