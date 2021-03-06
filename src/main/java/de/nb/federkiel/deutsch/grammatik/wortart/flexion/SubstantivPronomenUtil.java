package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.ABKUERZUNG_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.ADELSPRAEPOSITION;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.GEEIGNET_ALS_ADV_AKK_ODER_GEN_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.GENITIVATTRIBUTFAEHIG_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.GENUS_FEM;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.GENUS_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.GENUS_MASK;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.GENUS_NEUT;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.NN_WIE_EIN_EIGENNAME_GEBRAUCHT_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAMM_GENUS_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAMM_HOEFLICHKEITSFORM_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAMM_NUMERUS_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAMM_PERSON_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.SUBSTANTIVIERTES_ADJEKTIV_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.ZEITRAUMNAME_KEINER;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.ZEITRAUMNAME_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.ZUVOR_EIN_ODER_KEIN_AUCH_UNFLEKTIERT_KEY;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableMap;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.lexikon.GermanLexemeType;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.LexiconFeatureStructureUtil;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.lexikon.Lexeme;

/**
 * Utilities for (creating) nouns (Substantive) and pronouns.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public final class SubstantivPronomenUtil {
	private SubstantivPronomenUtil() {
		super();
	}

	/**
	 * Erzeugt ein neues NN (typical features)
	 */
	public static Lexeme createNN(final String nennform, final boolean substantiviertesAdjektiv, final Genus genus) {
		return createNN(nennform, genus, substantiviertesAdjektiv, false, ZEITRAUMNAME_KEINER, false);
	}

	/**
	 * Erzeugt ein neues NN
	 */
	public static Lexeme createNN(final String nennform, @Nullable final Genus genus,
			final boolean substantiviertesAdjektiv, final boolean nnWieEinEigennameGebraucht, final String zeitraumname,
			final boolean abkuerzung) {
		// Das Konzept ist dies:
		// Alles was F�R DIE GRAMMATIK RELEVANT IST, wird als Merkmale im Lexem (oder -
		// bei Abweichungen
		// - in der
		// Wortform) gespeichert. Alles was nur f�r die Bildung der Wortformen (oder die
		// Ermittlung
		// dieser Merkmale)
		// relevant ist, wird nur als Parameter hin- und hergereicht.

		// @formatter:off
    final FeatureStructure features =
    		LexiconFeatureStructureUtil.fromStringValues(ImmutableMap.<String, String>builder()
            .put(GENUS_KEY, FeatureStringConverter.toFeatureString(genus))
            .put(ZEITRAUMNAME_KEY, zeitraumname)
            .put(ABKUERZUNG_KEY, StringFeatureLogicUtil.booleanToString(abkuerzung))
            .put(GENITIVATTRIBUTFAEHIG_KEY, StringFeatureLogicUtil.TRUE)
            .put(SUBSTANTIVIERTES_ADJEKTIV_KEY, StringFeatureLogicUtil.booleanToString(substantiviertesAdjektiv))
            .put(NN_WIE_EIN_EIGENNAME_GEBRAUCHT_KEY, StringFeatureLogicUtil.booleanToString(nnWieEinEigennameGebraucht))
            .build());
    // @formatter:on
		return new Lexeme(GermanLexemeType.NORMALES_NOMEN, nennform, features);
	}

	/**
	 * Erzeugt ein neues NE (typical features)
	 *
	 * @param genus
	 *          <code>null</code> erlaubt bei Pluralia Tantum
	 */
	public static Lexeme createNE(final String nennform, final boolean substantiviertesAdjektiv,
			final @Nullable Genus genus, final boolean adelspraeposition) {
		// Das Konzept ist dies:
		// Alles was F�R DIE GRAMMATIK RELEVANT IST, wird als Merkmale im Lexem (oder -
		// bei Abweichungen
		// - in der
		// Wortform) gespeichert. Alles was nur f�r die Bildung der Wortformen (oder die
		// Ermittlung
		// dieser Merkmale)
		// relevant ist, wird nur als Parameter hin- und hergereicht.

		// @formatter:off
    final FeatureStructure features =
    		LexiconFeatureStructureUtil.fromStringValues(getDefaultSubstantivFSBuilder()
            .put(GENUS_KEY,FeatureStringConverter.toFeatureString(genus))
            .put(SUBSTANTIVIERTES_ADJEKTIV_KEY, StringFeatureLogicUtil.booleanToString(substantiviertesAdjektiv))
            .put(ADELSPRAEPOSITION, StringFeatureLogicUtil.booleanToString(adelspraeposition))
            .build());
    // @formatter:on
		return new Lexeme(GermanLexemeType.EIGENNAME, nennform, features);
	}

	/**
	 * Erzeugt ein Possessivpronomen
	 *
	 * @param stammHoeflichkeitsform
	 *          ob es sich um die H�flichkeitsform ("Sie", "Ihrer", "Ihnen", "Sie")
	 *          handelt
	 */
	public static Lexeme createPossessivpronomen(final GermanLexemeType type, final String stammPerson,
			final String stammNumerus, final boolean stammHoeflichkeitsform, final String stammGenus, final String nennform) {
		// @formatter:off
    return new Lexeme(type, nennform,
    		LexiconFeatureStructureUtil.fromStringValues(ImmutableMap.of(
            STAMM_PERSON_KEY, stammPerson,
            STAMM_NUMERUS_KEY, stammNumerus,
            STAMM_HOEFLICHKEITSFORM_KEY, StringFeatureLogicUtil.booleanToString(stammHoeflichkeitsform),
            STAMM_GENUS_KEY, stammGenus)));
    // @formatter:on
	}

	/**
	 * Erzeugt ein typisches, flektierbares Indefinitpronomen
	 */
	public static Lexeme createIndefinitpronomen(final String pos, final String nennform) {
		return createIndefinitpronomen(nennform, true, false); // zuvor kein
		// unflektiertes
		// ein / kein
		// m�glich
	}

	/**
	 * Erzeugt ein typisches Indefinitpronomen
	 *
	 * @param lexemFlektierbar
	 *          ob dieses Indefinitpronomen flektierbar ist
	 * @param zuvorEinOderKeinAuchUnflektiert
	 *          ob zuvor ein ein oder kein o.�. vorgesehen ist, das auch unflektiert
	 *          sein kann (wie bei <i>mit ein bisschen Nachdenken</i>, Duden 414).
	 */
	public static Lexeme createIndefinitpronomen(final String nennform, final boolean lexemFlektierbar,
			final boolean zuvorEinOderKeinAuchUnflektiert) {
		// @formatter:off
    return new Lexeme(GermanLexemeType.INDEFINITPRONOMEN, nennform,
    		LexiconFeatureStructureUtil.fromStringValues(ImmutableMap.of(
            ZUVOR_EIN_ODER_KEIN_AUCH_UNFLEKTIERT_KEY,
            StringFeatureLogicUtil.booleanToString(zuvorEinOderKeinAuchUnflektiert),
            GermanUtil.LEXEM_FLEKTIERBAR_KEY,
            StringFeatureLogicUtil.booleanToString(lexemFlektierbar))));
    // @formatter:on
	}

	public static Lexeme createDemonstrativpronomen(final String nennform, final boolean geeignetAlsAdvAkkOderGen) {
		return new Lexeme(GermanLexemeType.DEMONSTRATIVPRONOMEN, nennform,
				LexiconFeatureStructureUtil.fromStringValues(ImmutableMap.of(GEEIGNET_ALS_ADV_AKK_ODER_GEN_KEY,
						StringFeatureLogicUtil.booleanToString(geeignetAlsAdvAkkOderGen))));
	}

	/**
	 * Erzeugt ein typisches Pronomen
	 */
	public static Lexeme createPronoun(final GermanLexemeType type, final String nennform) {
		return new Lexeme(type, nennform, LexiconFeatureStructureUtil.EMPTY_FEATURE_STRUCTURE);
	}

	/*
	 * Versucht in heuristischer Weise zu ermitteln, ob es sich um einen -s-, einen
	 * -r- oder einen anderen Genitiv handelt - f�r eine Singularform.
	 *
	 * public final static KasusInfo guessGenitivInfoNomenSg( final String
	 * genitivNomenSg) { if (GermanUtil.endetAufSLaut(genitivNomenSg)) { return
	 * KasusInfo.GEN_S; }
	 *
	 * // -r kommt im Genitiv Sg nicht vor return KasusInfo.GEN_OHNE_S_UND_R; }
	 */

	/**
	 * Versucht in heuristischer Weise zu ermitteln, ob es sich um einen -s-, einen
	 * -r- oder einen anderen Genitiv handelt - f�r Nomen.
	 */
	public final static KasusInfo guessGenitivNomenInfoPl(final String genitivNomenPl) {
		return GermanUtil.guessGenitivInfoPl(genitivNomenPl, true);
	}

	/**
	 * @return a feature structure builder, suited for common nouns:
	 *         <ul>
	 *         <li>kein zeitraumname
	 *         <li>keine abk�rzung
	 *         </ul>
	 *         (This configuration in the builder can be overridden!)
	 */
	private static ImmutableMap.Builder<String, String> getDefaultSubstantivFSBuilder() {
		final ImmutableMap.Builder<String, String> res = ImmutableMap.<String, String>builder();
		res.put(ZEITRAUMNAME_KEY, ZEITRAUMNAME_KEINER);
		res.put(ABKUERZUNG_KEY, StringFeatureLogicUtil.FALSE);
		res.put(GENITIVATTRIBUTFAEHIG_KEY, StringFeatureLogicUtil.TRUE);
		return res;
	}

	/**
	 * @return <code>true</code>, if the genus is masculine, or unspecified
	 */
	public static boolean isMask(final Lexeme lexeme) {
		return hasStringFeatureThisValueOrIsUnspecified(lexeme, GENUS_KEY, GENUS_MASK);
	}

	/**
	 * @return <code>true</code>, if the genus is feminene, or unspecified
	 */
	public static boolean isFem(final Lexeme lexeme) {
		return hasStringFeatureThisValueOrIsUnspecified(lexeme, GENUS_KEY, GENUS_FEM);
	}

	/**
	 * @return <code>true</code>, if the genus is neuter, or unspecified
	 */
	public static boolean isNeut(final Lexeme lexeme) {
		return hasStringFeatureThisValueOrIsUnspecified(lexeme, GENUS_KEY, GENUS_NEUT);
	}

	private static boolean hasStringFeatureThisValueOrIsUnspecified(final Lexeme lexeme, final String featureName,
			final String featureValue) {
		final String actual = lexeme.getStringFeatureValue(featureName);

		if (actual == null) {
			return true;
		}

		return actual.equals(featureValue);
	}

	/*
	 * public static String retrieveSTTSPos(final boolean eigenname) { return
	 * eigenname == true ? NERecognizer.POS : NNRecognizer.POS; }
	 */

}
