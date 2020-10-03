package de.nb.federkiel.feature;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.logic.BinaryCompoundTerm;
import de.nb.federkiel.logic.ITerm;

/**
 * A term, that is build up from two (sub-)terms. If the first sub-term is specified, then this is
 * the value of the term - otherwise the value of the second sub-term is the value of the term
 * (functioning as a "default value").
 *
 * @author nbudzyn 2011
 */
public class FeatureDefaultTerm
    extends BinaryCompoundTerm<IFeatureValue, IFeatureValue, IFeatureValue, FeatureAssignment> {

  public FeatureDefaultTerm(final ITerm<IFeatureValue, FeatureAssignment> firstSubTerm,
      final ITerm<IFeatureValue, FeatureAssignment> secondSubTerm) {
    super(firstSubTerm, secondSubTerm);
  }

  @Override
  public IFeatureValue calculate(final IFeatureValue first, final IFeatureValue second) {
    if (!UnspecifiedFeatureValue.INSTANCE.equals(first)) {
      return first;
    }

    return second;
  }

  @Override
  public String toString(final boolean surroundWithBracketsIfApplicable) {
    final StringBuilder res = new StringBuilder();
    if (surroundWithBracketsIfApplicable) {
      res.append("(");
    }
    res.append(getFirstSubTerm().toString(true));
    res.append(" JOKER_DEFAULTING_TO ");
    res.append(getSecondSubTerm().toString(true));
    if (surroundWithBracketsIfApplicable) {
      res.append(")");
    }
    return res.toString();
  }
}
