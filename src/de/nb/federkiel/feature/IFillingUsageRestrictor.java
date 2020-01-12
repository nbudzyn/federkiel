package de.nb.federkiel.feature;


/**
 * This restrictor can restrict the usage of a free filling, so that it can only
 * be used to fill a slot or feature with specified name.
 * <p>
 * This is necessary to ensure that in a ellipse-like sentence like <i>Paul war
 * Komponist und ab 1924 Dirigent.<i>, Paul ist the <i>Subjekt<i> in both parts
 * - not the <i>Subjekt</i> in one part and the <i>Praedikatsnomen</i> in the
 * other!)
 *
 * @author nbudzyn 2009
 */
interface IFillingUsageRestrictor {
	/**
	 * @return the only slot or feature name which is allowed for this free filling
	 *         - or <code>null</code>, if the name is <i>not</i> restricted for this
	 *         free filling
	 */
	String getRestrictedNameFor(
			IHomogeneousConstituentAlternatives freeFilling);

	/**
	 * The restrictor can prescribe, that at least <i>x</i> fillings must fit into a
	 * slot / feature - after filling the slot / feature. Example: There are two
	 * role frames in a role frame collection. They both need at least <i>one</i>
	 * Akkusativobjekt. When the first role frame has <i>no Akkusativobjekt yet</i>,
	 * and the Akkusativobjekt-slot of the second role frame could be filled with
	 * some part of the input - without the first one being also filled!! - the role
	 * frame collection should say: NO - there must be at least <i>one</i> place
	 * kept free in the Akkusativobjekt slot!
	 */
	int keepPlaceFreeForHowManyFillings(String name);

	/**
	 * The restrictor can prescribe, that only a certain number of additional
	 * fillings may be added to slots / features with a certain name. Example: There
	 * are two role frames in a role frame collection. They both beed at least and
	 * at most <i>one</i> Akkusativobjekt. When the first role frame <i>already has
	 * one Akkusativobjekt</i>, and the Akkusativobjekt-slot of the second role
	 * frame <i>is not filled</i> - the role frame collection should say: NO - there
	 * must be at least <i>one</i> filling in the second Akkusativ slot!
	 *
	 * @return number of <i>additional</i> fillings allowed for slots / features
	 *         with this name - or <i>-1</i>, if theres is <i>no upper bound</i>
	 */
	int howManyAdditionalFillingsAreAllowed(String name);
}
