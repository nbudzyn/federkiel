package de.nb.federkiel.deutsch.grammatik.valenz;

import java.util.Arrays;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.SlotRequirements;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.feature.ThreeStateFeatureEqualityFormula;
import de.nb.federkiel.logic.FormulaUtil;
import de.nb.federkiel.logic.IFormula;

/**
 * Pr�dikativum - also Pr�dikatsnomen oder pr�dikatives Adjektiv (als Typ von Erg�nzung zu einem
 * Verb)
 *
 * @author nbudzyn 2011
 */
@Immutable
final class PraedikativumTyp extends AbstractErgaenzungsOderAngabenTyp {
  public static final PraedikativumTyp INSTANCE = new PraedikativumTyp();

  private static final RoleFrameSlot RESTRICTION_SLOT = RoleFrameSlot.of("Praedikativum",
      SlotRequirements.of("N_PRONOMEN_PHR_REIHUNG", buildPraedikatsnomenFeatureCondition(null)), // "ein
                                                                                                 // Esel"
      SlotRequirements.of("ADJEKTIV_PHR_UNFLEKT_REIHUNG",
          buildPraedikativeOderAdverbialeAdjektivphraseFeatureCondition(null, null, null, null))); // klug

  private PraedikativumTyp() {
    super();
  }

  /**
   * @param numerusDesSubjekts <code>null</code> erlaubt, wenn es kein Subjekt gibt ("Heute sind
   *        Ferien.", "Heute ist hitzefrei.") - wenn der Numerus des Pr�dikativums also nicht
   *        eingeschr�nkt werden soll.
   */
  @Override
  public RoleFrameSlot buildSlot(final String person, final Genus genusDesSubjekts,
      final @Nullable Numerus numerusDesSubjekts, final String hoeflichkeitsformDesSubjekts) {
    return RoleFrameSlot.of("Praedikativum",
        SlotRequirements.of("N_PRONOMEN_PHR_REIHUNG",
            buildPraedikatsnomenFeatureCondition(numerusDesSubjekts)), // "ein Esel"
        SlotRequirements.of("ADJEKTIV_PHR_UNFLEKT_REIHUNG",
            buildPraedikativeOderAdverbialeAdjektivphraseFeatureCondition(person, genusDesSubjekts,
                numerusDesSubjekts, hoeflichkeitsformDesSubjekts))); // "klug",
    // "Ihrer selbst �berdr�ssig"
  }

  @Override
  public RoleFrameSlot buildRestrictionSlot() {
    return RESTRICTION_SLOT;
  }

  private static IFormula<FeatureAssignment> buildPraedikatsnomenFeatureCondition(
      final @Nullable Numerus numerusDesSubjekts) {
    // @formatter:off
		// Petra war Regisseur. (Unterschiedliches Genus!)
		// Ich war Regisseur. (Unterschiedliche Person!)

		// Jetzt zum Numerus:

    if (numerusDesSubjekts == Numerus.SINGULAR) {
			// Anscheinend sind Verbindungen in der Art
			// "*Paris ist Charmeure und Clochards." nicht m�glich.
			// (So verstehe ich Duden Bd.4, 2006, 1579
			return FormulaUtil.and(
					Arrays.asList(
							ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
									"kasus", "nom"),
							ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
                                "geeignetAlsPraedikativum", StringFeatureLogicUtil.TRUE),
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
                    "geeignetAlsPraedikativum", StringFeatureLogicUtil.TRUE)));
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
    return "Pr�dikativum";
  }
}
