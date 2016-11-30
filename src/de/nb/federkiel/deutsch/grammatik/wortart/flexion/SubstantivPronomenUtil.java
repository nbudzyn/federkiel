package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableMap;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.lexikon.GermanLexemeType;
import de.nb.federkiel.feature.FeatureStructure;
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
	private static final String ZUVOR_EIN_ODER_KEIN_AUCH_UNFLEKTIERT_KEY = "zuvorEinOderKeinAuchUnflektiert";

	public final static String GENUS_KEY = "genus";
  public final static String GENUS_FEM = "f";
  public final static String GENUS_MASK = "m";
  public final static String GENUS_NEUT = "n";

	private final static String ZEITRAUMNAME_KEY = "zeitraumname";
	public final static String ZEITRAUMNAME_KEINER = "keiner";

	private final static String ABKUERZUNG_KEY = "abkuerzung";

	/**
	 * Ob dieses Lexeme als Genitivattribut verwendet werden kann (z.B. Petras
	 * Stuhl, aber nicht *ihrer Stuhl)
	 */
	public static final String GENITIVATTRIBUTFAEHIG_KEY = "genitivattributfaehig";

	/**
	 * Ob dieses Lexeme ein NN ist, das wie ein Eigenname gebraucht wird (z.B.
	 * <i>Mutter</i>, vgl. <i>Mutters Küche...</i>)
	 */
	public static final String NN_WIE_EIN_EIGENNAME_GEBRAUCHT_KEY = "nnWieEinEigennameGebraucht";

	/**
	 * Ob dieses Lexeme ein substantiviertes Adjektiv ist - z.B. <i>(das)
	 * Schöne</i>.
	 */
	public static final String SUBSTANTIVIERTES_ADJEKTIV_KEY = "substantiviertesAdjektiv";

	/**
	 * Angabe zur Zaehlbarkeit (zaehlbar, nicht zaehlbar, Plurale Tantum)
	 */
	public static final String ZAEHLBARKEIT_KEY = "zaehlbarkeit";

	/**
	 * Ob es sich um eine Adelspräposition handelt (<i>von</i> o.Ä.)
	 */
	public final static String ADELSPRAEPOSITION = "adelspraeposition";

	private SubstantivPronomenUtil() {
		super();
	}

	/**
	 * Erzeugt ein neues NN (typical features)
	 */
	public static Lexeme createNN(final String nennform,
      final boolean substantiviertesAdjektiv, final Genus genus,
			final NumerabilitaetsInfo zaehlbarkeit) {
		return createNN(nennform, genus, substantiviertesAdjektiv, false,
				zaehlbarkeit, ZEITRAUMNAME_KEINER, false);
	}

	/**
	 * Erzeugt ein neues NN
	 */
  public static Lexeme createNN(final String nennform, final Genus genus,
			final boolean substantiviertesAdjektiv,
			final boolean nnWieEinEigennameGebraucht,
			final NumerabilitaetsInfo zaehlbarkeit, final String zeitraumname,
			final boolean abkuerzung) {
		final FeatureStructure features = FeatureStructure
				.fromStringValues(ImmutableMap
						.<String, String> builder()
            .put(GENUS_KEY, FeatureStringConverter.toFeatureString(genus))
						.put(ZEITRAUMNAME_KEY, zeitraumname)
						.put(ABKUERZUNG_KEY,
								StringFeatureLogicUtil
										.booleanToString(abkuerzung))
						.put(GENITIVATTRIBUTFAEHIG_KEY, "j")
						.put(SUBSTANTIVIERTES_ADJEKTIV_KEY,
								StringFeatureLogicUtil
										.booleanToString(substantiviertesAdjektiv))
						.put(NN_WIE_EIN_EIGENNAME_GEBRAUCHT_KEY,
								StringFeatureLogicUtil
										.booleanToString(nnWieEinEigennameGebraucht))
						.put(ZAEHLBARKEIT_KEY, zaehlbarkeit.getString())
						.build());
		return new Lexeme(GermanLexemeType.NORMALES_NOMEN, nennform, features);
	}

	/**
	 * Erzeugt ein neues NE (typical features)
	 *
	 * @param genus
	 *            <code>null</code> erlaubt bei Pluralia Tantum
	 */
	public static Lexeme createNE(final String nennform,
      final boolean substantiviertesAdjektiv, final @Nullable Genus genus,
			final NumerabilitaetsInfo numerabilitaetsInfo,
			final boolean adelspraeposition) {
		final FeatureStructure features = FeatureStructure
				.fromStringValues(getDefaultSubstantivFSBuilder()
            .put(GENUS_KEY, FeatureStringConverter.toFeatureString(genus))
						.put(SUBSTANTIVIERTES_ADJEKTIV_KEY,
								substantiviertesAdjektiv ? "j" : "n")
						.put(ZAEHLBARKEIT_KEY, numerabilitaetsInfo.getString())
						.put(ADELSPRAEPOSITION, adelspraeposition ? "j" : "n")
						.build());
		return new Lexeme(GermanLexemeType.EIGENNAME, nennform, features);
	}

	/**
	 * Erzeugt ein Possessivpronomen
	 *
	 * @param stammHoeflichkeitsform
	 *            ob es sich um die Höflichkeitsform ("Sie", "Ihrer", "Ihnen",
	 *            "Sie") handelt
	 */
	public static Lexeme createPossessivpronomen(final GermanLexemeType type,
			final String stammPerson, final String stammNumerus,
			final boolean stammHoeflichkeitsform, final String stammGenus,
			final String nennform) {
		return new Lexeme(type, nennform,
				FeatureStructure.fromStringValues(ImmutableMap.of(
						"stammPerson", stammPerson, "stammNumerus",
						stammNumerus, "stammHoeflichkeitsform",
						StringFeatureLogicUtil
								.booleanToString(stammHoeflichkeitsform),
						"stammGenus", stammGenus)));
	}

	/**
	 * Erzeugt ein typisches, flektierbares Indefinitpronomen
	 */
	public static Lexeme createIndefinitpronomen(final String pos,
			final String nennform) {
		return createIndefinitpronomen(nennform, true, false); // zuvor kein
		// unflektiertes
		// ein / kein
		// möglich
	}

	/**
	 * Erzeugt ein typisches Indefinitpronomen
	 *
	 * @param lexemFlektierbar
	 *            ob dieses Indefinitpronomen flektierbar ist
	 * @param zuvorEinOderKeinAuchUnflektiert
	 *            ob zuvor ein ein oder kein o.Ä. vorgesehen ist, das auch
	 *            unflektiert sein kann (wie bei <i>mit ein bisschen
	 *            Nachdenken</i>, Duden 414).
	 */
	public static Lexeme createIndefinitpronomen(final String nennform,
			final boolean lexemFlektierbar,
			final boolean zuvorEinOderKeinAuchUnflektiert) {
		return new Lexeme(GermanLexemeType.INDEFINITPRONOMEN, nennform,
				FeatureStructure.fromStringValues(ImmutableMap.of(
						ZUVOR_EIN_ODER_KEIN_AUCH_UNFLEKTIERT_KEY,
						zuvorEinOderKeinAuchUnflektiert ? "j" : "n",
						GermanUtil.LEXEM_FLEKTIERBAR_KEY,
						lexemFlektierbar ? "j" : "n")));
	}

	/**
	 * Erzeugt ein typisches Pronomen
	 */
	public static Lexeme createPronoun(final GermanLexemeType type,
			final String nennform) {
		return new Lexeme(type, nennform, FeatureStructure.EMPTY);
	}

	/*
	 * Versucht in heuristischer Weise zu ermitteln, ob es sich um einen -s-,
	 * einen -r- oder einen anderen Genitiv handelt - für eine Singularform.
	 *
	 * public final static KasusInfo guessGenitivInfoNomenSg( final String
	 * genitivNomenSg) { if (GermanUtil.endetAufSLaut(genitivNomenSg)) { return
	 * KasusInfo.GEN_S; }
	 *
	 * // -r kommt im Genitiv Sg nicht vor return KasusInfo.GEN_OHNE_S_UND_R; }
	 */

	/**
	 * Versucht in heuristischer Weise zu ermitteln, ob es sich um einen -s-,
	 * einen -r- oder einen anderen Genitiv handelt - für Nomen.
	 */
	public final static KasusInfo guessGenitivNomenInfoPl(
			final String genitivNomenPl) {
		return GermanUtil.guessGenitivInfoPl(genitivNomenPl, true);
	}

	/**
	 * @return a feature structure builder, suited for common nouns:
	 *         <ul>
	 *         <li>kein zeitraumname
	 *         <li>keine abkürzung
	 *         </ul>
	 *         (This configuration in the builder can be overridden!)
	 */
	private static ImmutableMap.Builder<String, String> getDefaultSubstantivFSBuilder() {
		final ImmutableMap.Builder<String, String> res = ImmutableMap
				.<String, String> builder();
		res.put(ZEITRAUMNAME_KEY, ZEITRAUMNAME_KEINER);
		res.put(ABKUERZUNG_KEY, "n");
		res.put(GENITIVATTRIBUTFAEHIG_KEY, "j");
		return res;
	}

	/**
	 * @return <code>true</code>, if the genus is masculine, or unspecified
	 */
	public static boolean isMask(final Lexeme lexeme) {
		return hasStringFeatureThisValueOrIsUnspecified(lexeme, GENUS_KEY,
				GENUS_MASK);
	}

	/**
	 * @return <code>true</code>, if the genus is feminene, or unspecified
	 */
	public static boolean isFem(final Lexeme lexeme) {
		return hasStringFeatureThisValueOrIsUnspecified(lexeme, GENUS_KEY,
				GENUS_FEM);
	}

	/**
	 * @return <code>true</code>, if the genus is neuter, or unspecified
	 */
	public static boolean isNeut(final Lexeme lexeme) {
		return hasStringFeatureThisValueOrIsUnspecified(lexeme, GENUS_KEY,
				GENUS_NEUT);
	}

	/**
	 * @return <code>true</code>, wenn das lexeme<code>lexeme</code> ein Plurale
	 *         Tantum ist
	 */
	public static boolean isPluraleTantum(final Lexeme lexeme) {
		final String zaehlbarkeitString = lexeme
				.getStringFeatureValue(ZAEHLBARKEIT_KEY);
		return zaehlbarkeitString.equals(NumerabilitaetsInfo.PLURALE_TANTUM
				.getString());
	}

	/**
	 * @param lexeme
	 *            darf KEINE Plurale Tantum sein
	 *
	 * @return <code>true</code>, wenn das lexem zaehlbar ist,
	 *         <code>false</code> , wenn nicht. Pluralia Tantum erzeugen einen
	 *         Fehler.
	 */
	public static boolean isZaehlbar(final Lexeme lexeme) {
		final String zaehlbarkeitString = lexeme
				.getStringFeatureValue(ZAEHLBARKEIT_KEY);
		if (zaehlbarkeitString.equals(NumerabilitaetsInfo.ZAEHLBAR.getString())) {
			return true;
		}

		if (zaehlbarkeitString.equals(NumerabilitaetsInfo.NICHT_ZAEHLBAR
				.getString())) {
			return false;
		}

		throw new IllegalStateException("Unexpected value for key "
				+ ZAEHLBARKEIT_KEY + ": " + zaehlbarkeitString);
	}

	private static boolean hasStringFeatureThisValueOrIsUnspecified(
			final Lexeme lexeme, final String featureName,
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
