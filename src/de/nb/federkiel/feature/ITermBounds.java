package de.nb.federkiel.feature;

import de.nb.federkiel.logic.CannotFulfillTermException;
import de.nb.federkiel.logic.IDataFlowElement;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * Some bounds for the possible values of a term
 * <p>
 * For example a role frame value, that contains all slots, that some role frame
 * collection term might contain at most, after being fully evaluated
 * <p>
 * Note that for each subclass, we need a combineBounds() method.
 *
 * @author nbudzyn 2010
 */
@Deprecated
public interface ITermBounds extends IDataFlowElement {
	/**
	 * Combines (adds) some other bounds to these bounds.
	 * <p>
	 * All these <code>combineBounds()</code> operations shall be reflexive!
	 *
	 * @throws CannotFulfillTermException
	 *             , if there is no possible assigment, that could fulfill these
	 *             bounds and the other bounds.
	 */
	ITermBounds combineBounds(ITermBounds moreBounds)
	throws CannotFulfillTermException;

	/**
	 * Combines (adds) some only-value bounds to these bounds
	 *
	 * @throws CannotFulfillTermException
	 *             , if there is no possible assigment, that could fulfill these
	 *             bounds and the other bounds.
	 */
	ITermBounds combineBounds(
			final OnlyValueBound<?> otherOnlyValueBound)
	throws CannotFulfillTermException;

	/**
	 * Combines (adds) some role frame bounds to these bounds
	 *
	 * @throws CannotFulfillTermException
	 *             , if there is no possible assigment, that could fulfill these
	 *             bounds and the other bounds.
	 */
	ITermBounds combineBounds(final FeatureStructure roleFrameBounds)
	throws CannotFulfillTermException;

	/**
	 * If we have two terms with two term bounds - <code>this</code> and the
	 * <code>other</code> one - what will be the resulting term bound for the
	 * term, that results from merging the two terms?
	 */
	Plurival<FeatureStructure> mergeBounds(ITermBounds other);
}
