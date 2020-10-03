package de.nb.federkiel.logic;

import java.util.List;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Utility methods for building and dealing with
 * first-order logic formulae.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public final class FormulaUtil {
	private FormulaUtil() {
		super();
	}

	public static <A extends IAssignment> IFormula<A> and(
			final List<? extends IFormula<A>> formulae) {
		final int numFormulae = formulae.size();

		if (numFormulae == 1) {
			return formulae.get(0);
		}

		if (numFormulae >= 2) {
			return new AndFormula<>(formulae.get(0), and(formulae.subList(1, numFormulae)));
		}

		// numFormulae == 0
		return BooleanConstantTrue.<A>getInstance();
	}

	public static <A extends IAssignment> IFormula<A> or(
			final List<? extends IFormula<A>> formulae) {
		final int numFormulae = formulae.size();

		if (numFormulae == 1) {
			return formulae.get(0);
		}

		if (numFormulae >= 2) {
			return new OrFormula<>(formulae.get(0), or(formulae.subList(1, numFormulae)));
		}

		// numFormulae == 0
		return BooleanConstantFalse.<A>getInstance();
	}
}
