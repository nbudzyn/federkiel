package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

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
   * Deklination eines stets unver�nderten Adjektivs, etwa vom Typ <i>Kieler</i> oder <i>neunziger
   * (Jahre)</i>.
   */
  public Collection<IWordForm> unveraendert(final Lexeme lexeme, final Valenz valenz,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final String wordForm = lexeme.getCanonicalizedForm();

    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsSgMask = valenz
        .buildErgaenzungenUndAngabenSlots("3", MASKULINUM, SINGULAR, StringFeatureLogicUtil.FALSE);

    // Ich glaube, Formen auf -er lassen sich nicht steigern.
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.NOM_KEIN_NOMEN, SINGULAR, MASKULINUM,
        UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgMask, wordForm));
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.GEN_OHNE_S_UND_R, SINGULAR, MASKULINUM,
        UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgMask, wordForm));
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.DAT, SINGULAR, MASKULINUM, UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgMask, wordForm));
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.AKK, SINGULAR, MASKULINUM, UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgMask, wordForm));

    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsSgFem = valenz
        .buildErgaenzungenUndAngabenSlots("3", FEMININUM, SINGULAR, StringFeatureLogicUtil.FALSE);

    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.NOM_KEIN_NOMEN, SINGULAR, FEMININUM,
        UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgFem, wordForm));
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.GEN_OHNE_S_UND_R, SINGULAR, FEMININUM,
        UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgFem, wordForm));
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.DAT, SINGULAR, FEMININUM, UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgFem, wordForm));
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.AKK, SINGULAR, FEMININUM, UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgFem, wordForm));

    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsSgNeutr = valenz
        .buildErgaenzungenUndAngabenSlots("3", NEUTRUM, SINGULAR, StringFeatureLogicUtil.FALSE);

    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.NOM_KEIN_NOMEN, SINGULAR, NEUTRUM,
        UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgNeutr, wordForm));
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.GEN_OHNE_S_UND_R, SINGULAR, NEUTRUM,
        UNFLEKTIERT, ergaenzungenUndAngabenSlotsSgNeutr, wordForm));
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.DAT, SINGULAR, NEUTRUM, UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgNeutr, wordForm));
    res.add(buildWortform(lexeme, pos, POSITIV, KasusInfo.AKK, SINGULAR, NEUTRUM, UNFLEKTIERT,
        ergaenzungenUndAngabenSlotsSgNeutr, wordForm));

    res.addAll(unveraendertPl(lexeme, valenz, pos, UNFLEKTIERT, wordForm));

    return res.build();
  }

  /**
   * Unver�nderte Plural-Deklination eines Adjektivs, etwa vom Typ <i>Kieler</i> oder <i>neunziger
   * (Jahre)</i> oder <i>drei</i>.
   */
  private ImmutableList<IWordForm> unveraendertPl(final Lexeme lexeme, final Valenz valenz,
      final String pos, final @Nullable String starkSchwach, final String wordForm) {
    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlotsPl =
        valenz.buildErgaenzungenUndAngabenSlots("3", null, PLURAL, StringFeatureLogicUtil.FALSE);
    // Die ihrer selbst gedenkenden M�nner, aber nicht
    // *die Ihrer selbst gedenkenden M�nner!

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
      Kasus kasus) {
    switch (kasus) {
      case NOMINATIV:
        return buildWortform(lexeme, pos, POSITIV, KasusInfo.NOM_KEIN_NOMEN, PLURAL, null,
            starkSchwach, ergaenzungenUndAngabenSlotsPl, wordForm);
      case GENITIV:
        return buildWortform(lexeme, pos, POSITIV, guessGenitivAdjektivInfoPl(wordForm), PLURAL,
            null, starkSchwach, ergaenzungenUndAngabenSlotsPl, wordForm);
      case DATIV:
        return buildWortform(lexeme, pos, POSITIV, KasusInfo.DAT, PLURAL, null, starkSchwach,
            ergaenzungenUndAngabenSlotsPl, wordForm);
      case AKKUSATIV:
        return buildWortform(lexeme, pos, POSITIV, KasusInfo.AKK, PLURAL, null, starkSchwach,
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
        string);
  }

  /**
   * Gibt die starke Adjektivform zur�ck, ggf. auch Alternativen (dann die gebr�uchlichste zuerst).
   * <p>
   * Lexem und Stamm k�nnen in Einzelf�llen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjPositivStark(final Lexeme lexeme, String stamm, Kasus kasus,
      final Numerus numerus, Genus genus) {
    return adjStark(lexeme, stamm, POSITIV, kasus, numerus, genus);
  }

  /**
   * Gibt die starke Adjektivform zur�ck, in der angegebenen Komparation, ggf. auch Alternativen
   * (dann die gebr�uchlichste zuerst).
   * <p>
   * Lexem und Stamm k�nnen in Einzelf�llen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  private ImmutableList<IWordForm> adjStark(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus, final Numerus numerus, Genus genus) {
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
   * Gibt die starke Adjektivform im Singular zur�ck, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebr�uchlichste zuerst).
   * <p>
   * Lexem und Stamm k�nnen in Einzelf�llen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  private ImmutableList<IWordForm> adjStarkSg(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus, Genus genus) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    stammInKomparationGgfAuchNachETilgung(stamm, komparation).stream()
        .map(stammInKomparation -> adjStarkSg(lexeme, GermanPOS.ADJA.toString(),
            stammInKomparation, GenMaskNeutrSgModus.NUR_EN, NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG,
            VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
            Valenz.LEER, buildFeatureMap(komparation, STARK), kasus, genus))
        .forEach(t -> res.addAll(t));

    return res.build();
  }

  /**
   * Gibt die starke Adjektivform im Plural zur�ck, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebr�uchlichste zuerst).
   * <p>
   * Lexem und Stamm k�nnen in Einzelf�llen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjStarkPl(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus) {
    final ImmutableMap<String, IFeatureValue> additionalFeaturesPl =
        buildFeatureMap(komparation, STARK,
            Valenz.LEER.buildErgaenzungenUndAngabenSlots("3", null,
                // (IHRER selbst gedenkende) M�nner /
                // Frauen / Kinder,
                // -> alle Genera m�glich!
                PLURAL, StringFeatureLogicUtil.FALSE));
    // Die ihrer selbst gedenkenden M�nner,
    // ABER NICHT die Ihrer selbst gedenkenden M�nner!

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    stammInKomparationGgfAuchNachETilgung(stamm, komparation).stream()
        .map(stammInKomparation -> adjStarkPl(lexeme,
            VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, GermanPOS.ADJA.toString(),
            stammInKomparation,
            additionalFeaturesPl, kasus))
        .forEach(t -> res.addAll(t));

    return res.build();
  }

  /**
   * Gibt die schwache Adjektivform zur�ck, ggf. auch Alternativen (dann die gebr�uchlichste
   * zuerst).
   * <p>
   * Lexem und Stamm k�nnen in Einzelf�llen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjPositivSchwach(final Lexeme lexeme, String stamm, Kasus kasus,
      final Numerus numerus, Genus genus) {
    return adjSchwach(lexeme, stamm, POSITIV, kasus, numerus, genus);
  }

  /**
   * Gibt die schwache Adjektivform zur�ck, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebr�uchlichste zuerst).
   * <p>
   * Lexem und Stamm k�nnen in Einzelf�llen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjSchwach(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus, final Numerus numerus, Genus genus) {
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
   * Gibt die schwache Adjektivform im Singular zur�ck, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebr�uchlichste zuerst).
   * <p>
   * Lexem und Stamm k�nnen in Einzelf�llen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  private ImmutableList<IWordForm> adjSchwachSg(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus, Genus genus) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    stammInKomparationGgfAuchNachETilgung(stamm, komparation).stream()
        .map(stammInKomparation -> adjSchwachSg(lexeme,
            VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, GermanPOS.ADJA.toString(),
            stammInKomparation,
            GermanUtil.erlaubtAdjektivischeETilgungBeiSuffixEnUndEm(stammInKomparation),
            Valenz.LEER, buildFeatureMap(komparation, SCHWACH), kasus, genus))
        .forEach(t -> res.addAll(t));

    return res.build();
  }

  /**
   * Gibt die schwache Adjektivform im Plural zur�ck, in der angegebenen Komparation, ggf. auch
   * Alternativen (dann die gebr�uchlichste zuerst).
   * <p>
   * Lexem und Stamm k�nnen in Einzelf�llen voneinander abweichen, etwa <i>hoch</i> vs. <i>hoh</i>,
   * <i>anders</i> vs. <i>ander</i>.
   */
  public ImmutableList<IWordForm> adjSchwachPl(final Lexeme lexeme, final String stamm,
      final String komparation, final Kasus kasus) {
    final ImmutableMap<String, IFeatureValue> additionalFeaturesPl =
        buildFeatureMap(komparation, SCHWACH,
            Valenz.LEER.buildErgaenzungenUndAngabenSlots("3", // Person
                null,
                // die IHRER selbst gedenkende M�nner /
                // Frauen / Kinder,
                // -> alle Genera m�glich!
                PLURAL, StringFeatureLogicUtil.FALSE));
    // die ihrer selbst gedenkenden M�nner,
    // NICHT JEDOCH: *die Ihrer selbst gedenkenden M�nner!
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    stammInKomparationGgfAuchNachETilgung(stamm, komparation).stream()
        .map(stammInKomparation -> adjSchwachPl(lexeme,
            VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, GermanPOS.ADJA.toString(),
            stammInKomparation,
            GermanUtil.erlaubtAdjektivischeETilgungBeiSuffixEnUndEm(stammInKomparation),
            additionalFeaturesPl, kasus))
        .forEach(t -> res.addAll(t));

    return res.build();
  }


  /**
   * Liefert den Stamm in der vorgegebenen Komparation, ggf. auch mehrere Alternativen (dann die
   * �blichste zuerst).
   */
  private Collection<String> stammInKomparationGgfAuchNachETilgung(String stamm,
      String komparation) {
    switch (komparation) {
      case AbstractArtikelPronomenAdjektivFlektierer.POSITIV:
        final @Nullable String stammNachETilgung = GermanUtil.tilgeEAusStammWennMoeglich(stamm);
        return CollectionUtil.immmutableListSkipNulls(stamm, stammNachETilgung);
      case AbstractArtikelPronomenAdjektivFlektierer.KOMPARATIV:
        return komparativ(stamm);
      case AbstractArtikelPronomenAdjektivFlektierer.SUPERLATIV:
        final ImmutableList.Builder<String> res = ImmutableList.builder();
        komparativ(stamm).stream().map(komparativ -> superlativ(komparativ)).forEach(res::addAll);
        return res.build();
      default:
        throw new RuntimeException("Unerwartete komparation: " + komparation);
    }
  }

  /**
   * Versucht in heuristischer Weise zu ermitteln, ob es sich um einen -r- oder einen anderen
   * Genitiv handelt - f�r Adjektive.
   */
  public final static KasusInfo guessGenitivAdjektivInfoPl(final String genitivNomenPl) {
    return GermanUtil.guessGenitivInfoPl(genitivNomenPl, false);
  }

}
