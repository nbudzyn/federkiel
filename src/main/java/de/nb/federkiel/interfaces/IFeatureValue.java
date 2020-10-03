package de.nb.federkiel.interfaces;

import de.nb.federkiel.feature.SurfacePart;

/**
 * A value a feature could have.
 * <p>
 * All implementation MUST BE IMMUTABLE.
 *
 * @author nbudzyn 2009
 */
@javax.annotation.concurrent.Immutable
public interface IFeatureValue extends Comparable<IFeatureValue> {
	SurfacePart getSurfacePart();

	/**
	 * @param neverShowRequirements if <code>true</code>, requirements are never
	 *                              shown, even not if a slotted feature is empty
	 * @param forceShowRequirements if <code>true</code>, requirements are shown,
	 *                              even if the feature is filled.
	 */
	public String toString(final boolean neverShowRequirements, final boolean forceShowRequirements);

	/*
	 * If both <code>this</code> and the <code>otherValue</code> are <i>role frame
	 * collections</i>, then this method tries to find the <i>single point of
	 * difference<i> between the two (it will also be fine, if there is no essential
	 * difference); otherwise (especially in the case of atomic features), this
	 * method returns succesfully if and only if the two values have the same type
	 * and are equal.
	 *
	 * @return the <i>single point of difference<i> between the two feature values -
	 * or <code>null</code>, if there was no SINGLE difference found
	 *
	 * @throws TooManyDifferencesException if there is more than one &quot;point if
	 * difference&quot; between the <code>this</code> and the
	 * <code>otherValue</code> (for two role frame collection features) -- or if the
	 * two feature values are NOT equal (all other cases).
	 *
	 * ParseAlternativesDifference
	 * findSinglePointOfDifferenceForRoleFrameCollFeaturesAndCheckAtomicFeatures(
	 * final IFeatureValue otherValue) throws TooManyDifferencesException;
	 */
}
