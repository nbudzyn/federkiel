package de.nb.federkiel.deutsch.grammatik.valenz;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.SlotRequirements;
import de.nb.federkiel.logic.IFormula;

/**
 * Ein bestimmter Typ von Verberg�nzung: "Seiner Mutter [wird er] das Geld
 * geben"[.]
 * <p>
 * Ein reiner Infinitiv (also ohne "zu") mit seinen Erg�nzungen und Angaben -
 * allerdings nicht als (kontinuierliche) Phrase, sondern als Rollenrahmen.
 */
public class ReinerInfinitivTyp extends AbstractErgaenzungsOderAngabenTyp {
	public static final ReinerInfinitivTyp INSTANCE = new ReinerInfinitivTyp();

	/**
	 * @param numerusDesSubjekts
	 *          <code>null</code> erlaubt, wenn es kein Subjekt gibt ("Heute sind
	 *          Ferien.", "Heute ist hitzefrei.") - wenn der Numerus des
	 *          Pr�dikativums also nicht eingeschr�nkt werden soll.
	 */
	@Override
	public RoleFrameSlot buildSlot(final String person, final Genus genusDesSubjekts, final Numerus numerusDesSubjekts,
			final String hoeflichkeitsformDesSubjekts) {
		return RoleFrameSlot.of("verb", SlotRequirements.of("N_PRONOMEN_PHR_REIHUNG",
				buildFeatureCondition(person, genusDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts))); // "Seiner
																																																							// Mutter
																																																							// [...]
																																																							// das
																																																							// Geld
																																																							// geben"
		// "[Er wird] sich die Haare f�nen", aber nicht *"[Ich werde] sich die Haare
		// f�nen"

	}

	private IFormula<FeatureAssignment> buildFeatureCondition(final String person, final Genus genusDesSubjekts,
			final Numerus numerusDesSubjekts, final String hoeflichkeitsformDesSubjekts) {
		throw new RuntimeException("Not yet implemented");

		// TODO Eine Bedingung, die sicherstellt, dass es (in der - m�glicherweise
		// diskontinuierlichen - reinen Infinitivphrase)
		// eine Feature "verb" gibt, das

	}

	@Override
	public RoleFrameSlot buildRestrictionSlot() {
		// FIXME
		throw new RuntimeException("Not yet implemented");
	}
}
