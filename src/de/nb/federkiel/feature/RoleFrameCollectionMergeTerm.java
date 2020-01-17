package de.nb.federkiel.feature;

import de.nb.federkiel.plurivallogic.BinaryCompoundPlurivalTerm;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A merge term, joining two role frame collections by a merge operation.
 * <p>
 * When X and Y are role frame collections, the merged role frame collection contains all merges of
 * each role frame of X with each role frame of Y.
 * <p>
 * Merging two <i>role frames</i> means adding slots and filling slots with free fillings.
 * <p>
 * In some cases, merging is not possible (not all free fillings can be consumed by the slots,
 * e.g.), in other cases there are several possibilities for a merge (filling A fills slot X,
 * filling B fills Slot Y; or the other way round). So, there are several merge result alternatives.
 *
 * @author nbudzyn 2009
 */
public class RoleFrameCollectionMergeTerm extends
		BinaryCompoundPlurivalTerm<RoleFrameSlot, RoleFrameSlot, RoleFrameSlot, FeatureAssignment> {

	public RoleFrameCollectionMergeTerm(
			final IPlurivalTerm<RoleFrameSlot, FeatureAssignment> firstSubTerm,
			final IPlurivalTerm<RoleFrameSlot, FeatureAssignment> secondSubTerm) {
		super(firstSubTerm, secondSubTerm);
	}

	@Override
	public Plurival<RoleFrameSlot> calculate(final RoleFrameSlot first, final RoleFrameSlot second) {

		return first.mergeWithoutSemantics(second);
	}

	/*
	 * NOT USED
	 *
	 * Calculates the implicit restrictions, that this Term places on
	 * realizations for the symbol reference given by this Variable (typically,
	 * it will be a <code>QualifiedFeatureRefVariable</code>, that points to a
	 * symbol of the current rule and a feature of this symbol).
	 * <p>
	 * For example, if the term is <code>p.verb * sg.verb</code> and
	 * <code>p.verb</code> is already assigend to
	 * <code>{Subjekt: ? (N_PHR(kasus=&quot;nom&quot)), Verb: "läuft"}</code>,
	 * and we are looking for the implicit restricition on <code>p</code>, the
	 * result would be term, telling us, that IF p.verb contains NO SLOTS (that
	 * means, only free fillings), there should be AT MOST 1 free filling, and
	 * this should match a <code>Subjekt</code> slot.
	 *
	 * @see IPlurivalTerm#retrieveImplicitBoundsForTermAndBoundsForVariable(Variable,
	 *      de.nb.leseratte.logic.IAssignment)
	 *
	@Override
	public Plurival<BoundsForTermAndBoundsForVariable> retrieveImplicitBoundsForTermAndBoundsForVariable(
			final Variable<RoleFrameCollection, FeatureAssignment> variable,
			final FeatureAssignment assignment) {
		// 1. Find data flow element and variable bounds for first term
		final Plurival<BoundsForTermAndBoundsForVariable> firstSubResultAlternatives =
			getFirstSubTerm()
			.retrieveImplicitBoundsForTermAndBoundsForVariable(
					variable,
					assignment);

		for (final BoundsForTermAndBoundsForVariable firstSubResult : firstSubResultAlternatives) {
			final IDataFlowElement firstTermDataFlowElement = firstSubResult
			.getBoundsForTerm(); // might be null
			final ITermBounds firstBoundsForVariable = firstSubResult
			.getBoundsForVariable(); // might be null

			// 2. Find data flow element and variable bounds for second term
			final Plurival<BoundsForTermAndBoundsForVariable> secondSubResultAlternatives =
				getFirstSubTerm()
				.retrieveImplicitBoundsForTermAndBoundsForVariable(
						variable,
						assignment);

			for (final BoundsForTermAndBoundsForVariable secondSubResult : secondSubResultAlternatives) {
				final IDataFlowElement secondTermDataFlowElement = secondSubResult
				.getBoundsForTerm(); // might be null
				final ITermBounds secondBoundsForVariable = secondSubResult
				.getBoundsForVariable(); // might be null

				ITermBounds resBoundsForVariable;
				final Plurival<RoleFrame> resTermBoundsAlternatives;

				// 3. Combine the variable bounds from both sides
				if (firstBoundsForVariable != null) {
					if (secondTermDataFlowElement != null) {
						resBoundsForVariable = null; // FIXME (zurzeit nicht
														// verwendet?)
						// firstBoundsForVariable.combineBounds(
						// secondBoundsForVariable);
					} else {
						// firstBoundsForVariable != null, second == null
						resBoundsForVariable = firstBoundsForVariable;
					}
				} else {
					// firstBoundsForVariable == null
					resBoundsForVariable = secondBoundsForVariable; // might be null
				}

				// 4. The next step depends on what kind of data flow elements have been
				// retrieved for
				// the two sub-terms:
				// - If they both are term bounds, then we kind-of merge the term bounds
				// to find the
				// overall bound for this term.
				// - If one of them is the variable, we are interested in, we procede a
				// bit like a
				// we would in a merge and collect the restrictions on the variable, as
				// well as
				// the restrictions on the result term.
				// - If both are the variable, we are interested in,... well, this
				// should be a
				// very rare case!
				if ((firstTermDataFlowElement instanceof ITermBounds) ||
						firstTermDataFlowElement == null) {
					final ITermBounds firstTermBounds = (ITermBounds) firstTermDataFlowElement;

					if ((secondTermDataFlowElement instanceof ITermBounds)
							|| secondTermDataFlowElement == null) {
						final ITermBounds secondTermBounds = (ITermBounds) secondTermDataFlowElement;

						// They both are term bounds.
						// Then we kind-of merge the term bounds to find the overall bound for this term.

						// If any of them is null (no term restrictions known), then we do not know the
						// restrictions of the result.
						if (firstTermBounds == null || secondTermBounds == null) {
							resTermBoundsAlternatives = new Plurival<RoleFrame>();
						} else {
							// ok, both sub-term bounds are non-null.
							// get the "merge bound".
							resTermBoundsAlternatives = firstTermBounds.mergeBounds(secondTermBounds);
						}
						// FIXME Was geschieht jetzt mit den resTermBoundsAlternatives?
					} else {
						if (!(secondTermDataFlowElement instanceof Variable<?, ?>)) {
							throw new IllegalStateException(
									"Unexpected type of data flow element: "
									+ secondTermDataFlowElement);
						}

						if (!secondTermDataFlowElement.equals(variable)) {
							throw new IllegalStateException(
									"Unexpected variable found as data flow element "
									+ "for sub term: "
									+ secondTermDataFlowElement);
						}

						// The second one is the variable, we are interested in, the first one is a term bound.
						// Then we procede a bit like a we would in a merge and collect the restrictions on the
						// variable, as well as the restrictions on the result term.

						// TODO Do some stuff
						// Hier muss man vorgehen wie im zweiten Beispiel, wo die Bedingungen aus
						// [{Subjekt :  "Peter" (ausschliesslichNomenKern="j", endetMitLockererApposition="n", enthaeltLockereApposition="n",
						// folgekommaStehtAus="n", genitivattributfaehig="j", kasus="nom", numerus="sg", person="3" ),
						// Dativobjekt :  ?, AdverbialeAngabe : [],
						// Verb : ["hilft" (verb=[{Subjekt :  ?, Dativobjekt :  ?, AdverbialeAngabe : []}] )] }]
						//	*
						// p
						// ermittelt werden.
						//					    Relevant ist wohl, dass das linke Argument keine free fillings enthält, sondern Slots!
						//					    1a: Falls p.verb (Beispiel für das rechte Argument) keine Slots enthält (also nur free fillings), dann müssen alle free fillings
						//					          consumet werden. Das heißt, p.verb darf maximal ein
						//					          Dativobjekt enthalten und beliebig viele adverbiale Angaben - aber nichts anderes!
						//					    1b: Falls p.verb Slots enthält (also keine free fillings), dann darf p.verb kein Filling in einem Slot
						//					          enthalten, dass auch in der linken Alternative in irgendeinem Slot enthalten ist, d.h.
						//					          p.verb darf *in keinem Slot* "Peter" (...) oder "hilft" (...) enthalten
						//					    1c: Falls p.verb Slots enthält (also keine free fillings), dann darf p.verb keinen Slot mit einem
						//					         Namen enthalten, der auch links vorkommt, also keinen Slot mit einem der Namen
						//					         "Subjekt", "Dativobjekt", "Adverbiale Angabe" oder "Verb".
						//					    1b und 1c sind wohl kaum von praktischer Relevanz
						//					    1a hingegen schon.
						//
						//					    1a, 1b und 1c kann man ganz einfach zusammenfassen:
						//					    p.verb muss mit  [{Dativobjekt :  ?, AdverbialeAngabe : [] )] }] gemerget werden können! :-)
						//					    Dabei erhält man {Dativobjekt :  ?, AdverbialeAngabe : [] )] } so: Man nimmt den Ausgangs Role-Frame und ermittelt für jeden Slot,
						//					    wieviele WEITERE FILLINGS noch erlaubt sind.

						resTermBoundsAlternatives = null;
						// FIXME new Plurival<RoleFrame>(firstTermBounds);

						if (resBoundsForVariable != null) {
							resBoundsForVariable = null;
							// FIXME resBoundsForVariable.combineBounds(firstTermBounds.reduceToFreeSlots());
						} else {
							resBoundsForVariable = null; // FIXME firstTermBounds.reduceToFreeSlots();
						}
					}
				} else {
					if (!(firstTermDataFlowElement instanceof Variable<?, ?>)) {
						throw new IllegalStateException(
								"Unexpected type of data flow element: "
								+ firstTermDataFlowElement);
					}

					if (!firstTermDataFlowElement.equals(variable)) {
						throw new IllegalStateException(
								"Unexpected variable found as data flow element "
								+ "for sub term: " + firstTermDataFlowElement);
					}

					if ((secondTermDataFlowElement instanceof ITermBounds)
							|| secondTermDataFlowElement == null) {
						final ITermBounds secondTermBounds = (ITermBounds) secondTermDataFlowElement;

						// The second one is a term bound, the first one is the variable, we are interested in.
						// Then we procede a bit like a we would in a merge and collect the restrictions on the
						// variable, as well as the restrictions on the result term.

						// The same as above!
						resTermBoundsAlternatives = null; // FIXMEnew Plurival<RoleFrame>(secondTermBounds);

						if (resBoundsForVariable != null) {
							resBoundsForVariable = null; // FIXME resBoundsForVariable.combineBounds(
							// secondTermBounds.reduceToFreeSlots());
						} else {
							resBoundsForVariable = null; // FIXME secondTermBounds.reduceToFreeSlots();
						}
					} else {
						if (!(secondTermDataFlowElement instanceof Variable<?, ?>)) {
							throw new IllegalStateException(
									"Unexpected type of data flow element: "
									+ secondTermDataFlowElement);
						}

						if (!secondTermDataFlowElement.equals(variable)) {
							throw new IllegalStateException(
									"Unexpected variable found as data flow element "
									+ "for sub term: "
									+ secondTermDataFlowElement);
						}

						// They both are the variable, we are interested in!
						// This should be a very rare case!

						resTermBoundsAlternatives = new Plurival<RoleFrame>(new RoleFrame[] { null });
					}
				}

				// FIXME resBoundsForVariable und resTermBoundsAlternatives;
				// zu einem Plurival von BoundsForTermAndBoundsForVariable-Ergebnissen hinzufügen.



				return null; // FIXME new BoundsForTermAndBoundsForVariable(resBoundsForTerm,
				// resBoundsForVariable);
			}
		}
		// FIXME Plurival von BoundsForTermAndBoundsForVariable-Ergebnissen zurückgeben
		return null;
	}
	 */

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		final StringBuilder res = new StringBuilder();
		if (surroundWithBracketsIfApplicable) {
			res.append("(");
		}
		res.append(getFirstSubTerm().toString(true));
		res.append(" * ");
		res.append(getSecondSubTerm().toString(true));
		if (surroundWithBracketsIfApplicable) {
			res.append(")");
		}
		return res.toString();

	}

}
