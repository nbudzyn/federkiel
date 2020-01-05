package de.nb.federkiel.deutsch.grammatik.valenz;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.SlotRequirements;
import de.nb.federkiel.logic.BooleanConstantTrue;
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
	 *          <code>null</code> erlaubt, wenn es kein Subjekt gibt.
	 */
	@Override
	public RoleFrameSlot buildSlot(final String person, final Genus genusDesSubjekts,
			final @Nullable Numerus numerusDesSubjekts, final String hoeflichkeitsformDesSubjekts) {
		// Eine reiner Infinitiv w�re etwas wie "gehen", "seine Mutter sehen",
		// "einen Yeti gesehen haben wollen" oder
		// (diskontinuierlich!) "Seiner Mutter [wird er] das Geld geben".

		// "gehen", "seine Mutter sehen", "einen Yeti gesehen haben wollen" oder
		// "Seiner Mutter [wird er] das Geld geben"
		// sind also m�gliche Fillings f�r einen Slot vom Typ reiner Infinitiv.

		// Es stellt sich als erstes die Frage, welchen TYP ein Filling (f�r den
		// reiner-Infinitiv-Slot) haben kann. Die Fillings k�nnen ja auch
		// ganz offenbar diskontuierlich sein!

		// Es gibt nur einen Typ von Slot-Filling, n�mlich
		// IHomogeneousConstituentAlternatives: Ein Teil des Inputs mit
		// Features.

		// Als SlotRequirements legt man hier fest:
		// - Welchen Grammar Symbol Name muss ein Input-Abschnitt haben?
		// - Welche Features muss ein solcher Input-Abschnitt besitzen?

		// Betrachten wir die Alternativen:
		// - "gehen"
		// -- Symbol: VERBALKOMPLEXREST
		// -- n�tige Features:
		// --- verb, das im Infinitiv steht und COMPLETED ist ("gehen")
		// - "seine Mutter sehen"
		// -- Symbol: ?
		// -- n�tige Features: ?
		// --- verb, das im Infinitiv steht und COMPLETED ist ("sehen" / "seine Mutter")
		// - "einen Yeti gesehen haben wollen"
		// -- Symbol: ?
		// -- n�tige Features:
		// --- infinitiv ("wollen")
		// --- verb, das im Infinitiv steht und COMPLETED ist ("wollen" / "einen Yeti
		// gesehen haben")
		// - "Seiner Mutter [wird er] das Geld geben"
		// -- Symbol: ?
		// -- n�tige Features:
		// --- verb, das im Infinitiv steht und COMPLETED ist ("geben" / "Seiner
		// Mutter", "das Geld")

		// Scheint irgendwie alles schwierig. Man wei� ja hier
		// gar nicht, welche Objekte etc. man brauchen w�rde...

		// Letztlich braucht man ja eine ganze "potenziell diskontinuierlich Phrase"
		// MIT VERB.

		return RoleFrameSlot.of("ReinerInfinitiv", SlotRequirements.of(
				// TODO richtige Bedingungen
				"VERBALKOMPLEXRESTENDE", BooleanConstantTrue.getInstance()));

		// TODO? buildFeatureCondition(person, genusDesSubjekts, numerusDesSubjekts,
		// hoeflichkeitsformDesSubjekts))); // "Seiner
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
		// FIXME richtige Bedingung
		return RoleFrameSlot.of("reinerInfinitiv", SlotRequirements.of(
				// TODO richtige Bedingungen
				"VERBALKOMPLEXREST", BooleanConstantTrue.getInstance()));
	}
}
