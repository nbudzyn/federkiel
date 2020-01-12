package de.nb.federkiel.interfaces;

import java.util.Collection;

import de.nb.federkiel.feature.FillingInSlot;
import de.nb.federkiel.feature.IHomogeneousConstituentAlternatives;
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
	 * Checks whether this (additional) filling would be acceptable for this
	 * feature. If the filling would be acceptable, the methode returns a copy of
	 * this (slotted) feature with this filling added. Otherwise, the method returns
	 * <code>null</code>.
	 */
	IFeatureValue addFillingIfAccepted(final IHomogeneousConstituentAlternatives freeFilling,
			int keepPlaceFreeForHowManyFillings);

	/**
	 * @return How many fillings are missing, until this feature is completed?
	 */
	int howManyFillingsAreMissingUntilCompletion();

	/**
	 * Whether this feature value is completed. For a role frame collection, for
	 * example, this could mean, that all slot are filled and there are no free
	 * fillings...
	 */
	boolean isCompleted();

	/**
	 * @param neverShowRequirements if <code>true</code>, requirements are never
	 *                              shown, even not if a slotted feature is empty
	 * @param forceShowRequirements if <code>true</code>, requirements are shown,
	 *                              even if the feature is filled.
	 */
	public String toString(final boolean neverShowRequirements, final boolean forceShowRequirements);

	/**
	 * @return How many <i>additional</i> fillings are allowed? - <i>-1</i>, if
	 *         there is <i>no upper bound</i>.
	 */
	int howManyAdditionalFillingsAreAllowed();

	Collection<FillingInSlot> getFillings();

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
