package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.KOMPARATION_KOMPARATIV;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.KOMPARATION_POSITIV;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.KOMPARATION_SUPERLATIV;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_SCHWACH;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_STARK;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_UNFLEKTIERT;

import java.util.Collection;

import javax.annotation.Nullable;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.deutsch.grammatik.valenz.Valenz;
import de.nb.federkiel.deutsch.lexikon.GermanPOS;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.interfaces.IFeatureType;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;

/**
 * Kann Adjektive flektieren.
 *
 * @author nbudzyn 2010
 */
public class AdjektivFlektierer extends AbstractArtikelPronomenAdjektivFlektierer {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AdjektivFlektierer.class); // NOPMD
                                                                                // by
                                                                                // nbudzyn

  public static final String TYP = "Adjektiv";

  public AdjektivFlektierer() {
    super();
  }

  /**
   * Deklination eines stets unveränderten Adjektivs, etwa vom Typ <i>Kieler</i> oder <i>neunziger
   * (Jahre)</i>.
   */
  public Collection<IWordForm> unveraendert(final Lexeme lexeme, final Valenz valenz,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final String wordForm = lexeme.getCanonicalizedForm();

    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsSgMask = valenz
        .buildErgaenzungenUndAngabenSlots("3", MASKULINUM, SINGULAR, StringFeatureLogicUtil.FALSE,
            true);

    // Ich glaube, Formen auf -er lassen sich nicht steigern.
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.NOM_KEIN_NOMEN, SINGULAR, MASKULINUM,
        STAERKE_UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgMask, wordForm));
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.GEN_OHNE_S_UND_R, SINGULAR, MASKULINUM,
        STAERKE_UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgMask, wordForm));
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.DAT, SINGULAR, MASKULINUM, STAERKE_UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgMask, wordForm));
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.AKK, SINGULAR, MASKULINUM, STAERKE_UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgMask, wordForm));

    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsSgFem = valenz
        .buildErgaenzungenUndAngabenSlots("3", FEMININUM, SINGULAR, StringFeatureLogicUtil.FALSE,
            true);

    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.NOM_KEIN_NOMEN, SINGULAR, FEMININUM,
        STAERKE_UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgFem, wordForm));
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.GEN_OHNE_S_UND_R, SINGULAR, FEMININUM,
        STAERKE_UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgFem, wordForm));
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.DAT, SINGULAR, FEMININUM, STAERKE_UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgFem, wordForm));
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.AKK, SINGULAR, FEMININUM, STAERKE_UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgFem, wordForm));

    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsSgNeutr = valenz
        .buildErgaenzungenUndAngabenSlots("3", NEUTRUM, SINGULAR, StringFeatureLogicUtil.FALSE,
            true);

    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.NOM_KEIN_NOMEN, SINGULAR, NEUTRUM,
        STAERKE_UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgNeutr, wordForm));
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.GEN_OHNE_S_UND_R, SINGULAR, NEUTRUM,
        STAERKE_UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgNeutr, wordForm));
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.DAT, SINGULAR, NEUTRUM, STAERKE_UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgNeutr, wordForm));
    res.add(buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.AKK, SINGULAR, NEUTRUM, STAERKE_UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgNeutr, wordForm));

    res.addAll(unveraendertPl(lexeme, valenz, pos, STAERKE_UNFLEKTIERT, wordForm));

    return res.build();
  }

  /**
   * Unveränderte Plural-Deklination eines Adjektivs, etwa vom Typ <i>Kieler</i> oder <i>neunziger
   * (Jahre)</i> oder <i>drei</i>.
   */
  private ImmutableList<IWordForm> unveraendertPl(final Lexeme lexeme, final Valenz valenz,
      final String pos, final @Nullable String starkSchwach, final String wordForm) {
    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsPl =
        valenz.buildErgaenzungenUndAngabenSlots("3", null, PLURAL, StringFeatureLogicUtil.FALSE,
            true);
    // Die ihrer selbst gedenkenden Männer, aber nicht
    // *die Ihrer selbst gedenkenden Männer!

    return unveraendertPl(lexeme, pos, starkSchwach, wordForm, ergaenzungenUndAngabenSlotsPl);
  }

  private ImmutableList<IWordForm> unveraendertPl(final Lexeme lexeme, final String pos,
      final String starkSchwach, final String wordForm,
      final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsPl) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
    res.add(unveraendertPl(lexeme, pos, starkSchwach, wordForm, ergaenzungenUndAngabenSlotsPl,
        Kasus.NOMINATIV));
    res.add(unveraendertPl(lexeme, pos, starkSchwach, wordForm, ergaenzungenUndAngabenSlotsPl,
        Kasus.GENITIV));
    res.add(unveraendertPl(lexeme, pos, starkSchwach, wordForm, ergaenzungenUndAngabenSlotsPl,
        Kasus.DATIV));
    res.add(unveraendertPl(lexeme, pos, starkSchwach, wordForm, ergaenzungenUndAngabenSlotsPl,
        Kasus.AKKUSATIV));
    return res.build();
  }

  private IWordForm unveraendertPl(final Lexeme lexeme, final String pos, final String starkSchwach,
      final String wordForm, final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsPl,
      final Kasus kasus) {
    switch (kasus) {
      case NOMINATIV:
        return buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.NOM_KEIN_NOMEN, PLURAL, null,
            starkSchwach, ergaenzungenUndAngabenSlotsPl, wordForm);
      case GENITIV:
        return buildWortform(lexeme, pos, KOMPARATION_POSITIV, guessGenitivAdjektivInfoPl(wordForm), PLURAL,
            null, starkSchwach, ergaenzungenUndAngabenSlotsPl, wordForm);
      case DATIV:
        return buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.DAT, PLURAL, null, starkSchwach,
            ergaenzungenUndAngabenSlotsPl, wordForm);
      case AKKUSATIV:
        return buildWortform(lexeme, pos, KOMPARATION_POSITIV, KasusInfo.AKK, PLURAL, null, starkSchwach,
            ergaenzungenUndAngabenSlotsPl, wordForm);
      default:
        throw new IllegalStateException("Unerwarteter Kasus " + kasus);
    }
  }


  /**
   * Erzeugt eine Adjektiv-Wortform.
   *
   * @param lexeme Die Nennform des Lexeme soll nicht auf -e enden.
   */
  private IWordForm buildWortform(final Lexeme lexeme, final String pos, final String komparation,
      final KasusInfo kasusInfo, final Numerus numerus, final Genus genus,
      final String starkSchwach, final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlots,
      final String string) {

    return buildWortform(lexeme, pos, kasusInfo, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
        numerus, genus, buildFeatureMap(komparation, starkSchwach, ergaenzungenUndAngabenSlots),
        buildFeatureTypeMap(komparation, starkSchwach),
        string);
  }

  /**
   * Gibt die starke Adjektivform zurück, ggf. auch Alternativen (dann die gebräuchlichste zuerst).
   * <p>
   * Lexem und Stamm können in Einzelfällen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjPositivStark(final Lexeme lexeme, final String stamm, final Kasus kasus,
      final Numerus numerus, final Genus genus) {
    return adjStark(lexeme, stamm, KOMPARATION_POSITIV, kasus, numerus, genus);
  }

  /**
   * Gibt die starke Adjektivform zurück, in der angegebenen Komparation, ggf. auch Alternativen
   * (dann die gebräuchlichste zuerst).
   * <p>
   * Lexem und Stamm können in Einzelfällen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  private ImmutableList<IWordForm> adjStark(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus, final Numerus numerus, final Genus genus) {
    switch (numerus) {
      case SINGULAR:
        return adjStarkSg(lexeme, stamm, komparation, kasus, genus);
      case PLURAL:
        return adjStarkPl(lexeme, stamm, komparation, kasus);
      default:
        throw new IllegalStateException("Unerwarteter Numerus: " + numerus);
    }
  }

  /**
   * Gibt die starke Adjektivform im Singular zurück, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebräuchlichste zuerst).
   * <p>
   * Lexem und Stamm können in Einzelfällen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  private ImmutableList<IWordForm> adjStarkSg(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus, final Genus genus) {
    return stammInKomparationGgfAuchNachETilgung(stamm, komparation).stream()
        .map(stammInKomparation -> adjStarkSg(lexeme, GermanPOS.ADJA.toString(),
            stammInKomparation, GenMaskNeutrSgModus.NUR_EN, NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG,
            VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
            Valenz.LEER, buildFeatureMap(komparation, STAERKE_STARK),
            buildFeatureTypeMap(komparation, STAERKE_STARK), kasus, genus))
        .flatMap(Collection::stream)
        .collect(ImmutableList.toImmutableList());
  }

  /**
   * Gibt die starke Adjektivform im Plural zurück, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebräuchlichste zuerst).
   * <p>
   * Lexem und Stamm können in Einzelfällen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjStarkPl(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus) {
    final ImmutableMap<String, IFeatureValue> additionalFeaturesPl =
        buildFeatureMap(komparation, STAERKE_STARK,
            Valenz.LEER.buildErgaenzungenUndAngabenSlots("3", null,
                // (IHRER selbst gedenkende) Männer /
                // Frauen / Kinder,
                // -> alle Genera möglich!
                PLURAL, StringFeatureLogicUtil.FALSE, true));
    // Die ihrer selbst gedenkenden Männer,
    // ABER NICHT die Ihrer selbst gedenkenden Männer!
    final ImmutableMap<String, IFeatureType> additionalFeaturesTypesPl =
        buildFeatureTypeMap(komparation, STAERKE_STARK);

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    stammInKomparationGgfAuchNachETilgung(stamm, komparation).stream()
        .map(stammInKomparation -> adjStarkPl(lexeme,
            VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, GermanPOS.ADJA.toString(),
            stammInKomparation,
            additionalFeaturesPl, additionalFeaturesTypesPl, kasus))
        .forEach(t -> res.addAll(t));

    return res.build();
  }

  /**
   * Gibt die schwache Adjektivform zurück, ggf. auch Alternativen (dann die gebräuchlichste
   * zuerst).
   * <p>
   * Lexem und Stamm können in Einzelfällen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjPositivSchwach(final Lexeme lexeme, final String stamm, final Kasus kasus,
      final Numerus numerus, final Genus genus) {
    return adjSchwach(lexeme, stamm, KOMPARATION_POSITIV, kasus, numerus, genus);
  }

  /**
   * Gibt die schwache Adjektivform zurück, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebräuchlichste zuerst).
   * <p>
   * Lexem und Stamm können in Einzelfällen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjSchwach(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus, final Numerus numerus, final Genus genus) {
    switch(numerus) {
      case SINGULAR:
        return adjSchwachSg(lexeme, stamm, komparation, kasus, genus);
      case PLURAL:
        return adjSchwachPl(lexeme, stamm, komparation, kasus);
      default:
        throw new IllegalStateException("Unerwarteter Numerus: " + numerus);
    }
  }

  /**
   * Gibt die schwache Adjektivform im Singular zurück, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebräuchlichste zuerst).
   * <p>
   * Lexem und Stamm können in Einzelfällen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  private ImmutableList<IWordForm> adjSchwachSg(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus, final Genus genus) {
    return stammInKomparationGgfAuchNachETilgung(stamm, komparation).stream()
        .map(stammInKomparation -> adjSchwachSg(lexeme,
            VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, GermanPOS.ADJA.toString(),
            stammInKomparation,
            GermanUtil.erlaubtAdjektivischeETilgungBeiSuffixEnUndEm(stammInKomparation),
            Valenz.LEER, buildFeatureMap(komparation, STAERKE_SCHWACH),
            buildFeatureTypeMap(komparation, STAERKE_SCHWACH), kasus, genus))
        .flatMap(Collection::stream).collect(ImmutableList.toImmutableList());
  }

  /**
   * Gibt die schwache Adjektivform im Plural zurück, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebräuchlichste zuerst).
   * <p>
   * Lexem und Stamm können in Einzelfällen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjSchwachPl(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus) {
    final ImmutableMap<String, IFeatureValue> additionalFeaturesPl =
        buildFeatureMap(komparation, STAERKE_SCHWACH,
            Valenz.LEER.buildErgaenzungenUndAngabenSlots("3", // Person
                null,
                // die IHRER selbst gedenkende Männer /
                // Frauen / Kinder,
                // -> alle Genera möglich!
                PLURAL, StringFeatureLogicUtil.FALSE, true));
    // die ihrer selbst gedenkenden Männer,
    // NICHT JEDOCH: *die Ihrer selbst gedenkenden Männer!

    final ImmutableMap<String, IFeatureType> additionalFeaturesTypesPl =
        buildFeatureTypeMap(komparation, STAERKE_SCHWACH);

    return stammInKomparationGgfAuchNachETilgung(stamm, komparation).stream()
        .map(stammInKomparation -> adjSchwachPl(lexeme,
            VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, GermanPOS.ADJA.toString(),
            stammInKomparation,
            GermanUtil.erlaubtAdjektivischeETilgungBeiSuffixEnUndEm(stammInKomparation),
            additionalFeaturesPl, additionalFeaturesTypesPl, kasus))
        .flatMap(Collection::stream).collect(ImmutableList.toImmutableList());
  }


  /**
   * Liefert den Stamm in der vorgegebenen Komparation, ggf. auch mehrere Alternativen (dann die
   * üblichste zuerst).
   */
  private Collection<String> stammInKomparationGgfAuchNachETilgung(final String stamm,
      final String komparation) {
    switch (komparation) {
      case KOMPARATION_POSITIV:
        final @Nullable String stammNachETilgung = GermanUtil.tilgeEAusStammWennMoeglich(stamm);
        return CollectionUtil.immmutableListSkipNulls(stamm, stammNachETilgung);
      case KOMPARATION_KOMPARATIV:
        return komparativ(stamm);
      case KOMPARATION_SUPERLATIV:
        final ImmutableList.Builder<String> res = ImmutableList.builder();
        komparativ(stamm).stream().map(komparativ -> superlativ(komparativ)).forEach(res::addAll);
        return res.build();
      default:
        throw new RuntimeException("Unerwartete komparation: " + komparation);
    }
  }

  /**
   * Versucht in heuristischer Weise zu ermitteln, ob es sich um einen -r- oder einen anderen
   * Genitiv handelt - für Adjektive.
   */
  public final static KasusInfo guessGenitivAdjektivInfoPl(final String genitivNomenPl) {
    return GermanUtil.guessGenitivInfoPl(genitivNomenPl, false);
  }

}
