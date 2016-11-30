package de.nb.federkiel.deutsch.grammatik.wortart.substantiv;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.collection.Pair;
import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.Artikelwortbezug;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.FeatureStringConverter;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.FremdwortTyp;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.IFlektierer;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.KasusInfo;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.SubstantivFlexionsklasse;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.SubstantivPronomenUtil;
import de.nb.federkiel.deutsch.lexikon.GermanPOS;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;
import de.nb.federkiel.lexikon.Wortform;
import de.nb.federkiel.semantik.NothingInParticularSemantics;
import de.nb.federkiel.string.StringUtil;

/**
 * Kann Flexionsformen von Subtantiven bilden.
 *
 * @author nbudzyn 2009
 */
@ThreadSafe
public class SubstantivFlektierer implements IFlektierer {
  public static final String TYP = "Substantiv";

  /**
   * Die Reihenfolge ist relevant! Das erste "sticht"! Außerdem gehen diese vor
   * (wenn hier etwas passt, wird <code>AUSNAHMEENDEN_PLURAL</code> NICHT
   * berücksichtigt!)
   */
  private static final ImmutableCollection<Pair<String, String[]>> AUSNAHMEENDEN_MIT_ALTERNATIVEN_PLURAL;

  /**
   * Die Reihenfolge ist relevant! Das erste "sticht"!
   */
  private static final ImmutableCollection<Pair<String, String>> AUSNAHMEENDEN_PLURAL;

  /**
   * Die Reihenfolge ist relevant! Das erste "sticht"!
   */
  private static final ImmutableCollection<Pair<String, String>> AUSNAHMEENDEN_STAMMABWEICHUNGEN_KASUSFLEXION;

  /**
   * Ausnahmen für Flexionsklassen - NUR FÜR MASKULINE NOMEN! Die Reihenfolge
   * ist relevant! Das erste "sticht"!
   */
  private static final ImmutableCollection<Pair<String, EnumSet<SubstantivFlexionsklasse>>> AUSNAHMEENDEN_MIT_ALTERNATIVEN_MASK_FLEXIONSKLASSEN;

  private static final String[] FREMDE_MASK_WORTAUSGAENGE_PLURAL_BELEBT_MIT_EN = new String[] {
      "and", // Doktoand -> Doktoranden
      "ant", // Demonstrant -> Demonstranten
      "ent", // Absolvent -> Absolventen
      "ist", // Artist -> Artisten
      "ast", // Gymnasiast -> Gymnasiasten
      "at", // Kandidat -> Kandidaten
      "et", // Poet -> Poeten
      "it", // Bandit -> Banditen
      "ot", // Idiot -> Iditoten
      "nom" }; // Agronom -> Agronomen

  private static final Logger log = Logger
      .getLogger(SubstantivFlektierer.class); // NOPMD by nbudzyn on
  // 29.06.10 19:50

  static {
    AUSNAHMEENDEN_MIT_ALTERNATIVEN_PLURAL = new ImmutableSet.Builder<Pair<String, String[]>>()
        .add(Pair.of("blau", new String[] { "blau", "blaus" }))
        .add(Pair.of("bogen", new String[] { "bogen", "bögen" }))
        .add(Pair.of("bypass", new String[] { "bypässe", "bypasses" }))
        .add(Pair.of("embryo", new String[] { "embryonen", "embryos" }))
        .add(Pair.of("gelb", new String[] { "gelb", "gelbs" }))
        .add(Pair.of("general", new String[] { "generale", "generäle" }))
        .add(Pair.of("grün", new String[] { "grün", "grüns" }))
        .add(Pair.of("monitor", new String[] { "monitore", "monitoren" }))
        .add(Pair.of("motor", new String[] { "motore", "motoren" }))
        .add(Pair.of("index", new String[] { "indexe", "indizes" }))
        .add(Pair.of("interface", new String[] { "interface", // laut
            // Duden
            "interfaces" }))
        .add(Pair.of("konto", new String[] { "konti", "kontos", "konten" }))
        .add(Pair.of("lexikon", new String[] { "lexika", "lexiken" }))
        .add(Pair.of("lila", new String[] { "lila", "lilas" }))
        .add(Pair.of("pink", new String[] { "pink", "pinks" }))
        .add(Pair.of("pronomen", new String[] { "pronomen", "pronomina" }))
        .add(Pair.of("korpus", new String[] { "korpora", "korpusse" }))
        .add(Pair.of("rosa", new String[] { "rosa", "rosas" }))
        .add(Pair.of("test", new String[] { "tests", "teste" }))
        .add(Pair.of("brot", new String[] { "brote" }))
        .add(Pair.of("rot", new String[] { "rots", "rot" }))
        .add(
            Pair.of("sandwich", new String[] { "sandwichs", "sandwiches",
                "sandwiche" }))
        .add(Pair.of("scheusal", new String[] { "scheusale", "scheusäler" }))
        .add(Pair.of("bauer",
        // kommt drauf an!
            new String[] { "bauern", // der Bauer -> die
                // Bauern,
                // der Ackerbauer -> die Ackerbauern
                "bauer" })). // der Maschinenbauer ->
        // die
        // Maschinenbauer
        add(Pair.of("magnet", new String[] { "magneten", "magnete" // Duden
        // 333
            })).build();

    // IDEA: Geht vielleicht performanter? (vgl. ähnliche Code-Stellen!)
    AUSNAHMEENDEN_PLURAL = new ImmutableSet.Builder<Pair<String, String>>()
        .add(Pair.of("käse", "käse"))
        . // *Käsen (auch Hüttenkäse
        // etc.)
        add(Pair.of("nackedei", "nackedeis"))
        .add(Pair.of("kotau", "kotaus"))
        .add(Pair.of("ajatollah", "ajatollahs"))
        .add(Pair.of("see", "seen"))
        .add(Pair.of("nis", "nisse"))
        . // Geheimnis ->
          // Geheimnisse

        // eigentlich Z2, aber ei ist als unbetonter Vokal
        // schwer zu erkennen
        add(Pair.of("pfau", "pfauen"))
        .

        // Feminine mit e-Plural (Duden 281)
        add(Pair.of("kraft", "kräfte"))
        .add(Pair.of("hand", "hände"))
        .add(Pair.of("not", "nöte"))
        .add(Pair.of("schnur", "schnüre"))
        .add(Pair.of("nuss", "nüsse"))
        .add(Pair.of("maus", "mäuse"))
        .add(Pair.of("wand", "wände"))
        .add(Pair.of("gans", "gänse"))
        .add(Pair.of("stadt", "städte"))
        .add(Pair.of("kunst", "künste"))
        .add(Pair.of("wurst", "würste"))
        .

        // Endungslose Feminine (Duden 281)
        add(Pair.of("tochter", "töchter"))
        .add(Pair.of("mutter", "mütter"))
        .
        // FIXME es gibt auch Muttern...!

        // Maskulina und Neutra mit n-Plural (Duden 281)
        add(Pair.of("fürst", "fürsten"))
        .add(Pair.of("prinz", "prinzen"))
        .add(Pair.of("held", "helden"))
        .add(Pair.of("narr", "narren"))
        .add(Pair.of("graf", "grafen"))
        .add(Pair.of("staat", "staaten"))
        .add(Pair.of("muskel", "muskeln"))
        .add(Pair.of("vetter", "vettern"))
        .add(Pair.of("konsul", "konsuln"))
        .add(Pair.of("schaft", "schaften"))
        .add(Pair.of("ohr", "ohren"))
        .add(Pair.of("auge", "augen"))
        .add(Pair.of("ende", "enden"))
        .add(Pair.of("interesse", "interessen"))
        .add(Pair.of("vagabund", "vagabunden"))
        .add(Pair.of("bär", "bären"))
        .add(Pair.of("soph", "sophen"))
        . // Fremdwort-Endung?
          // Siehe oben bzgl
          // Genitiv!
        add(Pair.of("bayer", "bayern"))
        .add(Pair.of("herr", "herren"))
        . // Duden 330

        // S4: deutsche Wörter mit konsonantischem Wortausgang
        // und s-Plural
        add(Pair.of("gedeck", "gedecke"))
        . // vgl. Duden 283
          // Ausnahme
        add(Pair.of("ldeck", "ldecken"))
        .add(Pair.of("ndeck", "ndecken"))
        .add(Pair.of("rdeck", "rdecken"))
        .add(Pair.of("deck", "decks"))
        .add(Pair.of("wrack", "wracks"))
        .add(Pair.of("haff", "haffs"))
        .

        // Maskulina und Neutra mit er-Plural
        add(Pair.of("geist", "geister"))
        .add(Pair.of("land", "länder"))
        .add(Pair.of("lied", "lieder"))
        .add(Pair.of("wald", "wälder"))
        .add(Pair.of("wurm", "würmer"))
        .add(Pair.of("strauch", "sträucher"))
        .add(Pair.of("kind", "kinder"))
        .add(Pair.of("bild", "bilder"))
        .add(Pair.of("brett", "bretter"))
        .add(Pair.of("feld", "felder"))
        .add(Pair.of("loch", "löcher"))
        .add(Pair.of("haus", "häuser"))
        .add(Pair.of("reichtum", "reichtümer"))
        .add(Pair.of("fass", "fässer"))
        .add(Pair.of("buch", "bücher"))
        .add(Pair.of("horn", "hörner"))
        .

        // Neutra mit e-Plural und Umlaut
        add(Pair.of("floß", "flöße"))
        .add(Pair.of("wasser", "wässer"))
        .
        // TODO Duden 345
        // Neutra mit endungslosem Plural und Umlaut
        add(Pair.of("kloster", "klöster"))
        .

        // Maskulina mit e-Plural ohne Umlaut(282)
        add(Pair.of("tag", "tage"))
        .add(Pair.of("huf", "hufe"))
        .add(Pair.of("hund", "hunde"))
        .add(Pair.of("lachs", "lachse"))
        .add(Pair.of("barsch", "barsche"))
        .add(Pair.of("mittwoch", "mittwoche"))
        .add(Pair.of("bar", "bars"))
        . // Bar -> Bars
        add(Pair.of("ar", "are"))
        .add(Pair.of("august", "auguste"))
        .add(Pair.of("kontrast", "kontraste"))
        .

        // Maskulina mit endunslosem Plural ohne Umlaut (282)
        add(Pair.of("balken", "balken"))
        .add(Pair.of("stapel", "stapel"))
        .add(Pair.of("stummel", "stummel"))
        .add(Pair.of("haufen", "haufen"))
        .add(Pair.of("computer", "computer"))
        .add(Pair.of("schwarz", "schwarz"))
        . // Duden 291
        add(Pair.of("onkel", "onkel"))
        . // Duden 296

        // Manche Fremdwörter haben die Pluralform ihrer
        // Herkunftssprache behalten, andere nicht (Duden 284)
        add(Pair.of("sauna", "saunas"))
        .add(Pair.of("thema", "themen"))
        .add(Pair.of("spirans", "spiranten"))
        . // Duden 286
        add(Pair.of("stimulans", "stimulantien"))
        . // Duden 286
        add(Pair.of("bus", "busse"))
        .add(Pair.of("dus", "di"))
        .add(Pair.of("fus", "fi"))
        .add(Pair.of("gus", "gi"))
        .add(Pair.of("sozius", "soziusse"))
        . // Duden 289
        add(Pair.of("zirkus", "zirkusse"))
        .add(Pair.of("zyklus", "zyklen"))
        .add(Pair.of("lus", "li"))
        . // Stimulus -> Stimuli
        add(Pair.of("rhythmus", "rhythmen"))
        .add(Pair.of("smus", "smen"))
        . // Organismus ->
          // Organismen
        add(Pair.of("mus", "mi"))
        .add(Pair.of("tenor", "tenöre"))
        .add(Pair.of("genus", "genera"))
        .add(Pair.of("nus", "ni"))
        . // Bonus -> Boni
        add(Pair.of("tempus", "tempora")).add(Pair.of("quus", "qui"))
        .add(Pair.of("rus", "ri"))
        .add(Pair.of("sus", "sus"))
        . // Kasus -> Kasus
        add(Pair.of("ritus", "riten")).add(Pair.of("vus", "vi"))
        .add(Pair.of("vus", "vi"))
        .add(Pair.of("xus", "xi"))
        .

        add(Pair.of("chtum", "chtümer"))
        .add(Pair.of("eum", "een"))
        . // Museum -> Museen
        add(Pair.of("ium", "ien"))
        . // Gremium -> Gremien
        add(Pair.of("ktum", "kta"))
        . // Abstraktum -> Abstrakta

        add(Pair.of("ndix", "ndizes"))
        . // Appendix ->
          // Appendizes
        add(Pair.of("dex", "dizes"))
        . // Codex -> Codizes
        add(Pair.of("til", "tile"))
        . // Ventil -> Ventile,
        // Projektil ->
        // Projektile
        add(Pair.of("gur", "guren")).add(Pair.of("ion", "ionen"))
        .add(Pair.of("prinzip", "prinzipien")).add(Pair.of("totem", "totems")). // Problem,
                                                                                // Theorem
        add(Pair.of("em", "eme")). // Problem, Theorem
        add(Pair.of("quotient", "quotienten")). // Quotient
        // (Duden 332)
        add(Pair.of("sextant", "sextanten")). // Sextant (Duden
        // 332)
        add(Pair.of("loge", "logen")). // Psychologe ->
        // Psychologen (Duden
        // 331)
        add(Pair.of("agoge", "agogen")). // Pädagoge ->
        // Pädagogen,
        add(Pair.of("leutnant", "leutnants")). // Duden 331
        // (Ausnahme
        // gegenüber
        // Demonstrant)
        // Synagoge -> Synagogen (Duden 331)
        build();

    AUSNAHMEENDEN_STAMMABWEICHUNGEN_KASUSFLEXION = new ImmutableSet.Builder<Pair<String, String>>()
        .add(Pair.of("aas", "aas"))
        .add(Pair.of("as", "ass"))
        . // Atlas -> Atlasses
        add(Pair.of("anis", "anis"))
        . // Anis -> Anises
        add(Pair.of("ees", "ees")).add(Pair.of("ies", "ies"))
        .add(Pair.of("oes", "oes")).add(Pair.of("es", "es"))
        .add(Pair.of("ais", "ais"))
        .add(Pair.of("eis", "eis"))
        .add(Pair.of("iis", "iis"))
        .add(Pair.of("ois", "ois"))
        .add(Pair.of("uis", "uis"))
        .add(Pair.of("is", "iss"))
        . // Geheimnis
          // ->
          // Geheimnisses,
          // ...
        add(Pair.of("oos", "oos"))
        .add(Pair.of("os", "oss"))
        . // Rhinozeros -> des
          // Rhinozerosses
        add(Pair.of("aus", "aus")).add(Pair.of("äus", "äus"))
        .add(Pair.of("eus", "eus")).add(Pair.of("us", "uss")). // Bus ->
        // des
        // Busses,
        // dem
        // Busse
        build();

    AUSNAHMEENDEN_MIT_ALTERNATIVEN_MASK_FLEXIONSKLASSEN = new ImmutableSet.Builder<Pair<String, EnumSet<SubstantivFlexionsklasse>>>()
        .add(Pair.of("buchstabe", // vgl. Duden 340
            EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_BUCHSTABE)))
        .add(Pair.of("fels", // vgl. Duden 339
            EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FELS)))
        // Herz/Kunstherz ist NEUTRUM - wird daher separat
        // behandelt!
        .add(
            Pair.of("haufen",
                EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(
            Pair.of("gedanke",
                EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(
            Pair.of("gefallen",
                EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(
            Pair.of("schaden",
                EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(Pair.of("glaube", // Glaube, Aberglaube
            EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(
            Pair.of("frieden",
                EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(
            Pair.of("name",
                EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(
            Pair.of("samen",
                EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(
            Pair.of("funken",
                EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(Pair.of("wille", // Wille, Unwille
            EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE)))
        .add(Pair.of("drache", // vgl. Duden 339
            EnumSet.of(SubstantivFlexionsklasse.GEMISCHT_TYP_DRACHE)))
        .add(Pair.of("et",
        // Magnet
            EnumSet.of(
            // Duden 330
                SubstantivFlexionsklasse.STARK_II, // des
                // Magnets
                // (eigentlich
                // des
                // *Planets
                // -
                // aber das ist anscheinend im Umbruch,
                // vgl. Duden 333)
                SubstantivFlexionsklasse.SCHWACH_IV // des
                // Magneten
                // /
                // Planeten
                ))).add(Pair.of("it",
        // Stalaktit
            EnumSet.of(
            // Duden 330
                SubstantivFlexionsklasse.STARK_II, // des
                // Stalaktits
                // (Duden 332)
                SubstantivFlexionsklasse.SCHWACH_IV // des
                // Stalaktiten
                ))).add(Pair.of("bauer", EnumSet.of(
        // Duden 330
            SubstantivFlexionsklasse.STARK_II, // des
            // Bauers
            SubstantivFlexionsklasse.SCHWACH_IV // des
            // Bauern
            ))).add(Pair.of("vetter", EnumSet.of(
        // Duden 330
            SubstantivFlexionsklasse.STARK_II // des
            // Vetters
            ))).build();
  }

  /**
   * Ob diese Substantiv-Wortform (NN oder NE oder TRUNC) <i>im Normalstil ohne
   * vorangehendes Artikelwort</i> stehen kann (z.B. "Schweiz" (Nom), kann im
   * Normalstil nicht ohne vorangehendes Artikelwort stehen
   * ("?Wir fahren in Schweiz"). Auch "Anna" (Gen) kann nicht ohne vorangehendes
   * Artikelwort stehen ("*Anna Hund"). "Annas" (Gen) hingegen kann ohne
   * vorangehendes Artikelwort stehen ("Annas Hund").
   */
  public static final String IM_NORMALSTIL_OHNE_ARTIKELWORT_MOEGLICH_KEY = "imNormalstilOhneArtikelwortMoeglich";

  /**
   * Ob diese Substantiv-Wortform (NN oder NE oder TRUNC) <i>im Telegrammstil
   * ohne vorangehendes Artikelwort</i> stehen kann (z.B. "Anna" (Gen) kann
   * nicht ohne vorangehendes Artikelwort stehen ("*Anna Hund"). "Annas" (Gen)
   * hingegen kann ohne vorangehendes Artikelwort stehen ("Annas Hund"). Auch
   * "Schweiz" kann im Telegrammstil ohne vorangehendes Artikelwort stehen
   * ("Raub in Schweiz aufgeklärt").
   */
  public static final String IM_TELEGRAMMSTIL_OHNE_ARTIKELWORT_MOEGLICH_KEY = "imTelegrammstilOhneArtikelwortMoeglich";

  /**
   * Ob diese Substantiv-Wortform (NN oder NE oder TRUNC) nach einem Artikelwort
   * stehen kann (z.B. "Anna" (Gen) kann nach einem Artikelwort stehen, nämlich
   * in "der Anna". "Annas" (Gen) hingegen, kann nicht nach einem Artikelwort
   * stehen ("*der Annas").
   */
  public static final String MIT_ARTIKELWORT_MOEGLICH_KEY = "mitArtikelwortMoeglich";

  public final static String MOEGLICHERWEISE_DAT_ODER_AKK_MIT_UNTERLASSENER_KASUSFLEXION_KEY = "moeglicherweiseDatOderAkkMitUnterlassenerKasusflexion";

  public SubstantivFlektierer() {
    super();
  }

  public Collection<IWordForm> pluraleTantum(final Lexeme lexeme,
      final String pos, final boolean lexemStehtMitArtikelAusserImTelegrammstil) {
    return stdPlural(lexeme, pos, lexemStehtMitArtikelAusserImTelegrammstil,
        lexeme.getCanonicalizedForm());
  }

  /**
   * Standard-Deklination eines Substantivs gemäß Duden
   *
   * @param lexeme
   *          <i>kein</i> Plurale Tantum!
   * @param lexemStehtMitArtikelAusserImTelegrammstil
   *          ob das Lexem in der Regel mit Artikel steht ("das Haus",
   *          "die Schweiz") oder nicht ("Deutschland")
   */
  public Collection<IWordForm> std(final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final FremdwortTyp fremdwortTyp,
      final boolean kommtEinemEigennameNaheDassGenitivSAuchEntfallenKann,
      final boolean personOderTier) {
    final Collection<IWordForm> res = new ArrayList<>(8);

    final boolean zaehlbar = SubstantivPronomenUtil.isZaehlbar(lexeme);

    final Collection<String> pluralAlternativen = zaehlbar ? plural(lexeme,
        fremdwortTyp, kommtEinemEigennameNaheDassGenitivSAuchEntfallenKann,
        personOderTier) : ImmutableList.<String> of();

    res.addAll(stdSingular(lexeme, pos,
        lexemStehtMitArtikelAusserImTelegrammstil, fremdwortTyp,
        kommtEinemEigennameNaheDassGenitivSAuchEntfallenKann, personOderTier,
        pluralAlternativen));

    pluralAlternativen.forEach(pluralAlternative -> {
      res.addAll(stdPlural(lexeme, pos,
          lexemStehtMitArtikelAusserImTelegrammstil, pluralAlternative));
    });

    return res;
  }

  private Collection<String> plural(final Lexeme lexeme,
      final FremdwortTyp fremdwortTyp, final boolean kommtEinemEigennameNahe,
      final boolean personOderTier) {
    final String singular = lexeme.getCanonicalizedForm();

    // Verwendet die "Grundregeln" G1, G2, G3 (im Duden 279)
    // und die "Zusatzregeln" Z1, Z2, Z3, Z4 (Duden 280)
    // sowie den Sonderfall S4 (Duden 281, 285)
    // Gibt es eine Ausnahme?
    final Collection<String> pluralausnahmeAlternativen = findPluralAusnahme(singular);
    if (!pluralausnahmeAlternativen.isEmpty()) {
      return pluralausnahmeAlternativen;
    }

    if (fremdwortTyp.isFremdwort()) {
      // Sonderfall S4: Fremdwörter mit konsonantischem Wortausgang
      // (der Hit -> die Hits)
      if (GermanUtil.hatKonsonantischenWortausgang(singular)
          && !GermanUtil.endetAufSLaut(singular)) { // *Bosss (Bosse)

        if (singular.endsWith("er")) {
          return ImmutableList.<String> of(singular); // (Ausnahme:)
          // Computer ->
          // Computer
        }

        // Sonderfall S3: Fremdwörter mit en-Plural ("or")
        if (SubstantivPronomenUtil.isMask(lexeme) && !kommtEinemEigennameNahe) { // nicht
                                                                                 // etwa
          // "die Nestoren",
          // sondern
          // "die Nestors"!
          if (GermanUtil.endetAufLateinischemOr(singular)) {
            if (fremdwortTyp.equals(FremdwortTyp.FREMDWORT_AM_ENDE_BETONT)) {
              return ImmutableList.of(singular + "e"); // Ma'jor
              // ->
              // Majore
            }

            return ImmutableList.of(singular + "en"); // Di'rektor
            // ->
            // Direktoren
          }

          // Duden 331
          if (StringUtil.endsWith(singular,
              FREMDE_MASK_WORTAUSGAENGE_PLURAL_BELEBT_MIT_EN) && personOderTier) {
            // Duden 331, Doktorand -> Doktoranden; Demonstrant ->
            // Demonstraten...
            return ImmutableList.of(singular + "en");
          }

          if (StringUtil.endsWith(singular, "et", "it")
              && !singular.toLowerCase().endsWith("hit")) { // Hit
            // ->
            // Hits!
            // siehe
            // unten
            // Planet -> Planeten, Stalaktik -> Stalaktiten
            return ImmutableList.<String> of(singular + "en");
          }
        }

        if (SubstantivPronomenUtil.isNeut(lexeme)
            && StringUtil.endsWith(singular, "ent", "om")) {
          // Talent -> Talente, Patent -> Patente,
          // Metronom -> Metronome
          return ImmutableList.<String> of(singular + "e");
        }

        return ImmutableList.<String> of(singular + "s");
      }

      // Sonderfall S3: Fremdwörter mit en-Plural ("a")
      if (SubstantivPronomenUtil.isFem(lexeme)
          && GermanUtil.isLateinADeklination(singular)) {
        return ImmutableList.of(singular.substring(0, singular.length() - 1)
            + "en"); // Firma
        // ->
        // Firmen
      }

    }

    // Zusatzregel Z1 (der Zeuge -> die Zeugen)
    if (SubstantivPronomenUtil.isMask(lexeme) && singular.endsWith("e")
        && !singular.endsWith("ee")) {
      return ImmutableList.<String> of(singular + "n");
    }

    // Zusatzregel Z2 (die Oma -> die Omas)
    if (GermanUtil.endetAufUnbetontenVollvokal(singular)) {
      return ImmutableList.<String> of(singular + "s");
    }

    // Zusatzregel Z3 (das Tabu -> die Tabus)
    if (GermanUtil.istFremdwortUndEndetAufBetontenVollvokal(singular,
        fremdwortTyp.equals(FremdwortTyp.FREMDWORT_AM_ENDE_BETONT))) {
      // "Ausnahme" in Z3:
      if (SubstantivPronomenUtil.isFem(lexeme)) {
        if (StringUtil.endsWith(singular, "ie", "ee")) {
          return ImmutableList.<String> of(singular + "n"); // Familie-n
        }

        if (singular.endsWith("ei")) {
          return ImmutableList.<String> of(singular + "en"); // Datei-en
        }
      }

      return ImmutableList.<String> of(singular + "s");
    }

    // Zusatzregel Z4 (das Blümlein -> die Blümlein)
    if (singular.endsWith("lein") && singular.length() >= 6) { // nicht
      // Olein :-)
      if (singular.endsWith("klein")) {
        // Gänseklein
        log.warn("Unsichere Pluralbildung (Endung auf -klein):" + singular);
      } else {
        return ImmutableList.<String> of(singular);
      }
    }

    // Regel G1
    if (!SubstantivPronomenUtil.isFem(lexeme)) {
      // also Maskulina und Neutra

      // Regel G3
      if (folgeESollteFuerPluralOderSchwacheDeklinationEntfallen(singular)) {
        if (SubstantivPronomenUtil.isNeut(lexeme)) {
          return ImmutableList.<String> of(singular);
        }

        final int amEndeNichtFuerUmlautBeruecksichtigen = amEndeNichtFuerUmlautBeruecksichtigen(singular);

        final String basisFuerUmlautung = singular.substring(0,
            singular.length() - amEndeNichtFuerUmlautBeruecksichtigen);
        final String stammRest = singular.substring(singular.length()
            - amEndeNichtFuerUmlautBeruecksichtigen, singular.length());

        final String umgelauteteBasis = GermanUtil.ggfUmlauten(
            basisFuerUmlautung, fremdwortTyp);
        if (!basisFuerUmlautung.equals(umgelauteteBasis)) {
          log.warn("Unsichere Pluralbildung (Maskulium endungslos / Umlaut):"
              + umgelauteteBasis + stammRest);
        }
        return ImmutableList.<String> of(umgelauteteBasis + stammRest);
      }

      // siehe Duden 282
      if (singular.endsWith("nis")) {
        return ImmutableList.<String> of(singular + "se"); // Geheimnis-se
      }

      if (SubstantivPronomenUtil.isNeut(lexeme)) {
        return ImmutableList.<String> of(singular + "e"); // Schicksale
      }

      final String umgelautet = GermanUtil.ggfUmlauten(singular, fremdwortTyp);
      if (!singular.equals(umgelautet)) {
        log.warn("Unsichere Pluralbildung (Maskulium mit -e / Umlaut):"
            + umgelautet + "e");
      }
      return ImmutableList.<String> of(umgelautet + "e");
    }

    // siehe Duden 282
    if (singular.endsWith("nis")) {
      return ImmutableList.<String> of(singular + "se"); // Erkenntnis-se
    }
    if (singular.endsWith("sal")) {
      return ImmutableList.<String> of(singular + "e"); // Trübsal-e
    }

    // Regel G2
    if (folgeESollteFuerPluralOderSchwacheDeklinationEntfallen(singular)) {
      return ImmutableList.<String> of(singular + "n");
    }

    return ImmutableList.<String> of(singular + "en");
  }

  /**
   * Ermittelt die SubstantivFlexionsklasse(n) für den Singular eines Nomens
   * (normal, oder Eigenname), wenn es <i>auf ein Artikelwort folgt</i>. Das
   * Lexeme muss im Singular vorkommen (kein Plurale Tantum) (und ein Genus
   * besitzen).
   * <p>
   * Naturgemäß tut sich diese Funktion schwer bei den Flexionsklassen II und
   * IV.
   */
  private Collection<SubstantivFlexionsklasse> flexionsklassenSingularMitArtikelwort(
      final Lexeme lexeme, final boolean eigenname,
      final FremdwortTyp fremdwortTyp, final boolean personOderTier,
      final Collection<String> plurale) {
    // Verwendet die Regeln K1, K2 und K4 (Duden 299)

    final Genus genus = FeatureStringConverter.toGenus(lexeme
        .getStringFeatureValue("genus"));

    // Regel K1, die Zahl, der Zahl, ...
    if (genus == FEMININUM) {
      return ImmutableList.of(SubstantivFlexionsklasse.ENDUNGSLOS_I);
    }

    // Maskulinum oder Neutrum
    final String nomSg = lexeme.getCanonicalizedForm();
    if (genus == MASKULINUM) {
      if (eigenname) { // Eigennamen sind (auch mit Artikel) nie schwach
        // dekliniert!
        return ImmutableList.of(SubstantivFlexionsklasse.STARK_II);
      }

      final Collection<SubstantivFlexionsklasse> ausnahmeAlternativen = findAusnahmeMaskFlexionsklasse(nomSg);
      if (!ausnahmeAlternativen.isEmpty()) {
        return ausnahmeAlternativen;
      }

      if (GermanUtil.endetAufLateinischemOr(nomSg)
          && fremdwortTyp.equals(FremdwortTyp.FREMDWORT_NICHT_AM_ENDE_BETONT)) {
        // der Direktor, des Direktors (Duden 330)
        return ImmutableList.of(SubstantivFlexionsklasse.STARK_II);
      }

      if (nomSg.endsWith("ul")) {
        // der Konsul, des Konsuls (Duden 330)
        return ImmutableList.of(SubstantivFlexionsklasse.STARK_II);
      }

      if (personOderTier && nomSg.endsWith("e") && !nomSg.endsWith("ee")) {
        // Regel K4.1, Duden 328
        // der Zeuge, des Zeugen
        return ImmutableList.of(SubstantivFlexionsklasse.SCHWACH_IV);
      }

      // TODO Mischung von stark und schwach Duden 337-340

      final boolean enOderNPlural = plurale.contains(nomSg + "en")
          || plurale.contains(nomSg + "n");
      final boolean sPlural = plurale.contains(nomSg + "s");

      if (personOderTier && enOderNPlural && !sPlural) { // Vermutung.
        // Denn: der
        // Embryo -> die
        // Embryonen,
        // die Embryos,
        // Aber nur des
        // Embryos,
        // nicht *des
        // Eymbryonen
        // Regel K4.2, Duden 330
        return ImmutableList.of(SubstantivFlexionsklasse.SCHWACH_IV);
      }

      if (enOderNPlural && !sPlural && StringUtil.endsWith(nomSg, "ant", "ent")) {
        // Sextant (die Sextanten) -> des Sextanten
        // Quotient (die Quotienten) -> des Quotienten.
        // Nicht sicher -
        // vgl. Duden 332
        return ImmutableList.of(SubstantivFlexionsklasse.SCHWACH_IV);
      }

      // Regel K2, der Raum, des Raum(e)s, ...
      return ImmutableList.of(SubstantivFlexionsklasse.STARK_II);
    }

    if (nomSg.toLowerCase().endsWith("herz")) {
      // Duden 340
      return ImmutableList.of(SubstantivFlexionsklasse.GEMISCHT_TYP_HERZ);
    }

    // Regel K2, das Auto, des Autos, ...
    return ImmutableList.of(SubstantivFlexionsklasse.STARK_II);
  }

  /**
   * @param kommtEinemEigennameNaheDassGenitivSAuchEntfallenKann
   *          ob das Wort einem Eigennamen nahe kommt, so dass das Genitiv-s im
   *          Singular auch entfallen kann
   */
  private Collection<IWordForm> stdSingular(final Lexeme lexeme,
      final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final FremdwortTyp fremdwortTyp,
      final boolean kommtEinemEigennameNaheDassGenitivSAuchEntfallenKann,
      final boolean personOderTier, final Collection<String> plurale) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final String stammWennOhneEndung = lexeme.getCanonicalizedForm();

    final String stammWennMitEndung;
    // Wird der Stamm ausnahmsweise verändert (Geheimnis -> den
    // GeheimnisSen)?
    final String ausnahmeStamm = findAusname(lexeme.getCanonicalizedForm(),
        AUSNAHMEENDEN_STAMMABWEICHUNGEN_KASUSFLEXION);
    if (ausnahmeStamm == null) {
      stammWennMitEndung = lexeme.getCanonicalizedForm();
    } else {
      stammWennMitEndung = ausnahmeStamm;
    }

    res.addAll(stdGenSg(lexeme, pos, lexemStehtMitArtikelAusserImTelegrammstil,
        fremdwortTyp, stammWennOhneEndung, stammWennMitEndung,
        kommtEinemEigennameNaheDassGenitivSAuchEntfallenKann, personOderTier,
        plurale));

    final Collection<IWordForm> datAlternativen = stdDatSg(lexeme, pos,
        lexemStehtMitArtikelAusserImTelegrammstil, stammWennOhneEndung,
        stammWennMitEndung, fremdwortTyp, personOderTier, plurale);
    res.addAll(datAlternativen);

    final Collection<IWordForm> akkAlternativen = stdAkkSg(lexeme, pos,
        lexemStehtMitArtikelAusserImTelegrammstil, stammWennOhneEndung,
        stammWennMitEndung, fremdwortTyp, personOderTier, plurale);
    res.addAll(akkAlternativen);

    res.addAll(stdNomSg(lexeme, pos, lexemStehtMitArtikelAusserImTelegrammstil,
        fremdwortTyp, personOderTier, datAlternativen, akkAlternativen, plurale));

    return res.build();
  }

  private ImmutableCollection<IWordForm> stdNomSg(final Lexeme lexeme,
      final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final FremdwortTyp fremdwortTyp, final boolean personOderTier,
      final Collection<IWordForm> datAlternativen,
      final Collection<IWordForm> akkAlternativen,
      final Collection<String> plurale) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final boolean substantiviertesAdjektiv = StringFeatureLogicUtil
        .stringToBoolean(lexeme
            .getStringFeatureValue(SubstantivPronomenUtil.SUBSTANTIVIERTES_ADJEKTIV_KEY));

    final boolean eigenname = pos.equals(GermanPOS.NE.toString());

    final Collection<SubstantivFlexionsklasse> flexKlassen = flexionsklassenSingularMitArtikelwort(
        lexeme, eigenname, fremdwortTyp, personOderTier, plurale);

    if (CollectionUtil.containsAny(flexKlassen,
        SubstantivFlexionsklasse.GEMISCHT_TYP_FRIEDE,
        SubstantivFlexionsklasse.GEMISCHT_TYP_DRACHE)) {
      res.add(stdNomSg(
          lexeme,
          pos, // Friede, Drache
          substantiviertesAdjektiv, lexemStehtMitArtikelAusserImTelegrammstil,
          stammOhneN(lexeme.getCanonicalizedForm()), datAlternativen,
          akkAlternativen));
      res.add(stdNomSg(
          lexeme,
          pos, // Frieden, Drachen
          substantiviertesAdjektiv, lexemStehtMitArtikelAusserImTelegrammstil,
          stammMitNOderEN(lexeme.getCanonicalizedForm()), datAlternativen,
          akkAlternativen));
    } else if (flexKlassen.contains(SubstantivFlexionsklasse.GEMISCHT_TYP_FELS)) {
      res.add(stdNomSg(
          lexeme,
          pos, // Fels
          substantiviertesAdjektiv, lexemStehtMitArtikelAusserImTelegrammstil,
          stammOhneEn(lexeme.getCanonicalizedForm()), datAlternativen,
          akkAlternativen));
      res.add(stdNomSg(
          lexeme,
          pos, // Felsen
          substantiviertesAdjektiv, lexemStehtMitArtikelAusserImTelegrammstil,
          stammMitNOderEN(lexeme.getCanonicalizedForm()), datAlternativen,
          akkAlternativen));
    } else {
      // Normalfall: Anna, Frau, Hund, Haus, Peter, Buchstabe, Herz
      res.add(stdNomSg(lexeme, pos, substantiviertesAdjektiv,
          lexemStehtMitArtikelAusserImTelegrammstil,
          lexeme.getCanonicalizedForm(), datAlternativen, akkAlternativen));
    }

    return res.build();
  }

  private Wortform stdNomSg(final Lexeme lexeme, final String pos,
      final boolean substantiviertesAdjektiv,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String nomSg, final Collection<IWordForm> datAlternativen,
      final Collection<IWordForm> akkAlternativen) {
    final boolean datUnterschiedlichVonNom = !containWordform(datAlternativen,
        nomSg);
    final boolean akkUnterschiedlichVonNom = !containWordform(akkAlternativen,
        nomSg);

    final boolean nomMoeglicherweiseDatOderAkkMitUnterlassenerKasusflexion = !substantiviertesAdjektiv
        && (datUnterschiedlichVonNom || akkUnterschiedlichVonNom);
    // "mit Elefant" ist ok, aber "mit Verwandter (m)" (substantiviertes
    // Adjektiv)
    // geht nicht, dann muss es flektiert sein, also "mit Verwandtem"

    final Artikelwortbezug artikelwortbezug = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    final Wortform nomSgWortform = SubstantivFlektierer
        .buildSubstantivWortform(
            lexeme,
            pos,
            KasusInfo
                .nomNomen(nomMoeglicherweiseDatOderAkkMitUnterlassenerKasusflexion),
            SINGULAR, artikelwortbezug, nomSg);
    return nomSgWortform;
  }

  private static boolean containWordform(final Collection<IWordForm> wordForms,
      final String wordForm) {
    for (final IWordForm oneOfThem : wordForms) {
      if (oneOfThem.getString().equals(wordForm)) {
        return true;
      }
    }

    return false;
  }

  private Collection<IWordForm> stdGenSg(final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final FremdwortTyp fremdwortTyp, final String stammWennOhneEndung,
      final String stammWennMitEndung,
      final boolean kommtEinemEigennameNaheDassGenitivSEntfallenKann,
      final boolean personOderTier, final Collection<String> plurale) {
    final boolean eigenname = pos.equals(GermanPOS.NE.toString());

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final Collection<SubstantivFlexionsklasse> flexKlassen = flexionsklassenSingularMitArtikelwort(
        lexeme, eigenname, fremdwortTyp, personOderTier, plurale);

    if (flexKlassen.contains(SubstantivFlexionsklasse.ENDUNGSLOS_I)) {
      res.add(stdGenSgEndungslos(lexeme, pos,
          lexemStehtMitArtikelAusserImTelegrammstil, stammWennOhneEndung,
          eigenname));
    }

    boolean formAufSOhneArtikelwortBereitsHinzugefuegt;
    if (flexKlassen.contains(SubstantivFlexionsklasse.STARK_II)
        || SubstantivFlexionsklasse.anyGemscht(flexKlassen)) {
      final GenitivSgStarkOderGemischtResult genitivStarkOderGemischtResult = stdGenSgStarkOderGemischt(
          lexeme, pos, lexemStehtMitArtikelAusserImTelegrammstil, fremdwortTyp,
          stammWennOhneEndung, stammWennMitEndung,
          kommtEinemEigennameNaheDassGenitivSEntfallenKann, eigenname,
          flexKlassen);
      res.addAll(genitivStarkOderGemischtResult.getGenitives());
      formAufSOhneArtikelwortBereitsHinzugefuegt = genitivStarkOderGemischtResult
          .isEnthaeltFormAufSOhneArtikelwort();
    } else {
      formAufSOhneArtikelwortBereitsHinzugefuegt = false;
    }

    if (CollectionUtil.containsAny(flexKlassen,
        SubstantivFlexionsklasse.SCHWACH_IV,
        SubstantivFlexionsklasse.GEMISCHT_TYP_BUCHSTABE)) {
      final boolean gemischtTypBuchstabe = CollectionUtil.containsAny(
          flexKlassen, SubstantivFlexionsklasse.GEMISCHT_TYP_BUCHSTABE);

      res.add(stdGenSgSchwach(
          lexeme,
          pos,
          lexemStehtMitArtikelAusserImTelegrammstil,
          stammMitNOderEnFallsGemischt(stammWennMitEndung, gemischtTypBuchstabe)));
    }

    final boolean nnDasWieEinEigennameGebrauchtWird = eigenname ? false
        : StringFeatureLogicUtil
            .stringToBoolean(lexeme
                .getStringFeatureValue(SubstantivPronomenUtil.NN_WIE_EIN_EIGENNAME_GEBRAUCHT_KEY));

    if (eigenname || nnDasWieEinEigennameGebrauchtWird) {
      res.addAll(stdGenSgEigennamendeklination(lexeme, pos,
          lexemStehtMitArtikelAusserImTelegrammstil, stammWennOhneEndung,
          stammWennMitEndung, formAufSOhneArtikelwortBereitsHinzugefuegt));
    }

    return res.build();
  }

  private GenitivSgStarkOderGemischtResult stdGenSgStarkOderGemischt(
      final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final FremdwortTyp fremdwortTyp, final String stammWennOhneEndung,
      final String stammWennMitEndung,
      final boolean kommtEinemEigennameNaheDassGenitivSEntfallenKann,
      final boolean eigenname,
      final Collection<SubstantivFlexionsklasse> flexKlassen) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    boolean formAufSOhneArtikelwortBereitsHinzugefuegt;
    final boolean gemischt = SubstantivFlexionsklasse.anyGemscht(flexKlassen);

    final boolean eigennameOderAehnlichesDassGenitivSEntfallenKann = kommtEinemEigennameNaheDassGenitivSEntfallenKann
        || // des
           // Peter(s),
           // des
           // Montag(s)
        CollectionUtil.containsAny(flexKlassen,
            SubstantivFlexionsklasse.GEMISCHT_TYP_DRACHE, // des
            // Drachen(s)
            SubstantivFlexionsklasse.GEMISCHT_TYP_BUCHSTABE, // des
            // Buchstaben(s)
            SubstantivFlexionsklasse.GEMISCHT_TYP_FELS); // des
    // Felsen(s)

    final GenitivSgStarkOderGemischtResult genitivStarkOderGemischtResult = stdGenSgStark(
        lexeme, pos, fremdwortTyp, // Haus, Peter, Frieden, Felsen,
        lexemStehtMitArtikelAusserImTelegrammstil, // Haus,
        stammMitNOderEnFallsGemischt(stammWennOhneEndung, gemischt),
        // Herzen (!)
        stammMitNOderEnFallsGemischt(stammWennMitEndung, gemischt), // Peter,
        // Frieden,
        // Felsen, Herzen (!)
        eigennameOderAehnlichesDassGenitivSEntfallenKann, eigenname);

    res.addAll(genitivStarkOderGemischtResult.getGenitives());
    formAufSOhneArtikelwortBereitsHinzugefuegt = genitivStarkOderGemischtResult
        .isEnthaeltFormAufSOhneArtikelwort();

    if (CollectionUtil.containsAny(flexKlassen,
        SubstantivFlexionsklasse.GEMISCHT_TYP_FELS,
        SubstantivFlexionsklasse.GEMISCHT_TYP_HERZ)) {
      // Duden 339, 340
      // des Fels, des Felses, des Herzes, des Kunstherzes

      final boolean fels = flexKlassen
          .contains(SubstantivFlexionsklasse.GEMISCHT_TYP_FELS);

      final GenitivSgStarkOderGemischtResult genitivStarkResultFuerStammOhneEn = stdGenSgStark(
          lexeme, pos, fremdwortTyp, // Fels, Herz
          lexemStehtMitArtikelAusserImTelegrammstil, // Fels, Herz
          stammOhneEn(stammWennOhneEndung), // eigennameOderAehnlichesDassGenitivSEntfallenKann
          stammOhneEn(stammWennMitEndung), // -> des Fels (nicht aber
          // *des Herz), Duden
          fels, // 340
          eigenname);

      res.addAll(genitivStarkResultFuerStammOhneEn.getGenitives());
      formAufSOhneArtikelwortBereitsHinzugefuegt = // Wert von oben
      // (für
      // Felsen(s),
      // Herzen(s))
      // überschrieben
      // (für Felses,
      // *Herzes)
      genitivStarkResultFuerStammOhneEn.isEnthaeltFormAufSOhneArtikelwort();

    }

    return new GenitivSgStarkOderGemischtResult(res.build(),
        formAufSOhneArtikelwortBereitsHinzugefuegt);
  }

  /**
   * Lässt das Wort wie es ist - außer <code>gemischt</code> wäre
   * <code>true</code>. Falls <code>gemischt</code> <code>true</code> ist, dann
   * stellt diese Methode sicher, dass der Rückgabewert mit einem "n" (oder mit
   * "en") endet.
   * <p>
   * Haus ergibt also Haus, Friede und Frieden hingegeben führen beide zu
   * Frieden, Fels und Felsen führen beide zu Felsen.
   * <p>
   * (Dies ist notwendig für die gemischten Deklinationen.)
   */
  private String stammMitNOderEnFallsGemischt(final String stamm,
      final boolean gemischt) {
    if (!gemischt) {
      return stamm;
    }

    return stammMitNOderEN(stamm);
  }

  /**
   * Diese Methode stellt sicher, dass der Rückgabewert mit einem "n" (oder mit
   * "en") endet.
   * <p>
   * Friede und Frieden führen also beide zu Frieden, Fels und Felsen führen
   * beide zu Felsen.
   * <p>
   * (Dies ist notwendig für die gemischten Deklinationen.)
   */
  private String stammMitNOderEN(final String stamm) {
    if (stamm.endsWith("n")) {
      return stamm;
    }

    if (GermanUtil.endetAufSLaut(stamm)) {
      // Fels -> Felsen, Herz -> Herzen
      return stamm + "en";
    }

    return stamm + "n";
  }

  /**
   * Falls das Wort mit "en" endet, wird dies entfernt.
   */
  private String stammOhneEn(final String stamm) {
    if (stamm.length() <= 2 || !stamm.endsWith("en")) {
      return stamm;
    }

    return stamm.substring(0, stamm.length() - 2);
  }

  /**
   * Falls das Wort mit "n" endet, wird dies entfernt.
   */
  private String stammOhneN(final String stamm) {
    if (stamm.length() <= 1 || !stamm.endsWith("n")) {
      return stamm;
    }

    return stamm.substring(0, stamm.length() - 1);
  }

  private GenitivSgStarkOderGemischtResult stdGenSgStark(final Lexeme lexeme,
      final String pos, final FremdwortTyp fremdwortTyp,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String stammWennOhneEndung, final String stammWennMitEndung,
      final boolean eigennameOderAehnlichesDassGenitivSEntfallenKann,
      final boolean eigenname) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
    boolean enthaeltFormAufSOhneArtikelwort = false;
    boolean endungsloseFormBereitsHinzugefügt = false;

    final Artikelwortbezug artikelwortbezugLexem = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    if (GermanUtil.endetAufSLaut(stammWennOhneEndung)) {
      // Duden 302 Endung -s NICHT möglich
      if (fremdwortTyp.equals(FremdwortTyp.FREMDWORT_NICHT_AM_ENDE_BETONT)) {
        // Zirkus -> des Zirkus (endungslos), Duden 302
        res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos, // s
                                                                          // ist
                                                                          // ja
                                                                          // schon
                                                                          // da
            KasusInfo.GEN_S, SINGULAR, // des
            artikelwortbezugLexem, // Zirkus,
            // des
            // Atlas
            stammWennOhneEndung));
        endungsloseFormBereitsHinzugefügt = true;
      }
    } else {
      // endet *nicht* auf s-Laut -> Endung -s möglich
      // des Raums
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.GEN_S, SINGULAR, artikelwortbezugLexem, // des Raums,
          // des alten
          // Roms,
          // Roms
          stammWennMitEndung + "s"));
      enthaeltFormAufSOhneArtikelwort = true;
    }

    if (genitivendungEsErlaubtBeiStarkerDeklination(stammWennOhneEndung,
        fremdwortTyp)) {
      // des Raumes, des Busses
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.GEN_S, SINGULAR, artikelwortbezugLexem, stammWennMitEndung
              + "es"));
    }
    if (eigenname || eigennameOderAehnlichesDassGenitivSEntfallenKann) {
      // Monatsnamen, Wochentage, ...
      // "des Peter" (neben "des Peters")
      if (!endungsloseFormBereitsHinzugefügt) {
        final KasusInfo genitivTypEndungslos = GermanUtil
            .endetAufSLaut(stammWennOhneEndung) ? KasusInfo.GEN_S
            : KasusInfo.GEN_OHNE_S_UND_R;

        res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
            genitivTypEndungslos, SINGULAR,
            Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUCH_IM_TELEGRAMMSTIL,
            stammWennOhneEndung));
      }
    }
    return new GenitivSgStarkOderGemischtResult(res.build(),
        enthaeltFormAufSOhneArtikelwort);
  }

  private Collection<Wortform> stdGenSgEigennamendeklination(
      final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String stammWennOhneEndung, final String stammWennMitEndung,
      boolean formAufSOhneArtikelwortBereitsHinzugefuegt) {

    final ImmutableList.Builder<Wortform> res = new ImmutableList.Builder<>();
    final Genus genus = FeatureStringConverter.toGenus(lexeme
        .getStringFeatureValue("genus"));

    final Artikelwortbezug artikelwortbezugLexem = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    final Artikelwortbezug artikelwortBezug = genus == FEMININUM ? Artikelwortbezug.ZWINGEND_OHNE_ARTIKELWORT
        :
        // bei femininen Eigennamen zwingend ohne
        // Artikelwort!
        // "*der Annas"
        artikelwortbezugLexem; // durchaus "des Hannes'"

    if (GermanUtil.endetAufSLaut(stammWennOhneEndung)) {
      // Iris'
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos, // s ist
                                                                        // ja
                                                                        // schon
                                                                        // da
          KasusInfo.GEN_S, SINGULAR, artikelwortBezug, stammWennMitEndung));
      // FIXME -> Grammatik: Apostroph danach! "Iris'"!

      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos, // s
          // ist
          // ja
          // schon
          // da
          KasusInfo.GEN_S, SINGULAR, artikelwortBezug, stammWennMitEndung
              + "ens")); // Marxens
    } else {
      if (!formAufSOhneArtikelwortBereitsHinzugefuegt) {
        // Annas, Mutters
        res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
            KasusInfo.GEN_S, SINGULAR, artikelwortBezug, stammWennMitEndung
                + "s"));
        formAufSOhneArtikelwortBereitsHinzugefuegt = true;
      }
    }

    return res.build();
  }

  private Wortform stdGenSgEndungslos(final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String stammWennOhneEndung, final boolean eigenname) {
    final Artikelwortbezug artikelwortbezugLexem = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    final Artikelwortbezug artikelwortbezugFuerGattungsdeklinationGenitiv = eigenname ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUCH_IM_TELEGRAMMSTIL
        : // der
        // Anna
        // Haus,
        // *Anna
        // Haus
        artikelwortbezugLexem; // Mutter,
    // der
    // Mutter;
    // Zahl,
    // der
    // Zahl

    return SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
        KasusInfo.GEN_OHNE_S_UND_R, SINGULAR,
        artikelwortbezugFuerGattungsdeklinationGenitiv, stammWennOhneEndung);
  }

  private Wortform stdGenSgSchwach(final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String stammWennMitEndung) {
    final Artikelwortbezug artikelwortbezug = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    if (folgeESollteFuerPluralOderSchwacheDeklinationEntfallen(stammWennMitEndung)
        || stammWennMitEndung.toLowerCase().endsWith("herr")) { // Ausnahme
      // "des Herrn"
      // (Duden 330)
      // des Zeuge-n, des Bauer-n
      return SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.GEN_OHNE_S_UND_R, SINGULAR, artikelwortbezug,
          stammWennMitEndung + "n");
    }

    // des Prinz-en
    return SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
        KasusInfo.GEN_OHNE_S_UND_R, SINGULAR, artikelwortbezug,
        stammWennMitEndung + "en");
  }

  private boolean genitivendungEsErlaubtBeiStarkerDeklination(
      final String nomSg, final FremdwortTyp fremdwortTyp) {
    if (nomSg.endsWith("e")) {// ee-es nicht möglich, und ich denke, e-es
      // ebenfalls nicht
      // (*See-es), *Auge-es
      // Duden 305
      return false;
    }

    if (fremdwortTyp.isFremdwort() && GermanUtil.endetAufVollvokal(nomSg)) { // -es
      // bei
      // Fremdwörtern
      // auf Vokal
      // verboten
      // (*Zoo-es),
      // Duden 305
      return false;
    }

    if (GermanUtil.endetUnbetontMitSilbenreimElEnEndEmEr(nomSg)) { // Duden
      // 306
      return false;
    }

    if (StringUtil.endsWith(nomSg, // Duden
        // 306
        "lein", "ing")) {
      return false;
    }

    return true;
  }

  private Collection<IWordForm> stdDatSg(final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String stammWennOhneEndung, final String stammWennMitEndung,
      final FremdwortTyp fremdwortTyp, final boolean personOderTier,
      final Collection<String> plurale) {
    final boolean eigenname = pos.equals(GermanPOS.NE.toString());

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final Collection<SubstantivFlexionsklasse> flexKlassen = flexionsklassenSingularMitArtikelwort(
        lexeme, eigenname, fremdwortTyp, personOderTier, plurale);

    if (flexKlassen.contains(SubstantivFlexionsklasse.SCHWACH_IV)) {
      res.addAll(stdDatSgSchwachAuchEigennamen(lexeme, pos,
          lexemStehtMitArtikelAusserImTelegrammstil, stammWennOhneEndung,
          stammWennMitEndung, eigenname));
    }

    if (flexKlassen.contains(SubstantivFlexionsklasse.ENDUNGSLOS_I)
        || flexKlassen.contains(SubstantivFlexionsklasse.STARK_II)
        || SubstantivFlexionsklasse.anyGemscht(flexKlassen)) {
      // Zahl, der Zahl, Anna, der Anna, Mutter, der Mutter, Raum, dem
      // Raum, dem Buchstaben, dem Fels, dem Felsen, dem Herz, dem Herzen,
      // ...
      res.addAll(stdDatEndungslosStarkOderGemischt(lexeme, pos,
          lexemStehtMitArtikelAusserImTelegrammstil, stammWennOhneEndung,
          stammWennMitEndung, fremdwortTyp, eigenname, flexKlassen));
    }

    return res.build();
  }

  private ImmutableCollection<IWordForm> stdDatEndungslosStarkOderGemischt(
      final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String stammWennOhneEndung, final String stammWennMitEndung,
      final FremdwortTyp fremdwortTyp, final boolean eigenname,
      final Collection<SubstantivFlexionsklasse> flexKlassen) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final boolean gemischt = SubstantivFlexionsklasse.anyGemscht(flexKlassen);

    final Artikelwortbezug artikelwortbezugLexem = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
        KasusInfo.DAT, SINGULAR, artikelwortbezugLexem,
        stammMitNOderEnFallsGemischt(stammWennOhneEndung, gemischt))); // Haus,
    // Frieden,
    // Felsen,
    // Herzen

    if (CollectionUtil.containsAny(flexKlassen,
        SubstantivFlexionsklasse.GEMISCHT_TYP_FELS,
        SubstantivFlexionsklasse.GEMISCHT_TYP_HERZ)) {
      // Duden 339, 340
      // dem Fels, dem Herz, dem Kunstherz
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.DAT, SINGULAR, artikelwortbezugLexem,
          stammOhneEn(stammWennOhneEndung))); // Fels, Herz, Kunstherz
    }

    if (flexKlassen.contains(SubstantivFlexionsklasse.STARK_II)) {
      if (!StringUtil.endsWith(stammWennMitEndung, "el")) { // *dem
        // Segele
        // dem Raume
        if (genitivendungEsErlaubtBeiStarkerDeklination(stammWennOhneEndung,
            fremdwortTyp)) {

          // Bei einem (männlichen) EIGENNAMEN kann das Dativ-e
          // nur
          // dann stehen, wenn
          // vor dem Eigennamen ein Artikel steht! DENN:
          // Hat der Eigenname KEINE Artikel, gilt die
          // EIGENNAMEN-DEKLINATION -
          // und diese HAT KEIN DATIV-e!
          final Artikelwortbezug artikelwortBezugFuerDativE = eigenname ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUCH_IM_TELEGRAMMSTIL
              : artikelwortbezugLexem;

          // Dudem 317, Faktor I
          res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
              KasusInfo.DAT, SINGULAR, artikelwortBezugFuerDativE,
              stammWennMitEndung + "e"));
        }
      }
    }

    CollectionUtil.addIfNotNull(
        res,
        alterDatOderAkkAufEnOderN(lexeme, pos, eigenname, gemischt,
            stammWennMitEndung, KasusInfo.DAT));

    return res.build();
  }

  /**
   * Mutter -> Muttern, Gellert -> Gellerten, Menzel -> Menzeln (Duden 327)
   */
  private IWordForm alterDatOderAkkAufEnOderN(final Lexeme lexeme,
      final String pos, final boolean eigenname,
      final boolean gemischtFlektiert, final String stammWennMitEndung,
      final KasusInfo kasus) {
    if (gemischtFlektiert) {
      return null;
    }

    final boolean nnDasWieEinEigennameGebrauchtWird = eigenname ? false
        : StringFeatureLogicUtil
            .stringToBoolean(lexeme
                .getStringFeatureValue(SubstantivPronomenUtil.NN_WIE_EIN_EIGENNAME_GEBRAUCHT_KEY));
    if (!eigenname && !nnDasWieEinEigennameGebrauchtWird) {
      return null;
    }

    if (folgeESollteFuerPluralOderSchwacheDeklinationEntfallen(stammWennMitEndung)) {
      return SubstantivFlektierer.buildSubstantivWortform(lexeme, pos, kasus,
          SINGULAR, Artikelwortbezug.ZWINGEND_OHNE_ARTIKELWORT,
          stammWennMitEndung + "n"); // Muttern, Menzeln
    }

    return SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
        KasusInfo.DAT, SINGULAR, Artikelwortbezug.ZWINGEND_OHNE_ARTIKELWORT,
        stammWennMitEndung + "en"); // Gellerten
  }

  private ImmutableCollection<IWordForm> stdDatSgSchwachAuchEigennamen(
      final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String stammWennOhneEndung, final String stammWennMitEndung,
      final boolean eigenname) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final Artikelwortbezug artikelwortbezugLexem = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    final Artikelwortbezug artikelwortbezugFuerGattungsdeklinationDativSchwach = eigenname ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUCH_IM_TELEGRAMMSTIL
        : artikelwortbezugLexem;

    if (folgeESollteFuerPluralOderSchwacheDeklinationEntfallen(stammWennMitEndung)
        || stammWennMitEndung.toLowerCase().endsWith("herr")) { // Ausnahme
      // "dem Herrn"
      // (Duden 330)
      // dem Zeuge-n, dem Bauer-n
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.DAT, SINGULAR,
          artikelwortbezugFuerGattungsdeklinationDativSchwach,
          stammWennMitEndung + "n"));
    } else {
      // dem Prinz-en
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.DAT, SINGULAR,
          artikelwortbezugFuerGattungsdeklinationDativSchwach,
          stammWennMitEndung + "en"));
    }
    if (eigenname) {
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.DAT, SINGULAR, Artikelwortbezug.ZWINGEND_OHNE_ARTIKELWORT,
          stammWennOhneEndung));
    }

    return res.build();
  }

  private Collection<IWordForm> stdAkkSg(final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String stammWennOhneEndung, final String stammWennMitEndung,
      final FremdwortTyp fremdwortTyp, final boolean personOderTier,
      final Collection<String> plurale) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final boolean eigenname = pos.equals(GermanPOS.NE.toString());

    final Collection<SubstantivFlexionsklasse> flexKlassen = flexionsklassenSingularMitArtikelwort(
        lexeme, eigenname, fremdwortTyp, personOderTier, plurale);

    if (flexKlassen.contains(SubstantivFlexionsklasse.SCHWACH_IV)) {
      res.addAll(stdAkkSgSchwachAuchEigennamen(lexeme, pos,
          lexemStehtMitArtikelAusserImTelegrammstil, stammWennOhneEndung,
          stammWennMitEndung, eigenname));

    }

    if (flexKlassen.contains(SubstantivFlexionsklasse.ENDUNGSLOS_I)
        || flexKlassen.contains(SubstantivFlexionsklasse.STARK_II)
        || SubstantivFlexionsklasse.anyGemscht(flexKlassen)) {
      // Mutter, die Mutter, Anna, die Anna, Raum, den Raum, das Herz, den
      // Drachen, den Frieden
      res.addAll(stdAkkEndungslosStarkOderGemischt(lexeme, pos,
          lexemStehtMitArtikelAusserImTelegrammstil, eigenname,
          stammWennOhneEndung, stammWennMitEndung, flexKlassen));
    }

    return res.build();
  }

  private ImmutableCollection<IWordForm> stdAkkEndungslosStarkOderGemischt(
      final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final boolean eigenname, final String stammWennOhneEndung,
      final String stammWennMitEndung,
      final Collection<SubstantivFlexionsklasse> flexKlassen) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final boolean gemischt = SubstantivFlexionsklasse.anyGemscht(flexKlassen);

    final Artikelwortbezug artikelwortbezug = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
        KasusInfo.AKK, SINGULAR, artikelwortbezug,
        stammMitNOderEnFallsGemischt(stammWennOhneEndung, gemischt))); // Haus,

    if (CollectionUtil.containsAny(flexKlassen,
        SubstantivFlexionsklasse.GEMISCHT_TYP_FELS,
        SubstantivFlexionsklasse.GEMISCHT_TYP_HERZ)) {
      // Duden 339, 340
      // dem Fels, dem Herz, dem Kunstherz
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.AKK, SINGULAR, artikelwortbezug,
          stammOhneEn(stammWennOhneEndung))); // Fels, Herz, Kunstherz
    }

    CollectionUtil.addIfNotNull(
        res,
        alterDatOderAkkAufEnOderN(lexeme, pos, eigenname, gemischt,
            stammWennMitEndung, KasusInfo.AKK));

    return res.build();
  }

  private Collection<IWordForm> stdAkkSgSchwachAuchEigennamen(
      final Lexeme lexeme, final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String stammWennOhneEndung, final String stammWennMitEndung,
      final boolean eigenname) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final Artikelwortbezug artikelwortbezugLexem = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    final Artikelwortbezug artikelwortbezugFuerGattungsdeklinationAkkSchwach = eigenname ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUCH_IM_TELEGRAMMSTIL
        : artikelwortbezugLexem;
    if (folgeESollteFuerPluralOderSchwacheDeklinationEntfallen(stammWennMitEndung)
        || stammWennMitEndung.toLowerCase().endsWith("herr")) { // Ausnahme
      // "den Herrn"
      // (Duden 330)
      // den Zeuge-n, den Bauer-n
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.AKK, SINGULAR,
          artikelwortbezugFuerGattungsdeklinationAkkSchwach, stammWennMitEndung
              + "n"));
    } else {
      // den Prinz-en
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.AKK, SINGULAR,
          artikelwortbezugFuerGattungsdeklinationAkkSchwach, stammWennMitEndung
              + "en"));
    }
    if (eigenname) {
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.AKK, SINGULAR, Artikelwortbezug.ZWINGEND_OHNE_ARTIKELWORT,
          stammWennOhneEndung));
    }

    return res.build();
  }

  private Collection<IWordForm> stdPlural(final Lexeme lexeme,
      final String pos,
      final boolean lexemStehtMitArtikelAusserImTelegrammstil,
      final String plural) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final Artikelwortbezug artikelwortbezug = lexemStehtMitArtikelAusserImTelegrammstil ? Artikelwortbezug.ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL
        : Artikelwortbezug.IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT;

    // NOMINATIV
    final Wortform nomPl = SubstantivFlektierer.buildSubstantivWortform(lexeme,
        pos, KasusInfo.NOM_NICHT_ETWA_DAT_AKK_MIT_UNTERL_KASUSFLEX, // nur im
        // Singular
        // möglich.
        // Nicht
        // "mit Kinder*",
        // sondern "mit Kindern".
        PLURAL, artikelwortbezug, plural);

    res.add(nomPl);

    // Regel K5 (Duden 299): SubstantivFlexionsklasse für Plural
    // GENITIV
    res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
        SubstantivPronomenUtil.guessGenitivNomenInfoPl(plural), // Eine
        // Endung
        // wie
        // bei
        // "Kinder"
        // gilt
        // wohl
        // als "r"-Endung - obwohl
        // bereits der Nominativ Plural die Endung trägt!
        PLURAL, artikelwortbezug, plural));

    // DATIV
    // Vgl. Duden 341
    final Boolean endetUnbetontAufEElEr = GermanUtil
        .endetUnbetontMitSilbenreimEELEr(plural);

    if (Boolean.TRUE.equals(endetUnbetontAufEElEr)
        || endetUnbetontAufEElEr == null) {
      // den Trümmern
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.DAT, PLURAL, artikelwortbezug, plural + "n"));
    }

    if (Boolean.FALSE.equals(endetUnbetontAufEElEr)
        || // den Sachen
        endetUnbetontAufEElEr == null
        || plural.toLowerCase().endsWith("länder")) { // auch die
      // Ausnahme:
      // "aus aller Herren Länder"
      res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
          KasusInfo.DAT, PLURAL, artikelwortbezug, plural));
    }

    // AKKUSATIV
    res.add(SubstantivFlektierer.buildSubstantivWortform(lexeme, pos,
        KasusInfo.AKK, PLURAL, artikelwortbezug, plural));

    return res.build();
  }

  /**
   * Bei Wörtern wie "Garten" darf man nicht versuchen, das 'e' umzulauten,
   * sondern das 'a'!
   *
   * @return Wieviele Zeichen am Ende nicht für die Umlautung berücksichtigt
   *         werden dürfen z.B. 2 bei <code>Gart<b>en</b></code>
   */
  private int amEndeNichtFuerUmlautBeruecksichtigen(
      final String singularMaskGemaessG3) {
    if (StringUtil.endsWith(singularMaskGemaessG3, "el", "em", "en", "er")) {
      return 2;
    }

    if (StringUtil.endsWith(singularMaskGemaessG3, "e")) {
      return 1;
    }

    return 0;
  }

  public static Wortform buildSubstantivWortform(final Lexeme lexeme,
      final String pos, final KasusInfo kasusInfo, final Numerus numerus,
      final Artikelwortbezug artikelwortbezug, final String string) {

    final FeatureStructure features = FeatureStructure
        .fromStringValues(ImmutableMap
            .<String, String> builder()
            .put("kasus",
                FeatureStringConverter.toFeatureString(kasusInfo.getKasus()))
            .put("numerus", FeatureStringConverter.toFeatureString(numerus))
            .put(
                GermanUtil.GENITIV_SICHTBAR_DURCH_R_KEY,
                StringFeatureLogicUtil.booleanToString(kasusInfo
                    .isGenitivSichtbarDurchR()))
            .put(
                GermanUtil.GENITIV_SICHTBAR_DURCH_S_KEY,
                StringFeatureLogicUtil.booleanToString(kasusInfo
                    .isGenitivSichtbarDurchS()))
            .put(
                IM_NORMALSTIL_OHNE_ARTIKELWORT_MOEGLICH_KEY,
                StringFeatureLogicUtil.booleanToString(artikelwortbezug
                    .isImNormalstilOhneArtikelwortMoeglich()))
            .put(
                IM_TELEGRAMMSTIL_OHNE_ARTIKELWORT_MOEGLICH_KEY,
                StringFeatureLogicUtil.booleanToString(artikelwortbezug
                    .isImTelegrammstilOhneArtikelwortMoeglich()))
            .put(
                MIT_ARTIKELWORT_MOEGLICH_KEY,
                StringFeatureLogicUtil.booleanToString(artikelwortbezug
                    .isMitArtikelwortMoeglich()))
            .put(
                MOEGLICHERWEISE_DAT_ODER_AKK_MIT_UNTERLASSENER_KASUSFLEXION_KEY,
                StringFeatureLogicUtil.booleanToString(kasusInfo
                    .isMglwDatOderAkkMitUnterlassenerKasusflexion())).build());

    final Wortform res = new Wortform(lexeme, pos, string, features,
        NothingInParticularSemantics.INSTANCE);

    return res;
  }

  /**
   * @return leer, falls keine Plural-Ausnahme hierfür bekannt ist
   */
  private static Collection<String> findPluralAusnahme(final String singular) {
    final Collection<String> pluralausnahmenMitMehrerenAlternativen = findAusnahmealternativen(
        singular, AUSNAHMEENDEN_MIT_ALTERNATIVEN_PLURAL);

    if (!pluralausnahmenMitMehrerenAlternativen.isEmpty()) {
      return pluralausnahmenMitMehrerenAlternativen;
    }

    final String pluralAusname = findAusname(singular, AUSNAHMEENDEN_PLURAL);
    if (pluralAusname != null) {
      return ImmutableList.of(pluralAusname);
    }

    return ImmutableList.of();
  }

  /**
   * @return <code>null</code>, falls keine Ausnahme hierfür bekannt ist
   */
  private static String findAusname(final String input,
      final ImmutableCollection<Pair<String, String>> ausnahmeEnden) {
    final String singularToLowerCase = input.toLowerCase();

    for (final Pair<String, String> ausnahmeende : ausnahmeEnden) {
      final String ende = ausnahmeende.first();
      if (singularToLowerCase.endsWith(ende)) {
        final String neuesEnde = ausnahmeende.second();

        return tauscheEndeAus(input, ende, neuesEnde);
      }
    }

    return null;
  }

  /**
   * @return leer, falls keine Ausnahme hierfür bekannt ist
   */
  private static Collection<String> findAusnahmealternativen(
      final String input,
      final ImmutableCollection<Pair<String, String[]>> ausnahmeEndenMitAlternativen) {
    final String singularToLowerCase = input.toLowerCase();

    for (final Pair<String, String[]> ausnahmeende : ausnahmeEndenMitAlternativen) {
      final String ende = ausnahmeende.first();
      if (singularToLowerCase.endsWith(ende)) {
        final ImmutableList.Builder<String> plurale = ImmutableList.builder();

        for (final String neuesEndeAlternative : ausnahmeende.second()) {
          plurale.add(tauscheEndeAus(input, ende, neuesEndeAlternative));
        }
        return plurale.build();
      }
    }

    return ImmutableList.of();
  }

  /**
   * @return leer, falls keine Ausnahme hierfür bekannt ist
   */
  private static Collection<SubstantivFlexionsklasse> findAusnahmeMaskFlexionsklasse(
      final String singular) {
    final String singularToLowerCase = singular.toLowerCase();
    final Collection<String> singularAlternativen = new LinkedList<>();
    singularAlternativen.add(singularToLowerCase);
    if (singularToLowerCase.endsWith("e")) {
      // z.B. Friede -> auch nach Frieden suchen!
      singularAlternativen.add(singularToLowerCase + "n");
    }
    if (singularToLowerCase.endsWith("n") && singularToLowerCase.length() > 1) {
      // z.B. Frieden -> auch nach Friede suchen!
      singularAlternativen.add(singularToLowerCase.substring(0,
          singularToLowerCase.length() - 1));
    }

    final ImmutableSet.Builder<SubstantivFlexionsklasse> res = ImmutableSet
        .builder();

    for (final String singularAlternative : singularAlternativen) {
      for (final Pair<String, EnumSet<SubstantivFlexionsklasse>> ausnahmeende : AUSNAHMEENDEN_MIT_ALTERNATIVEN_MASK_FLEXIONSKLASSEN) {
        final String ende = ausnahmeende.first();
        if (singularAlternative.endsWith(ende)) {
          res.addAll(ausnahmeende.second());
          break; // INNERE SCHLEIFE abbrechen - für DIESE ALTERNATIVE
          // die weiteren Ausnahmen nicht mehr durchsuchen (es gilt
          // die erste Ausnahme!) -- wenn es aber mehrere alternative
          // "Singulare"
          // gibt, DANN für jeden Singular die Flexionsklassen
          // ermitteln (also ÄUSSERE SCHLEIFE FORTSETZEN).
        }
      }
    }

    return res.build();
  }

  private static String tauscheEndeAus(final String basis,
      final String altesEnde, final String neuesEnde) {
    if (basis.equals(altesEnde)) {
      // singular == ende -> Groß/Kleinschreibung übernehmen
      if (Character.isUpperCase(basis.charAt(0))) {
        return Character.toUpperCase(neuesEnde.charAt(0))
            + neuesEnde.substring(1);
      }
    }

    final String pluralKlein = basis.substring(0,
        basis.length() - altesEnde.length())
        + neuesEnde;
    return Character.toUpperCase(pluralKlein.charAt(0))
        + pluralKlein.substring(1);
  }

  private static boolean folgeESollteFuerPluralOderSchwacheDeklinationEntfallen(
      final String singular) {
    /*
     * Duden 279 (Regel G3): Wenn ein Substantiv im Singular auf unbetontes -e,
     * -el, -em, -en oder -er ausgeht, wird das e der folgenden Endung getilgt.
     */

    if (singular.endsWith("ee")) {
      return true; // dreifaches e gibt's ja nicht!
    }

    // analoge *betonte* Endungen ausschließen
    if (StringUtil.endsWith(singular, "eel", "eem", "een", "eer", "iel" // (Ziel)
    )) {
      return false;
    }

    if (StringUtil.endsWith(singular, "e", "el", "em", "en", "er")) {
      return true;
    }

    return false;
  }

  @Immutable
  @ThreadSafe
  private static final class GenitivSgStarkOderGemischtResult {
    private final ImmutableCollection<IWordForm> genitives;
    private final boolean enthaeltFormAufSOhneArtikelwort;

    public GenitivSgStarkOderGemischtResult(
        final ImmutableCollection<IWordForm> genitives,
        final boolean enthaeltFormAufSOhneArtikelwort) {
      super();
      this.genitives = genitives;
      this.enthaeltFormAufSOhneArtikelwort = enthaeltFormAufSOhneArtikelwort;
    }

    public ImmutableCollection<IWordForm> getGenitives() {
      return genitives;
    }

    public boolean isEnthaeltFormAufSOhneArtikelwort() {
      return enthaeltFormAufSOhneArtikelwort;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
          + (enthaeltFormAufSOhneArtikelwort ? 1231 : 1237);
      result = prime * result
          + ((genitives == null) ? 0 : genitives.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final GenitivSgStarkOderGemischtResult other = (GenitivSgStarkOderGemischtResult) obj;
      if (enthaeltFormAufSOhneArtikelwort != other.enthaeltFormAufSOhneArtikelwort) {
        return false;
      }
      if (genitives == null) {
        if (other.genitives != null) {
          return false;
        }
      } else if (!genitives.equals(other.genitives)) {
        return false;
      }
      return true;
    }
  }

}
