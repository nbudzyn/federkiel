package de.nb.federkiel.deutsch.grammatik.valenz;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.FeatureStringConverter;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.feature.ThreeStateFeatureEqualityFormula;
import de.nb.federkiel.feature.UnspecifiedFeatureValue;
import de.nb.federkiel.logic.FormulaUtil;
import de.nb.federkiel.logic.IFormula;

/**
 * Beschreibt einen Typ von Ergänzung oder Angabe zu einem Verb - z.B. das
 * Subjekt, das Akkusativobjekt oder adverbiale Angaben.
 * <p>
 * Abstract-Factory-Pattern.
 *
 * @author nbudzyn 2011
 */
abstract public class AbstractErgaenzungsOderAngabenTyp {
  /**
   * @param person
   *          kann <code>null</code> sein, wenn es sich <i>nicht</i> um einen
   *          {@link SubjektTyp} handelt
   */
  public abstract RoleFrameSlot buildSlot(final String person,
      Genus genusDesSubjekts, @Nullable final Numerus numerusDesSubjekts,
      String hoeflichkeitsformDesSubjekts);

  /**
   * Erzeugt einen {@link RoleFrameSlot}, der angibt, was diese Ergänzung oder
   * Angabe (unabhängig von Person und Numerus des Subjekts) fordert. Diese
   * Angaben kann verwendet werden, um beim Parsen rechtzeitig abzubrechen.
   * <p>
   * Beispiel: Ein IM ENGEREN SINNE TRANSITIVES Verb steht mit einem Subjekt und
   * einem Akkusativobjekt, ein Dativobjekt wäre verboten.
   */
  public abstract RoleFrameSlot buildRestrictionSlot();

  static IFormula<FeatureAssignment> buildPraedikativeOderAdverbialeAdjektivphraseFeatureCondition(
      final String person, final Genus genusDesSubjekts,
      final Numerus numerusDesSubjekts,
      final String hoeflichkeitsformDesSubjekts) {
    final List<IFormula<FeatureAssignment>> featureReqs = new LinkedList<>();

    // Man glaubt es ja kaum, aber auch ein "einfaches" (unflektiertes) ADJD
    // hat Person, Genus und Kasus -
    // denn diese sind relevant für REFLEXIVE OBJEKTE!

    // Person muss übereinstimmen!
    // Ich war meiner selbst überdrüssig. - Aber nicht: *Ich war ihrer
    // selbst überdrüssig.

    if (UnspecifiedFeatureValue.notNullAndNotUnspecified(person)) {
      featureReqs.add(ThreeStateFeatureEqualityFormula
          .featureEqualsExplicitValue("person", person));
    }

    // Genuss muss übereinstimmen!
    // Sie war ihrer selbst überdrüssig. - Aber nicht: *Sie war seiner
    // selbst überdrüssig.

    if (genusDesSubjekts != null) {
      featureReqs.add(ThreeStateFeatureEqualityFormula
          .featureEqualsExplicitValue("genus",
              FeatureStringConverter.toFeatureString(genusDesSubjekts)));
    }

    // Numerus muss übereinstimmen!
    // ?Sie war sich sehend. - Aber nicht nicht: *Sie war einander sehend.
    if (numerusDesSubjekts != null) {
      featureReqs.add(ThreeStateFeatureEqualityFormula
          .featureEqualsExplicitValue("numerus",
              FeatureStringConverter.toFeatureString(numerusDesSubjekts)));
    }

    // Bei ?"Ich bin über mich nachdenkend." muss die Interpretation, in der
    // "mich" ein PPER ist (irreflexiv), AUSGESCHLOSSEN werden -
    // es kann nur ein (reflexives) PRF sein!
    // Allerdings tue ich mich schwer, dass auf unflektierte ADJEKTIVPHRASEN
    // zu übertragen...
    // ich möchte vermeiden, dass es jedes unflektierte Adjektiv mehrfach
    // geben muss...

    // final ThreeStateFeatureEqualityFormula
    // featureConditionExcludingIrrreflPersonalPronounIfAppropriate =
    // buildFeatureConditionExcludingIrrreflPersonalPronounIfAppropriate(person,
    // numerusDesSubjekts, hoeflichkeitsformDesSubjekts);
    // if (featureConditionExcludingIrrreflPersonalPronounIfAppropriate !=
    // null) {
    // featureReqs.add(featureConditionExcludingIrrreflPersonalPronounIfAppropriate);
    // }

    return FormulaUtil.and(featureReqs); // immutable
  }

  /**
   * Bei "Ich denke über mich nach." muss die Interpretation, in der die
   * Nominalphrase "mich" ein PPER enthält, AUSGESCHLOSSEN werden!
   * <p>
   * Regel: Ein Satz (genauer: Ein Verb-Rahmen), dessen Subjekt in der 1. oder
   * 2. Person steht (oder in der "Sie"-Form) und einen gewissen Numerus hat,
   * KANN kein PPER in derselbe Person und demselben Numerus enthalten (nicht im
   * Objekt (egal ob rein reflexiv oder nicht), nicht im Adverbial)
   *
   * @return ggf. <code>null</code>
   */
  protected static final ThreeStateFeatureEqualityFormula buildFeatureConditionExcludingIrrreflPersonalPronounIfAppropriate(
      final @Nullable String personDesSubjekts,
      final @Nullable Numerus numerusDesSubjekts,
      final @Nullable String hoeflichkeitsformDesSubjekts) {
		String featureName = null;

		if ("1".equals(personDesSubjekts)) {
			if (numerusDesSubjekts == Numerus.SINGULAR) {
				// "meiner", "mir", "mich"
				featureName = "enthaeltIrreflPersonalpronomen1Sg";
      } else if (numerusDesSubjekts == Numerus.PLURAL) {
				// ("unser", "uns" / dat, "uns" / akk
				featureName = "enthaeltIrreflPersonalpronomen1Pl";
			}
		} else if ("2".equals(personDesSubjekts)) {
			if (numerusDesSubjekts == Numerus.SINGULAR) {
				// "deiner", "dir", "dich"
				featureName = "enthaeltIrreflPersonalpronomen2Sg";
			} else if (numerusDesSubjekts  == Numerus.PLURAL) {
				// "euer", "euch" /dat, "euch" / akk
				featureName = "enthaeltIrreflPersonalpronomen2Pl";
			}
    } else if ("3".equals(personDesSubjekts)
        && numerusDesSubjekts == Numerus.PLURAL
        && StringFeatureLogicUtil.stringToBoolean(hoeflichkeitsformDesSubjekts)) {
			// "Sie"-Form
			// "Ihrer", "Ihnen", "Sie"
			featureName = "enthaeltIrreflPersonalpronomen3PlHoefl";
		}

		if (featureName == null) {
			// Nichts ausschließen. Er setzt ihn und Er setzt sich geht beides!
			return null;
		}

		// Bei "Ich denke über mich nach." muss die Interpretation, in der die
		// Nominalphrase "mich" ein PPER enthält, AUSGESCHLOSSEN werden!
		return ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
        featureName, StringFeatureLogicUtil.FALSE);
	}

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (!this.getClass().equals(obj.getClass())) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

}
