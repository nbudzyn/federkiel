package de.nb.federkiel.logic;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * An first-order logic formula, that consists of two formulas, connected with a logical operator
 * (AND, OR, ...).
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
abstract class BinaryLogicalOperatorFormula<A extends IAssignment> implements IFormula<A> {

  private final IFormula<A> firstFormula;
  private final IFormula<A> secondFormula;

  /**
   * The string to use for the (infix) operator (when displaying the formula), (&quot;AND&quot;,
   * e.g.)
   */
  private final String operatorString;

  protected BinaryLogicalOperatorFormula(final IFormula<A> firstFormula,
      final IFormula<A> secondFormula, final String operatorString) {
    super();
    this.firstFormula = firstFormula;
    this.secondFormula = secondFormula;
    this.operatorString = operatorString;
  }

  public IFormula<A> getFirstFormula() {
    return this.firstFormula;
  }

  public IFormula<A> getSecondFormula() {
    return this.secondFormula;
  }

  @Override
  public ImmutableSet<Variable<?, A>> getAllVariables() {
    return Sets.union(firstFormula.getAllVariables(),
        secondFormula.getAllVariables()).immutableCopy();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.firstFormula.hashCode();
    result = prime * result + this.secondFormula.hashCode();
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
    final BinaryLogicalOperatorFormula<?> other = (BinaryLogicalOperatorFormula<?>) obj;
    if (!this.firstFormula.equals(other.firstFormula)) {
      return false;
    }
    if (!this.secondFormula.equals(other.secondFormula)) {
      return false;
    }
    return true;
  }


  @Override
  public String toString() {
    return toString(false);
  }

  @Override
  public String toString(final boolean surroundWithBracketsIfApplicable) {
    final StringBuilder res = new StringBuilder();
    if (surroundWithBracketsIfApplicable) {
      res.append("(");
    }

    res.append(this.firstFormula.toString(true)); // brackets (if applicable)

    res.append(" ");
    res.append(this.operatorString);
    res.append(" ");
    res.append(this.secondFormula.toString(true)); // brackets (if applicable)

    if (surroundWithBracketsIfApplicable) {
      res.append(")");
    }

    return res.toString();
  }

  @Override
  public int compareTo(final IFormula<? extends IAssignment> obj) {
    final int classNameCompared =
        this.getClass().getCanonicalName().compareTo(obj.getClass().getCanonicalName());
    if (classNameCompared != 0) {
      return classNameCompared;
    }

    final BinaryLogicalOperatorFormula<? extends IAssignment> other =
        (BinaryLogicalOperatorFormula<? extends IAssignment>) obj;

    final int firstFormulasCompared = this.firstFormula.compareTo(other.firstFormula);
    if (firstFormulasCompared != 0) {
      return firstFormulasCompared;
    }

    return this.secondFormula.compareTo(other.secondFormula);
  }

}
