package de.nb.federkiel.deutsch.grammatik.valenz;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.deutsch.grammatik.featurestructure.GrammarFSUtil;
import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.RestrictedFSSet;
import de.nb.federkiel.feature.SlotRequirements;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.feature.ThreeStateFeatureEqualityFormula;
import de.nb.federkiel.logic.FormulaUtil;
import de.nb.federkiel.logic.IFormula;

/**
 * Der Typ f�r Adverbiale Angaben, der etwa durch Pr�positionalphrasen, Adverbialphrase,
 * unflektierte Adjektivphrasen etc. realisiert wird.
 *
 * @author nbudzyn 2011
 */
@Immutable
final class AdverbialeAngabenTyp extends AbstractErgaenzungsOderAngabenTyp {
  private static final String GEEIGNET_ALS_ADVERBIALER_AKK_ODER_GEN = "geeignetAlsAdvAkkOderGen";

  private static final String GEEIGNET_ALS_ADV_AKK_ODER_GEN_ZUR_ADJEKTIVISCHEN_FORM =
      "geeignetAlsAdvAkkOderGenZurAdjektivischenForm";

  private final String slotName;

  private final boolean geeignetZurAdjektivischenForm;

  /**
   * Gecachet.
   */
  private final RestrictedFSSet restrictionSlot;


  /**
   * Erzeugt einen <code>AdverbialeAngabenTyp</code>.
   *
   * @param geeignetZurAdjektivischenForm Ob die adverbialen Angaben auch mit einem Partizip,
   *        Adjektiv oder Adverb stehen k�nnen sollen
   */
  AdverbialeAngabenTyp(final String slotName, boolean geeignetZurAdjektivischenForm) {
    super();
    this.slotName = slotName;
    this.geeignetZurAdjektivischenForm = geeignetZurAdjektivischenForm;
    restrictionSlot = buildSlot(null, null, null, null);
  }

  @Override
  public String getName() {
  	return slotName;
  }
  
  @Override
  public RestrictedFSSet buildSlot(final @Nullable String personDesSubjekts,
      final @Nullable Genus genusDesSubjekts, final @Nullable Numerus numerusDesSubjekts,
      final @Nullable String hoeflichkeitsformDesSubjekts) {
    // adverbiale Pr�positionalphrase ("vor zwei Tagen")
    final SlotRequirements praepositionalPhrReqs = buildPRAEPOSITIONAL_PHRSlot(personDesSubjekts,
        numerusDesSubjekts, hoeflichkeitsformDesSubjekts);
    // adverbiale Adverbphrase ("immer")
		final SlotRequirements adverbPhrReqs = GrammarFSUtil.buildSlotRequirements("ADVERB_PHR_REIHUNG");
    // adverbiale Adjektivphrase ("sorgf�ltig") - Duden 1288
    // Beachte "Er singt seiner selbst gewiss." - Aber
    // "*Er singt ihrer selbst gewiss."!
    final SlotRequirements adjektivPhrReqs = GrammarFSUtil.buildSlotRequirements(
    		"ADJEKTIV_PHR_UNFLEKT_REIHUNG",
				buildPraedikativeOderAdverbialeAdjektivphraseFeatureCondition(personDesSubjekts,
            genusDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts));
    // adverbialer Genitiv ("eines sch�nen Tages")
    final SlotRequirements genitivePhrReqs =
    		GrammarFSUtil.buildSlotRequirements("N_PRONOMEN_PHR_REIHUNG", buildAdvGenitiveOrAccusativeFeatureCondition(
            "gen", personDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts));
    // adverbialer Akkusativ ("jeden Morgen")
    final SlotRequirements accusativePhrReqs =
    		GrammarFSUtil.buildSlotRequirements("N_PRONOMEN_PHR_REIHUNG", buildAdvGenitiveOrAccusativeFeatureCondition(
            "akk", personDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts));

    final SlotRequirements[] reqAlternatives = new SlotRequirements[] {praepositionalPhrReqs,
        adverbPhrReqs, adjektivPhrReqs, genitivePhrReqs, accusativePhrReqs};

		return RestrictedFSSet.of(true, // optional
        true, // multiple
        reqAlternatives);
  }

  private IFormula<FeatureAssignment> buildAdvGenitiveOrAccusativeFeatureCondition(
      final String adverbialGenitiveOrAccusative, final String personDesSubjekts,
      final Numerus numerusDesSubjekts, final String hoeflichkeitsformDesSubjekts) {
    final List<IFormula<FeatureAssignment>> featureReqs = new LinkedList<>();

    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue("kasus",
        adverbialGenitiveOrAccusative));

    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
        GEEIGNET_ALS_ADVERBIALER_AKK_ODER_GEN, StringFeatureLogicUtil.TRUE));

    if (geeignetZurAdjektivischenForm) {
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
          GEEIGNET_ALS_ADV_AKK_ODER_GEN_ZUR_ADJEKTIVISCHEN_FORM, StringFeatureLogicUtil.TRUE));
    }

    // Ich ging meiner /PRF selbst gewiss..., nicht aber
    // *Ich ging meiner /PPER selbst gewiss...!
    final ThreeStateFeatureEqualityFormula featureConditionExcludingIrrreflPersonalPronounIfAppropriate =
        buildFeatureConditionExcludingIrrreflPersonalPronounIfAppropriate(personDesSubjekts,
            numerusDesSubjekts, hoeflichkeitsformDesSubjekts);
    if (featureConditionExcludingIrrreflPersonalPronounIfAppropriate != null) {
      featureReqs.add(featureConditionExcludingIrrreflPersonalPronounIfAppropriate);
    }

    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
        GermanUtil.IST_DAS_SUBJEKT_KEY, StringFeatureLogicUtil.FALSE));

    return FormulaUtil.and(featureReqs);
  }

  private SlotRequirements buildPRAEPOSITIONAL_PHRSlot(final @Nullable String personDesSubjekts,
      final @Nullable Numerus numerusDesSubjekts,
      final @Nullable String hoeflichkeitsformDesSubjekts) {
    // Bei "Ich denke �ber mich nach." muss die Interpretation, in der die
    // Nominalphrase "mich" ein
    // PPER enth�lt, AUSGESCHLOSSEN werden!
    // Regel:
    // Ein Satz (genauer: Ein Verb-Rahmen), dessen Subjekt in der 1. oder 2.
    // Person steht (oder in der "Sie"-Form) und einen gewissen
    // Numerus hat, KANN kein PPER in derselbe Person und demselben Numerus
    // enthalten (nicht im Objekt (egal ob rein reflexiv oder nicht), nicht
    // im Adverbial)

    final IFormula<FeatureAssignment> featureConditionExcludingIrreflexivePersonalPronoun =
        buildFeatureConditionExcludingIrrreflPersonalPronounIfAppropriate(personDesSubjekts,
            numerusDesSubjekts, hoeflichkeitsformDesSubjekts);

    if (featureConditionExcludingIrreflexivePersonalPronoun == null) {
			return GrammarFSUtil.buildSlotRequirements("PRAEPOSITIONAL_PHR");
    }
    
		return GrammarFSUtil.buildSlotRequirements("PRAEPOSITIONAL_PHR",
						featureConditionExcludingIrreflexivePersonalPronoun);
  }

  @Override
  public RestrictedFSSet buildRestrictionSlot() {
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
