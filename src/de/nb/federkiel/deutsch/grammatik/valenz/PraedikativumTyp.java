package de.nb.federkiel.deutsch.grammatik.valenz;

import java.util.Arrays;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.deutsch.grammatik.featurestructure.GrammarFSUtil;
import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.RestrictedFSSet;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.feature.ThreeStateFeatureEqualityFormula;
import de.nb.federkiel.logic.FormulaUtil;
import de.nb.federkiel.logic.IFormula;

/**
 * Prädikativum - also Prädikatsnomen oder prädikatives Adjektiv (als Typ von Ergänzung zu einem
 * Verb)
 *
 * @author nbudzyn 2011
 */
@Immutable
final class PraedikativumTyp extends AbstractErgaenzungsOderAngabenTyp {
  private static final String SLOT_NAME = "Praedikativum";

	public static final PraedikativumTyp INSTANCE = new PraedikativumTyp();

	private static final RestrictedFSSet RESTRICTION_SLOT = RestrictedFSSet.of(
			GrammarFSUtil.buildSlotRequirements("N_PRONOMEN_PHR_REIHUNG", buildPraedikatsnomenFeatureCondition(null)), // "ein
                                                                                                 // Esel"
			GrammarFSUtil.buildSlotRequirements("ADJEKTIV_PHR_UNFLEKT_REIHUNG",
          buildPraedikativeOderAdverbialeAdjektivphraseFeatureCondition(null, null, null, null))); // klug

  private PraedikativumTyp() {
    super();
  }

  /**
   * @param numerusDesSubjekts <code>null</code> erlaubt, wenn es kein Subjekt gibt ("Heute sind
   *        Ferien.", "Heute ist hitzefrei.") - wenn der Numerus des Prädikativums also nicht
   *        eingeschränkt werden soll.
   */
  @Override
  public RestrictedFSSet buildSlot(final String person, final Genus genusDesSubjekts,
      final @Nullable Numerus numerusDesSubjekts, final String hoeflichkeitsformDesSubjekts) {
		return RestrictedFSSet.of(
				GrammarFSUtil.buildSlotRequirements("N_PRONOMEN_PHR_REIHUNG",
            buildPraedikatsnomenFeatureCondition(numerusDesSubjekts)), // "ein Esel"
				GrammarFSUtil.buildSlotRequirements("ADJEKTIV_PHR_UNFLEKT_REIHUNG",
            buildPraedikativeOderAdverbialeAdjektivphraseFeatureCondition(person, genusDesSubjekts,
                numerusDesSubjekts, hoeflichkeitsformDesSubjekts))); // "klug",
    // "Ihrer selbst überdrüssig"
  }

  @Override
  public RestrictedFSSet buildRestrictionSlot() {
    return RESTRICTION_SLOT;
  }

  @Override
  public String getName() {
  	return SLOT_NAME;
  }

  private static IFormula<FeatureAssignment> buildPraedikatsnomenFeatureCondition(
      final @Nullable Numerus numerusDesSubjekts) {
    // @formatter:off
		// Petra war Regisseur. (Unterschiedliches Genus!)
		// Ich war Regisseur. (Unterschiedliche Person!)

		// Jetzt zum Numerus:

    if (numerusDesSubjekts == Numerus.SINGULAR) {
			// Anscheinend sind Verbindungen in der Art
			// "*Paris ist Charmeure und Clochards." nicht möglich.
			// (So verstehe ich Duden Bd.4, 2006, 1579
			return FormulaUtil.and(
					Arrays.asList(
							ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
									"kasus", "nom"),
						    ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
						        GermanUtil.IST_DAS_SUBJEKT_KEY, StringFeatureLogicUtil.FALSE),
							ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
									"numerus", "sg")));

			// buildFeatureConditionExcludingIrrreflPersonalPronounIfAppropriate()
			// ist
			// nur deshalb nicht notwendig, weil es kein PRF im NOMINATIV gibt!
		}

		// subjekt pl - oder gar kein subjekt (null)
		// Duden Bd.4 2006, 1579: "Diese Sachen sind mein einziger Besitz."!
		return FormulaUtil.and(
            Arrays.asList(
                ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
                        "kasus", "nom"),
                ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
                    GermanUtil.IST_DAS_SUBJEKT_KEY, StringFeatureLogicUtil.FALSE)));
	      // @formatter:on
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
    return "Prädikativum";
  }
}
