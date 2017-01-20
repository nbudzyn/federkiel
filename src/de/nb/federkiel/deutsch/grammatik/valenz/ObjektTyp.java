package de.nb.federkiel.deutsch.grammatik.valenz;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.FeatureStringConverter;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.SlotRequirements;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.feature.ThreeStateFeatureEqualityFormula;
import de.nb.federkiel.feature.UnspecifiedFeatureValue;
import de.nb.federkiel.logic.FormulaUtil;
import de.nb.federkiel.logic.IFormula;

/**
 * Objekt (als Typ von Ergänzung zu einem Verb)
 *
 * @author nbudzyn 2011
 */
@Immutable
final class ObjektTyp extends AbstractErgaenzungsOderAngabenTyp {
  private final String slotName;

  private final Kasus kasus;

  /**
   * <code>true</code>, falls es sich um einen Pseudoaktanten handeln MUSS ("Ich habe ES gut.") -
   * <code>false</code>, falls es sich NICHT UM EINEN PSEUDOAKTANTEN HANDELN DARF ("Ich liebe ES.",
   * "Ich öffne DIE TUER.").
   */
  private final boolean pseudoaktantEs;

  /**
   * <code>true</code>, falls es sich um ein reflexives Verb handelt (er freut SICH) -
   * <code>false</code>, falls es sowohl nurReflexivesObjektErlaubt wie auch irreflexiv verwendet
   * werden kann
   */
  private final boolean nurReflexivesObjektErlaubt;

  /**
   * Minimale Anzahl an Objekten dieses Typs (normalerweise 1 - in seltenen Fällen 0)
   */
  private final int minFillings;

  /**
   * Maximale Anzahl an Objekten dieses Typs (normalerweise 1 - in seltenen Fällen 2)
   */
  private final int maxFillings;

  /**
   * Gecachet.
   */
  private final RoleFrameSlot restrictionSlot;

  /**
   * Slot für ein "normales" Objekt (nicht ausschließlich reflexiv, kein Pseudoaktant, genau ein
   * Vorkommen)
   */
  ObjektTyp(final String slotName, final Kasus kasus) {
    this(slotName, kasus, 1, 1, false, // nicht ausschließlich
        // reflexiv
        false); // obligatorisch, KEIN
                // Pseudoaktant
  }

  /**
   * @param minFillings Minimale Anzahl an Objekten dieses Typs (normalerweise 1 - in seltenen
   *        Fällen 0
   * @param maxFillings Maximale Anzahl an Objekten dieses Typs (normalerweise 1 - in seltenen
   *        Fällen 2)
   */
  ObjektTyp(final String slotName, final Kasus kasus, final int minFillings, final int maxFillings,
      final boolean nurReflexivesObjektErlaubt, final boolean pseudoaktant) {
    super();
    this.slotName = slotName;
    this.kasus = kasus;
    this.minFillings = minFillings;
    this.maxFillings = maxFillings;
    this.nurReflexivesObjektErlaubt = nurReflexivesObjektErlaubt;
    pseudoaktantEs = pseudoaktant;

    restrictionSlot = buildSlot(null, null, null, null);
  }

  @Override
  public RoleFrameSlot buildSlot(final @Nullable String person,
      final @Nullable Genus genusDesSubjekts, final @Nullable Numerus numerusDesSubjekts,
      final @Nullable String hoeflichkeitsformDesSubjekts) {
    // Das Objekte könnte irreflexiv sein (Ich dusche das Kind)
    // oder reflexiv (ich dusche mich, nicht aber: *ich dusche sich).
    // Daher brauche ich in der Regel ZWEI req-Alternativen!

    if (nurReflexivesObjektErlaubt) {
      // nur Reflexives Objekt erlaubt
      // Ich dusche mich, aber nicht *Ich dusche sich.
      // Auch nicht *Ich befinde sogar mich in Paris.
      final SlotRequirements reqsAlternativeReinReflexiv =
          SlotRequirements.of("N_PRONOMEN_PHR_REIHUNG",
              buildFeatureConditionForN_PRONOMEN_PHR_REIHUNGEinzelnesReflexivGebrauchtesPronomen(
                  person, genusDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts));

      return RoleFrameSlot.of(slotName, 1, // minFillings
          maxFillings, // maxFillings
          reqsAlternativeReinReflexiv);

    }

    // Reflexive und nicht reflexive Objekte erlaubt - auch "gemischte"
    // Objekte
    // (Er betrachtete mich und sich selbst.)
    // In "gemischten" Objekten müssen FÜR DIE REFLEXIVEN ANTEILE ("sich")
    // Person, Numerus und Genus
    // mit dem Subjekt übereinstimmen
    // (nicht "*Ich dusche mich und sich.",
    // "*Ich dusche sogar sich." und "*Ich dusche sich selbst.")

    // Die Regel, die "*Ich dusche mich und sich." ausschließt, ist in
    // etwa:
    // Für alle REFLEXIVPRONOMEN ("sich") im Akkusativobjekt
    // ("mich und sich")
    // gilt:
    // Das Reflexivpronomen stimmt in Person (3!), Numerus (Sg) und
    // Genus (z.B. m) mit dem Subjekt überein!
    // Die N_PRONOMEN_PHR_REIHUNGs (und ggf. das drunter)
    // haben deshalb neue Merkmale erhalten: personDesReflexivenPronomens,
    // numerusDesReflexivenPronomens, genusDesReflexivenPronomens -
    // die ausschließlich durch Reflexivpronomen gesetzt werden.
    // Bei einer REIHUNG müssen personDesReflexivenPronomens /
    // numerusDesReflexivenPronomens /
    // genusDesReflexivenPronomens auf
    // beiden Seiten
    // ÜBEREINSTIMMEN - sofern sie gesetzt sind ("mich und dich" ist ok,
    // "mich und sich" ist ok, aber
    // "*sich(dat pl m) und sich (akk sg m)"
    // ist
    // nicht ok).
    // Der Slot muss dann sicherstellen, dass
    // personDesReflexivenPronomens / numerusDesReflexivenPronomens /
    // genusDesReflexivenPronomens - sofern sie gesetzt sind! -
    // gleich person / numerus und genus des Subjekts sind.
    final SlotRequirements reqsAlternativeNichtReinReflexiv =
        SlotRequirements.of("N_PRONOMEN_PHR_REIHUNG",
            buildFeatureConditionForN_PRONOMEN_PHR_REIHUNGNichtReinReflexiv(person,
                genusDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts));
    return RoleFrameSlot.of(slotName, 1, // minFillings
        maxFillings, // maxFillings
        reqsAlternativeNichtReinReflexiv);
  }

  @Override
  public RoleFrameSlot buildRestrictionSlot() {
    return restrictionSlot;
  }

  /**
   * @return Eine Bedingung für eine N_PRONOMEN_PHR_REIHUNG, die sicherstellt, dass es sich um ein
   *         einzelnes reflexiv gebrauchtes Pronomen handelt: <i>mich</i>, <i>sich</i> - aber nicht
   *         <i>sogar mich</i> oder <i>sich und dich</i>.
   */
  private IFormula<FeatureAssignment> buildFeatureConditionForN_PRONOMEN_PHR_REIHUNGEinzelnesReflexivGebrauchtesPronomen(
      final String person, final Genus genusDesSubjekts, final Numerus numerusDesSubjekts,
      final String hoeflichkeitsformDesSubjekts) {
    final List<IFormula<FeatureAssignment>> featureReqs = new LinkedList<>();

    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue("kasus",
        FeatureStringConverter.toFeatureString(kasus)));
    if (pseudoaktantEs) {
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
          GermanUtil.GEEIGNET_ALS_PSEUDOAKTANT_ES,
          StringFeatureLogicUtil.booleanToString(pseudoaktantEs)));
    }

    // Er duscht sich, nicht aber: Ich dusche *sich.
    featureReqs.addAll(buildReinReflexivConditions(person, genusDesSubjekts, numerusDesSubjekts,
        hoeflichkeitsformDesSubjekts));

    // Er freut sich, aber nicht *er freut ihn oder
    // *er freut sich selbst.
    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
        GermanUtil.EINZELNES_REFLEXIVPRONOMEN, StringFeatureLogicUtil.TRUE));

    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
        GermanUtil.IST_DAS_SUBJEKT, StringFeatureLogicUtil.FALSE));

    // Wegen der vorangegangenen Bedinung können wir auf
    // buildFeatureConditionExcludingIrrreflPersonalPronounIfAppropriate()
    // verzichten!

    return FormulaUtil.and(featureReqs); // immutable
  }

  /**
   * @return Eine Bedingung für eine N_PRONOMEN_PHR_REIHUNG, die sicherstellt, dass alle
   *         Reflexivpronomen (sofern in diesem Objekt überhaupt welche vorkommen, z.B. in "sich"
   *         oder in "mich und sich selbst") Person, Numerus (Sg), Genus (z.B. m) und
   *         Höflichkeitsform (ihrer vs. Ihrer) mit dem Subjekt übereinstimmen.
   */
  private IFormula<FeatureAssignment> buildFeatureConditionForN_PRONOMEN_PHR_REIHUNGNichtReinReflexiv(
      final String person, final Genus genusDesSubjekts, final Numerus numerusDesSubjekts,
      final String hoeflichkeitsformDesSubjekts) {
    final List<IFormula<FeatureAssignment>> featureReqs = new LinkedList<>();

    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue("kasus",
        FeatureStringConverter.toFeatureString(kasus)));
    if (pseudoaktantEs) {
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
          GermanUtil.GEEIGNET_ALS_PSEUDOAKTANT_ES,
          StringFeatureLogicUtil.booleanToString(pseudoaktantEs)));
    }

    // Das Reflexivpronomen soll in Person (3!), Numerus (Sg) und
    // Genus (z.B. m) mit dem Subjekt übereinstimmen!
    // Die N_PRONOMEN_PHR_REIHUNGs (und ggf. das drunter) haben
    // daher neue Merkmale erhalten: personDesReflexivenPronomens,
    // numerusDesReflexivenPronomens, genusDesReflexivenPronomens -
    // die ausschließlich durch Reflexivpronomen gesetzt werden.
    // Der Slot muss also sicherstellen, dass
    // personDesReflexivenPronomens / numerusDesReflexivenPronomens /
    // genusDesReflexivenPronomens - sofern sie gesetzt sind! -
    // gleich person / numerus und genus des Subjekts sind.
    featureReqs.addAll(buildReflexivConditionsNichtUnbedingtRein(person, genusDesSubjekts,
        numerusDesSubjekts, hoeflichkeitsformDesSubjekts));

    // Bei "Ich betrachte mich" muss die Interpretation, in der die
    // Nominalphrase "mich" ein PPER enthält, AUSGESCHLOSSEN werden!
    // (Nur PRF ist grammatisch!)
    // (Er betrachtet ihn / PPER und Er betrachtet sich / PRF sind
    // allerdings beide möglich!)
    final ThreeStateFeatureEqualityFormula featureConditionExcludingIrrreflPersonalPronounIfAppropriate =
        buildFeatureConditionExcludingIrrreflPersonalPronounIfAppropriate(person,
            numerusDesSubjekts, hoeflichkeitsformDesSubjekts);
    if (featureConditionExcludingIrrreflPersonalPronounIfAppropriate != null) {
      featureReqs.add(featureConditionExcludingIrrreflPersonalPronounIfAppropriate);
    }

    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
        GermanUtil.IST_DAS_SUBJEKT, StringFeatureLogicUtil.FALSE));

    return FormulaUtil.and(featureReqs); // immutable
  }

  private List<IFormula<FeatureAssignment>> buildReinReflexivConditions(final String person,
      final Genus genusDesSubjekts, final Numerus numerusDesSubjekts,
      final String hoeflichkeitsformDesSubjekts) {
    return buildReflexivCondition(person, genusDesSubjekts, numerusDesSubjekts,
        hoeflichkeitsformDesSubjekts, "person", "genus", "numerus", "hoeflichkeitsform");
  }

  /**
   * @return eine Bedingung, die sicherstellt, dass das Reflexivpronomen soll in Person (3!),
   *         Numerus (Sg), Genus (z.B. m) und Höflichkeitsform (ihrer vs. Ihrer) mit dem Subjekt
   *         übereinstimmen!
   */
  private List<IFormula<FeatureAssignment>> buildReflexivConditionsNichtUnbedingtRein(
      final String person, final Genus genusDesSubjekts, final Numerus numerusDesSubjekts,
      final String hoeflichkeitsformDesSubjekts) {
    // die N_PRONOMEN_PHR_REIHUNGs haben Merkmale erhalten:
    // personDesReflexivenPronomens,
    // numerusDesReflexivenPronomens,
    // genusDesReflexivenPronomens -
    // die ausschließlich durch
    // Reflexivpronomen gesetzt werden.
    // Die Bedingung muss also
    // sicherstellen, dass personDesReflexivenPronomens /
    // numerusDesReflexivenPronomens / genusDesReflexivenPronomens -
    // sofern sie gesetzt sind! - gleich person / numerus / genus und
    // Höflichkeitsform (sie / Sie)
    // des Subjekts sind.
    return buildReflexivCondition(person, genusDesSubjekts, numerusDesSubjekts,
        hoeflichkeitsformDesSubjekts, "personDesReflexivenPronomens", "genusDesReflexivenPronomens",
        "numerusDesReflexivenPronomens", "hoeflichkeitsformDesReflexivenPronomens");
  }

  private List<IFormula<FeatureAssignment>> buildReflexivCondition(final String person,
      final Genus genusDesSubjekts, final Numerus numerusDesSubjekts,
      final String hoeflichkeitsformDesSubjekts, final String personMerkmalFuerReflexivbedingung,
      final String genusMerkmalFuerReflexivbedingung,
      final String numerusMerkmalFuerReflexivbedingung,
      final String hoeflichkeitsformMerkmalFuerReflexivbedingung) {
    final List<IFormula<FeatureAssignment>> featureReqs = new LinkedList<>();

    if (UnspecifiedFeatureValue.notNullAndNotUnspecified(person)) {
      // Ich freue mich, aber nicht *Ich freue dich.
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
          personMerkmalFuerReflexivbedingung, person));
    }
    if (genusDesSubjekts != null) {
      // Er freut sich /m, aber nicht *Er freut sich /f.
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
          genusMerkmalFuerReflexivbedingung,
          FeatureStringConverter.toFeatureString(genusDesSubjekts)));
    }
    if (numerusDesSubjekts != null) {
      // Ich freue mich, aber nicht *Ich freue uns.
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
          numerusMerkmalFuerReflexivbedingung,
          FeatureStringConverter.toFeatureString(numerusDesSubjekts)));
    }
    if (UnspecifiedFeatureValue.notNullAndNotUnspecified(hoeflichkeitsformDesSubjekts)) {
      // Sie freuen sich (Höflichkeitsform), aber nicht
      // *Sie freuen sich (keine Höflichkeitsform).
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
          hoeflichkeitsformMerkmalFuerReflexivbedingung, hoeflichkeitsformDesSubjekts));
    }

    return featureReqs;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((kasus == null) ? 0 : kasus.hashCode());
    result = prime * result + maxFillings;
    result = prime * result + minFillings;
    result = prime * result + (pseudoaktantEs ? 1231 : 1237);
    result = prime * result + ((slotName == null) ? 0 : slotName.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ObjektTyp other = (ObjektTyp) obj;
    if (kasus == null) {
      if (other.kasus != null) {
        return false;
      }
    } else if (!kasus.equals(other.kasus)) {
      return false;
    }
    if (maxFillings != other.maxFillings) {
      return false;
    }
    if (minFillings != other.minFillings) {
      return false;
    }
    if (pseudoaktantEs != other.pseudoaktantEs) {
      return false;
    }
    if (slotName == null) {
      if (other.slotName != null) {
        return false;
      }
    } else if (!slotName.equals(other.slotName)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder res = new StringBuilder();

    res.append(slotName);
    if (maxFillings != 1 || minFillings != 1 || pseudoaktantEs) {
      res.append(" (");
      if (pseudoaktantEs) {
        res.append("Pseudoaktant \"es\", ");
      }

      res.append(minFillings);
      res.append(" - ");
      res.append(maxFillings);
      res.append(")");
    }

    return res.toString();
  }
}
