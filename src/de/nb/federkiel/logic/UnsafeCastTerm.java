package de.nb.federkiel.logic;

/**
 * This term is necessary, when a term with a LESS specific
 * type is used (in another term or a formula), where a term with a MORE SPECIFIC
 * type is expected.<p>
 * This type of cast is NOT SAFE! We need the term to conform to the
 * necessities of the generics, but it CAN LEAD TO AN ERROR AT RUNTIME!<p>
 * ST is the sub-type, UT is the upper type. -
 * A is the variable assignment type.
 *
 * @author nbudzyn 2009
 */
public final class UnsafeCastTerm<UT extends Object, ST extends UT, A extends IAssignment>
implements ITerm<ST, A> {
	private final ITerm<UT, A> term;

	private UnsafeCastTerm(final ITerm<UT, A> term) {
		this.term = term;
	}

	public static <UT extends Object, ST extends UT, A extends IAssignment>
	UnsafeCastTerm<UT, ST, A> of (final ITerm<UT, A> term) {
		return new UnsafeCastTerm<UT, ST, A>(term);
	}

	@Override
	public ST evaluate(final A variableAssignment) throws UnassignedVariableException {
		// This cast is unsafe and can lead to an exception at runtime!
		// IDEA: Have some kind of TermEvalutationException?
		return (ST) this.term.evaluate(variableAssignment); // UnassignedVariableException
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		return this.term.toString(surroundWithBracketsIfApplicable);
	}

	@Override
	public String toString() {
		return this.term.toString();
	}

	@Override
	public int hashCode() {
		return this.term.hashCode();
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

		final UnsafeCastTerm<?, ?, ?> other = (UnsafeCastTerm<?, ?, ?>) obj;

		return this.term.equals(other.term);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(final ITerm<? extends Object, ? extends IAssignment> o) {
		final int classNameCompared =
			this.getClass().getCanonicalName().compareTo(
					o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final UnsafeCastTerm<?, ?, ? extends IAssignment> other =
			(UnsafeCastTerm<?, ?, ? extends IAssignment>) o;

		return this.term.compareTo(other.term);
	}
}
