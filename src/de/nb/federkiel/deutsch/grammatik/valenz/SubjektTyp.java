package de.nb.federkiel.deutsch.grammatik.valenz;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.FeatureStringConverter;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.RestrictedFSSet;
import de.nb.federkiel.feature.SlotRequirements;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.feature.ThreeStateFeatureEqualityFormula;
import de.nb.federkiel.feature.UnspecifiedFeatureValue;
import de.nb.federkiel.logic.FormulaUtil;
import de.nb.federkiel.logic.IFormula;

/**
 * Subjekt (als Typ von Ergänzung zu einem Verb). Es kann sich auch um ein (ggf. sogar optionales)
 * FORMALES SUBJEKT handeln (Pseudoaktant <i>es</i> im Nominativ).
 *
 * @author nbudzyn 2011
 */
@Immutable
final class SubjektTyp extends AbstractErgaenzungsOderAngabenTyp {
  private final String slotName;

  /**
   * <code>true</code>, falls es sich um einen Pseudoaktanten handeln MUSS ("ES regnet") -
   * <code>false</code>, falls es sich NICHT UM EINEN PSEUDOAKTANTEN HANDELN DARF ("ES kommt die
   * Treppe herauf.", "DER MANN liebt seine Frau.").
   */
  private final boolean pseudoaktantEs;

  /**
   * Minimale Anzahl an Subjekten dieses Typs (normalerweise 1 - in ganz seltenen Fällen 0, etwa bei
   * der Valenz hinter <i>() Mich graut.</i> / <i>*Es* graut mich.</i>)
   */
  private final int minFillings;

  /**
   * Gecachet.
   */
  private final RestrictedFSSet restrictionSlot;

  public SubjektTyp(final String slotName, final int minFillings, final boolean pseudoaktantEs) {
    super();
    this.slotName = slotName;
    this.minFillings = minFillings;
    this.pseudoaktantEs = pseudoaktantEs;

    restrictionSlot = buildSlot(null, null, null, null);
  }

  @Override
  public RestrictedFSSet buildSlot(final String person, final Genus genusDesSubjekts,
      final Numerus numerusDesSubjekts, final String hoeflichkeitsformDesSubjekts) {
    final SlotRequirements reqsAlternative = SlotRequirements.of("N_PRONOMEN_PHR_REIHUNG",
        buildFeatureConditionForN_PRONOMEN_PHR_REIHUNG(person, genusDesSubjekts,
            numerusDesSubjekts, hoeflichkeitsformDesSubjekts));

		return RestrictedFSSet.of(minFillings, // minFillings
        1, // max fillings
        reqsAlternative);
  }

  @Override
  public RestrictedFSSet buildRestrictionSlot() {
    return restrictionSlot;
  }

  private IFormula<FeatureAssignment> buildFeatureConditionForN_PRONOMEN_PHR_REIHUNG(
      final String person, final Genus genus, final Numerus numerus,
      final String hoeflichkeitsformDesSubjekts) {
    final List<IFormula<FeatureAssignment>> featureReqs = new LinkedList<>();

    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue("kasus", "nom"));
    if (pseudoaktantEs) {
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
          GermanUtil.GEEIGNET_ALS_PSEUDOAKTANT_ES_KEY,
          StringFeatureLogicUtil.booleanToString(pseudoaktantEs)));
    }

    if (UnspecifiedFeatureValue.notNullAndNotUnspecified(person)) {
      featureReqs.add(
          ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue("person", person));
    }
    if (genus != null) {
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue("genus",
          FeatureStringConverter.toFeatureString(genus)));
    }
    if (numerus != null) {
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue("numerus",
          FeatureStringConverter.toFeatureString(numerus)));
    }
    if (UnspecifiedFeatureValue.notNullAndNotUnspecified(hoeflichkeitsformDesSubjekts)) {
      featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
          "hoeflichkeitsform", hoeflichkeitsformDesSubjekts));
    }

    // "Pianist kommt." ist nur im Telegrammstil möglich.
    // (Allerdings sind "Peter ist Pianist" und "Peter lernt Koch" problemlos möglich. Duden
    // 445.)

    featureReqs.add(ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
        GermanUtil.IST_DAS_SUBJEKT_KEY, StringFeatureLogicUtil.TRUE));

    return FormulaUtil.and(featureReqs); // immutable
  }

  @Override
  public String getName() {
  	return slotName;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
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
    final SubjektTyp other = (SubjektTyp) obj;
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
    if (minFillings != 1 || pseudoaktantEs) {
      res.append(" (");
      if (pseudoaktantEs) {
        res.append("Pseudoaktant \"es\", ");
      }

      res.append(minFillings);
      res.append(" - 1)");
    }

    return res.toString();
  }
}
