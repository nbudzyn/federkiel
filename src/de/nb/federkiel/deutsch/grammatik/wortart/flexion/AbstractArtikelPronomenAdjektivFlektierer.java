package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.AKKUSATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.DATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.GENITIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.NOMINATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.KOMPARATION_FEATURE_TYPE;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.KOMPARATION_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.KOMPARATION_KOMPARATIV;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.KOMPARATION_POSITIV;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.KOMPARATION_SUPERLATIV;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_FEATURE_TYPE;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_KEY;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_SCHWACH;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_STARK;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.collection.Pair;
import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.deutsch.grammatik.valenz.Valenz;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.LexiconFeatureStructureUtil;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.feature.StringFeatureValue;
import de.nb.federkiel.interfaces.IFeatureType;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;
import de.nb.federkiel.lexikon.Wortform;
import de.nb.federkiel.semantik.NothingInParticularSemantics;
import de.nb.federkiel.string.StringUtil;

/**
 * Sammelt einige Gemeinsamkeiten für die Flexion von Artikelwörtern und
 * Pronomen sowie von Adjektiv.en
 *
 * @author nbudzyn 2010
 */
@ThreadSafe
public class AbstractArtikelPronomenAdjektivFlektierer implements IFlektierer {
	/**
	 * Legt fest, welcher Genitiv maskulinum / neutrum erzeugt werden soll.
	 */
	public enum GenMaskNeutrSgModus {
		/**
		 * Kein Genitiv maskulinum / neutrum (z.B. "*das Ergebnis alles")
		 */
		NICHT,

		/**
		 * Nur Genitiv maskulinum / neutrum auf -es
		 */
		NUR_ES,

		/**
		 * Nur Genitiv maskulinum / neutrum auf -en ("kalten Rauchs")
		 */
		NUR_EN,

		/**
		 * Nur Genitiv maskulinum / neutrum auf -es und -en
		 */
		ES_UND_EN;
	}

	/**
	 * Legt fest, welche Formen als Nomintativ Singular Maskulinum sowie als
	 * Nominativ Singular Neutrun und Akkusativ Singular Neutrum gebildet werden
	 * sollen
	 */
	public enum NomSgMaskUndNomAkkSgNeutrModus {
		/**
		 * Nominativ Singular Maskulinum sowie Nominativ Singular Neutrum und Akkusativ
		 * Singular Neutrum nicht bilden
		 */
		NICHT,

		/**
		 * Nur mit Endung (z.B. Nom. Sg. Mask. "dieser", Nom. Sg. Neutrum "dieses").
		 */
		MIT_ENDUNG,
		/**
		 * Nur endungslos (z.B. Nom. Sg. Mask. "ein", Nom. Sg. Neutrum "ein").
		 */
		ENDUNGSLOS,
		/**
		 * Nur mit Endung, und im Nominativ und Akkusativ Neutrum zusätzlich auch mit
		 * Endung -s statt -es (z.B. Nom. Sg. Mask. "seiner", Nom. Sg. Neutrum "seines"
		 * und "seins").
		 */
		MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES;
	}

	private static final ImmutableCollection<Pair<String, String>> AUSNAHMEENDEN_KOMPARATIV;
	private static final ImmutableCollection<Pair<String, String>> AUSNAHMEENDEN_SUPERLATIV;

	static {
		AUSNAHMEENDEN_KOMPARATIV = new ImmutableSet.Builder<Pair<String, String>>().
		// Die üblicheren immer zuerst!

		// (Duden 498)
		// Umlaut, Vokal a
				add(Pair.of("alt", "älter")).add(Pair.of("arg", "ärger")).add(Pair.of("arm", "ärmer"))
				.add(Pair.of("hart", "härter")).add(Pair.of("kalt", "kälter")).add(Pair.of("lang", "länger"))
				.add(Pair.of("nah", "näher")).add(Pair.of("scharf", "schärfer")).add(Pair.of("schwach", "schwächer"))
				.add(Pair.of("schwarz", "schwärzer")).add(Pair.of("stark", "stärker")).add(Pair.of("warm", "wärmer")).
				// Vokal o
				add(Pair.of("grob", "gröber")).add(Pair.of("groß", "größer")).add(Pair.of("hoch", "höher")).
				// Vokal u
				add(Pair.of("dumm", "dümmer")).add(Pair.of("jung", "jünger")).add(Pair.of("klug", "klüger"))
				.add(Pair.of("kurz", "kürzer")).
				// Schwankend, Vokal a
				add(Pair.of("bang", "banger")).add(Pair.of("bang", "bänger")).add(Pair.of("blass", "blasser"))
				.add(Pair.of("blass", "blässer")).add(Pair.of("glatt", "glatter")).add(Pair.of("glatt", "glätter"))
				.add(Pair.of("karg", "karger")).add(Pair.of("karg", "kärger")).add(Pair.of("krank", "kranker")). // (nicht im
																																																					// Duden
				// 498)
				add(Pair.of("krank", "kränker")).add(Pair.of("nass", "nasser")).add(Pair.of("nass", "nässer"))
				.add(Pair.of("schmal", "schmaler")).add(Pair.of("schmal", "schmäler")).
				// Schwankend, Vokal o
				add(Pair.of("fromm", "frommer")).add(Pair.of("fromm", "frömmer")).add(Pair.of("rot", "roter"))
				.add(Pair.of("rot", "röter")).
				// Schwankend, Vokal u
				add(Pair.of("krumm", "krummer")).add(Pair.of("krumm", "krümmer")).
				// gesund
				add(Pair.of("gesund", "gesünder")).
				// weitere unregelmäßige Formen (Duden 501)
				add(Pair.of("gut", "besser")).add(Pair.of("viel", "mehr")).add(Pair.of("wenig", "weniger"))
				.add(Pair.of("wenig", "minder")).build();

		AUSNAHMEENDEN_SUPERLATIV = new ImmutableSet.Builder<Pair<String, String>>().
		// Die üblicheren immer zuerst!

		// (Duden 501)
				add(Pair.of("größer", "größt")).add(Pair.of("höher", "höchst")).add(Pair.of("näher", "nächst"))
				.add(Pair.of("besser", "best")).add(Pair.of("mehr", "meist")).build();
	}

	AbstractArtikelPronomenAdjektivFlektierer() {
		super();
	}

	/**
	 * Standard-Deklination eines Adjektivs gemäß Duden (stark und schwach) - ggf.
	 * einschließlich E-Tilgung (finster -> finstrer)
	 *
	 * @param lexeme
	 *          Die Nennform des Lexems darf <i>nicht</i> auf <i>e</i> enden! (Also
	 *          <i>müd</i>, nicht <i>müde</i>.)
	 */
	public Collection<IWordForm> stdAdj(final Lexeme lexeme, final Valenz valenzBeiImplizitemSubjekt, final String pos,
			final boolean steigerbar) {
		final String stamm = lexeme.getCanonicalizedForm();

		return ImmutableList.copyOf(stdAdj(lexeme, valenzBeiImplizitemSubjekt, pos, stamm, steigerbar));
	}

	/**
	 * Standard-Deklination eines Adjektivs gemäß Duden (stark und schwach)
	 *
	 * @param stamm
	 *          Der Stamm endet <i>nicht</i> auf <i>e</i>! (Also <i>müd</i>, nicht
	 *          <i>müde</i>.)
	 */
	public Collection<IWordForm> stdAdj(final Lexeme lexeme, final Valenz valenzBeiImplizitemSubjekt, final String pos,
			final String stamm, final boolean steigerbar) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(adjStark(lexeme, valenzBeiImplizitemSubjekt, pos, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
				KOMPARATION_POSITIV, stamm, GenMaskNeutrSgModus.NUR_EN, NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG));
		res.addAll(adjSchwach(lexeme, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, valenzBeiImplizitemSubjekt, pos,
				KOMPARATION_POSITIV, stamm));

		// e-Tilgung für Positiv
		final @Nullable String stammNachETilgung = GermanUtil.tilgeEAusStammWennMoeglich(stamm);

		if (stammNachETilgung != null) {
			res.addAll(adjStark(lexeme, valenzBeiImplizitemSubjekt, pos, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
					KOMPARATION_POSITIV, stammNachETilgung, GenMaskNeutrSgModus.NUR_EN,
					NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG));
			res.addAll(adjSchwach(lexeme, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, valenzBeiImplizitemSubjekt, pos,
					STAERKE_SCHWACH, stammNachETilgung));
		}

		if (steigerbar) {
			// (ggf. Komparativ mit e-Tilgung)
			for (final String komparativ : komparativ(stamm)) {
				res.addAll(adjStark(lexeme, valenzBeiImplizitemSubjekt, pos, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
						KOMPARATION_KOMPARATIV, komparativ, GenMaskNeutrSgModus.NUR_EN, NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG));
				res.addAll(adjSchwach(lexeme, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, valenzBeiImplizitemSubjekt, pos,
						KOMPARATION_KOMPARATIV, komparativ));

				for (final String superlativ : superlativ(komparativ)) {
					res.addAll(adjStark(lexeme, valenzBeiImplizitemSubjekt, pos, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
							KOMPARATION_SUPERLATIV, superlativ, GenMaskNeutrSgModus.NUR_EN,
							NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG));
					res.addAll(adjSchwach(lexeme, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, valenzBeiImplizitemSubjekt,
							pos, KOMPARATION_SUPERLATIV, superlativ));
				}
			}
		}

		return res.build();
	}

	protected final static ImmutableMap<String, IFeatureValue> buildFeatureMap(final String komparation,
			final String staerke) {
		return ImmutableMap.<String, IFeatureValue>of(KOMPARATION_KEY, StringFeatureValue.of(komparation), STAERKE_KEY,
				StringFeatureValue.of(staerke));
	}

	protected final static ImmutableMap<String, IFeatureValue> buildFeatureMap(final String komparation,
			final String staerke, final FeatureStructure ergaenzungenUndAngabenSlots) {

		return ImmutableMap.<String, IFeatureValue>of(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB,
				RoleFrameSlot.of(0, -1, ImmutableSet.of(ergaenzungenUndAngabenSlots)), KOMPARATION_KEY,
				StringFeatureValue.of(komparation), STAERKE_KEY,
				StringFeatureValue.of(staerke));
	}

	protected final static ImmutableMap<String, IFeatureType> buildFeatureTypeMap(final String komparation,
			final String staerke) {

		return ImmutableMap.<String, IFeatureType>of(KOMPARATION_KEY, KOMPARATION_FEATURE_TYPE, STAERKE_KEY,
				STAERKE_FEATURE_TYPE);
	}

	protected final static ImmutableMap<String, IFeatureValue> buildFeatureMap(final @Nullable String staerke,
			final FeatureStructure ergaenzungenUndAngabenSlots) {

		if (staerke == null) {
			return ImmutableMap.<String, IFeatureValue>of(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB,
					RoleFrameSlot.of(0, -1, ImmutableSet.of(ergaenzungenUndAngabenSlots)));
		}

		return ImmutableMap.<String, IFeatureValue>of(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB,
				RoleFrameSlot.of(0, -1, ImmutableSet.of(ergaenzungenUndAngabenSlots)), STAERKE_KEY,
				StringFeatureValue.of(staerke));
	}

	protected final static ImmutableMap<String, IFeatureType> buildFeatureTypeMap(final @Nullable String staerke) {
		if (staerke == null) {
			return ImmutableMap.of();
		}

		return ImmutableMap.<String, IFeatureType>of(STAERKE_KEY, STAERKE_FEATURE_TYPE);
	}

	/**
	 * @param staerke
	 *          <code>null</code> erlaubt - dann fällt dieses Merkmal aus
	 */
	protected final static ImmutableMap<String, IFeatureValue> buildFeatureMap(final String staerke) {
		if (staerke == null) {
			return ImmutableMap.of();
		}

		return ImmutableMap.<String, IFeatureValue>of(STAERKE_KEY, StringFeatureValue.of(staerke));
	}

	protected final static ImmutableMap<String, IFeatureValue> buildFeatureMap(
			final FeatureStructure ergaenzungenUndAngabenSlots) {
		return ImmutableMap.<String, IFeatureValue>of(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB,
				RoleFrameSlot.of(0, -1, ImmutableSet.of(ergaenzungenUndAngabenSlots)));
	}

	/**
	 * Liefert die möglichen Komparative zu diesem Positiv - den üblichsten als
	 * erstes.
	 *
	 * @param positiv
	 *          Der Positiv endet <i>nicht</i> auf <i>e</i>! (Also <i>müd</i>, nicht
	 *          <i>müde</i>.)
	 */
	public Collection<String> komparativ(final String positiv) {
		// Gibt es Ausnahmen?
		// (Wenn ein Umlaut erforderlich ist, handelt es sich um eine Ausnahme!)
		final Collection<String> komparativAusnahmen = findKomparativAusnahme(positiv);
		if (komparativAusnahmen != null) {
			return komparativAusnahmen;
		}

		final ImmutableList.Builder<String> res = ImmutableList.builder();

		// Komparativ mit e-Tilgung im Stamm des Positivs (muntrer, teurer)
		// (Duden 499)
		final String positivNachETilgung = GermanUtil.tilgeEAusStammWennMoeglich(positiv); // ggf. null
		if (positivNachETilgung != null) {
			res.add(positivNachETilgung + "er");
		}

		// Komparativ ohne e-Tilgung (munterer, teuerer)
		res.add(positiv + "er");

		// Eine e-Tilgung im Komparations-Suffix "er" (*munterr) ist nicht
		// möglich.

		return res.build();
	}

	/**
	 * Liefert die möglichen Superlative zu diesem Komparativ - den üblichsten als
	 * erstes.
	 *
	 * @param komparativ
	 *          Basis ist NICHT der Positiv, sondern der Komparativ!
	 */
	public Collection<String> superlativ(final String komparativ) {
		// Gibt es Ausnahmen?
		// (Wenn ein Umlaut erforderlich ist, handelt es sich um eine Ausnahme!)
		final Collection<String> superlativAusnahmen = findSuperlativAusnahme(komparativ);
		if (superlativAusnahmen != null) {
			return superlativAusnahmen;
		}

		// Angelehnt an Duden 500

		if (komparativ.length() <= 2 || !komparativ.endsWith("er")) {
			return ImmutableList.<String>of();
		}

		final String komparativStamm = komparativ.substring(0, komparativ.length() - 2); // "er
																																											// entfernen"

		// Duden 500 i.
		if (komparativStamm.endsWith("isch")) {
			return ImmutableList.<String>of(komparativStamm + "st"); // fantastisch
		}

		if (ersteSuperlativRegelErfuellt(komparativStamm)) {
			return ImmutableList.<String>of(komparativStamm + "est"); // müdest
		}

		// Duden 500 ii.
		if (GermanUtil.endetAufBetontenVollvokal(komparativStamm)) {
			return ImmutableList.<String>of(komparativStamm + "est", // rohest,
																																// neuest,
																																// genauest
					komparativStamm + "st"); // rohst, neust, genaust
		}

		// Duden 500 iii.
		return ImmutableList.<String>of(komparativStamm + "st"); // kleinst

		// TODO Ausnahmen...?
		// FIXME: [das] höchste [Haus]

		// Duden 500 iv.
		// FIXME

		// Duden 500 v.
		// FIXME
	}

	/**
	 * Dies hier ist etwas heuristisch...
	 */
	private static boolean ersteSuperlativRegelErfuellt(final String stamm) {
		if (StringUtil.endsWith(stamm, "d", "t", "s", // "ss", "ß",
				"z", // "tz",
				"x", "sk", "sch")) {

			// Prüfen, ob die letzte Silbe Vollvokal aufweist

			if (StringUtil.endsWith(stamm, "esk")) {
				return true; // Quasi Ausnahme, z.B. grotesk - hier ist das e
				// KEIN Schwa!
			}

			// letzten Vokal finden
			int posLetztesVokalzeichen = stamm.length() - 1;
			while (posLetztesVokalzeichen >= 0 && !GermanUtil.isVokal(stamm.charAt(posLetztesVokalzeichen))) {
				posLetztesVokalzeichen--;
			}

			if (posLetztesVokalzeichen < 0) {
				// gar kein Vokal
				return false;
			}

			if (stamm.charAt(posLetztesVokalzeichen) != 'e' && stamm.charAt(posLetztesVokalzeichen) != 'E') {
				// Vollvokal!
				return true; // z.B. verstört
			}

			if (posLetztesVokalzeichen == 0) {
				return true; // Nur Schwa wäre seltsam -> wohl vollvokal
			}

			final char zeichenDavor = stamm.charAt(posLetztesVokalzeichen - 1);

			if (GermanUtil.isVokal(zeichenDavor)) {
				return true; // Kein Schwa, also wohl Vollvokal
			}

			// Zeichen davor ist KEIN VOKAL - dann ist's wohl Schwa (kein
			// Vollvokal)
			return false; // z.B.: "packend"
		}

		// endet falsch
		return false;
	}

	/**
	 * @param positiv
	 *          Der Positiv endet <i>nicht</i> auf <i>e</i>! (Also <i>müd</i>, nicht
	 *          <i>müde</i>.)
	 * @return <code>null</code>, falls keine Komparativ-Ausnahme hierfür bekannt
	 *         ist
	 */
	private static Collection<String> findKomparativAusnahme(final String positiv) {
		return findAusnahmen(positiv, AUSNAHMEENDEN_KOMPARATIV);
	}

	/**
	 * @return <code>null</code>, falls keine Superlativ-Ausnahme hierfür bekannt
	 *         ist
	 */
	private static Collection<String> findSuperlativAusnahme(final String komparativ) {
		return findAusnahmen(komparativ, AUSNAHMEENDEN_SUPERLATIV);
	}

	private static Collection<String> findAusnahmen(final String basis,
			final ImmutableCollection<Pair<String, String>> ausnahmeEnden) {
		final ImmutableList.Builder<String> resBuilder = ImmutableList.<String>builder();

		for (final Pair<String, String> ausnahmeende : ausnahmeEnden) {
			final String ende = ausnahmeende.first();
			if (basis.endsWith(ende)) {
				final String neuesEnde = ausnahmeende.second();

				resBuilder.add(basis.substring(0, basis.length() - ende.length()) + neuesEnde);
			}
		}

		final ImmutableList<String> res = resBuilder.build();

		if (res.isEmpty()) {
			return null;
		}

		return res;
	}

	public Collection<IWordForm> stdSchwach(final Lexeme lexeme, final String pos,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String stammNichtSteigerbar) {
		return adjSchwach(lexeme, vorgabeFuerNachfolgendesAdjektiv, Valenz.LEER, pos, KOMPARATION_POSITIV,
				stammNichtSteigerbar);
	}

	/**
	 * @param lexeme
	 *          z.B. <i>groß</i>, <i>*ander</i> oder <i>dick</i>
	 * @param vorgabeFuerNachfolgendesAdjektiv
	 *          ob ein AUF DIESES WORT NACHFOLGENDES Adjektiv (wenn es überhaupt ein
	 *          solches gibt!) STARK oder SCHWACH konjugiert sein muss
	 * @param stammInKomparation
	 *          z.B. <i>groß</i>, <i>ander</i> oder <i>dicker</i>
	 *          <p>
	 *          Darf nicht auf -e enden!
	 * @return die schwachen Wortformen, auch unter Berücksichtigung möglicher
	 *         e-Tilgung im Suffix (<i>dunkeln</i>)
	 */
	public Collection<IWordForm> adjSchwach(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final Valenz valenzBeiImplizitemSubjekt,
			final String pos, final String komparativ, final String stammInKomparation) {
		final boolean eTilgungImSuffixEnUndEmErlaubt = GermanUtil
				.erlaubtAdjektivischeETilgungBeiSuffixEnUndEm(stammInKomparation);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(adjSchwachSg(lexeme, vorgabeFuerNachfolgendesAdjektiv, valenzBeiImplizitemSubjekt, pos, komparativ,
				stammInKomparation, eTilgungImSuffixEnUndEmErlaubt));

		final ImmutableMap<String, IFeatureValue> additionalFeaturesPl = buildFeatureMap(komparativ, STAERKE_SCHWACH,
				valenzBeiImplizitemSubjekt.buildErgaenzungenUndAngabenSlots("3", // Person
						null,
						// die IHRER selbst gedenkende Männer /
						// Frauen / Kinder,
						// -> alle Genera möglich!
						PLURAL, StringFeatureLogicUtil.FALSE, true));
		// die ihrer selbst gedenkenden Männer,
		// NICHT JEDOCH: *die Ihrer selbst gedenkenden Männer!

		final ImmutableMap<String, IFeatureType> additionalFeatureTypesPl = buildFeatureTypeMap(komparativ,
				STAERKE_SCHWACH);

		res.addAll(adjSchwachPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
				eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesPl, additionalFeatureTypesPl));

		return res.build();
	}

	private ImmutableList<IWordForm> adjSchwachSg(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final Valenz valenzBeiImplizitemSubjekt,
			final String pos, final String komparativ, final String stammInKomparation,
			final boolean eTilgungImSuffixEnUndEmErlaubt) {
		final ImmutableMap<String, IFeatureValue> additionalFeaturesSg = buildFeatureMap(komparativ, STAERKE_SCHWACH);

		final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg = buildFeatureTypeMap(komparativ,
				STAERKE_SCHWACH);

		return ImmutableList.copyOf(adjSchwachSg(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
				eTilgungImSuffixEnUndEmErlaubt, valenzBeiImplizitemSubjekt, additionalFeaturesSg, additionalFeatureTypesSg));
	}

	protected ImmutableList<IWordForm> adjSchwachPl(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesPl,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesPl) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(adjSchwachPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
				eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesPl, additionalFeatureTypesPl, NOMINATIV));
		res.addAll(adjSchwachPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
				eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesPl, additionalFeatureTypesPl, GENITIV));
		res.addAll(adjSchwachPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
				eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesPl, additionalFeatureTypesPl, DATIV));
		res.addAll(adjSchwachPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
				eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesPl, additionalFeatureTypesPl, AKKUSATIV));

		return res.build();
	}

	public ImmutableList<IWordForm> adjSchwachPl(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesPl,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesPl, final Kasus kasus) {
		switch (kasus) {
		case NOMINATIV:
			return adjSchwachNomPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesPl, additionalFeatureTypesPl);
		case GENITIV:
			return adjSchwachGenPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesPl, additionalFeatureTypesPl);
		case DATIV:
			return adjSchwachOderStarkDatPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesPl, additionalFeatureTypesPl);
		case AKKUSATIV:
			return adjSchwachAkkPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesPl, additionalFeatureTypesPl);
		default:
			throw new IllegalStateException("Unerwarteter Kasus: " + kasus);
		}
	}

	private ImmutableList<IWordForm> adjSchwachNomPl(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesPl,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesPl) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
		res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null,
				additionalFeaturesPl, additionalFeatureTypesPl, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null,
					additionalFeaturesPl, additionalFeatureTypesPl, stammInKomparation + "n"));
		}
		return res.build();
	}

	private ImmutableList<IWordForm> adjSchwachGenPl(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesPl,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesPl) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
		res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null,
				additionalFeaturesPl, additionalFeatureTypesPl, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null,
					additionalFeaturesPl, additionalFeatureTypesPl, stammInKomparation + "n"));
		}
		return res.build();
	}

	private ImmutableList<IWordForm> adjSchwachOderStarkDatPl(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesPl,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesPl) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
		res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null,
				additionalFeaturesPl, additionalFeatureTypesPl, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null,
					additionalFeaturesPl, additionalFeatureTypesPl, stammInKomparation + "n"));
		}
		return res.build();
	}

	private ImmutableList<IWordForm> adjSchwachAkkPl(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesPl,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesPl) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
		res.add(buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null,
				additionalFeaturesPl, additionalFeatureTypesPl, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null,
					additionalFeaturesPl, additionalFeatureTypesPl, stammInKomparation + "n"));
		}
		return res.build();
	}

	private ImmutableList<IWordForm> adjSchwachSg(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final Valenz valenzBeiImplizitemSubjekt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesSgOhneVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(
				adjSchwachSg(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, eTilgungImSuffixEnUndEmErlaubt,
						valenzBeiImplizitemSubjekt, additionalFeaturesSgOhneVerbFrame, additionalFeatureTypesSg, MASKULINUM));

		res.addAll(
				adjSchwachSg(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, eTilgungImSuffixEnUndEmErlaubt,
						valenzBeiImplizitemSubjekt, additionalFeaturesSgOhneVerbFrame, additionalFeatureTypesSg, FEMININUM));

		res.addAll(
				adjSchwachSg(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, eTilgungImSuffixEnUndEmErlaubt,
						valenzBeiImplizitemSubjekt, additionalFeaturesSgOhneVerbFrame, additionalFeatureTypesSg, NEUTRUM));

		return res.build();
	}

	protected ImmutableList<IWordForm> adjSchwachSgNeutr(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final Valenz valenzBeiImplizitemSubjekt, final ImmutableMap<String, IFeatureValue> additionalFeaturesSg,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg, final Kasus kasus) {

		final RoleFrameSlot verbRoleFrameSlot = buildVerbRoleFrameP3(valenzBeiImplizitemSubjekt, NEUTRUM,
				SINGULAR);

		final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame = ImmutableMap
				.<String, IFeatureValue>builder().putAll(additionalFeaturesSg)
				.put(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB, verbRoleFrameSlot).build();

		switch (kasus) {
		case NOMINATIV:
			return ImmutableList.of(adjSchwachNomSgNeutr(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg));
		case GENITIV:
			return adjSchwachGenSgNeutr(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg);
		case DATIV:
			return adjSchwachDatSgNeutr(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg);
		case AKKUSATIV:
			return ImmutableList.of(adjSchwachAkkSgNeutr(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg));
		default:
			throw new IllegalStateException("Unerwarteter Kasus: " + kasus);
		}
	}

	private Wortform adjSchwachNomSgNeutr(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		return buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "e");
	}

	private ImmutableList<IWordForm> adjSchwachGenSgNeutr(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
		res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, vorgabeFuerNachfolgendesAdjektiv, SINGULAR,
					NEUTRUM, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "n"));
		}
		return res.build();
	}

	private ImmutableList<IWordForm> adjSchwachDatSgNeutr(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
		res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM,
					additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "n"));
		}
		return res.build();
	}

	public IWordForm adjSchwachAkkSgNeutr(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		return buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "e");
	}

	public ImmutableList<IWordForm> adjSchwachSg(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final Valenz valenzBeiImplizitemSubjekt, final ImmutableMap<String, IFeatureValue> additionalFeaturesSg,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg, final Kasus kasus, final Genus genus) {
		switch (genus) {
		case MASKULINUM:
			return adjSchwachSgMask(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, valenzBeiImplizitemSubjekt, additionalFeaturesSg, additionalFeatureTypesSg,
					kasus);
		case FEMININUM:
			return adjSchwachSgFem(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, valenzBeiImplizitemSubjekt, additionalFeaturesSg, additionalFeatureTypesSg,
					kasus);
		case NEUTRUM:
			return adjSchwachSgNeutr(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, valenzBeiImplizitemSubjekt, additionalFeaturesSg, additionalFeatureTypesSg,
					kasus);
		default:
			throw new IllegalStateException("Unerwartetes Genus: " + genus);
		}
	}

	private ImmutableList<IWordForm> adjSchwachSgFem(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final Valenz valenzBeiImplizitemSubjekt, final ImmutableMap<String, IFeatureValue> additionalFeaturesSg,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg, final Kasus kasus) {
		final RoleFrameSlot verbRoleFrameSlot = buildVerbRoleFrameP3(valenzBeiImplizitemSubjekt, FEMININUM,
				SINGULAR);

		final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame = ImmutableMap
				.<String, IFeatureValue>builder().putAll(additionalFeaturesSg)
				.put(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB, verbRoleFrameSlot).build();

		switch (kasus) {
		case NOMINATIV:
			return ImmutableList.of(adjSchwachNomSgFem(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg));
		case GENITIV:
			return adjSchwachGenSgFem(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg);
		case DATIV:
			return adjSchwachDatSgFem(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg);
		case AKKUSATIV:
			return ImmutableList.of(adjSchwachAkkSgFem(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg));
		default:
			throw new IllegalStateException("Unerwarteter Kasus " + kasus);
		}
	}

	private Wortform adjSchwachNomSgFem(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		return buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "e");
	}

	private ImmutableList<IWordForm> adjSchwachGenSgFem(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
		res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, vorgabeFuerNachfolgendesAdjektiv, SINGULAR,
				FEMININUM, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, vorgabeFuerNachfolgendesAdjektiv, SINGULAR,
					FEMININUM, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "n"));
		}
		return res.build();
	}

	private ImmutableList<IWordForm> adjSchwachDatSgFem(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM,
					additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "n"));
		}

		return res.build();
	}

	private Wortform adjSchwachAkkSgFem(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		return buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "e");
	}

	private ImmutableList<IWordForm> adjSchwachSg(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final Valenz valenzBeiImplizitemSubjekt, final ImmutableMap<String, IFeatureValue> additionalFeaturesSg,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg, final Genus genus) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(
				adjSchwachSg(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, eTilgungImSuffixEnUndEmErlaubt,
						valenzBeiImplizitemSubjekt, additionalFeaturesSg, additionalFeatureTypesSg, NOMINATIV, genus));
		res.addAll(
				adjSchwachSg(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, eTilgungImSuffixEnUndEmErlaubt,
						valenzBeiImplizitemSubjekt, additionalFeaturesSg, additionalFeatureTypesSg, GENITIV, genus));
		res.addAll(
				adjSchwachSg(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, eTilgungImSuffixEnUndEmErlaubt,
						valenzBeiImplizitemSubjekt, additionalFeaturesSg, additionalFeatureTypesSg, DATIV, genus));
		res.addAll(
				adjSchwachSg(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, eTilgungImSuffixEnUndEmErlaubt,
						valenzBeiImplizitemSubjekt, additionalFeaturesSg, additionalFeatureTypesSg, AKKUSATIV, genus));

		return res.build();
	}

	public ImmutableList<IWordForm> adjSchwachSgMask(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final Valenz valenzBeiImplizitemSubjekt, final ImmutableMap<String, IFeatureValue> additionalFeaturesSg,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg, final Kasus kasus) {
		final RoleFrameSlot verbRoleFrameSlot = buildVerbRoleFrameP3(valenzBeiImplizitemSubjekt, MASKULINUM, // der
				// SEINER
				// selbst
				// gedenkende
				// Mann,
				// aber
				// nicht
				// *der IHRER selbst gedenkende Mann !
				SINGULAR);

		final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame = ImmutableMap
				.<String, IFeatureValue>builder().putAll(additionalFeaturesSg)
				.put(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB, verbRoleFrameSlot).build();

		switch (kasus) {
		case NOMINATIV:
			return ImmutableList.of(adjSchwachNomSgMask(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg));
		case GENITIV:
			return adjSchwachGenSgMask(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg);
		case DATIV:
			return adjSchwachDatSgMask(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg);
		case AKKUSATIV:
			return adjSchwachOderStarkAkkSgMask(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg);
		default:
			throw new IllegalStateException("Unerwarteter Kasus " + kasus);
		}
	}

	private IWordForm adjSchwachNomSgMask(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		return buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "e");
	}

	/**
	 * Liefert die schwache Form des Genitiv Singular Maskulinum, ggf. auch mehrere
	 * Alternativen, dann die gebräuchlichste zuerst.
	 */
	private ImmutableList<IWordForm> adjSchwachGenSgMask(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, vorgabeFuerNachfolgendesAdjektiv, SINGULAR,
				MASKULINUM, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(
					buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM,
							additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "n")); // dunkeln
		}

		return res.build();
	}

	/**
	 * Liefert die schwache Form des Dativ Singular Maskulinum, ggf. auch mehrere
	 * Alternativen, dann die gebräuchlichste zuerst.
	 */
	private ImmutableList<IWordForm> adjSchwachDatSgMask(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "en"));
		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM,
					additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "n"));
		}

		return res.build();
	}

	/**
	 * Liefert die schwache oder starke Form des Akkusativ Singular Maskulinum, ggf.
	 * auch mehrere Alternativen, dann die gebräuchlichste zuerst.
	 */
	private ImmutableList<IWordForm> adjSchwachOderStarkAkkSgMask(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSgMitVerbFrame) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM,
				additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "en"));

		if (eTilgungImSuffixEnUndEmErlaubt) {
			res.add(buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM,
					additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSgMitVerbFrame, stammInKomparation + "n"));
		}

		return res.build();
	}

	protected RoleFrameSlot buildVerbRoleFrameP3(final Valenz valenzBeiImplizitemSubjekt,
			final Genus genusDesBezugsworts, final Numerus numerusDesBezugsworts) {
		final FeatureStructure ergaenzungenUndAngabenSlots = valenzBeiImplizitemSubjekt
				.buildErgaenzungenUndAngabenSlots("3", // Person
						genusDesBezugsworts, // der SEINER selbst gedenkende
						// Mann, aber nicht
						// *der IHRER selbst gedenkende Mann !
						numerusDesBezugsworts, StringFeatureLogicUtil.FALSE, // die ihrer
																																	// selbst
						true);
		return RoleFrameSlot.of(0, -1, ImmutableSet.of(ergaenzungenUndAngabenSlots));
	}

	/*
	 * @param lexem z.B. <i>groß</i>, <i>*ander</i> oder <i>dick</i>
	 *
	 * @param stammInKomparation z.B. <i>groß</i>, <i>ander</i> oder <i>dicker</i>
	 * <p> Darf nicht auf -e enden!
	 *
	 * @return die starken Wortformen, auch unter Berücksichtigung möglicher
	 * e-Tilgung im Suffix (<i>dunkeln</i>); Genitiv Mask und Neutr Sg immer nur auf
	 * -en; Nominativ und Akkusativ Sg Neutrum immer auf -es (nicht -s)
	 *
	 * public Collection<IWordForm> adjStark(final Lexeme lexem, final String
	 * komparation, final String stammInKomparation) { return adjStark(lexem,
	 * stammInKomparation, GenMaskNeutrSgModus.NUR_EN, // Adjektiv nur auf -en
	 * NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG, // im Nom und Akk Sg // Neutrum
	 * ist die // Endung immer -es, // nicht -s ImmutableMap.<String, String> of());
	 * }
	 */

	/**
	 * @param lexeme
	 *          z.B. <i>groß</i>, <i>*ander</i> oder <i>dick</i>
	 * @param vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung
	 *          TODO
	 * @param stammInKomparation
	 *          z.B. <i>groß</i>, <i>ander</i> oder <i>dicker</i>
	 *          <p>
	 *          Darf nicht auf -e enden!
	 * @param genMaskNeutrSgModus
	 *          legt fest, welche(r) Genitiv(e) in Maskulinum und im Neutrum
	 *          Singular erzeugt werden sollen
	 * @param nomSgMaskUndNomAkkSgNeutrModus
	 *          legt fest, welche Formen im Nominativ Singular Maskulinum sowie im
	 *          Nominativ und Akkusativ Singular Neutrum erzeugt werden sollen
	 * @return die starken Wortformen, auch unter Berücksichtigung möglicher
	 *         e-Tilgung im Suffix (<i>dunkeln</i>)
	 */
	public Collection<IWordForm> adjStark(final Lexeme lexeme, final Valenz valenzFuerImplizitesSubjekt, final String pos,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
			final String komparation, final String stammInKomparation, final GenMaskNeutrSgModus genMaskNeutrSgModus,
			final NomSgMaskUndNomAkkSgNeutrModus nomSgMaskUndNomAkkSgNeutrModus) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(adjStarkSg(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzFuerImplizitesSubjekt,
				buildFeatureMap(komparation, STAERKE_STARK), buildFeatureTypeMap(komparation, STAERKE_STARK)));

		res.addAll(adjStarkPl(lexeme, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, pos, stammInKomparation,
				buildFeatureMap(komparation, STAERKE_STARK,
						valenzFuerImplizitesSubjekt.buildErgaenzungenUndAngabenSlots("3", null,
								// (IHRER selbst gedenkende) Männer /
								// Frauen / Kinder,
								// -> alle Genera möglich!
								PLURAL, StringFeatureLogicUtil.FALSE, true)),
				// Die ihrer selbst gedenkenden Männer,
				// ABER NICHT die Ihrer selbst gedenkenden Männer!
				buildFeatureTypeMap(komparation, STAERKE_STARK)));

		return res.build();
	}

	/**
	 * @param stammInKomparation
	 *          z.B. <i>groß</i>, <i>ander</i> oder <i>dicker</i>
	 *          <p>
	 *          Darf nicht auf -e enden!
	 * @param genMaskNeutrSgModus
	 *          legt fest, welche(r) Genitiv(e) in Maskulinum und im Neutrum
	 *          Singular erzeugt werden sollen
	 * @param nomSgMaskUndNomAkkSgNeutrModus
	 *          legt fest, welche Formen im Nominativ Singular Maskulinum sowie im
	 *          Nominativ und Akkusativ Singular Neutrum erzeugt werden sollen
	 * @param generateFeatureWortartTraegtFlexionsendung
	 *          Ob das Feature <code>wortartTraegtFlexionsendung</code> mitgeneriert
	 *          werden soll
	 * @return die starken Wortformen, auch unter Berücksichtigung möglicher
	 *         e-Tilgung im Suffix (<i>dunkeln</i>)
	 */
	public ImmutableList<IWordForm> adjStarkSg(final Lexeme lexeme, final String pos, final String stammInKomparation,
			final GenMaskNeutrSgModus genMaskNeutrSgModus,
			final NomSgMaskUndNomAkkSgNeutrModus nomSgMaskUndNomAkkSgNeutrModus,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
			final Valenz valenzBeiImplizitemSubjekt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesOhneVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypes) {
		final boolean eTilgungImSuffixEnUndEmErlaubt = GermanUtil
				.erlaubtAdjektivischeETilgungBeiSuffixEnUndEm(stammInKomparation);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		// MASKULINUM
		res.addAll(adjStarkSgMask(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame,
				additionalFeatureTypes, eTilgungImSuffixEnUndEmErlaubt));

		// FEMININUM
		res.addAll(adjStarkSgFem(lexeme, pos, stammInKomparation, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
				valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame, additionalFeatureTypes));

		// NEUTRUM
		res.addAll(adjStarkSgNeutr(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame,
				additionalFeatureTypes, eTilgungImSuffixEnUndEmErlaubt));

		return res.build();
	}

	public ImmutableList<IWordForm> adjStarkSg(final Lexeme lexeme, final String pos, final String stammInKomparation,
			final GenMaskNeutrSgModus genMaskNeutrSgModus,
			final NomSgMaskUndNomAkkSgNeutrModus nomSgMaskUndNomAkkSgNeutrModus,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
			final Valenz valenzBeiImplizitemSubjekt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesOhneVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypes, final Kasus kasus, final Genus genus) {
		final boolean eTilgungImSuffixEnUndEmErlaubt = GermanUtil
				.erlaubtAdjektivischeETilgungBeiSuffixEnUndEm(stammInKomparation);

		switch (genus) {
		case MASKULINUM:
			return adjStarkSgMask(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
					vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt,
					additionalFeaturesOhneVerbFrame, additionalFeatureTypes, eTilgungImSuffixEnUndEmErlaubt, kasus);
		case FEMININUM:
			return ImmutableList
					.of(adjStarkSgFem(lexeme, pos, stammInKomparation, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
							valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame, additionalFeatureTypes, kasus));
		case NEUTRUM:
			return adjStarkSgNeutr(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
					vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt,
					additionalFeaturesOhneVerbFrame, additionalFeatureTypes, eTilgungImSuffixEnUndEmErlaubt, kasus);
		default:
			throw new IllegalStateException("Unerwartetes Genus: " + genus);
		}
	}

	private ImmutableList<IWordForm> adjStarkSgNeutr(final Lexeme lexeme, final String pos,
			final String stammInKomparation, final GenMaskNeutrSgModus genMaskNeutrSgModus,
			final NomSgMaskUndNomAkkSgNeutrModus nomSgMaskUndNomAkkSgNeutrModus,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
			final Valenz valenzBeiImplizitemSubjekt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesOhneVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypes, final boolean eTilgungImSuffixEnUndEmErlaubt) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(adjStarkSgNeutr(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame,
				additionalFeatureTypes, eTilgungImSuffixEnUndEmErlaubt, Kasus.NOMINATIV));

		res.addAll(adjStarkSgNeutr(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame,
				additionalFeatureTypes, eTilgungImSuffixEnUndEmErlaubt, Kasus.GENITIV));

		res.addAll(adjStarkSgNeutr(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame,
				additionalFeatureTypes, eTilgungImSuffixEnUndEmErlaubt, Kasus.DATIV));

		res.addAll(adjStarkSgNeutr(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame,
				additionalFeatureTypes, eTilgungImSuffixEnUndEmErlaubt, Kasus.AKKUSATIV));

		return res.build();
	}

	private ImmutableList<IWordForm> adjStarkSgNeutr(final Lexeme lexeme, final String pos,
			final String stammInKomparation, final GenMaskNeutrSgModus genMaskNeutrSgModus,
			final NomSgMaskUndNomAkkSgNeutrModus nomSgMaskUndNomAkkSgNeutrModus,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
			final Valenz valenzBeiImplizitemSubjekt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesOhneVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final Kasus kasus) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		final RoleFrameSlot verbRoleFrameSlot = buildVerbRoleFrameP3(valenzBeiImplizitemSubjekt, NEUTRUM, // ein
				// SEINER
				// selbst
				// gedenkender
				// Mann,
				// aber
				// nicht
				// *ein IHRER selbst gedenkender Mann !
				SINGULAR);

		final ImmutableMap<String, IFeatureValue> additionalFeaturesMitVerbFrame = ImmutableMap
				.<String, IFeatureValue>builder().putAll(additionalFeaturesOhneVerbFrame)
				.put(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB, verbRoleFrameSlot).build();

		switch (kasus) {
		case NOMINATIV:
			if (nomSgMaskUndNomAkkSgNeutrModus.equals(NomSgMaskUndNomAkkSgNeutrModus.ENDUNGSLOS)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_STARK,
						SINGULAR, NEUTRUM, additionalFeaturesMitVerbFrame, additionalFeatureTypesSg, stammInKomparation)); // sein
			}
			if (nomSgMaskUndNomAkkSgNeutrModus.equals(NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG)
					|| nomSgMaskUndNomAkkSgNeutrModus
							.equals(NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
						SINGULAR, NEUTRUM, additionalFeaturesMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "es")); // seines
			}
			if (nomSgMaskUndNomAkkSgNeutrModus
					.equals(NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
						SINGULAR, NEUTRUM, additionalFeaturesMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "s")); // seins
			}
			return res.build();
		case GENITIV:
			if (genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.NUR_ES)
					|| genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.ES_UND_EN)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
						SINGULAR, NEUTRUM, additionalFeaturesMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "es"));
			}

			if (genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.NUR_EN)
					|| genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.ES_UND_EN)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
						vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, NEUTRUM, additionalFeaturesMitVerbFrame,
						additionalFeatureTypesSg, stammInKomparation + "en"));
				if (eTilgungImSuffixEnUndEmErlaubt) {
					res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
							vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, NEUTRUM, additionalFeaturesMitVerbFrame,
							additionalFeatureTypesSg, stammInKomparation + "n"));
				}
			}
			return res.build();
		case DATIV:
			res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR,
					NEUTRUM, additionalFeaturesMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "em"));
			if (eTilgungImSuffixEnUndEmErlaubt) {
				res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR,
						NEUTRUM, additionalFeaturesMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "m"));
			}
			return res.build();
		case AKKUSATIV:
			if (nomSgMaskUndNomAkkSgNeutrModus.equals(NomSgMaskUndNomAkkSgNeutrModus.ENDUNGSLOS)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.AKK, VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_STARK, SINGULAR,
						NEUTRUM, additionalFeaturesMitVerbFrame, additionalFeatureTypesSg, stammInKomparation)); // sein
			}

			if (nomSgMaskUndNomAkkSgNeutrModus.equals(NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG)
					|| nomSgMaskUndNomAkkSgNeutrModus
							.equals(NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR,
						NEUTRUM, additionalFeaturesMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "es")); // seines
			}
			if (nomSgMaskUndNomAkkSgNeutrModus
					.equals(NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR,
						NEUTRUM, additionalFeaturesMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "s")); // seins
			}
			return res.build();
		default:
			throw new IllegalStateException("Unerwarteter Kasus: " + kasus);
		}
	}

	public ImmutableList<IWordForm> adjStarkSgFem(final Lexeme lexeme, final String pos, final String stammInKomparation,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
			final Valenz valenzBeiImplizitemSubjekt,
			final ImmutableMap<String, IFeatureValue> additionalFeaturesOhneVerbFrame,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypes) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(adjStarkSgFem(lexeme, pos, stammInKomparation, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
				valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame, additionalFeatureTypes, Kasus.NOMINATIV));

		res.add(adjStarkSgFem(lexeme, pos, stammInKomparation, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
				valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame, additionalFeatureTypes, Kasus.GENITIV));

		res.add(adjStarkSgFem(lexeme, pos, stammInKomparation, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
				valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame, additionalFeatureTypes, Kasus.DATIV));

		res.add(adjStarkSgFem(lexeme, pos, stammInKomparation, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
				valenzBeiImplizitemSubjekt, additionalFeaturesOhneVerbFrame, additionalFeatureTypes, Kasus.AKKUSATIV));

		return res.build();
	}

	private IWordForm adjStarkSgFem(final Lexeme lexeme, final String pos, final String stammInKomparation,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
			final Valenz valenzBeiImplizitemSubjekt, final ImmutableMap<String, IFeatureValue> additionalFeatures,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypes, final Kasus kasus) {
		final RoleFrameSlot verbRoleFrameSlot = buildVerbRoleFrameP3(valenzBeiImplizitemSubjekt, FEMININUM,
				SINGULAR);

		final ImmutableMap<String, IFeatureValue> additionalFeaturesMitVerbFrame = ImmutableMap
				.<String, IFeatureValue>builder().putAll(additionalFeatures)
				.put(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB, verbRoleFrameSlot).build();

		switch (kasus) {
		case NOMINATIV:
			return adjSchwachNomSgFem(lexeme, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, pos, stammInKomparation,
					additionalFeaturesMitVerbFrame, additionalFeatureTypes);
		case GENITIV:
			return buildWortform(lexeme, pos, KasusInfo.GEN_R, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR,
					FEMININUM, additionalFeaturesMitVerbFrame, additionalFeatureTypes, stammInKomparation + "er");
		case DATIV:
			return buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR,
					FEMININUM, additionalFeaturesMitVerbFrame, additionalFeatureTypes, stammInKomparation + "er");
		case AKKUSATIV:
			return adjSchwachAkkSgFem(lexeme, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, pos, stammInKomparation,
					additionalFeaturesMitVerbFrame, additionalFeatureTypes);
		default:
			throw new IllegalStateException("Unerwarteter Kasus: " + kasus);
		}
	}

	public ImmutableList<IWordForm> adjStarkSgMask(final Lexeme lexeme, final String pos, final String stammInKomparation,
			final GenMaskNeutrSgModus genMaskNeutrSgModus,
			final NomSgMaskUndNomAkkSgNeutrModus nomSgMaskUndNomAkkSgNeutrModus,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
			final Valenz valenzBeiImplizitemSubjekt, final ImmutableMap<String, IFeatureValue> additionalFeaturesSg,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg, final boolean eTilgungImSuffixEnUndEmErlaubt) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(adjStarkSgMask(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesSg,
				additionalFeatureTypesSg, eTilgungImSuffixEnUndEmErlaubt, Kasus.NOMINATIV));

		res.addAll(adjStarkSgMask(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesSg,
				additionalFeatureTypesSg, eTilgungImSuffixEnUndEmErlaubt, Kasus.GENITIV));

		res.addAll(adjStarkSgMask(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesSg,
				additionalFeatureTypesSg, eTilgungImSuffixEnUndEmErlaubt, Kasus.DATIV));

		res.addAll(adjStarkSgMask(lexeme, pos, stammInKomparation, genMaskNeutrSgModus, nomSgMaskUndNomAkkSgNeutrModus,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, valenzBeiImplizitemSubjekt, additionalFeaturesSg,
				additionalFeatureTypesSg, eTilgungImSuffixEnUndEmErlaubt, Kasus.AKKUSATIV));

		return res.build();
	}

	private ImmutableList<IWordForm> adjStarkSgMask(final Lexeme lexeme, final String pos,
			final String stammInKomparation, final GenMaskNeutrSgModus genMaskNeutrSgModus,
			final NomSgMaskUndNomAkkSgNeutrModus nomSgMaskUndNomAkkSgNeutrModus,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
			final Valenz valenzBeiImplizitemSubjekt, final ImmutableMap<String, IFeatureValue> additionalFeaturesSg,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypesSg, final boolean eTilgungImSuffixEnUndEmErlaubt,
			final Kasus kasus) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		final RoleFrameSlot verbRoleFrameSlot = buildVerbRoleFrameP3(valenzBeiImplizitemSubjekt, MASKULINUM, // ein
				// SEINER
				// selbst
				// gedenkender
				// Mann,
				// aber
				// nicht
				// *ein IHRER selbst gedenkender Mann !
				SINGULAR);

		final ImmutableMap<String, IFeatureValue> additionalFeaturesSgMitVerbFrame = ImmutableMap
				.<String, IFeatureValue>builder().putAll(additionalFeaturesSg)
				.put(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB, verbRoleFrameSlot).build();

		// TODO schön Heinrich, schön Heinrichs (generell unflektiert)

		switch (kasus) {
		case NOMINATIV:
			if (nomSgMaskUndNomAkkSgNeutrModus.equals(NomSgMaskUndNomAkkSgNeutrModus.ENDUNGSLOS)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_STARK,
						SINGULAR, MASKULINUM, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg, stammInKomparation));
			}
			if (nomSgMaskUndNomAkkSgNeutrModus.equals(NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG)
					|| nomSgMaskUndNomAkkSgNeutrModus
							.equals(NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
						SINGULAR, MASKULINUM, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg,
						stammInKomparation + "er"));
			}
			return res.build();
		case GENITIV:
			if (genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.NUR_ES)
					|| genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.ES_UND_EN)) {
				res.add(
						buildWortform(lexeme, pos, KasusInfo.GEN_S, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR,
								MASKULINUM, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "es"));
			}

			if (genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.NUR_EN)
					|| genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.ES_UND_EN)) {
				res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
						vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM, additionalFeaturesSgMitVerbFrame,
						additionalFeatureTypesSg, stammInKomparation + "en"));
				if (eTilgungImSuffixEnUndEmErlaubt) {
					res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
							vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM,
							additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "n")); // dunkeln
				}
			}
			return res.build();
		case DATIV:
			res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR,
					MASKULINUM, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "em"));
			if (eTilgungImSuffixEnUndEmErlaubt) {
				res.add(buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR,
						MASKULINUM, additionalFeaturesSgMitVerbFrame, additionalFeatureTypesSg, stammInKomparation + "m"));
			}
			return res.build();
		case AKKUSATIV:
			return adjSchwachOderStarkAkkSgMask(lexeme, vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, pos,
					stammInKomparation, eTilgungImSuffixEnUndEmErlaubt, additionalFeaturesSgMitVerbFrame,
					additionalFeatureTypesSg);
		default:
			throw new IllegalStateException("Unerwarteter Kasus: " + kasus);
		}
	}

	/**
	 * @param stammInKomparation
	 *          z.B. <i>groß</i>, <i>ander</i> oder <i>dicker</i>
	 *          <p>
	 *          Darf nicht auf -e enden!
	 * @return die starken Wortformen, auch unter Berücksichtigung möglicher
	 *         e-Tilgung im Suffix (<i>dunkeln</i>)
	 */
	protected Collection<IWordForm> adjStarkPl(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final ImmutableMap<String, IFeatureValue> additionalFeatures,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypes) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(adjStarkPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, additionalFeatures,
				additionalFeatureTypes, Kasus.NOMINATIV));

		res.addAll(adjStarkPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, additionalFeatures,
				additionalFeatureTypes, Kasus.GENITIV));

		res.addAll(adjStarkPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, additionalFeatures,
				additionalFeatureTypes, Kasus.DATIV));

		res.addAll(adjStarkPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation, additionalFeatures,
				additionalFeatureTypes, Kasus.AKKUSATIV));

		return res.build();
	}

	/**
	 * @param stammInKomparation
	 *          z.B. <i>groß</i>, <i>ander</i> oder <i>dicker</i>
	 *          <p>
	 *          Darf nicht auf -e enden!
	 * @return die starken Wortformen, auch unter Berücksichtigung möglicher
	 *         e-Tilgung im Suffix (<i>dunkeln</i>)
	 */
	protected ImmutableList<IWordForm> adjStarkPl(final Lexeme lexeme,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final String pos,
			final String stammInKomparation, final ImmutableMap<String, IFeatureValue> additionalFeatures,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypes, final Kasus kasus) {
		final boolean eTilgungImSuffixEnUndEmErlaubt = GermanUtil
				.erlaubtAdjektivischeETilgungBeiSuffixEnUndEm(stammInKomparation);

		switch (kasus) {
		case NOMINATIV:
			return ImmutableList.of(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, vorgabeFuerNachfolgendesAdjektiv,
					PLURAL, null, additionalFeatures, additionalFeatureTypes, stammInKomparation + "e"));
		case GENITIV:
			return ImmutableList.of(buildWortform(lexeme, pos, KasusInfo.GEN_R, vorgabeFuerNachfolgendesAdjektiv, PLURAL,
					null, additionalFeatures, additionalFeatureTypes, stammInKomparation + "er"));
		case DATIV:
			return adjSchwachOderStarkDatPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stammInKomparation,
					eTilgungImSuffixEnUndEmErlaubt, additionalFeatures, additionalFeatureTypes);
		case AKKUSATIV:
			return ImmutableList.of(buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null,
					additionalFeatures, additionalFeatureTypes, stammInKomparation + "e"));
		default:
			throw new IllegalStateException("Unerwarteter Kasus " + kasus);
		}
	}

	/**
	 * Erzeugt eine Wortform.
	 *
	 * @param lexeme
	 *          Die Nennform des Lexeme soll nicht auf -e enden.
	 */
	Wortform buildWortform(final Lexeme lexeme, final String pos, final KasusInfo kasusInfo,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final Numerus numerus,
			final @Nullable Genus genus, final ImmutableMap<String, IFeatureValue> additionalFeatures,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypes, final String string) {

		final FeatureStructure features = buildFeatures(kasusInfo, vorgabeFuerNachfolgendesAdjektiv, numerus, genus,
				additionalFeatures, additionalFeatureTypes);

		return new Wortform(lexeme, pos, string, features);
	}

	private static FeatureStructure buildFeatures(final KasusInfo kasusInfo,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final Numerus numerus, final Genus genus,
			final ImmutableMap<String, IFeatureValue> additionalFeatures,
			final ImmutableMap<String, IFeatureType> additionalFeatureTypes) {
		final ImmutableMap.Builder<String, IFeatureValue> featureBuilder = ImmutableMap.builder();

		// @formatter:off
    featureBuilder
        .put("kasus",
            FeatureStructure
                .toFeatureValue(FeatureStringConverter.toFeatureString(kasusInfo.getKasus())))
        .put("numerus",
            FeatureStructure.toFeatureValue(FeatureStringConverter.toFeatureString(numerus)))
        .put("genus",
            FeatureStructure.toFeatureValue(FeatureStringConverter.toFeatureString(genus)))
        .put(GermanUtil.GENITIV_SICHTBAR_DURCH_R_KEY,
            FeatureStructure.toFeatureValue(
                StringFeatureLogicUtil.booleanToString(kasusInfo.isGenitivSichtbarDurchR())))
        .put(GermanUtil.GENITIV_SICHTBAR_DURCH_S_KEY, FeatureStructure.toFeatureValue(
            StringFeatureLogicUtil.booleanToString(kasusInfo.isGenitivSichtbarDurchS())));

    if (vorgabeFuerNachfolgendesAdjektiv.isErzeugen()) {
      featureBuilder
        .put(GermanUtil.ERLAUBT_NACHGESTELLTES_SCHWACH_FLEKTIERTES_ADJEKTIV_KEY,
          FeatureStructure.toFeatureValue(StringFeatureLogicUtil
              .booleanToString(vorgabeFuerNachfolgendesAdjektiv.isErlaubtSchwach())))
        .put(GermanUtil.ERLAUBT_NACHGESTELLTES_STARK_FLEKTIERTES_ADJEKTIV_KEY,
          FeatureStructure.toFeatureValue(StringFeatureLogicUtil
              .booleanToString(vorgabeFuerNachfolgendesAdjektiv.isErlaubtStark())));
    }

    featureBuilder.putAll(additionalFeatures);
    // @formatter:on

		return LexiconFeatureStructureUtil.fromValues(featureBuilder.build(), NothingInParticularSemantics.INSTANCE);
	}
}
