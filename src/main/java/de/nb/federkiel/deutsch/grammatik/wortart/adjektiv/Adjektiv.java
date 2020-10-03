/**
 * Ein Adjektiv.
 *
 * @author Nikolaj Budzyn
 */

package de.nb.federkiel.deutsch.grammatik.wortart.adjektiv;

import static de.nb.federkiel.deutsch.grammatik.phrase.Flexionstyp.SCHWACHE_FLEXION;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.ABLEITUNG_AUF_ER_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.LEXEM_FLEKTIERBAR_KEY;
import static de.nb.federkiel.deutsch.lexikon.GermanLexemeType.ADJEKTIV;
import static de.nb.federkiel.feature.StringFeatureLogicUtil.booleanToString;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.phrase.Flexionstyp;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.AdjektivFlektierer;
import de.nb.federkiel.deutsch.grammatik.wortart.substantiv.Substantiv;
import de.nb.federkiel.feature.LexiconFeatureStructureUtil;
import de.nb.federkiel.lexikon.Lexeme;
import de.nb.federkiel.string.StringUtil;

public class Adjektiv {
	/**
	 * (ein) hoh(es) Regal
	 */
	private final String stamm;

	private final AdjektivFlexionsklasse flexionsklasse;

	/**
	 * Die Substantivierung des Adjektivs. Bei <code>null</code> wird die
	 * Substantivierung geraten.
	 */
	private final @Nullable Substantiv substantivierung;

	private final Lexeme lexeme;

	private final AdjektivFlektierer flekt;

	/**
	 * Erzeugt ein Adjektiv.
	 * <p>
	 * Die fehlenden Angaben werden durch das System geraten.
	 */
	public Adjektiv(final String stamm) {
		this(stamm, stamm);
	}

	/**
	 * Erzeugt ein Adjektiv.
	 * <p>
	 * Die fehlenden Angaben werden durch das System geraten.
	 */
	public Adjektiv(final String stamm, final AdjektivFlexionsklasse flexionsklasse) {
		this(stamm, stamm, flexionsklasse, null);
	}

	/**
	 * Erzeugt ein Adjektiv.
	 * <p>
	 * Stamm und unflektierte Form kˆnnen in Einzelf‰llen voneinander abweichen,
	 * etwa <i>hoh</i> vs. <i>hoch</i>, <i>ander</i> vs. <i>anders</i>.
	 * <p>
	 * Die fehlenden Angaben werden durch das System geraten.
	 */
	public Adjektiv(final String stamm, final String unflektiert) {
		this(stamm, unflektiert, rateFlexionsklasse(stamm, unflektiert), null);
	}

	private static AdjektivFlexionsklasse rateFlexionsklasse(final String stamm, final String unflektiert) {
		if (!stamm.equals(unflektiert)) {
			// hoh / hoch, ander / anders
			return AdjektivFlexionsklasse.FLEKTIERBAR_KEIN_ZAHLADJEKTIV;
		}

		if (stamm.endsWith("er") && StringUtil.startsWithUpperCase(stamm)) {
			// Kieler
			return AdjektivFlexionsklasse.ABLEITUNG_AUF_ER;
		}

		if (StringUtil.endsWith(stamm, "nuller", "zehner", "zwanziger", "dreiﬂiger", "vierziger", "f¸nfziger", "sechziger",
				"siebziger", "achtziger", "neunziger", "hunderter", "tausender")) {
			return AdjektivFlexionsklasse.ABLEITUNG_AUF_ER;
		}

		if (StringUtil.endsWith(stamm, "zwei", "drei", "vier", "f¸nf", "sechs", "sieben", "acht", "neun", "zehn", "elf",
				"zwˆlf", "zehn", "zwanzig", "dreiﬂig", "vierzig", "f¸nfzig", "sechzig", "siebzig", "achtzig", "neunzig",
				"hundert", "tausend")) {
			return AdjektivFlexionsklasse.FLEKTIERBAR_ZAHLADJEKTIV;
		}

		if (StringUtil.contains(stamm, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")) {
			return AdjektivFlexionsklasse.NICHT_FLEKTIERBAR_KEINE_ABLEITUNG_AUF_ER;
		}

		if (StringUtil.endsWith(stamm, "a")) {
			// lila, rosa

			return AdjektivFlexionsklasse.NICHT_FLEKTIERBAR_KEINE_ABLEITUNG_AUF_ER;
		}

		return AdjektivFlexionsklasse.FLEKTIERBAR_KEIN_ZAHLADJEKTIV;
	}

	/**
	 * Erzeugt ein Adjektiv.
	 * <p>
	 * Stamm und unflektierte Form kˆnnen in Einzelf‰llen voneinander abweichen,
	 * etwa <i>hoh</i> vs. <i>hoch</i>, <i>ander</i> vs. <i>anders</i>.
	 */
	public Adjektiv(final String stamm, final String unflektiert, final AdjektivFlexionsklasse flexionsklasse,
			final @Nullable Substantiv substantivierung) {
		this.stamm = stamm;
		this.flexionsklasse = flexionsklasse;
		this.substantivierung = substantivierung;

		// @formatter:off
    lexeme = new Lexeme(ADJEKTIV, unflektiert,
    		LexiconFeatureStructureUtil.fromStringValues(ImmutableMap.of(
            LEXEM_FLEKTIERBAR_KEY, booleanToString(flexionsklasse.isFlektierbar()),
            ABLEITUNG_AUF_ER_KEY, booleanToString(flexionsklasse.isAbleitungAufEr()))));
    // @formatter:on

		flekt = new AdjektivFlektierer();
	}

	/**
	 * Schwache Flexion: der groﬂe Krieger die gute Frau das weiﬂe Einhorn des
	 * groﬂen Kriegers der guten Frau des weiﬂen Einhorns dem groﬂen Krieger der
	 * guten Frau dem weiﬂen Einhorn den groﬂen Krieger die gute Frau das weiﬂe
	 * Einhorn die groﬂen Krieger die guten Frauen die weiﬂen Einhˆrner der groﬂen
	 * Krieger der guten Frauen der weiﬂen Einhˆrner den groﬂen Kriegern den guten
	 * Frauen den weiﬂen Einhˆrnern die groﬂen Krieger die guten Frauen die weiﬂen
	 * Einhˆrner
	 *
	 * Starke Flexion: ein groﬂer Krieger eine gute Frau ein weiﬂes Einhorn eines
	 * groﬂen Kriegers einer guten Frau eines weiﬂen Einhorns einem groﬂen Krieger
	 * einer guten Frau einem weiﬂen Einhorn einen groﬂen Krieger eine gute Frau ein
	 * weiﬂes Einhorn groﬂe Krieger gute Frauen weiﬂe Einhˆrner groﬂer Krieger guter
	 * Frauen weiﬂer Einhˆrner groﬂen Kriegern guten Frauen weiﬂen Einhˆrnern groﬂe
	 * Krieger gute Frauen weiﬂe Einhˆrner
	 */
	public String getWortform(final Flexionstyp inflexionType, final Kasus kasus, final Numerus numerus,
			final Genus genus) {
		if (inflexionType == SCHWACHE_FLEXION) {
			// TODO Gegen den Duden pr¸fen, ob das so richtig ist
			// TODO Auﬂerdem an die richtige Stelle verschieben, dass es auch im
			// AdjektivFlektierer
			// richtig verwendet wird.
			// Dort gibt es schon ein unflektiertPl(), das man aber anpassen muss
			// (komparation...)
			if (flexionsklasse == AdjektivFlexionsklasse.FLEKTIERBAR_ZAHLADJEKTIV && numerus == Numerus.PLURAL) {
				return getUnflektiert();
			}

			return flekt.adjPositivSchwach(lexeme, stamm, kasus, numerus, genus).get(0).getString();
		}

		if (inflexionType == Flexionstyp.UNFLEKTIERT) {
			return getUnflektiert(); // hoch
		}

		if (flexionsklasse == AdjektivFlexionsklasse.NICHT_FLEKTIERBAR_KEINE_ABLEITUNG_AUF_ER
				|| flexionsklasse == AdjektivFlexionsklasse.ABLEITUNG_AUF_ER) {
			return getUnflektiert(); // lila
		}

		if (inflexionType == Flexionstyp.STARKE_FLEXION) {
			if (flexionsklasse == AdjektivFlexionsklasse.FLEKTIERBAR_ZAHLADJEKTIV) {
				// TODO Gegen den Duden pr¸fen, ob das so richtig ist
				// TODO Auﬂerdem an die richtige Stelle verschieben, dass es auch im
				// AdjektivFlektierer
				// richtig verwendet wird.
				if (kasus == Kasus.GENITIV && numerus == Numerus.PLURAL) {
					return stamm + "er";
				}

				return stamm;
			}

			return flekt.adjPositivStark(lexeme, stamm, kasus, numerus, genus).get(0).getString();
		}

		throw new RuntimeException("Unexpected InflexionType: " + inflexionType);
	}

	public String getStamm() {
		return stamm;
	}

	public String getUnflektiert() {
		return lexeme.getCanonicalizedForm();
	}

	public Substantiv toSubstantiv() {
		if (substantivierung != null) {
			return substantivierung;
		}
		return rateSubstantivierung();
	}

	// ------ PRIVATE ---------

	private Substantiv rateSubstantivierung() {
		// IDEA...
		final String unflektiertesSubstantiv = StringUtil.capitalize(stamm + "lichkeit");
		return Substantiv.schwachDekliniert(unflektiertesSubstantiv, unflektiertesSubstantiv,
				unflektiertesSubstantiv + "en", Genus.FEMININUM);
	}

}
