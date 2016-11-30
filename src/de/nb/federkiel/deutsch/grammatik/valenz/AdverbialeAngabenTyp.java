package de.nb.federkiel.deutsch.grammatik.valenz;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.SlotRequirements;
import de.nb.federkiel.feature.ThreeStateFeatureEqualityFormula;
import de.nb.federkiel.logic.FormulaUtil;
import de.nb.federkiel.logic.IFormula;

/**
 * Der Typ für Adverbiale Angaben, der etwa durch Präpositionalphrasen,
 * Adverbialphrase, unflektierte Adjektivphrasen etc. realisiert wird.
 *
 * @author nbudzyn 2011
 */
@Immutable
final class AdverbialeAngabenTyp extends
		AbstractErgaenzungsOderAngabenTyp {
	private static final String GEEIGNET_ALS_ADVERBIALER_AKK_ODER_GEN = "geeignetAlsAdvAkkOderGen";

	private final String slotName;

	/**
	 * Gecachet.
	 */
	private final RoleFrameSlot restrictionSlot;

	public AdverbialeAngabenTyp(final String slotName) {
		super();
		this.slotName = slotName;
		restrictionSlot = buildSlot(null, null, null, null);
	}

	@Override
  public RoleFrameSlot buildSlot(final @Nullable String personDesSubjekts,
      final @Nullable Genus genusDesSubjekts,
      final @Nullable Numerus numerusDesSubjekts,
      final @Nullable String hoeflichkeitsformDesSubjekts) {
		// adverbiale Präpositionalphrase ("vor zwei Tagen")
		final SlotRequirements praepositionalPhrReqs =
				buildPRAEPOSITIONAL_PHRSlot(personDesSubjekts, numerusDesSubjekts,
						hoeflichkeitsformDesSubjekts);
		// adverbiale Adverbphrase ("immer")
		final SlotRequirements adverbPhrReqs = SlotRequirements.of(
				"ADVERB_PHR_REIHUNG");
		// adverbiale Adjektivphrase ("sorgfältig") - Duden 1288
		// Beachte "Er singt seiner selbst gewiss." - Aber
		// "*Er singt ihrer selbst gewiss."!
		final SlotRequirements adjektivPhrReqs = SlotRequirements.of(
				"ADJEKTIV_PHR_UNFLEKT_REIHUNG",
				buildPraedikativeOderAdverbialeAdjektivphraseFeatureCondition(
						personDesSubjekts, genusDesSubjekts, numerusDesSubjekts,
						hoeflichkeitsformDesSubjekts));
		// adverbialer Genitiv ("eines schönen Tages")
		final SlotRequirements genitivePhrReqs =
				SlotRequirements
						.of("N_PRONOMEN_PHR_REIHUNG",
								buildAdvGenitiveOrAccusativeFeatureCondition("gen",
										personDesSubjekts, numerusDesSubjekts,
										hoeflichkeitsformDesSubjekts));
		// adverbialer Akkusativ ("jeden Morgen")
		final SlotRequirements accusativePhrReqs =
				SlotRequirements.of(
						"N_PRONOMEN_PHR_REIHUNG",
						buildAdvGenitiveOrAccusativeFeatureCondition("akk", personDesSubjekts,
								numerusDesSubjekts, hoeflichkeitsformDesSubjekts));

		final SlotRequirements[] reqAlternatives =
				new SlotRequirements[] { praepositionalPhrReqs,
						adverbPhrReqs, adjektivPhrReqs, genitivePhrReqs,
						accusativePhrReqs };

		return RoleFrameSlot.of(slotName, true, // optional
				true, // multiple
				reqAlternatives);
	}

	private IFormula<FeatureAssignment> buildAdvGenitiveOrAccusativeFeatureCondition(
      final String adverbialGenitiveOrAccusative,
      final String personDesSubjekts, final Numerus numerusDesSubjekts,
      final String hoeflichkeitsformDesSubjekts) {
		final List<IFormula<FeatureAssignment>> featureReqs =
				new LinkedList<>();

		featureReqs.add(ThreeStateFeatureEqualityFormula
				.featureEqualsExplicitValue("kasus", adverbialGenitiveOrAccusative));

		featureReqs.add(ThreeStateFeatureEqualityFormula
				.featureEqualsExplicitValue(
						GEEIGNET_ALS_ADVERBIALER_AKK_ODER_GEN, "j"));

		// Ich ging meiner /PRF selbst gewiss..., nicht aber
		// *Ich ging meiner /PPER selbst gewiss...!
		final ThreeStateFeatureEqualityFormula featureConditionExcludingIrrreflPersonalPronounIfAppropriate =
				buildFeatureConditionExcludingIrrreflPersonalPronounIfAppropriate(
						personDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts);
		if (featureConditionExcludingIrrreflPersonalPronounIfAppropriate != null) {
			featureReqs.add(featureConditionExcludingIrrreflPersonalPronounIfAppropriate);
		}

		return FormulaUtil.and(featureReqs);
	}

  private SlotRequirements buildPRAEPOSITIONAL_PHRSlot(
      final @Nullable String personDesSubjekts,
      final @Nullable Numerus numerusDesSubjekts,
      final @Nullable String hoeflichkeitsformDesSubjekts) {
		// Bei "Ich denke über mich nach." muss die Interpretation, in der die
		// Nominalphrase "mich" ein
		// PPER enthält, AUSGESCHLOSSEN werden!
		// Regel:
		// Ein Satz (genauer: Ein Verb-Rahmen), dessen Subjekt in der 1. oder 2.
		// Person steht (oder in der "Sie"-Form) und einen gewissen
		// Numerus hat, KANN kein PPER in derselbe Person und demselben Numerus
		// enthalten (nicht im Objekt (egal ob rein reflexiv oder nicht), nicht
		// im Adverbial)

		final ThreeStateFeatureEqualityFormula featureConditionExcludingIrreflexivePersonalPronoun =
				buildFeatureConditionExcludingIrrreflPersonalPronounIfAppropriate(
						personDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts);

		if (featureConditionExcludingIrreflexivePersonalPronoun == null) {
			return SlotRequirements.of("PRAEPOSITIONAL_PHR");
		}

		return SlotRequirements.of(
				"PRAEPOSITIONAL_PHR",
				featureConditionExcludingIrreflexivePersonalPronoun);
	}

	@Override
	public RoleFrameSlot buildRestrictionSlot() {
		return restrictionSlot;
	}

	@Override
	public boolean equals(final Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return "Subjekt";
	}
}
