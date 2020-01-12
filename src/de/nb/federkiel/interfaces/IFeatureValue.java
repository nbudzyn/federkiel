package de.nb.federkiel.interfaces;

import de.nb.federkiel.feature.FillingInSlot;
import de.nb.federkiel.feature.IHomogeneousConstituentAlternatives;

/**
 * A value a feature could have.
 * <p>
 * All implementation MUST BE IMMUTABLE.
 *
 * @author nbudzyn 2009
 */
@javax.annotation.concurrent.Immutable
public interface IFeatureValue extends Comparable<IFeatureValue> {
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
	 * @return <code>true</code>, iff the feature contains any {@link FillingInSlot}
	 *         that is equal to a <code>FillingInSlot</code> of the other feature
	 */
	boolean hasOneEqualFillingInSlotAs(IFeatureValue other);

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
