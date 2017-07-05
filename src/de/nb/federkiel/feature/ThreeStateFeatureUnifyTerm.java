package de.nb.federkiel.feature;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.logic.BinaryCompoundTerm;
import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.YieldsNoResultException;

/**
 * A term, that is build up from two (sub-)terms. This term unifies the to subterms: It checks for
 * String equality (allowing unspecified values) and returns the most specific value, if the are
 * counted as equal. If not equal, this term has no result and the calculation has to be aborted.
 * <p>
 * Examples:
 * <ul>
 * <li>"sg" unified with "sg" yields "sg"
 * <li>"sg" unified with UNSPECIFIED yields "sg"
 * <li>UNSPECIFIED unified with UNSPECIFIED yields UNSPECIFIED
 * <li>"sg" unified with "pl" causes the calculation to be aborted
 * </ul>
 *
 * @author nbudzyn 2017
 */
public class ThreeStateFeatureUnifyTerm
    extends BinaryCompoundTerm<IFeatureValue, IFeatureValue, IFeatureValue, FeatureAssignment> {

  public ThreeStateFeatureUnifyTerm(final ITerm<IFeatureValue, FeatureAssignment> firstSubTerm,
      final ITerm<IFeatureValue, FeatureAssignment> secondSubTerm) {
    super(firstSubTerm, secondSubTerm);
  }

  @Override
  public IFeatureValue calculate(final IFeatureValue first, final IFeatureValue second)
      throws YieldsNoResultException {
    final IFeatureValue unified = FeatureStructure.unifyStrings(first, second);
    if (unified != null) {
      return unified;
    }

    throw new YieldsNoResultException();
  }

  @Override
  public String toString(final boolean surroundWithBracketsIfApplicable) {
    final StringBuilder res = new StringBuilder();
    if (surroundWithBracketsIfApplicable) {
      res.append("(");
    }
    res.append(getFirstSubTerm().toString(true));
    res.append(" U ");
    res.append(getSecondSubTerm().toString(true));
    if (surroundWithBracketsIfApplicable) {
      res.append(")");
    }
    return res.toString();
  }
}
