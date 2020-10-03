package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;


/**
 * The logical constant False.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
final class BooleanConstantFalse<A extends IAssignment> implements
		IFormula<A> {
	private BooleanConstantFalse() {
		super();
	}

	protected static <A extends IAssignment> BooleanConstantFalse<A> getInstance() {
		return new BooleanConstantFalse<>();
	}

	@Override
	public boolean evaluate(final IAssignment variableAssignment) {
		return false;
	}

  @Override
  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return ImmutableSet.of();
  }

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public int compareTo(final IFormula<? extends IAssignment> obj) {
		final int classNameCompared =
			this.getClass().getCanonicalName().compareTo(
					obj.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		// Class names are equal
		return 0;
	}


	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		// brackets are not applicable here
		return "FALSE";
	}
}
