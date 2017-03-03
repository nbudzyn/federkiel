package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.log4j.Logger;

import de.nb.federkiel.string.StringUtil;

/**
 * Utility methods for the German language.
 *
 * @author nbudzyn 2010
 */
@Immutable
@ThreadSafe
public final class GermanUtil {
  private static final String VOKAL_ZEICHEN = "aäeioöuüyAÄEIOÖUÜY";
  private static final String[] DIPHTHONGE =
      new String[] {"ai", "au", "äu", "ei", "eu", "ey", "oi", "öi", "ui"};
  private static final String[] TYPISCHE_VERSCHRIFTUNGEN_LANGER_VOKALE =
      new String[] {"aa", "ah", "ää", "ää", "ee", "eh", "ii", "ie", "ih", "ieh", "oo", "oh", "oe", // Itzehoe
          "öö", "öh", "uu", "uh", "üü", "üh", "yy", "yh"};
  private static final Logger log = Logger.getLogger(GermanUtil.class); // NOPMD

  public static final String DEFINIT_KEY = "definit";

  public static final String LEXEM_FLEKTIERBAR_KEY = "lexemFlektierbar";
  /**
   * für Adjektive: Frankfurter, neunziger
   */
  public static final String ABLEITUNG_AUF_ER_KEY = "ableitungAufEr";
  public static final String GENITIV_SICHTBAR_DURCH_S_KEY = "genitivSichtbarDurchS";
  public static final String GENITIV_SICHTBAR_DURCH_R_KEY = "genitivSichtbarDurchR";
  public static final String ERLAUBT_NACHGESTELLTES_SCHWACH_FLEKTIERTES_ADJEKTIV_KEY =
      "erlaubtNachgestelltesSchwachFlektiertesAdjektiv";
  protected static final String ERLAUBT_NACHGESTELLTES_STARK_FLEKTIERTES_ADJEKTIV_KEY =
      "erlaubtNachgestelltesStarkFlektiertesAdjektiv";

  public static final String GEEIGNET_ALS_PSEUDOAKTANT_ES_KEY = "geeignetAlsPseudoaktantEs";

  public static final String EINZELNES_REFLEXIVPRONOMEN_KEY = "einzelnesReflexivpronomen";

  public static final String IST_DAS_SUBJEKT_KEY = "istDasSubjekt";
  public static final String NENNFORM_KEY = "nennform";

  /**
   * Führt eine E-Tilgung von einem Stamm durch - wenn möglich. Beispiele:
   * <ul>
   * <li>Adjektive (siehe Duden 494)
   * <ul>
   * <li>finster -> finstr (ein finstres Kapitel)
   * <li>dunkel -> dunkl (ein dunkles Kapitel)
   * <li>genauer -> genaur (eine genaure Beschreibung)
   * </ul>
   * <li>Verben (siehe Duden 620)
   * <ul>
   * <li>sammeln -> samml (ich sammle)
   * <li>bedauern -> bedaur (ich bedaure) (Duden 620 fordert für eine e-Tilgung bei Verben, dass -er
   * auf einen Vokal folgt. Nach meinem Sprachgefühl ist rudern -> rudr (ich rudre) ebenfalls
   * möglich.)
   * </ul>
   * </ul>
   *
   * @return stamm nach e-Tilgung - oder <code>null</code>
   */
  public static String tilgeEAusStammWennMoeglich(final String stamm) {
    // Nur UNBETONTES -el, -er oder -en ist relevant ->
    // analoge *betonte* Endungen ausschließen
    if (StringUtil.endsWith(stamm, "eel", "eer", "een", "iel", "ier", "ien")) {
      return null;
    }

    if (stamm.length() <= 2) {
      return null;
    }

    if (StringUtil.endsWith(stamm, "el", "er", "en")) {
      // finster

      return stamm.substring(0, stamm.length() - 2) + // finst
          stamm.substring(stamm.length() - 1, stamm.length()); // r
    }

    return null;
  }

  /**
   * Diese Methode ist eher Heuristik!!!
   */
  protected static boolean endetAufBetontenVollvokal(final String wortform) {
    // Vollvokal bedeutet wohl "betonbarer Vokal", und das wiederum ist
    // wohl jeder Vokal außer Schwa.

    // IDEA Betonung kann ich hier nur schwer erschließen - brächte wohl
    // eine
    // Silbenanalyse o.Ä.

    // Vollvokale, und diese sind vermutlich immer betont
    if (StringUtil.endsWith(wortform, TYPISCHE_VERSCHRIFTUNGEN_LANGER_VOKALE)) {
      return true;
    }

    // TODO Vermutung: Diphtonge zwar Vollvokale, und meist auch
    // betont, aber Nackedei, angeblich Kotau
    if (StringUtil.endsWith(wortform, DIPHTHONGE)) {
      log.warn("Wortbildung unsicher (betonter Diphtong am Ende?): " + wortform);
      return true;
    }

    // Ein (einzelnes) "e am Ende ist vermutlich ein Schwa (Duden hat auch
    // kein anderes Beispiel in 280), also kein Vollvokal
    if (wortform.endsWith("e")) {
      return false;
    }

    if (!GermanUtil.letztesZeichenIstVokal(wortform)) {
      // Endet anscheinend nicht auf Vokal
      return false;
    }

    // OK, es sieht irgedwie alles so aus, als endete dieses Wort mit
    // einem *unbetonten Vollvokal*

    log.warn("Wortbildung unsicher (unbetonter Vollvokal am Ende?): " + wortform);

    return false; // IDEA: Daumen drücken!
  }

  public static boolean endetAufObstruentPlusMOderN(final String string) {
    if (string.isEmpty()) {
      return false;
    }

    if (!StringUtil.endsWith(string, "m", "n")) {
      return false;
    }

    return endetAufObstruent(string.substring(0, string.length() - 1));
  }

  private static boolean endetAufObstruent(final String string) {
    // Vgl. Duden 23

    if (string.endsWith("ng")) {
      // Sonorant!
      return false;
    }

    if (StringUtil.endsWith(string, "ow", "Ow")) {
      // Wohl ein Diphtong in einem Fremdwort aus dem Englischen?
      return false;
    }

    if (endetAufSLaut(string)) {
      return true;
    }

    if (StringUtil.endsWith(string, "ah", "eh", "ih", "oh", "uh", "äh", "üh", "öh", "yh")) {
      // Dehnungs-h!
      // zB. "woh", wie in "woh(nt)"
      return false;
    }

    return StringUtil.endsWith(string, "b", "c", "d", "f", "g", "h", // z.B. ch, wie in "reCH(net)";
                                                                     // oder sch
        "j", // (vermute ich zumindest)
        "k", "p", "q", "t", "v", "w");
  }

  public static boolean endetAufSLaut(final String wortform) {
    if (StringUtil.endsWith(wortform, "s", "ß", "x", "z", "ce")) { // z.B. Interface
      return true;
    }

    return false;
  }

  public static boolean endetAufSchLaut(final String wortform) {
    if (StringUtil.endsWith(wortform, "sch", "sh" // vielleicht Fremdwörter
    )) {
      return true;
    }

    return false;
  }

  /**
   * Diese Methode ist eher Heuristik!!!
   */
  public static boolean endetAufUnbetontenVollvokal(final String wortform) {
    // Vollvokal bedeutet wohl "betonbarer Vokal", und das wiederum ist
    // wohl jeder Vokal außer Schwa.

    // IDEA Betonung kann ich hier nur schwer erschließen - brächte wohl
    // eine
    // Silbenanalyse o.Ä.

    // Vollvokale, aber diese sind vermutlich immer betont
    if (StringUtil.endsWith(wortform, TYPISCHE_VERSCHRIFTUNGEN_LANGER_VOKALE)) {
      return false;
    }

    // TODO Vermutung: Diphtonge zwar Vollvokale, aber wohl meist auch
    // betont, aber Nackedei, angeblich Kotau
    if (StringUtil.endsWith(wortform, DIPHTHONGE)) {
      log.warn("Wortbildung unsicher (betonter Diphtong am Ende?): " + wortform);
      return false;
    }

    // Ein (einzelnes) "e am Ende ist vermutlich ein Schwa (Duden hat auch
    // kein anderes Beispiel in 280), also kein Vollvokal
    if (wortform.endsWith("e")) {
      return false;
    }

    if (!GermanUtil.letztesZeichenIstVokal(wortform)) {
      // Ende anscheindne nicht auf Vokal
      return false;
    }

    // OK, es sieht irgedwie alles so aus, als endete dieses Wort mit
    // einem *unbetonten Vollvokal*

    log.warn("Wortbildung unsicher (unbetonter Vollvokal am Ende?): " + wortform);

    return true; // IDEA: Daumen drücken!
  }

  public static boolean endetAufVollvokal(final String wort) {
    // Vollvokal bedeutet wohl "betonbarer Vokal", und das wiederum ist
    // wohl jeder Vokal außer Schwa.
    // Vollvokale, aber diese sind vermutlich immer betont
    if (StringUtil.endsWith(wort, TYPISCHE_VERSCHRIFTUNGEN_LANGER_VOKALE)) {
      return true;
    }

    if (StringUtil.endsWith(wort, DIPHTHONGE)) {
      return true;
    }

    // Ein (einzelnes) "e am Ende ist vermutlich ein Schwa (Duden hat auch
    // kein anderes Beispiel in 280), also kein Vollvokal
    if (wort.endsWith("e")) {
      return false;
    }

    if (GermanUtil.letztesZeichenIstVokal(wort)) {
      return true;
    }

    return false;
  }

  /**
   * Heuristik - ermittelt, ob dieses Wort mit or endet und versucht zu ermitteln, ob es sich dabei
   * wohl um eine lateinische Endung handelt
   */
  public static boolean endetAufLateinischemOr(final String wortform) {
    if (!wortform.endsWith("or")) {
      return false;
    }

    if (StringUtil.endsWith(wortform, "cor", "hor", "oor", "qor", "ßor", "wor", "zor")) {
      return false;
    }

    return true;
  }

  /**
   * @return <code>null</code>, falls sich diese Methode nicht entscheiden kann
   */
  public static Boolean endetUnbetontMitSilbenreimEELEr(final String wortform) {
    // Silbenreim: Alles was in der (letzten) Silbe nach dem Anfangsrand
    // (Onset) steht, -> Duden 26
    // Längere Silben ausschließen
    if (StringUtil.endsWith(wortform, "ce", // Interface
        "ee", "eel", "schmoel", // einsilbiger Ortsname
        "eer")) {
      return Boolean.FALSE;
    }

    if (StringUtil.endsWith(wortform, "ie", "oe", "iel", "ier")) {
      return null; // unsicher
    }

    if (StringUtil.endsWith(wortform, "e", "el", "er")) {
      // Ist wohl dann automatisch unbetont
      return true;
    }

    return false;
  }

  /**
   * Dies ist eine Heuristik - die im Zweifel eher mal sagt: "Nein, endet NICHT so"!
   */
  public static boolean endetUnbetontMitSilbenreimElEnEndEmEr(final String wortform) {
    // Silbenreim: Alles was in der (letzten) Silbe nach dem Anfangsrand
    // (Onset) steht, -> Duden 26
    // Längere Silben ausschließen
    if (StringUtil.endsWith(wortform, "eel",
        // iel, ien, iend, iem, ier sind alle unsicher
        "schmoel", // einsilbiger Ortsname
        "een", "eend", "eem", "eer")) {
      return false;
    }

    if (StringUtil.endsWith(wortform, "el", "en", "end", "em", "er")) {
      // Ist wohl ann automatisch unbetont
      return true;
    }

    return false;
  }

  /**
   * @return ob der Stamm (eines Adjektivs - vielleicht auch eines Pronomens) gemäß Duden 494 eine
   *         e-Tilgung in den <i>Suffixen</i> <i>en</i> und <i>em</i> erlaubt (wie in <i>dunkelm</i>
   *         - statt *dunkelem).
   *         <p>
   *         Dieser Fall ist nicht zu verwechsln mit eine e-Tilgung im Stamm wie in <i>dunklem</i>!
   *         <p>
   *         Der Stamm endet <i>nicht</i> auf <i>e</i>! (Also <i>müd</i>, nicht <i>müde</i>.)
   */
  protected static boolean erlaubtAdjektivischeETilgungBeiSuffixEnUndEm(final String stamm) {
    // Duden 494: Bei Adjektiven auf UNBETONTES -el und -er (z.B.
    // finster) kann das Suffix -en zu -n, und das Suffix -em zu m
    // gekürzt werden.

    // Beachte: Bei Adjektiven auf -en werden die Flexionssuffixe -en und
    // -em
    // NIE verkürzt. (ebenes Gelände oder ebnes Gelände, aber nicht *ebens
    // Gelände)

    // analoge *betonte* Endungen ausschließen
    if (StringUtil.endsWith(stamm, "eel", "eer", "iel", "ier")) {
      return false;
    }

    if (stamm.length() <= 2) {
      return false;
    }

    if (StringUtil.endsWith(stamm, "el", "er")) {
      // dunkel, finster

      return true; // dunkeln / finstern ist erlaubt
    }

    return false;
  }

  public static String ggfUmlauten(final String wortform, final FremdwortTyp fremdwortTyp) {
    if (fremdwortTyp.isFremdwort()) {
      return wortform; // Fremdwörter werden im Regelfall nicht umgelautet
    }

    for (int i = wortform.length() - 1; i >= 0; i--) {
      final char vokal = wortform.charAt(i);
      if (!isVokal(vokal)) {
        continue;
      }

      // Vokal gefunden
      // auf Diphtong prüfen
      if (i > 0) {
        final char vokalDavor = wortform.charAt(i - 1);
        if (isVokal(vokalDavor)) {
          // zwei Vokale in Folge - umlautbarer Diphtong "au"?
          if (vokalDavor == 'a' && vokal == 'u') {
            // ja, Diphton "au" -> umlauten
            return StringUtil.replaceRegion(wortform, i - 1, "äu");
          }
          if (vokalDavor == 'A' && vokal == 'u') {
            // ja, Diphton "Au" -> umlauten
            return StringUtil.replaceRegion(wortform, i - 1, "Äu");
          }
          // kein umlautbarer Diphtong! Vielleicht aber der Diphtong
          // "eu"?
          if ((vokalDavor == 'e' && vokal == 'u') || (vokalDavor == 'E' && vokal == 'u')) {
            // ja, Diphton "eu", nicht umlauten
            return wortform;
          }
          // (ei und ui muss man hier nur prüfen, da i ohnehin nicht
          // umgelautet werden kann)
        }
      }
      // kein (relevanter Diphtong, also nur den letzten Vokal betrachten

      if (vokal == 'a') {
        return StringUtil.replaceRegion(wortform, i, "ä");
      }
      if (vokal == 'A') {
        return StringUtil.replaceRegion(wortform, i, "Ä");
      }
      if (vokal == 'o') {
        return StringUtil.replaceRegion(wortform, i, "ö");
      }
      if (vokal == 'O') {
        return StringUtil.replaceRegion(wortform, i, "Ö");
      }
      if (vokal == 'u') {
        return StringUtil.replaceRegion(wortform, i, "ü");
      }
      if (vokal == 'U') {
        return StringUtil.replaceRegion(wortform, i, "Ü");
      }

      // kein umlautfbarer Vokal (z.B. e, i, y oder ä, ö, ü)
      // nicht umlauten
      return wortform;
    }

    return wortform; // gar keinen Vokal im "Wort" gefunden
  }

  public final static KasusInfo guessGenitivInfoPl(final String genitivNomenPl,
      final boolean auchSEndenBerücksichtigen) {
    if (genitivNomenPl.endsWith("er")) {
      // Wir gedenken rosa Kinder.
      return KasusInfo.GEN_R;
    }

    if (auchSEndenBerücksichtigen) {
      if (endetAufSLaut(genitivNomenPl)) {
        // ?Wir gedenken rosa Uhus.
        return KasusInfo.GEN_S;
      }
    }

    return KasusInfo.GEN_OHNE_S_UND_R;
  }

  /**
   * Dies ist etwas heuristisch. Fälle wie <i>Interface</i> werden nicht erkannt.
   *
   * @param ob das Wort - gesprochen! - mit einem Konsonant ausgeht
   */
  public static boolean hatKonsonantischenWortausgang(final String word) {
    if (StringUtil.endsWith(word, TYPISCHE_VERSCHRIFTUNGEN_LANGER_VOKALE)) {
      return false;
    }

    if (StringUtil.endsWith(word, DIPHTHONGE)) {
      return false;
    }

    if (GermanUtil.letztesZeichenIstVokal(word)) {
      return false;
    }

    return true;
  }

  /**
   * Heuristik - versucht zu ermitteln, ob dies ein lateinisches Wort der a-Deklination sein könnte.
   */
  public static boolean isLateinADeklination(final String wortform) {
    if (!wortform.endsWith("a")) {
      return false;
    }

    if (StringUtil.endsWith(wortform, "aa", "ca", "ha", "ja", "enda", // Hazienda
                                                                      // ->
                                                                      // Haziendas
        "qa", "ßa", "wa", "za")) {
      return false;
    }

    return true;
  }

  /**
   * @param fremdwortAmEndeBetont ob das Wort ein am Ende betontes Fremdwort ist
   */
  public static boolean istFremdwortUndEndetAufBetontenVollvokal(final String word,
      final boolean fremdwortAmEndeBetont) {
    if (!fremdwortAmEndeBetont) {
      return false;
    }

    if (StringUtil.endsWith(word, TYPISCHE_VERSCHRIFTUNGEN_LANGER_VOKALE)) {
      return true; // ja, endet auch auf Vollvokal
    }

    if (StringUtil.endsWith(word, DIPHTHONGE)) {
      return true; // ja, endet auch auf Vollvokal
    }

    if (GermanUtil.letztesZeichenIstVokal(word)) {
      // OK, d.h. es endet auf Vokal und ist außerdem Fremdwort und am
      // Ende
      // betont.
      // ein "betontes Schwa" wird's wohl in Fremdwörtern nicht geben!
      // War doch nicht schwer!
      return true;
    }

    // Endet nicht auf Vollvokal
    return false;
  }

  protected static boolean isVokal(final char character) {
    if (VOKAL_ZEICHEN.indexOf(character) >= 0) {
      return true;
    }

    return false;
  }

  private static boolean letztesZeichenIstVokal(final String string) {
    if (string.length() < 1) {
      return false;
    }

    return isVokal(string.charAt(string.length() - 1));
  }

  private GermanUtil() {
    super();
  }

  /*
   * public final static boolean enthaeltWortform( final Collection<? extends IWordForm> wordforms,
   * final String wortform) { for (final IWordForm element : wordforms) { if
   * (element.getString().equals(wortform)) { return true; } }
   *
   * return false; }
   */
}
