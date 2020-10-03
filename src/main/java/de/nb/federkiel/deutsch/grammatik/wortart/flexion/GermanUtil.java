package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.FeatureStringConverter.NUMERUS_FEATURE_TYPE;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.nb.federkiel.feature.EnumStringFeatureType;
import de.nb.federkiel.feature.FeatureTypeDictionary;
import de.nb.federkiel.feature.RoleFrameCollectionFeatureType;
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
	private static final String[] DIPHTHONGE = new String[] { "ai", "au", "äu", "ei", "eu", "ey", "oi", "öi", "ui" };
	private static final String[] TYPISCHE_VERSCHRIFTUNGEN_LANGER_VOKALE = new String[] { "aa", "ah", "ää", "ää", "ee",
			"eh", "ii", "ie", "ih", "ieh", "oo", "oh", "oe", // Itzehoe
			"öö", "öh", "uu", "uh", "üü", "üh", "yy", "yh" };
	private static final Logger log = LogManager.getLogger(GermanUtil.class); // NOPMD

	public static final String DEFINIT_KEY = "definit";

	public static final String LEXEM_FLEKTIERBAR_KEY = "lexemFlektierbar";
	/**
	 * für Adjektive: Frankfurter, neunziger
	 */
	public final static String ABKUERZUNG_KEY = "abkuerzung";
	public static final String ABLEITUNG_AUF_ER_KEY = "ableitungAufEr";

	/**
	 * Ob es sich um eine Adelspräposition handelt (<i>von</i> o.Ä.)
	 */
	public final static String ADELSPRAEPOSITION = "adelspraeposition";

	public static final String AKK_MUSS_DURCH_ARTIKEL_ANGEZEIGT_WERDEN_AUSSER_IM_TELEGRAMMSTIL_KEY = "akkMussDurchArtikelAngezeigtWerdenAusserImTelegrammstil";

	public static final String CARD_TYP_ZIFFER_NULL = "zifferNull";
	public static final String CARD_TYP_ZIFFER_AUSSER_NULL = "zifferAusserNull";
	public static final String CARD_TYP_DEZIMALTRENNZEICHEN = "dezimaltrennzeichen";
	public static final String CARD_TYP_TAUSENDERTRENNZEICHEN = "tausendertrennzeichen";
	public static final String CARD_TYP_ROEMISCHE_ZIFFER = "roemischeZiffer";
	public static final String CARD_TYP_SPORTERGEBNIS_TRENNER = "sportergebnisTrenner";
	public static final String CARD_TYP_AUSGESCHRIEBENE_GANZE_ZAHL = "ausgeschriebeneGanzeZahl";

	public static final String EINZELNES_REFLEXIVPRONOMEN_KEY = "einzelnesReflexivpronomen";
	public static final String ERLAUBT_NACHGESTELLTES_SCHWACH_FLEKTIERTES_ADJEKTIV_KEY = "erlaubtNachgestelltesSchwachFlektiertesAdjektiv";
	protected static final String ERLAUBT_NACHGESTELLTES_STARK_FLEKTIERTES_ADJEKTIV_KEY = "erlaubtNachgestelltesStarkFlektiertesAdjektiv";
	public static final String GEEIGNET_ALS_ADV_AKK_ODER_GEN_KEY = "geeignetAlsAdvAkkOderGen";
	public static final String GEEIGNET_ALS_PSEUDOAKTANT_ES_KEY = "geeignetAlsPseudoaktantEs";

	/**
	 * Ob dieses Lexeme als Genitivattribut verwendet werden kann (z.B. Petras
	 * Stuhl, aber nicht *ihrer Stuhl)
	 */
	public static final String GENITIVATTRIBUTFAEHIG_KEY = "genitivattributfaehig";

	public static final String GENITIV_SICHTBAR_DURCH_S_KEY = "genitivSichtbarDurchS";
	public static final String GENITIV_SICHTBAR_DURCH_R_KEY = "genitivSichtbarDurchR";

	public final static String GENUS_KEY = "genus";
	public final static String GENUS_MASK = "m";
	public final static String GENUS_FEM = "f";
	public final static String GENUS_NEUT = "n";

	public static EnumStringFeatureType GENUS_FEATURE_TYPE = new EnumStringFeatureType(GENUS_MASK, GENUS_FEM, GENUS_NEUT);

	/**
	 * Ob diese Substantiv-Wortform (NN oder NE) <i>im Normalstil als einzelnes
	 * Subjekt ohne vorangehendes Artikelwort</i> stehen kann (z.B. "Pianist" (Nom),
	 * kann im Normalstil nicht ohne vorangehendes Artikelwort stehen ("?Pianist
	 * spielt Klavier." - hingegen ist "Peter ist Pianist." erlaubt. "Pianisten"
	 * kann auch als Subjekt ohne vorangehendes Artikelwort stehen).
	 */
	public static final String IM_NORMALSTIL_ALS_SUBJEKT_OHNE_ARTIKELWORT_MOEGLICH_KEY = "imNormalstilAlsSubjektOhneArtikelwortMoeglich";

	/**
	 * Ob diese Substantiv-Wortform (NN oder NE oder TRUNC) <i>im Normalstil ohne
	 * vorangehendes Artikelwort</i> stehen kann (z.B. "Schweiz" (Nom), kann im
	 * Normalstil nicht ohne vorangehendes Artikelwort stehen ("?Wir fahren in
	 * Schweiz"). Auch "Anna" (Gen) kann nicht ohne vorangehendes Artikelwort stehen
	 * ("*Anna Hund"). "Annas" (Gen) hingegen kann ohne vorangehendes Artikelwort
	 * stehen ("Annas Hund").
	 */
	public static final String IM_NORMALSTIL_OHNE_ARTIKELWORT_MOEGLICH_KEY = "imNormalstilOhneArtikelwortMoeglich";

	/**
	 * Ob diese Substantiv-Wortform (NN oder NE oder TRUNC) <i>im Telegrammstil ohne
	 * vorangehendes Artikelwort</i> stehen kann (z.B. "Anna" (Gen) kann nicht ohne
	 * vorangehendes Artikelwort stehen ("*Anna Hund"). "Annas" (Gen) hingegen kann
	 * ohne vorangehendes Artikelwort stehen ("Annas Hund"). Auch "Schweiz" kann im
	 * Telegrammstil ohne vorangehendes Artikelwort stehen ("Raub in Schweiz
	 * aufgeklärt").
	 */
	public static final String IM_TELEGRAMMSTIL_OHNE_ARTIKELWORT_MOEGLICH_KEY = "imTelegrammstilOhneArtikelwortMoeglich";

	public static final String KONJUNKTIONALPHRASENFAEHIG_KEY = "konjunktionalphrasenfaehig";

	public static final String KOMPARATION_KEY = "komparation";
	public static final String KOMPARATION_POSITIV = "positiv";
	public static final String KOMPARATION_KOMPARATIV = "komparativ";
	public static final String KOMPARATION_SUPERLATIV = "superlativ"; // TODO Duden 500 ff

	public static final EnumStringFeatureType KOMPARATION_FEATURE_TYPE = new EnumStringFeatureType(KOMPARATION_POSITIV,
			KOMPARATION_KOMPARATIV, KOMPARATION_SUPERLATIV);

	/**
	 * Ob diese Substantiv-Wortform (NN oder NE oder TRUNC) nach einem Artikelwort
	 * stehen kann (z.B. "Anna" (Gen) kann nach einem Artikelwort stehen, nämlich in
	 * "der Anna". "Annas" (Gen) hingegen, kann nicht nach einem Artikelwort stehen
	 * ("*der Annas").
	 */
	public static final String MIT_ARTIKELWORT_MOEGLICH_KEY = "mitArtikelwortMoeglich";

	public final static String MOEGLICHERWEISE_DAT_ODER_AKK_MIT_UNTERLASSENER_KASUSFLEXION_KEY = "moeglicherweiseDatOderAkkMitUnterlassenerKasusflexion";

	public static final String IST_DAS_SUBJEKT_KEY = "istDasSubjekt";
	public static final String KOMPARATIV_HOMONYM_ZUM_POSITIV_MIT_STARKER_ENDUNG = "komparativHomonymZumPositivMitStarkerEndung";

	public static final String MODUS_KEY = "modus";
	/**
	 * Ob dieses Lexeme ein NN ist, das wie ein Eigenname gebraucht wird (z.B.
	 * <i>Mutter</i>, vgl. <i>Mutters Küche...</i>). Dies sind wohl fast nur
	 * Verwandschaftsbezeichnungenn (Duden, 397), evtl. "Apostels Gedanke" o.Ä.
	 * <p>
	 * Hierunter fallen jeden falls nicht "Januar", "Weihnachten" oder Ähnliches!
	 */
	public static final String NN_WIE_EIN_EIGENNAME_GEBRAUCHT_KEY = "nnWieEinEigennameGebraucht";

	public static final String INDIKATIV = "ind";
	public static final String KONJUNKTIV = "konj";

	public static EnumStringFeatureType MODUS_FEATURE_TYPE = new EnumStringFeatureType(INDIKATIV, KONJUNKTIV);

	public static final String TEMPUS = "tempus";
	public static final String PRAESENS = "praes";
	public static final String PRAETERITUM = "praet";

	public static final String PRESTIGE_PRAEPOSITION_KEY = "prestigePraeposition";

	/**
	 * Ob dieses Lexeme ein substantiviertes Adjektiv ist - z.B. <i>(das)
	 * Schöne</i>.
	 */
	public static final String SUBSTANTIVIERTES_ADJEKTIV_KEY = "substantiviertesAdjektiv";

	public static final EnumStringFeatureType TEMPUS_FEATURE_TYPE = new EnumStringFeatureType(PRAESENS, PRAETERITUM);

	public static final String NENNFORM_KEY = "nennform";

	public static final String OHNE_VERBINDUNG_MIT_ZEIT_MENGEN_ODER_GROESSEN_ANGABE_MGL_KEY = "ohneVerbindungMitZeitMengenOderGroessenAngabeMgl";

	public static final String GEEIGNET_ALS_FOKUSPARTIKEL_KEY = "geeignetAlsFokuspartikel";

	public static final EnumStringFeatureType PERSON_FEATURE_TYPE = new EnumStringFeatureType("1", "2", "3");

	public static final String REG_KASUS_KEY = "regKasus";

	/**
	 * Merkmal - kann den Wert stark, schwach oder unflektiert haben
	 */
	public static final String STAERKE_KEY = "staerke";
	public static final String STAERKE_STARK = "stark";
	public static final String STAERKE_SCHWACH = "schwach";
	public static final String STAERKE_UNFLEKTIERT = "unflektiert";

	public static final EnumStringFeatureType STAERKE_FEATURE_TYPE = new EnumStringFeatureType(STAERKE_STARK,
			STAERKE_SCHWACH, STAERKE_UNFLEKTIERT);

	public static final String STAMM_PERSON_KEY = "stammPerson";
	public static final String STAMM_NUMERUS_KEY = "stammNumerus";
	public static final String STAMM_GENUS_KEY = "stammGenus";
	public static final String STAMM_HOEFLICHKEITSFORM_KEY = "stammHoeflichkeitsform";

	public final static String ZEITRAUMNAME_KEY = "zeitraumname";

	public final static String ZEITRAUMNAME_KEINER = "keiner";
	public final static String ZEITRAUMNAME_WOCHENTAG = "wochentag";
	public final static String ZEITRAUMNAME_MONAT = "monat";

	public static final EnumStringFeatureType ZEITRAUMNAME_FEATURE_TYPE = new EnumStringFeatureType(ZEITRAUMNAME_KEINER,
			ZEITRAUMNAME_WOCHENTAG, ZEITRAUMNAME_MONAT);

	public static final String ZUVOR_EIN_ODER_KEIN_AUCH_UNFLEKTIERT_KEY = "zuvorEinOderKeinAuchUnflektiert";

	public static final FeatureTypeDictionary FEATURE_TYPE_DICTIONARY = buildGermanFeatureTypeDictionary();

	private static FeatureTypeDictionary buildGermanFeatureTypeDictionary() {
		final FeatureTypeDictionary res = new FeatureTypeDictionary();
		res.put(ABKUERZUNG_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(ABLEITUNG_AUF_ER_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(ADELSPRAEPOSITION, EnumStringFeatureType.BOOLEAN);
		res.put(AKK_MUSS_DURCH_ARTIKEL_ANGEZEIGT_WERDEN_AUSSER_IM_TELEGRAMMSTIL_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(DEFINIT_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(ERLAUBT_NACHGESTELLTES_SCHWACH_FLEKTIERTES_ADJEKTIV_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(ERLAUBT_NACHGESTELLTES_STARK_FLEKTIERTES_ADJEKTIV_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(GENITIVATTRIBUTFAEHIG_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(GEEIGNET_ALS_ADV_AKK_ODER_GEN_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(GEEIGNET_ALS_FOKUSPARTIKEL_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(GENITIV_SICHTBAR_DURCH_R_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(GENITIV_SICHTBAR_DURCH_S_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(GENUS_KEY, GENUS_FEATURE_TYPE);
		res.put(IM_NORMALSTIL_ALS_SUBJEKT_OHNE_ARTIKELWORT_MOEGLICH_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(IM_NORMALSTIL_OHNE_ARTIKELWORT_MOEGLICH_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(IM_TELEGRAMMSTIL_OHNE_ARTIKELWORT_MOEGLICH_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(IST_SATZANFANG_KEY, EnumStringFeatureType.BOOLEAN);
		res.put("kasus", FeatureStringConverter.KASUS_FEATURE_TYPE);
		res.put(KOMPARATION_KEY, KOMPARATION_FEATURE_TYPE);
		res.put(KOMPARATIV_HOMONYM_ZUM_POSITIV_MIT_STARKER_ENDUNG, EnumStringFeatureType.BOOLEAN);
		res.put(KONJUNKTIONALPHRASENFAEHIG_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(LEXEM_FLEKTIERBAR_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(MIT_ARTIKELWORT_MOEGLICH_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(MODUS_KEY, MODUS_FEATURE_TYPE);
		res.put(MOEGLICHERWEISE_DAT_ODER_AKK_MIT_UNTERLASSENER_KASUSFLEXION_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(NN_WIE_EIN_EIGENNAME_GEBRAUCHT_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(NUMERUS_KEY, NUMERUS_FEATURE_TYPE);
		res.put(OHNE_VERBINDUNG_MIT_ZEIT_MENGEN_ODER_GROESSEN_ANGABE_MGL_KEY, EnumStringFeatureType.BOOLEAN);
		res.put("person", PERSON_FEATURE_TYPE);
		res.put(PRESTIGE_PRAEPOSITION_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(REG_KASUS_KEY, FeatureStringConverter.KASUS_FEATURE_TYPE);
		res.put(ROLE_FRAME_COLLECTION_NAME_VERB, RoleFrameCollectionFeatureType.INSTANCE);
		res.put(STAERKE_KEY, STAERKE_FEATURE_TYPE);
		res.put(STAMM_PERSON_KEY, GermanUtil.PERSON_FEATURE_TYPE);
		res.put(STAMM_NUMERUS_KEY, FeatureStringConverter.NUMERUS_FEATURE_TYPE);
		res.put(STAMM_HOEFLICHKEITSFORM_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(STAMM_GENUS_KEY, GENUS_FEATURE_TYPE);
		res.put(SUBSTANTIVIERTES_ADJEKTIV_KEY, EnumStringFeatureType.BOOLEAN);
		res.put(TEMPUS, TEMPUS_FEATURE_TYPE);
		// unter "typ" fassen wir im Moment alles Mögliche!
		res.put(ZEITRAUMNAME_KEY, ZEITRAUMNAME_FEATURE_TYPE);
		res.put(ZUVOR_EIN_ODER_KEIN_AUCH_UNFLEKTIERT_KEY, EnumStringFeatureType.BOOLEAN);

		return res;
	}

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
	 * <li>bedauern -> bedaur (ich bedaure) (Duden 620 fordert für eine e-Tilgung
	 * bei Verben, dass -er auf einen Vokal folgt. Nach meinem Sprachgefühl ist
	 * rudern -> rudr (ich rudre) ebenfalls möglich.)
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
	 * Heuristik - ermittelt, ob dieses Wort mit or endet und versucht zu ermitteln,
	 * ob es sich dabei wohl um eine lateinische Endung handelt
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
	 * Dies ist eine Heuristik - die im Zweifel eher mal sagt: "Nein, endet NICHT
	 * so"!
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
	 * @return ob der Stamm (eines Adjektivs - vielleicht auch eines Pronomens)
	 *         gemäß Duden 494 eine e-Tilgung in den <i>Suffixen</i> <i>en</i> und
	 *         <i>em</i> erlaubt (wie in <i>dunkelm</i> - statt *dunkelem).
	 *         <p>
	 *         Dieser Fall ist nicht zu verwechsln mit eine e-Tilgung im Stamm wie
	 *         in <i>dunklem</i>!
	 *         <p>
	 *         Der Stamm endet <i>nicht</i> auf <i>e</i>! (Also <i>müd</i>, nicht
	 *         <i>müde</i>.)
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
					if (vokalDavor == 'e' && vokal == 'u' || vokalDavor == 'E' && vokal == 'u') {
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
	 * @param ob
	 *          das Wort - gesprochen! - mit einem Konsonant ausgeht
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
	 * Heuristik - versucht zu ermitteln, ob dies ein lateinisches Wort der
	 * a-Deklination sein könnte.
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
	 * @param fremdwortAmEndeBetont
	 *          ob das Wort ein am Ende betontes Fremdwort ist
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

	public static final String IST_SATZANFANG_KEY = "istSatzanfang";
	public static final String ROLE_FRAME_COLLECTION_NAME_VERB = "verb";
	public static final String NUMERUS_KEY = "numerus";

	private GermanUtil() {
		super();
	}

	/*
	 * public final static boolean enthaeltWortform( final Collection<? extends
	 * IWordForm> wordforms, final String wortform) { for (final IWordForm element :
	 * wordforms) { if (element.getString().equals(wortform)) { return true; } }
	 *
	 * return false; }
	 */
}
