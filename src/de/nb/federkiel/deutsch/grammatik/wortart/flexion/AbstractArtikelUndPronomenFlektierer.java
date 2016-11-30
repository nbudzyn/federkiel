package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.deutsch.grammatik.valenz.Valenz;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;
import de.nb.federkiel.lexikon.Wortform;
import de.nb.federkiel.semantik.NothingInParticularSemantics;

/**
 * Sammelt einige Gemeinsamkeiten von Artikel- und Pronomen- Flektierern
 *
 * @author nbudzyn 2009
 */
@ThreadSafe()
abstract class AbstractArtikelUndPronomenFlektierer extends
    AbstractArtikelPronomenAdjektivFlektierer {
  AbstractArtikelUndPronomenFlektierer() {
    super();
  }

  public Collection<IWordForm> einKeinUnser(final Lexeme lexeme,
      final String pos, final boolean auchPlural,
      final boolean generateFeatureWortartTraegtFlexionsendung,
      final boolean generateStaerke) {
    return einKeinUnser(
        lexeme,
        pos,
        lexeme.getCanonicalizedForm().substring(0,
            lexeme.getCanonicalizedForm().length()), auchPlural, // "ein"
        generateFeatureWortartTraegtFlexionsendung, generateStaerke);
  }

  private Collection<IWordForm> einKeinUnser(final Lexeme lexeme,
      final String pos, final String stamm, final boolean auchPlural,
      final boolean generateFeatureWortartTraegtFlexionsendung,
      final boolean generateStaerke) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList
        .<IWordForm> builder();

    final String staerke = generateStaerke ? STARK : null;

    res.addAll(adjStarkSg(
        lexeme,
        pos,
        stamm, // nur "eines (Autos)", nicht
        GenMaskNeutrSgModus.NUR_ES, // "ein (Auto)",
        // "*einen (Autos)"
        NomSgMaskUndNomAkkSgNeutrModus.ENDUNGSLOS, // nicht
        // "*einer (Auto)"
        generateFeatureWortartTraegtFlexionsendung ? VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH
            : VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, Valenz.LEER,
        buildFeatureMap(staerke)));

    if (auchPlural) {
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv = generateFeatureWortartTraegtFlexionsendung ? VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH
          : VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN;

      res.addAll(adjStarkPl(
          lexeme,
          vorgabeFuerNachfolgendesAdjektiv,
          pos,
          stamm,
          buildFeatureMap(staerke,
              Valenz.LEER.buildErgaenzungenUndAngabenSlots("3", null,
              // (IHRER selbst gedenkende) M�nner /
              // Frauen / Kinder,
              // -> alle Genera m�glich!
              // (und au�erdem macht es bei LEERER Valenz
              // ohnehin keinen Unterschied!)
                  PLURAL, StringFeatureLogicUtil.FALSE))));
      // Die ihrer selbst gedenkenden M�nner, aber nicht
      // *die Ihrer selbst gedenkenden M�nner!
    }

    return res.build();
  }

  /**
   * Erzeugt eine attribuierende Pronomen- oder Artikel-Wortform. Sie besitzt
   * (da sie attribuierend ist) <i>keine</i> Person.
   *
   * @param numerus
   *          for an unspecified value use
   *          UnspecifiedFeatureValue.UNSPECIFIED_STRING!
   */
  Wortform buildWortform(final Lexeme lexeme, final String pos,
      final KasusInfo kasusInfo, final String staerke,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv,
      final Numerus numerus, final @Nullable Genus genus, final String string) {
    final ImmutableMap<String, IFeatureValue> additionalFeatures = buildFeatureMap(
        staerke, Valenz.LEER.buildErgaenzungenUndAngabenSlots("3", genus,
            numerus, StringFeatureLogicUtil.FALSE));
    // Die ihrer selbst gedenkenden M�nner, aber nicht
    // *die Ihrer selbst gedenkenden M�nner!

    return buildWortform(lexeme, pos, kasusInfo,
        vorgabeFuerNachfolgendesAdjektiv, numerus, genus, additionalFeatures,
        string);
  }

  /**
   * Erzeugt eine attribuierende Pronomen- oder Artikel-Wortform. Sie besitzt
   * (da sie attribuierend ist) <i>keine</i> Person.
   *
   * @param numerus
   *          for an unspecified value use
   *          UnspecifiedFeatureValue.UNSPECIFIED_STRING!
   */
  Wortform buildWortform(final Lexeme lexeme, final String pos,
      final KasusInfo kasusInfo,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv,
      final Numerus numerus, final @Nullable Genus genus, final String string) {
    final ImmutableMap<String, IFeatureValue> additionalFeatures = buildFeatureMap(Valenz.LEER
        .buildErgaenzungenUndAngabenSlots("3", genus, numerus,
            StringFeatureLogicUtil.FALSE));
    // Die ihrer selbst gedenkenden M�nner, aber nicht
    // *die Ihrer selbst gedenkenden M�nner!

    return buildWortform(lexeme, pos, kasusInfo,
        vorgabeFuerNachfolgendesAdjektiv, numerus, genus, additionalFeatures,
        string);
  }

  /**
   * Erzeugt eine Personalpronomen-Wortform f�r die erste Person.
   */
  Wortform buildWortformPersPronP1(final Lexeme lexeme, final String pos,
      final Kasus kasus, final Numerus numerus,
      final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit,
      final String string) {
    return buildWortformPersPron(lexeme, pos, kasus, "1", numerus,
        false, // Keine H�flichkeitsform
        null, pseudoaktantMoeglichkeit,
        string);
  }

  /**
   * Erzeugt eine Personalpronomen-Wortform f�r die zweite Person.
   *
   */
  Wortform buildWortformPersPronP2(final Lexeme lexeme, final String pos,
      final @Nullable Kasus kasus, final Numerus numerus,
      final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit,
      final String string) {
    return buildWortformPersPron(lexeme, pos, kasus, "2", numerus,
        false, // Keine Hoeflichkeitsform
        null, pseudoaktantMoeglichkeit,
        string);
  }

  /**
   * Erzeugt eine <i>substituierende</i> Pronomen-Wortform. Sie besitzt (da sie
   * substituierend ist) das Merkmal "Person", hier dritte person.
   *
   * @param kasus
   *          for an unspecified value use
   *          UnspecifiedFeatureValue.UNSPECIFIED_STRING!
   * @param genus
   *          for an unspecified value use
   *          UnspecifiedFeatureValue.UNSPECIFIED_STRING!
   * @param hoeflichkeitsform
   *          ob es sich um die H�flichkeitsform ("Sie", "Ihrer", "Ihnen",
   *          "Sie") handelt
   */
  Wortform buildWortformPersPronP3(final Lexeme lexeme, final String pos,
      final @Nullable Kasus kasus, final @Nullable Numerus numerus,
      final boolean hoeflichkeitsform, final Genus genus,
      final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit,
      final String string) {
    return buildWortformPersPron(lexeme, pos, kasus, "3", numerus,
        hoeflichkeitsform, genus, pseudoaktantMoeglichkeit, string);
  }

  /**
   * Builds a plural word form with a person feature. The genus will be
   * unspecified.
   *
   * @param hoeflichkeitsform
   *          ob es sich um die H�flichkeitsform ("Sie", "Ihrer", "Ihnen",
   *          "Sie") handelt
   */
  Wortform buildWortformPersPronPluralP3(final Lexeme lexeme, final String pos,
      final @Nullable Kasus kasus, final boolean hoeflichkeitsform,
      final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit,
      final String string) {
    return buildWortformPersPronP3(lexeme, pos, kasus, PLURAL,
        hoeflichkeitsform, null,
        pseudoaktantMoeglichkeit, string);
  }

  /**
   * Builds a plural word form - without a person feature. The genus will be
   * unspecified.
   */
  Wortform buildWortformPlural(final Lexeme lexeme, final String pos,
      final KasusInfo kasusInfo, final String staerke, final String string,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv) {
    return buildWortform(lexeme, pos, kasusInfo, staerke,
        vorgabeFuerNachfolgendesAdjektiv, PLURAL, null, string);
  }

  /**
   * Builds a plural word form - without a person feature. The genus will be
   * unspecified.
   */
  Wortform buildWortformPlural(final Lexeme lexeme, final String pos,
      final KasusInfo kasusInfo, final String string,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv) {
    return buildWortform(lexeme, pos, kasusInfo,
        vorgabeFuerNachfolgendesAdjektiv, PLURAL, null, string);
  }

  /**
   * Erzeugt eine Personlapronomen-Wortform. Sie besitzt insbesondere das
   * Merkmal "Person".
   *
   * @param hoeflichkeitsform
   *          ob es sich um die H�flichkeitsform ("Sie", "Ihrer", "Ihnen",
   *          "Sie") handelt
   * @param pseudoaktantMoeglichkeit
   *          ob dieses Personalpronomen-Form als Pseudoaktant geeignet ist.
   *          (Nur "es" ist - im Nominativ und Akkusativ - als Pseudoaktant
   *          geeignet - vgl. "Es regnet".)
   */
  private Wortform buildWortformPersPron(final Lexeme lexeme, final String pos,
      final @Nullable Kasus kasus, final String person,
      final @Nullable Numerus numerus, final boolean hoeflichkeitsform,
      final @Nullable Genus genus,
      final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit,
      final String string) {

    final Builder<String, String> builder = ImmutableMap
        .<String, String> builder()
        .put("kasus", FeatureStringConverter.toFeatureString(kasus))
        .put("numerus", FeatureStringConverter.toFeatureString(numerus))
        .put("genus", FeatureStringConverter.toFeatureString(genus))
        .put("person", person)
        .put("hoeflichkeitsform",
            StringFeatureLogicUtil.booleanToString(hoeflichkeitsform));

    if (pseudoaktantMoeglichkeit.isMerkmalVorgesehen()) {
      builder.put(GermanUtil.GEEIGNET_ALS_PSEUDOAKTANT_ES,
          StringFeatureLogicUtil.booleanToString(pseudoaktantMoeglichkeit
              .isMoeglich()));
    }

    final ImmutableMap<String, String> featureMap = builder.build();

    final FeatureStructure features = FeatureStructure
        .fromStringValues(featureMap);

    final Wortform res = new Wortform(lexeme, pos, string, features,
        NothingInParticularSemantics.INSTANCE);

    return res;
  }

}