package de.nb.federkiel.feature;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.plurivallogic.BinaryCompoundPlurivalTerm;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A term, that is build up from two (sub-)terms. This term unifies the to subterms: It checks for
 * String equality (allowing unspecified values) and returns the most specific value, if the are
 * counted as equal. If not equal, this term has no result.
 * <p>
 * Examples:
 * <ul>
 * <li>"sg" unified with "sg" yields "sg"
 * <li>"sg" unified with UNSPECIFIED yields "sg"
 * <li>UNSPECIFIED unified with UNSPECIFIED yields UNSPECIFIED
 * <li>"sg" unified with "pl" has no result (empty Plurival)
 * </ul>
 *
 * @author nbudzyn 2017
 */
public class ThreeStateFeatureUnifyPlurivalTerm extends
    BinaryCompoundPlurivalTerm<IFeatureValue, IFeatureValue, IFeatureValue, FeatureAssignment> {

  public ThreeStateFeatureUnifyPlurivalTerm(
      final IPlurivalTerm<IFeatureValue, FeatureAssignment> firstSubTerm,
      final IPlurivalTerm<IFeatureValue, FeatureAssignment> secondSubTerm) {
    super(firstSubTerm, secondSubTerm);
  }

  @Override
  public Plurival<IFeatureValue> calculate(final IFeatureValue first, final IFeatureValue second) {
    final IFeatureValue unified = FeatureStructure.unifyStrings(first, second);
    if (unified != null) {
      return Plurival.of(unified);
    }

    return Plurival.<IFeatureValue>empty();
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
