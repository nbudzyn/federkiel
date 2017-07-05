package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.logic.BinaryPredicateFormula;
import de.nb.federkiel.logic.Constant;
import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.YieldsNoResultException;


/**
 * An equaliy (t1 = t2) in firstTerm-order logic, where the terms evalute to Features
 * and where an UNSPECIFIED feature equals everything.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class ThreeStateFeatureEqualityFormula
extends BinaryPredicateFormula<IFeatureValue, IFeatureValue, FeatureAssignment> {
	public ThreeStateFeatureEqualityFormula(
			final ITerm<IFeatureValue, FeatureAssignment> firstTerm,
			final ITerm<IFeatureValue, FeatureAssignment> secondTerm) {
		super(firstTerm, secondTerm);
	}

	@Override
	public boolean evaluate(final FeatureAssignment variableAssignment)
      throws UnassignedVariableException, YieldsNoResultException {
		return FeatureStructure.doFeatureValuesMatch(
        getFirstTerm().evaluate(variableAssignment), // UnassignedVariableException,
                                                     // YieldsNoResultException
        getSecondTerm().evaluate(variableAssignment)); // UnassignedVariableException,
                                                       // YieldsNoResultException
	}


	/**
	 * Factory method, generates something like
	 * <i>kasus=&quot;akk&quot;</i>.
	 */
	public static ThreeStateFeatureEqualityFormula featureEqualsExplicitValue(
			final String featureName, final String stringFeatureValueOrMarkerForUnspecified) {
		return featureEqualsExplicitValue(featureName,
				FeatureStructure.toFeatureValue(stringFeatureValueOrMarkerForUnspecified));
	}

	/**
	 * Factory method, generates something like
	 * <i>kasus=&quot;akk&quot;</i>.
	 */
	private static ThreeStateFeatureEqualityFormula featureEqualsExplicitValue(
			final String featureName, final IFeatureValue featureValue) {
		return new ThreeStateFeatureEqualityFormula(
				new UnqualifiedFeatureRefVariable(featureName),
				new Constant<IFeatureValue, FeatureAssignment>(featureValue));
	}


	/**
	 * Factory method, generates something like
	 * <i>kasus=PRAEPOSITION.erwarteterKasus;</i>.
	 */
	public static ThreeStateFeatureEqualityFormula featureEqualsFeatureReference(
			final String featureName,
			final int otherSymbolRefPosition,
			final String otherSymbolRefString,
			final String otherFeatureName) {
		return new ThreeStateFeatureEqualityFormula(
				new UnqualifiedFeatureRefVariable(featureName),
				new QualifiedFeatureRefVariable(
						otherSymbolRefPosition,
						otherSymbolRefString,
						otherFeatureName));
	}



	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		// brackets are not applicable here
		final StringBuilder res = new StringBuilder();
		res.append(getFirstTerm().toString());
		res.append("==");
		res.append(getSecondTerm().toString());
		return res.toString();

	}
}
