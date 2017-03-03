package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.collection.Pair;
import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.valenz.Valenz;
import de.nb.federkiel.deutsch.grammatik.valenz.Valenzvariante;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;
import de.nb.federkiel.lexikon.Wortform;
import de.nb.federkiel.string.StringUtil;

/**
 * Kann Flexionsformen von Subtantiven bilden.
 *
 * @author nbudzyn 2010
 */
@Immutable
@ThreadSafe
public final class VerbFlektierer implements IFlektierer {
  public static final String TYP = "Verb";

  public static final String INDIKATIV = "ind";

  public static final String KONJUNKTIV = "konj";

  public static final String PRAESENS = "praes";

  public static final String PRAETERITUM = "praet";

  /**
   * (Enden von) Ausnahmen. Hier müssen insbesondere alle (Enden von) starken Vollverben und von
   * unregelmäßigen schwachen Vollverben aufgeführt werden (außer <i>wissen</i> - das wird separat
   * behandelt) - außerdem alle Verben, die ihr Perfekt mit sein bilden!
   * <p>
   * Die Reihenfolge ist relevant! Das erste "sticht"!
   * <p>
   * Siehe Duden 704
   */
  private static final ImmutableCollection<Basisformen> AUSNAHMEENDEN =
      ImmutableList.of(
          // @formatter:off
          new Basisformen("backen",
          // du
          alt("bäckst", "backst"),
          // er
          alt("bäckt", "backt"), "back", // !
          // ich
          alt("backte", "buk"),
          // dass ich
          alt("backte", "büke"),
          // ich habe
          "gebacken", Perfektbildung.HABEN),
          new Basisformen("befehlen", "befiehlst", "befiehlt", "befiehl", "befahl",
              alt("beföhle", "befähle"), "befohlen", Perfektbildung.HABEN),
          new Basisformen("befleißigen", "befleißt", "befleißt", alt("befleiß", "befleiße"),
              "befliss", "beflisse", "beflissen", Perfektbildung.HABEN),
          new Basisformen("beginnen", "beginnst", "beginnt", alt("beginn", "beginne"), "begann",
              alt("begänne", "begönne"), "begonnen", Perfektbildung.HABEN),
          new Basisformen("beißen", "beißt", "beißt", alt("beiß", "beiße"), "biss", "bisse",
              "gebissen", Perfektbildung.HABEN),
          new Basisformen("bergen", "birgst", "birgt", "birg", "barg", "bärge", "geborgen",
              Perfektbildung.HABEN),
          new Basisformen("bersten", alt("birst", "berstest"), alt("birst", "berstet"), "birst",
              "barst", "bärste", "geborsten", Perfektbildung.SEIN),
          // FIXME teils VERANLASSEN, teils räumlich bewegen!
          new Basisformen("bewegen", "bewegst", "bewegt", alt("beweg", "bewege"),
              alt("bewegte", "bewog"), alt("bewegte", "bewöge"), alt("bewegt", "bewogen"),
              Perfektbildung.HABEN),
          new Basisformen("biegen", "biegst", "biegt", alt("bieg", "biege"), "bog", "böge",
              "gebogen", Perfektbildung.HABEN),
          new Basisformen("bieten", "bietest", "bietet", alt("biete", "biet"), "bot", "böte",
              "geboten", Perfektbildung.HABEN),
          new Basisformen("binden", "bindest", "bindet", alt("binde", "bind"), "band", "bände",
              "gebunden", Perfektbildung.HABEN),
          new Basisformen("bitten", "bittest", "bittet", alt("bitte", "bitt"), "bat", "bäte",
              "gebeten", Perfektbildung.HABEN),
          new Basisformen("blasen", "bläst", "bläst", alt("blas", "blase"), "blies", "bliese",
              "geblasen", Perfektbildung.HABEN),
          new Basisformen("bleiben", "bleibst", "bleibt", alt("bleib", "bleibe"), "blieb", "bliebe",
              "geblieben", Perfektbildung.SEIN),
          new Basisformen("braten", "brätst", "brät", alt("brat", "brate"), "briet", "briete",
              "gebraten", Perfektbildung.HABEN),
          new Basisformen("brechen", "brichst", "bricht", "brich", "brach", "bräche", "gebrochen",
              Perfektbildung.SEIN),
          new Basisformen("brennen", "brennst", "brennt", alt("brenn", "brenne"), "brannte",
              "brennte", "gebrannt", Perfektbildung.HABEN),
          new Basisformen("bringen", "bringst", "bringt", alt("bring", "bringe"), "brachte",
              "brächte", "gebracht", Perfektbildung.HABEN),
          new Basisformen("denken", "denkst", "denkt", alt("denke", "denk"), "dachte", "dächte",
              "gedacht", Perfektbildung.HABEN),
          new Basisformen("dingen", "dingst", "dingt", alt("ding", "dinge"), alt("dingte", "dang"),
              alt("dingte", "dänge"), alt("gedungen", "gedingt"), Perfektbildung.HABEN),
          new Basisformen("dreschen", alt("drischst", "dreschst"), alt("drischt", "drescht"),
              "drisch", alt("drosch", "drasch", "dreschte"), alt("drösche", "dräsche", "dreschte"),
              "gedroschen", Perfektbildung.HABEN),
          new Basisformen("dringen", "dringst", "dringt", alt("dring", "dringe"), "drang", "dränge",
              "gedrungen", Perfektbildung.SEIN),
          new Basisformen("dünken", alt("dünkst", "deuchst"), alt("dünkt", "deucht"),
              alt("dünk", "dünke", "deuch", "deuche"), alt("dünkte", "deuchte"),
              alt("dünkte", "deuchte"), alt("gedünkt", "gedeucht"), Perfektbildung.HABEN),
          new Basisformen("empfangen", "empfängst", "empfängt", alt("empfang", "empfange"),
              "empfing", "empfinge", "empfangen", Perfektbildung.HABEN),
          new Basisformen("empfehlen", "empfiehlst", "empfiehlt", "empfiehl", "empfahl",
              alt("empföhle", "empfähle"), "empfohlen", Perfektbildung.HABEN),
          new Basisformen("empfingen", "empfindest", "empfindet", alt("empfinde", "empfind"),
              "empfand", "empfände", "empfunden", Perfektbildung.HABEN),
          new Basisformen("erbleichen", "erbleichst", "erbleicht", alt("erbleiche", "erbleich"),
              "erblich", "erbliche", "erblichen", Perfektbildung.SEIN),
          new Basisformen("erkiesen", "erkiest", "erkiest", alt("erkies", "erkiese"), "erkor",
              "erköre", "erkoren", Perfektbildung.HABEN),
          new Basisformen("erküre", "erkürst", "erkürt", alt("erkür", "erküre"), "erkürte",
              "erkürte", "erkürt", Perfektbildung.HABEN),
          new Basisformen("erlöschen", "erlischst", "erlischt", "erlisch", "erlosch", "erlösche",
              "erloschen", Perfektbildung.SEIN),
          new Basisformen("erschrecken", "erschrickst", "erschrickt", "erschrick", "erschrak",
              "erschräke", "erschrocken", Perfektbildung.SEIN),
          new Basisformen("essen", "isst", "isst", "iss", "aß", "äße", "gegessen",
              Perfektbildung.HABEN),
          new Basisformen("fahren", "fährst", "fährt", alt("fahr", "fahre"), "fuhr", "führe",
              "gefahren", Perfektbildung.SEIN),
          new Basisformen("fallen", "fällst", "fällt", alt("fall", "falle"), "fiel", "fiele",
              "gefallen", Perfektbildung.SEIN),
          new Basisformen("fangen", "fängst", "fängt", alt("fang", "fange"), "fing", "finge",
              "gefangen", Perfektbildung.HABEN),
          new Basisformen("fechten", alt("fichtst", "fechtest"), "ficht", "ficht", "focht",
              "föchte", "gefochten", Perfektbildung.HABEN),
          new Basisformen("finden", "findest", "findet", alt("finde", "find"), "fand", "fände",
              "gefunden", Perfektbildung.HABEN),
          new Basisformen("flechten", alt("flichtst", "flechtest"), "flicht", "flicht", "flocht",
              "flöchte", "geflochten", Perfektbildung.HABEN),
          new Basisformen("fliegen", "fliegst", "fliegt", alt("flieg", "fliege"), "flog", "flöge",
              "geflogen", Perfektbildung.SEIN),
          new Basisformen("fliehen", "fliehst", "flieht", alt("flieh", "fliehe"), "floh", "flöhe",
              "geflohen", Perfektbildung.SEIN),
          new Basisformen("fließen", "fließt", "fließt", alt("fließ", "fließe"), "floss", "flösse",
              "geflossen", Perfektbildung.SEIN),
          new Basisformen("fragen", alt("fragst", "frägst"), alt("fragt", "frägt"),
              alt("frag", "frage"), alt("fragte", "frug"), alt("fragte", "früge"), "gefragt",
              Perfektbildung.HABEN),
          new Basisformen("fressen", "frisst", "frisst", "friss", "fraß", "fräße", "gefressen",
              Perfektbildung.HABEN),
          new Basisformen("frieren", "frierst", "friert", alt("frier", "friere"), "fror", "fröre",
              "gefroren", Perfektbildung.HABEN),
          new Basisformen("gären", "gärst", "gärt", alt("gär", "gäre"), alt("gor", "gärte"),
              alt("göre", "gärte"), alt("gegoren", "gegärt"), Perfektbildung.SEIN),
          new Basisformen("gebären", "gebierst", "gebiert", "gebier", "gebar", "gebäre",
              alt("geboren", "*"), Perfektbildung.HABEN),
          new Basisformen("geben", "gibst", "gibt", "gib", "gab", "gäbe", "gegeben",
              Perfektbildung.HABEN),
          new Basisformen("gedeihen", "gedeihst", "gedeiht", alt("gedeih", "gedeihe"), "gedieh",
              "gediehe", "gediehen", Perfektbildung.SEIN),
          new Basisformen("gehen", "gehst", "geht", alt("geh", "gehe"), "ging", "ginge", "gegangen",
              Perfektbildung.SEIN),
          new Basisformen("gelingen", "gelingst", "gelingt", alt("geling", "gelinge"), "gelang",
              "gelänge", "gelungen", Perfektbildung.SEIN),
          new Basisformen("gelten", "giltst", "gilt", "gilt", "galt", alt("gälte", "gölte"),
              "gegolten", Perfektbildung.HABEN),
          new Basisformen("genesen", "genest", "genest", alt("genese", "genes"), "genas", "genäse",
              "genesen", Perfektbildung.SEIN),
          new Basisformen("genießen", "genießt", "genießt", alt("genieß", "genieße"), "genoss",
              "genösse", "genossen", Perfektbildung.HABEN),
          new Basisformen("geschehen", "geschiehst", "geschieht", alt("gescheh", "geschehe"),
              "geschah", "geschähe", "geschehen", Perfektbildung.SEIN),
          new Basisformen("gewinnen", "gewinnst", "gewinnt", alt("gewinn", "gewinne"), "gewann",
              alt("gewänne", "gewönne"), "gewonnen", Perfektbildung.HABEN),
          new Basisformen("gießen", "gießt", "gießt", alt("gieß", "gieße"), "goss", "gösse",
              "gegossen", Perfektbildung.HABEN),
          new Basisformen("gleichen", "gleichst", "gleicht", alt("gleiche", "gleich"), "glich",
              "gliche", "geglichen", Perfektbildung.HABEN),
          new Basisformen("gleiten", "gleitest", "gleitet", alt("gleite", "gleit"), "glitt",
              "glitte", "geglitten", Perfektbildung.SEIN),
          new Basisformen("glimmen", "glimmst", "glimmt", alt("glimm", "glimme"),
              alt("glomm", "glimmte"), alt("glömme", "glimmte"), alt("geglommen", "geglimmt"),
              Perfektbildung.HABEN),
          new Basisformen("graben", "gräbst", "gräbt", alt("grab", "grabe"), "grub", "grübe",
              "gegraben", Perfektbildung.HABEN),
          new Basisformen("greifen", "greifst", "greift", alt("greif", "greife"), "griff", "griffe",
              "gegriffen", Perfektbildung.HABEN),
          new Basisformen("halten", "hältst", "hält", alt("halt", "halte"), "hielt", "hielte",
              "gehalten", Perfektbildung.HABEN),
          new Basisformen("hängen", "hängst", "hängt", alt("häng", "hänge"), alt("hing, hängte"),
              alt("hinge", "hängte"), alt("gehangen", "gehängt"), Perfektbildung.HABEN),
          new Basisformen("hauen", "haust", "haut", alt("hau", "haue"), alt("hieb", "haute"),
              alt("hiebe", "haute"), "gehauen", Perfektbildung.HABEN),
          new Basisformen("heben", "hebst", "hebt", alt("heb", "hebe"), alt("hob", "hub"),
              alt("höbe", "hübe"), "gehoben", Perfektbildung.HABEN),
          new Basisformen("heißen", "heißt", "heißt", alt("heiße", "heiß"), "hieß", "hieße",
              "geheißen", Perfektbildung.HABEN),
          new Basisformen("helfen", "hilfst", "hilft", "hilf", "half", alt("hülfe", "hälfe"),
              "geholfen", Perfektbildung.HABEN),
          new Basisformen("kennen", "kennst", "kennt", alt("kenne", "kenn"), "kannte", "kennte",
              "gekannt", Perfektbildung.HABEN),
          new Basisformen("klimmen", "klimmst", "klimmt", alt("klimm", "klimme"),
              alt("klomm", "klimmte"), alt("klömme", "klimmte"), "geklommen", Perfektbildung.SEIN),
          new Basisformen("klingen", "klingst", "klingt", alt("kling", "klinge"), "klang", "klänge",
              "geklungen", Perfektbildung.HABEN),
          new Basisformen("kneifen", "kniffst", "kniff", alt("kneif", "kneife"), "kniff", "kniffe",
              "gekniffen", Perfektbildung.HABEN),
          new Basisformen("knien", "kniest", "kniet", "knie", "kniete", "kniete", "gekniet",
              Perfektbildung.HABEN),
          new Basisformen("kommen", "kommst", "kommt", alt("komm"), "kam", "käme", "gekommen",
              Perfektbildung.SEIN),
          new Basisformen("kriechen", "kriechst", "kriecht", alt("kriech", "krieche"), "kroch",
              "kröche", "gekrochen", Perfektbildung.SEIN),
          // erküren steht oben und hat also Vorrang
          new Basisformen("küren", "kürst", "kürt", alt("kür", "küre"), alt("kürte", "kor"),
              alt("kürte", "köre"), "gekoren", Perfektbildung.HABEN),
          new Basisformen("laden", alt("lädst", "ladest"), alt("lädt", "ladet"), alt("lad", "lade"),
              "lud", "lüde", "geladen", Perfektbildung.HABEN),
          new Basisformen("lassen", "lässt", "lässt", alt("lass", "lasse"), "ließ", "ließe",
              "gelassen", Perfektbildung.HABEN),
          new Basisformen("laufen", "läufst", "läuft", alt("lauf", "laufe"), "lief", "liefe",
              "gelaufen", Perfektbildung.SEIN),
          new Basisformen("leiden", "leidest", "leidet", alt("leide", "leid"), "litt", "litte",
              "gelitten", Perfektbildung.HABEN),
          new Basisformen("leihen", "leihst", "leiht", alt("leih", "leihe"), "lieh", "liehe",
              "geliehen", Perfektbildung.HABEN),
          new Basisformen("lesen", "liest", "liest", "lies", "las", "läse", "gelesen",
              Perfektbildung.HABEN),
          new Basisformen("liegen", "liegst", "liegt", alt("leg", "lege"), "lag", "läge", "gelegen",
              Perfektbildung.HABEN),
          new Basisformen("lügen", "lügst", "lügt", alt("lüg", "lüge"), "log", "löge", "gelogen",
              Perfektbildung.HABEN),
          new Basisformen("mahlen", "mahlst", "mahlt", alt("mahl", "mahle"), "mahlte", "mahlte",
              "gemahlen", Perfektbildung.HABEN),
          new Basisformen("meiden", "meidest", "meidet", alt("meide", "meid"), "mied", "miede",
              "gemieden", Perfektbildung.HABEN),
          new Basisformen("melken", "melkst", "melkt", alt("melk", "melke"), "molk", "mölke",
              "gemolken", Perfektbildung.HABEN),
          new Basisformen("messen", "misst", "misst", "miss", "maß", "mäße", "gemessen",
              Perfektbildung.HABEN),
          new Basisformen("misslingen", "misslingst", "misslingt", alt("missling", "misslinge"),
              "misslang", "misslänge", "misslungen", Perfektbildung.SEIN),
          new Basisformen("nehmen", "nimmst", "nimmt", "nimm", "nahm", "nähme", "genommen",
              Perfektbildung.HABEN),
          new Basisformen("pfeifen", "pfeifst", "pfeift", alt("pfeif", "pfeife"), "pfiff", "pfiffe",
              "gepfiffen", Perfektbildung.HABEN),
          new Basisformen("pflegen", "pflegst", "pflegt", alt("pfleg", "pflege"),
              alt("pflegte", // seine
                  // Mutter
                  "pflog"), // der Ruhe
              // dass er seine Mutter
              alt("pflegte",
              // dass er der Ruhe
                  "pflöge"),
              // er hat seine Mutter
              alt("gepflegt",
              // er hat der Ruhe
                  "gepflogen"),
              Perfektbildung.HABEN),
          // TODO ... (noch 6 1/2 Seiten!)
          new Basisformen("nennen", "nennst", "nennt", alt("nenn", "nenne"), "nannte", "nennte", // FIXME
              // stimmt
              // das?
              "genannt", Perfektbildung.HABEN),
          // TODO ...
          // TODO schneiden
          // TODO...
          new Basisformen("sehen", "siehst", "sieht", alt("sieh", "siehe"), "sah", "sähe",
              "gesehen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("einschlafen", "schläfst|ein", // FIXME
              "schläft|ein", alt("schlaf|ein", "schlafe|ein"), "schlief|ein", "schliefe|ein",
              "eingeschlafen", Perfektbildung.SEIN), // IST
          // eingeschlafen,
          // aber
          // HAT
          // geschlafen!
          new Basisformen("schlafen", "schläfst", "schläft", alt("schlaf", "schlafe"), "schlief",
              "schliefe", "geschlafen", Perfektbildung.HABEN),
          new Basisformen("schlagen", "schlägst", "schlägt", alt("schlag", "schlage"), "schlug",
              "schlüge", "geschlagen", Perfektbildung.HABEN),

      // TODO ...
          new Basisformen("schließen", "schließt", "schließt", alt("schließ", "schließe"),
              "schloss", "schlösse", "geschlossen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("sinnen", "sinnst", "sinnt", alt("sinn", "sinne"), "sann",
              alt("sänne", "sönne"), "gesonnen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("sprechen", "sprichst", "spricht", "sprich", "sprach", "spräche",
              "gesprochen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("stehlen", "stiehlst", "stiehlt", "stiehl", "stahl",
              alt("stähle", "stöhle"), "gestohlen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("sterben", "stirbst", "stirbt", "stirb", "starb", "stürbe",
              alt("gestorben", "†"), Perfektbildung.SEIN),
          // TODO...
          new Basisformen("tragen", "trägst", "trägt", "trag", "trug", "trüge", "getragen",
              Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("treffen", "triffst", "trifft", "triff", "traf", "träfe", "getroffen",
              Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("wachsen", "wächst", "wächst", alt("wachs", "wachse"), "wuchs", "wüchse",
              "gewachsen", Perfektbildung.SEIN),
          // TODO ...
          new Basisformen("ziehen", alt("ziehst", "zeuchts"), alt("zieht", "zeucht"),
              alt("zieh", "ziehe", "zeuch"), "zog", "zöge", "gezogen", Perfektbildung.HABEN) // TODO
                                                                                             // Sie
      // SIND
      // nach
      // Osten
      // gezogen.
      // TODO ...

      // @formatter:on
      // TODO Alle Verben mit allen ihren Valenzen in
      // VerbLister übertragen
      // ...
      );

  @SuppressWarnings("unused")
  private static final Logger log = // NOPMD by nbudzyn on 29.06.10 19:50
      Logger.getLogger(VerbFlektierer.class);

  public VerbFlektierer() {
    super();
  }

  /**
   * Infinitiv eines Verbs
   *
   * @param numerusDesImplizitenSubjekts Numerus des impliziten Subjekts - hat Auswirkungen auf
   *        Partizipien von Verben, die ein Prädikativ fordern (z.B. sein):
   *        <ul>
   *        <li>Ist das (implizite) Subjekt im Singular, so kann das Prädikatsnomen auch nur im
   *        Singular stehen: <i>Der KOMPONIST werdende Mann</i>, jedoch nicht <i>der *KOMPONISTEN
   *        werdende Mann</i>!
   *        <li>Ist das (implizite) Subjekt im Plural, so kann das Prädikatsnomen im Plural - oder
   *        auch im Singular stehen: <i>Die STANDARDS werdenden Vorgaben. Die STANDARD werdenden
   *        Vorgaben.</i>
   *        </ul>
   *        <code>null</code> erlaubt, falls diese Valenz kein Subjekt vorsieht (also keinen
   *        Infinitiv erlaubt)
   * @return Eine Infinitiv-Wortform dieser Valenz mit diesem String - oder <code>null</code>, falls
   *         die Valenz kein Subjekt vorsieht (also keinen Infinitiv ermöglicht)
   */
  public IWordForm stdInf(final Lexeme lexeme, final String pos,
      final Valenzvariante valenzvariante, final String personDesImplizitenSubjekts,
      final Genus genusDesImplizitenSubjekts, final @Nullable Numerus numerusDesImplizitenSubjekts,
      final boolean hoeflichkeitsformDesImplizitenSubjekts) {
    return stdInf(lexeme, pos, valenzvariante.getValenz(), personDesImplizitenSubjekts,
        genusDesImplizitenSubjekts, numerusDesImplizitenSubjekts,
        hoeflichkeitsformDesImplizitenSubjekts);
  }

  /**
   * Standard-Deklination eines Verbs gemäß Duden - finite Formen
   */
  public Collection<IWordForm> stdFin(final Valenzvariante valenzvariante, final Lexeme lexeme,
      final String pos) {
    final Collection<IWordForm> auxFin = auxFin(valenzvariante, lexeme, pos);
    if (auxFin != null) {
      return auxFin;
    }

    final Collection<IWordForm> modFin = modFin(valenzvariante, lexeme, pos);
    if (modFin != null) {
      return modFin;
    }

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final String stammGemaessInfinitiv = stammGemaessInfinitiv(lexeme);

    final Basisformen ausnahmeformen = findAusnahme(lexeme.getCanonicalizedForm()); // ggf. null

    res.addAll(stdPraesens(lexeme, pos, valenzvariante.getValenz(), ausnahmeformen,
        stammGemaessInfinitiv));
    res.addAll(stdPraeteritum(lexeme, pos, valenzvariante.getValenz(), ausnahmeformen,
        stammGemaessInfinitiv));

    return res.build();
  }

  /**
   * Deklination eines Hilfsverbs - finite Formen
   *
   * @return <code>null</code>, falls es kein Hilfsverb ist
   */
  private Collection<IWordForm> auxFin(final Valenzvariante variante, final Lexeme lexeme,
      final String pos) {
    final String infinitive = variante.getCanonicalForm();
    if (infinitive.equals("sein")) {
      return seinFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("werden")) {
      return werdenFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("haben")) {
      return habenFin(variante.getValenz(), lexeme, pos);
    }

    return null;
  }

  /**
   * Deklination eines Modalverbs - finite Formen
   *
   * @return <code>null</code>, falls es sich nicht um ein Modalverb handelt
   */
  private Collection<IWordForm> modFin(final Valenzvariante variante, final Lexeme lexeme,
      final String pos) {
    final String infinitive = variante.getCanonicalForm();
    if (infinitive.equals("dürfen")) {
      return duerfenFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("können")) {
      return koennenFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("mögen")) {
      return moegenFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("müssen")) {
      return muessenFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("sollen")) {
      return sollenFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("wollen")) {
      return wollenFin(variante.getValenz(), lexeme, pos);
    }

    return null;
  }

  /**
   * Deklination eines Modalverbs - Partizip Perfekt
   *
   * @return <code>null</code>, falls es sich nicht um ein Modalverb handelt
   */
  private Collection<String> modPartPerfStrings(final Valenzvariante variante, final Lexeme lexeme,
      final String pos) {
    final String infinitive = variante.getCanonicalForm();
    if (infinitive.equals("dürfen")) {
      return ImmutableList.of("gedurft");
    }
    if (infinitive.equals("können")) {
      return ImmutableList.of("gekonnt");
    }
    if (infinitive.equals("mögen")) {
      return ImmutableList.of("gemocht");
    }
    if (infinitive.equals("müssen")) {
      return ImmutableList.of("gemusst", "gemußt");
    }
    if (infinitive.equals("sollen")) {
      return ImmutableList.of("gesollt");
    }
    if (infinitive.equals("wollen")) {
      return ImmutableList.of("gewollt");
    }

    return null;
  }

  /**
   * Deklination eines Hilfsverbs - Imperative
   *
   * @return <code>null</code>, falls es kein Hilfsverb ist
   */
  private Collection<IWordForm> auxImp(final Valenzvariante variante, final Lexeme lexeme,
      final String pos) {
    final String infinitive = variante.getCanonicalForm();
    if (infinitive.equals("sein")) {
      return seinImp(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("werden")) {
      return werdenImp(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("haben")) {
      return habenImp(variante.getValenz(), lexeme, pos);
    }

    return null;
  }

  /**
   * Deklination eines Hilfsverbs - Partizip Perfekt
   *
   * @return <code>null</code>, falls es kein Hilfsverb ist
   */
  private Collection<String> auxPartPerfStrings(final Valenzvariante variante, final Lexeme lexeme,
      final String pos) {
    final String infinitive = variante.getCanonicalForm();
    if (infinitive.equals("sein")) {
      return ImmutableList.of("gewesen");
    }
    if (infinitive.equals("werden")) {
      return ImmutableList.of("geworden", "worden"); // Hier gibt es
      // ... syntaktische... Unterschiede
    }
    if (infinitive.equals("haben")) {
      return ImmutableList.of("gehabt");
    }

    return null;
  }

  /**
   * @return Standard-Deklination eines Verbs gemäß Duden - unflektiertes Partizip Präsens mit
   *         Wortform-String und Valenz (Subjekt ist IMPLIZIT) - oder <code>null</code>, falls es
   *         kein Partizip Präsens gibt (z.B. bei der Verbvariante, die bei "Mich deucht." vorliegt)
   */
  public Pair<String, Valenz> stdPartPraes(final Valenzvariante valenzvariante) {
    final Valenz valenzBeiImplizitemSubjekt = valenzvariante.getValenz().beiImplizitemSubjekt();
    if (valenzBeiImplizitemSubjekt == null) {
      return null;
    }

    // laufend (Duden 612)
    return Pair.of(valenzvariante.getCanonicalForm() + "d", valenzBeiImplizitemSubjekt);
  }

  /**
   * Standard-Deklination eines Verbs gemäß Duden - Imperative Sg. und Plural
   */
  public Collection<IWordForm> stdImp(final Valenzvariante valenzvariante, final Lexeme lexeme,
      final String pos) {
    final Collection<IWordForm> auxImp = auxImp(valenzvariante, lexeme, pos);
    if (auxImp != null) {
      return auxImp;
    }

    if (!valenzvariante.getValenz().fordertSubjekt()) {
      return ImmutableList.of();
    }

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final String stammGemaessInfinitiv = stammGemaessInfinitiv(lexeme);

    final Basisformen ausnahmeformen = findAusnahme(lexeme.getCanonicalizedForm()); // ggf. null

    res.addAll(
        stdImpSg(lexeme, pos, valenzvariante.getValenz(), ausnahmeformen, stammGemaessInfinitiv));
    res.addAll(stdImpPl(lexeme, pos, valenzvariante.getValenz(), stammGemaessInfinitiv));

    return res.build();
  }

  /**
   * Partizip Perfekt - sofern es zur attributiven Verwendung erlaubt ist (z.B. <i>das gedachte
   * Experiment</i>), nicht aber <i>*die gekränkelten Kinder</i>).
   */
  public ImmutableList<Pair<String, Valenz>> stdPartPerfStringsSofernAusserhalbDesVerbalkomplexesVerwendbar(
      final Valenzvariante valenzvariante, final Lexeme lexeme, final String pos) {
    // Duden Bd. 4 2006, 831
    final Basisformen ausnahmeBasisformen = findAusnahme(valenzvariante.getCanonicalForm()); // ggf.
                                                                                             // false

    final Collection<String> wordFormStrings = stdPartPerfStrings(valenzvariante, lexeme, pos);

    final ImmutableList.Builder<Valenz> valenzenBeiImplizitenErgaenzungen = ImmutableList.builder();

    // Siehe Duden Bd.4 2006, 831
    if (valenzvariante.istTransitivUndBildetWerdenOderSeinPassiv()) {
      // Auch Perfekt-Partizipien von Verben wie lehren, abhören
      // etc.
      // können außerhalb des Verbalkomplexes verwendet werden.

      // gelehrt
      final Valenz valenzBeiImplizitemSUndAkkObj =
          valenzvariante.getValenz().beiImplizitemSubjektUndAkkusativObjekt();
      if (valenzBeiImplizitemSUndAkkObj != null) {
        // jdn. etwas lehren - der Französisch gelehrte Mann
        valenzenBeiImplizitenErgaenzungen.add(valenzBeiImplizitemSUndAkkObj);
      }

      final Valenz valenzBeiImplizitemSUndZusPersAkkObj =
          valenzvariante.getValenz().beiImplizitemSubjektUndZusPersonAkkusativObjekt();
      if (valenzBeiImplizitemSUndZusPersAkkObj != null) {
        // jdn. etwas lehren - das den Mann gelehrte Französisch
        valenzenBeiImplizitenErgaenzungen.add(valenzBeiImplizitemSUndAkkObj);
      }
    }

    if (valenzvariante.bildetZustandsreflexiv()) {
      final Valenz valenzBeiImplizitemSUndReflAkkObj =
          valenzvariante.getValenz().beiImplizitemSubjektUndReflAkkObj();
      if (valenzBeiImplizitemSUndReflAkkObj != null) {
        // sich erkälten -> das erkältete Kind
        valenzenBeiImplizitenErgaenzungen.add(valenzBeiImplizitemSUndReflAkkObj);
      }
    }

    if (istIntransivUndTransformativoderTelischUndBildetPerfektMitSein(valenzvariante,
        ausnahmeBasisformen)) {
      final Valenz valenzBeiImplizitemSubjekt = valenzvariante.getValenz().beiImplizitemSubjekt();
      if (valenzBeiImplizitemSubjekt != null) {
        // die Sonne ist untergegangen - die untergegangene Sonne
        valenzenBeiImplizitenErgaenzungen.add(valenzBeiImplizitemSubjekt);
      }
    }

    // @formatter:off
    return valenzenBeiImplizitenErgaenzungen.build().stream()
        .flatMap(v -> wordFormStrings.stream().map(w -> Pair.of(w, v)))
        .collect(toImmutableList());
    // @formatter:on
  }

  /**
   * Dies hier ist eher eine Heuristik, die im Zweifel eher "ja" sagt.
   */
  private boolean istIntransivUndTransformativoderTelischUndBildetPerfektMitSein(
      final Valenzvariante variante, final Basisformen ausnahmeBasisformen) {
    if (!variante.isIntransitiv()) {
      return false;
    }

    if (!isTransformativoderTelischUndBildetPerfektMitSein(variante, ausnahmeBasisformen)) {
      return false;
    }

    return true;
  }

  /**
   * Dies hier ist eher eine Heuristik, die im Zweifel eher "ja" sagt.
   *
   * @param ausnahmeBasisformen <code>null</code> erlaubt
   */
  private boolean isTransformativoderTelischUndBildetPerfektMitSein(final Valenzvariante variante,
      final Basisformen ausnahmeBasisformen) {
    if (!bildetPerfektMitSein(ausnahmeBasisformen)) {
      return false;
    }

    if (StringUtil.equals(variante.getCanonicalForm(), "bleiben")) {
      // zwar Perfekt mit sein, aber nicht telisch, siehe Duden Bd.4 2006,
      // 659.
      return false;
    }

    return true;
  }

  /**
   * @param ausnahmeBasisformen <code>null</code> erlaubt
   */
  private boolean bildetPerfektMitSein(final Basisformen ausnahmeBasisformen) {
    if (ausnahmeBasisformen == null) {
      return false;
    }

    return ausnahmeBasisformen.getPerfektbildung().equals(Perfektbildung.SEIN);
  }

  /**
   * Standard-Deklination eines Verbs gemäß Duden - Partizip Perfekt (Partizip II)
   */
  public Collection<String> stdPartPerfStrings(final Valenzvariante valenzvariante,
      final Lexeme lexeme, final String pos) {
    final Collection<String> auxPartPerfStrings = auxPartPerfStrings(valenzvariante, lexeme, pos);
    if (auxPartPerfStrings != null) {
      return auxPartPerfStrings;
    }

    final Collection<String> modPartPerfStrings = modPartPerfStrings(valenzvariante, lexeme, pos);
    if (modPartPerfStrings != null) {
      return modPartPerfStrings;
    }

    final ImmutableList.Builder<String> res = ImmutableList.builder();

    final String stammGemaessInfinitiv = stammGemaessInfinitiv(lexeme);

    final Basisformen ausnahmeformen = findAusnahme(lexeme.getCanonicalizedForm()); // ggf. null

    res.addAll(stdPartPerfStrings(lexeme, pos, valenzvariante.getValenz(), ausnahmeformen,
        stammGemaessInfinitiv));

    return res.build();
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<IWordForm> stdImpSg(final Lexeme lexeme, final String pos, final Valenz valenz,
      final Basisformen ausnahmeformen, final String stammGemaessInfinitiv) {
    if (ausnahmeformen != null) {
      return stdImp(lexeme, pos, valenz, SINGULAR, ausnahmeformen.getImpSgAltern());
    }

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    // Duden 609
    if (!GermanUtil.endetAufObstruentPlusMOderN(stammGemaessInfinitiv)) {
      // geh(!)
      // trauer(!), lächel(!) - zumindest "in der Alltagssprache"
      // (aber nicht atm!*, sondern atme!)
      addStdImpIfNotNul(res, lexeme, pos, valenz, SINGULAR, stammGemaessInfinitiv);
    }

    // Duden 609 meint auch: "Verben deren Stamm auf d oder t [...] endet,
    // erhalten im Imperativ Sg. regelmäßig -e" - allerdings
    // empfinde ich persönlich "bind" als korrekt (nicht nur "binde").

    // gehe(!), trauere(!), lächele(!)

    res.addAll(stdImp(lexeme, pos, valenz, SINGULAR,
        // Duden 609
        // trauern -> traure(!), lächeln -> lächle(!)
        tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stammGemaessInfinitiv, "e")));

    return res.build();
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<String> stdPartPerfStrings(final Lexeme lexeme, final String pos,
      final Valenz valenz, final Basisformen ausnahmeformen, final String stammGemaessInfinitiv) {
    if (stammGemaessInfinitiv.equals("wiss")) {
      return ImmutableList.of("gewusst");
    }

    if (ausnahmeformen != null) {
      return ausnahmeformen.getPartPerfAltern();
    }

    // Vgl. Duden 614, 617
    // ge-lach-t, ge-gründ-e-t
    // @formatter:off
    return tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stammGemaessInfinitiv, "t").stream()
        .map(partPerfOhneGe -> "ge" + partPerfOhneGe)
        .collect(ImmutableList.toImmutableList());
    // @formatter:on
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<IWordForm> stdImpPl(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String stammGemaessInfinitiv) {
    // Duden 609: "Im Plural werden Präsensformen verwendet."
    // geht(!)
    return ImmutableList.copyOf(
        stdImp(lexeme, pos, valenz, SINGULAR, stdP2PlPraesIndStrings(stammGemaessInfinitiv)));
  }

  /**
   * @return Basisformen für diesen Infinitiv - wenn es sich um eine Ausnahme handelt - sonst
   *         <code>null</code>
   */
  private Basisformen findAusnahme(final String inputInfinitiv) {
    // @formatter:off
    return AUSNAHMEENDEN.stream()
        .map(basisform -> basisform.erzeugeKopieMitPraefix(inputInfinitiv))
        .filter(res -> res != null)
        .findFirst()
        .orElse(null);
    // @formatter:on
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Iterable<? extends IWordForm> stdPraesens(final Lexeme lexeme, final String pos,
      final Valenz valenz, final Basisformen ausnahmeformen, final String stammGemaessInfinitiv) {

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.addAll(stdPraesensInd(lexeme, pos, valenz, ausnahmeformen, stammGemaessInfinitiv));
    res.addAll(stdPraesKonjUndPraetStarkKonj(lexeme, pos, valenz, PRAESENS, stammGemaessInfinitiv));

    return res.build();
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<IWordForm> stdPraesensInd(final Lexeme lexeme, final String pos,
      final Valenz valenz, final Basisformen ausnahmeformen,
      final String stammPraesIndGemaessInfinitiv) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.addAll(stdSgPraesInd(lexeme, pos, valenz, ausnahmeformen, stammPraesIndGemaessInfinitiv));

    if (valenz.fordertSubjekt()) {
      res.addAll(stdPlPraesInd(lexeme, pos, valenz, stammPraesIndGemaessInfinitiv));
    }

    return res.build();
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<IWordForm> stdSgPraesInd(final Lexeme lexeme, final String pos,
      final Valenz valenz, final Basisformen ausnahmeformen,
      final String stammPraesIndGemaessInfinitiv) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    if (valenz.fordertSubjekt()) {
      res.addAll(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false,
          stdP1SgPraesIndStrings(stammPraesIndGemaessInfinitiv)));

      res.addAll(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false,
          stdP2SgPraesIndStrings(ausnahmeformen, stammPraesIndGemaessInfinitiv)));
    }

    res.addAll(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false,
        stdP3SgPraesIndStrings(ausnahmeformen, stammPraesIndGemaessInfinitiv)));
    res.addAll(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false,
        stdP3SgPraesIndStrings(ausnahmeformen, stammPraesIndGemaessInfinitiv)));
    res.addAll(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false,
        stdP3SgPraesIndStrings(ausnahmeformen, stammPraesIndGemaessInfinitiv)));

    return res.build();
  }

  private Collection<IWordForm> stdPlPraesInd(final Lexeme lexeme, final String pos,
      final Valenz valenz, final String stammPraesInd) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    final String infinitiv = lexeme.getCanonicalizedForm();

    // lachen, lächeln
    res.addAll(
        stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, ImmutableList.of(infinitiv)));

    res.addAll(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false,
        stdP2PlPraesIndStrings(stammPraesInd)));

    // lachen, lächeln
    res.addAll(
        stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, ImmutableList.of(infinitiv)));
    res.addAll(
        stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, ImmutableList.of(infinitiv)));
    return res.build();
  }

  private Collection<IWordForm> stdPraesInd(final Lexeme lexeme, final String pos,
      final Valenz valenz, final String person, final Genus genus, final Numerus numerus,
      final boolean hoeflichkeitsform, final Collection<String> strings) {
    return stdFin(lexeme, pos, valenz, person, genus, numerus, hoeflichkeitsform, PRAESENS,
        INDIKATIV, strings);
  }

  private Collection<String> stdP1SgPraesIndStrings(final String stammPraesInd) {
    if (stammPraesInd.equals("wiss")) {
      // Duden 4 2006 644
      return ImmutableList.of("weiß");
    }

    // Duden 620
    return ImmutableList.copyOf(
        tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stammPraesInd, "e"));
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<String> stdP2SgPraesIndStrings(final Basisformen ausnahmeformen,
      final String stammPraesIndGemaessInfinitiv) {
    if (stammPraesIndGemaessInfinitiv.equals("wiss")) {
      // Duden 4 2006 644
      return ImmutableList.of("weißt");
    }

    if (ausnahmeformen != null) {
      return ausnahmeformen.getP2SgPraesIndAltern();
    }

    final ImmutableList.Builder<String> res = ImmutableList.builder();

    if (GermanUtil.endetAufSLaut(stammPraesIndGemaessInfinitiv)) {
      // Sonderfall: rasen, küssen
      // du ras-t, du küss-t
      res.addAll(tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(
          stammPraesIndGemaessInfinitiv, "t"));
    } else {
      // Regelfall: Du lachst, du lächelst, du naschst
      res.addAll(tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(
          stammPraesIndGemaessInfinitiv, "st"));
    }

    // In jedem Fall:
    // Wenn der Stamm auf -s oder -sch endet...
    if (GermanUtil.endetAufSLaut(stammPraesIndGemaessInfinitiv)
        || GermanUtil.endetAufSchLaut(stammPraesIndGemaessInfinitiv)) {
      // ..., so
      // KANN auch ein e eingeschoben werden (Duden 4, 2006 618)
      // du rasest, du küssest, du naschest ("poetisch, veraltet")
      res.addAll(tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(
          stammPraesIndGemaessInfinitiv, "est"));
    }

    return res.build();
  }

  /**
   * Fügt einen Stamm und eine Endung zusammen - kümmert sich außerdem um e-Tilgung (im Stamm) und
   * um e-Einschub (vor der Endung):
   * <ul>
   * <li>Wenn der Stamm auf -el, -en oder auf -er endet, wird - sofern gemäß Endung möglich - auch
   * die Form geliefert, bei der das e des Stamms getilgt ist (also sowohl <i>sammel-e</i> als auch
   * <i>samml-e</i>).
   * <li>In manchen Fällen wird zwischen Stammd und Endung ein e eingeschoben (teilweise als einzige
   * Möglichkeit, teilweise werden beide Möglichkeiten geliefert).
   * </ul>
   */
  private Collection<String> tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(
      final String stamm, final String endung) {
    final ImmutableList.Builder<String> res = ImmutableList.builder();

    // lache, lacht, lächele, lächelt
    res.addAll(fuegeGgfEVorEndungEinUndHaengeEndungAn(stamm, endung));
    if (endung.startsWith("e")) {
      final String stammNachETilgung = GermanUtil.tilgeEAusStammWennMoeglich(stamm);

      if (stammNachETilgung != null) {
        // lächl -> ich lächle, bedau -> ich bedaure
        res.addAll(fuegeGgfEVorEndungEinUndHaengeEndungAn(stammNachETilgung, endung));
      }
    }

    return res.build();
  }

  /**
   * Fügt einen (ggf. schon e-getilgten!) Stamm und eine Endung zusammen - kümmert sich außerdem um
   * um e-Einschub (vor der Endung).
   * <p>
   * Vgl. Duden Bd. 4 2006 617ff.
   */
  private Collection<String> fuegeGgfEVorEndungEinUndHaengeEndungAn(final String stamm,
      final String endung) {
    final ImmutableList.Builder<String> res = ImmutableList.builder();

    // Duden 4 2006 617
    if (eEinschubZwingend(stamm, endung)) {
      // (er) redet, (du) atmest
      res.add(stamm + "e" + endung);
    } else {
      // z.B. Lipid (r, l) + Nasal (n, m)
      // (er) denkt, (du) denkst, (er) qualmte
      res.add(stamm + endung);
    }

    return res.build();
  }

  /**
   * @return <code>true</code>, falls zwischen diesem Stamm und dieser Endung ein e eingeschoben
   *         werden <i>muss</i>. <code>false</code>, falls <i>kein e eingeschoben werden darf</i>.
   *         (Vgl. Duden 4 2006 617, 618)
   */
  private boolean eEinschubZwingend(final String stamm, final String endung) {
    // Dies hier ergibt sich ohnehin:
    // if (GermanUtil.endetAufSLaut(stamm) ||
    // GermanUtil.endetAufSchLaut(stamm)) {
    // Duden 4 2006 618: "Außerhalb der 2. Pers. Sg. Ind. Präs. findet
    // hier normalerweise
    // kein e-Einschub statt."
    // return false;
    // }

    // Ebenfalls ergibt sich automatisch, dass bei Verben auf -el kein e
    // eingefügt wird
    // (du wandelst, Duden 620).

    if (!StringUtil.endsWith(stamm, "d", "t") && !GermanUtil.endetAufObstruentPlusMOderN(stamm)) {
      return false;
    }

    if (endung.startsWith("t") // t (Präs), te, test, tet (Prät)
        || endung.endsWith("st")) {
      return true;
    }

    return false;
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<String> stdP3SgPraesIndStrings(final Basisformen ausnahmeformen,
      final String stammPraesIndGemaessInfinitiv) {
    if (stammPraesIndGemaessInfinitiv.equals("wiss")) {
      // Duden 4 2006 644
      return ImmutableList.of("weiß");
    }

    if (ausnahmeformen != null) {
      return ausnahmeformen.getP3SgPraesIndAltern();
    }

    return tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stammPraesIndGemaessInfinitiv,
        "t");
  }

  private Collection<String> stdP2PlPraesIndStrings(final String stammPraesInd) {
    final ImmutableSet.Builder<String> res = ImmutableSet.builder();

    res.addAll(tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stammPraesInd, "t"));

    // Duden Bd. 4 2006, 619:
    // "Gelegentlich ist der e-Einschub über die genannten Regelfälle hinaus
    // anzutreffen, besonders bei Imperativformen der Lutherübersetzung der
    // Bibel.
    // Diese gelten als veraltet und dichterisch:
    // Seid fruchtbar und mehret euch[...]"
    // Wegen des Sets während Duplikate hier unproblematisch
    res.addAll(tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stammPraesInd, "et"));

    return res.build();
  }

  /**
   * @return der Stamm der Indikativ-Präsens-Formen, wie er sich aus dem Infinitiv ergeben müsste
   *         (berücksichtigt KEINE Ausnahmen!)
   */
  private String stammGemaessInfinitiv(final Lexeme lexeme) {
    final String infinitiv = lexeme.getCanonicalizedForm();

    if (infinitiv.length() <= 2) {
      return infinitiv;
    }

    if (infinitiv.length() >= 3 && "en".equals(infinitiv.substring(infinitiv.length() - 2))) {
      // lachen

      return infinitiv.substring(0, infinitiv.length() - 2);
    }

    // fächeln, sammeln
    return infinitiv.substring(0, infinitiv.length() - 1);
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<IWordForm> stdPraeteritumInd(final Lexeme lexeme, final String pos,
      final Valenz valenz, final Basisformen ausnahmeformen, final String stammGemaessInfinitiv) {
    if (stammGemaessInfinitiv.equals("wiss")) {
      return stdPraeteritumSchwach(lexeme, pos, valenz, "wuss", INDIKATIV);
    }

    if (ausnahmeformen != null) {
      final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

      for (final String p1SgPraetInd : ausnahmeformen.getP1SgPraetIndAltern()) {
        if (p1SgPraetInd.endsWith("te")) {
          // nicht stark
          final String stammPraetInd = p1SgPraetInd.substring(0, p1SgPraetInd.length() - 2);
          res.addAll(stdPraeteritumSchwach(lexeme, pos, valenz, stammPraetInd, INDIKATIV));
        } else {
          res.addAll(stdPraeteritumIndStark(lexeme, pos, valenz, p1SgPraetInd));
        }
      }

      return res.build();
    }

    // Keine Ausnahme -> schwach
    return stdPraeteritumSchwach(lexeme, pos, valenz, stammGemaessInfinitiv, INDIKATIV);
  }

  private Collection<IWordForm> stdPraeteritumSchwach(final Lexeme lexeme, final String pos,
      final Valenz valenz, final String stammPraetSchwach, final String modus) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, stammPraetSchwach, SINGULAR, modus,
        "te", "test", "te"));

    if (valenz.fordertSubjekt()) {
      res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, stammPraetSchwach, PLURAL, modus,
          "ten", "tet", "ten"));
    }

    return res.build();
  }

  private Iterable<? extends IWordForm> stdPraeteritumIndStark(final Lexeme lexeme,
      final String pos, final Valenz valenz, final String stammPraetInd) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, stammPraetInd, SINGULAR, INDIKATIV,
        "", "st", ""));

    if (valenz.fordertSubjekt()) {
      res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, stammPraetInd, PLURAL, INDIKATIV,
          "en", "t", "en"));
    }

    return res.build();
  }

  private Collection<IWordForm> stdPraetSchwachUndIndStark(final Lexeme lexeme, final String pos,
      final Valenz valenz, final String stammPraetInd, final Numerus numerus, final String modus,
      final String p1Suffix, final String p2Suffix, final String p3Suffix) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    if (valenz.fordertSubjekt()) {
      res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, "1", null, numerus, false, modus,
          stdPraetIndStrings(stammPraetInd, p1Suffix)));

      res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, "2", null, numerus, false, modus,
          stdPraetIndStrings(stammPraetInd, p2Suffix)));
    }

    if (numerus.equals(SINGULAR)) {
      res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, "3", MASKULINUM, numerus, false,
          modus, stdPraetIndStrings(stammPraetInd, p3Suffix)));
      res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, "3", FEMININUM, numerus, false,
          modus, stdPraetIndStrings(stammPraetInd, p3Suffix)));
      res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, "3", NEUTRUM, numerus, false,
          modus, stdPraetIndStrings(stammPraetInd, p3Suffix)));
    } else {
      res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, "3", null, numerus, false, modus,
          stdPraetIndStrings(stammPraetInd, p3Suffix)));
      // Höflichkeitsform
      res.addAll(stdPraetSchwachUndIndStark(lexeme, pos, valenz, "3", null, numerus, true, modus,
          stdPraetIndStrings(stammPraetInd, p3Suffix)));
    }

    return res.build();
  }

  private Collection<IWordForm> stdPraetSchwachUndIndStark(final Lexeme lexeme, final String pos,
      final Valenz valenz, final String person, final Genus genus, final Numerus numerus,
      final boolean hoeflichkeitsform, final String modus, final Collection<String> strings) {
    return stdFin(lexeme, pos, valenz, person, genus, numerus, hoeflichkeitsform, PRAETERITUM,
        modus, strings);
  }

  private ImmutableCollection<String> stdPraetIndStrings(final String stammPraetInd,
      final String suffixOhneOptionalesE) {
    final ImmutableList.Builder<String> res = ImmutableList.builder();

    res.addAll(
        // Mir ist nicht klar, ob es bei einem starken Verb auf -eln
        // oder -ern eine e-Tilgung im Stamm gäbe. Duden 620 sagt das
        // nicht deutlich... -
        // ich habe allerdings (auch in Duden 704) kein einziges starkes
        // Verb auf -eln oder -ern gefunden.
        // Klar ist jedoch, dass es eine e-Einfügung vor der Endung
        // geben muss.
        tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stammPraetInd,
            suffixOhneOptionalesE));

    return res.build();
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<IWordForm> stdPraeteritum(final Lexeme lexeme, final String pos,
      final Valenz valenz, final Basisformen ausnahmeformen, final String stammGemaessInfinitiv) {

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.addAll(stdPraeteritumInd(lexeme, pos, valenz, ausnahmeformen, stammGemaessInfinitiv));
    res.addAll(stdPraeteritumKonj(lexeme, pos, valenz, ausnahmeformen, stammGemaessInfinitiv));

    return res.build();
  }

  private Collection<IWordForm> stdPraesKonjUndPraetStarkKonj(final Lexeme lexeme, final String pos,
      final Valenz valenz, final String tempus, final String stammPraesKonj) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.addAll(stdSgPraesKonjUndPraetStarkKonj(lexeme, pos, valenz, tempus, stammPraesKonj));

    if (valenz.fordertSubjekt()) {
      res.addAll(stdPlPraesKonjUndPraetStarkKonj(lexeme, pos, valenz, tempus, stammPraesKonj));
    }
    return res.build();
  }

  private Collection<IWordForm> stdSgPraesKonjUndPraetStarkKonj(final Lexeme lexeme,
      final String pos, final Valenz valenz, final String tempus, final String stamm) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    if (valenz.fordertSubjekt()) {
      // dass ich lache?
      res.addAll(stdKonj(lexeme, pos, valenz, "1", SINGULAR, tempus,
          tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stamm, "e")));

      // dass du lachest
      res.addAll(stdKonj(lexeme, pos, valenz, "2", SINGULAR, tempus,
          tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stamm, "est")));
    }

    // dass er lache
    res.addAll(stdKonj(lexeme, pos, valenz, "3", SINGULAR, tempus,
        tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stamm, "e")));

    return res.build();
  }

  private Collection<IWordForm> stdPlPraesKonjUndPraetStarkKonj(final Lexeme lexeme,
      final String pos, final Valenz valenz, final String tempus, final String stamm) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    // dass wir lachen?, dass wir lächlen?
    res.addAll(stdKonj(lexeme, pos, valenz, "1", PLURAL, tempus,
        tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stamm, "en")));

    // dass ihr lachet
    res.addAll(stdKonj(lexeme, pos, valenz, "2", PLURAL, tempus,
        tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stamm, "et")));

    // dass sie lachen?
    res.addAll(stdKonj(lexeme, pos, valenz, "3", PLURAL, tempus,
        tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stamm, "en")));

    return res.build();
  }

  /**
   * @param ausnahmeformen <code>null</code> erlaubt
   */
  private Collection<IWordForm> stdPraeteritumKonj(final Lexeme lexeme, final String pos,
      final Valenz valenz, final Basisformen ausnahmeformen, final String stammGemaessInfinitiv) {
    if (stammGemaessInfinitiv.equals("wiss")) {
      // Duden 4 2006, 645
      return stdPraeteritumSchwach(lexeme, pos, valenz, "wüss", KONJUNKTIV);
    }

    if (ausnahmeformen != null) {
      final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

      for (final String p1SgPraetKonj : ausnahmeformen.getP1SgPraetKonjAltern()) {
        if (p1SgPraetKonj.endsWith("te")) {
          final String stammPraetKonj = p1SgPraetKonj.substring(0, p1SgPraetKonj.length() - 2);
          res.addAll(stdPraeteritumSchwach(lexeme, pos, valenz, stammPraetKonj, KONJUNKTIV));
        } else {
          res.addAll(
              stdPraesKonjUndPraetStarkKonj(lexeme, pos, valenz, PRAETERITUM, p1SgPraetKonj));
        }
      }

      return res.build();
    }

    // Keine Ausnahme -> schwach
    return stdPraeteritumSchwach(lexeme, pos, valenz, stammGemaessInfinitiv, KONJUNKTIV);
  }

  private Collection<IWordForm> seinFin(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "bin"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "bist"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "ist"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "ist"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "ist"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "sind"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "seid"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "sind")); // sie
    // sind
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "sind")); // Sie
    // sind

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "war"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "warst"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "war"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "war"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "war"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "waren"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "wart"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "waren"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "waren"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "sei"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "seist"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "seiest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "sei"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "sei"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "sei"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "seien"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "seiet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "seien"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "seien"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "wäre"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "wärst"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "wärest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "wäre"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "wäre"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "wäre"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "wären"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "wärt"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "wäret"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "wären"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "wären"));

    return res.build();
  }

  private Collection<IWordForm> seinImp(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    addStdImpIfNotNul(res, lexeme, pos, valenz, SINGULAR, "sei");
    addStdImpIfNotNul(res, lexeme, pos, valenz, PLURAL, "seid");

    return res.build();
  }

  private Collection<IWordForm> habenFin(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "habe"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "hab"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "hast"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "hat"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "hat"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "hat"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "haben"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "habt"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "haben")); // sie
                                                                                  // haben
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "haben")); // Sie
                                                                                 // haben

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "hatte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "hattest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "hatte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "hatte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "hatte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "hatten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "hattet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "hatten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "hatten"));

    // TODO ..., dass ich habe* -> dass ich hätte
    // -> neues Merkmal "alsKonjZuErkennen"?
    // (Ist es so ähnlich wie die Fälle, wo der Nominativ einspringt?
    // Wie sind diese Fälle gelöst? Oder ähnliche wie Genitivregel?)
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "habe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "habest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "habe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "habe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "habe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "haben"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "habet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "haben"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "haben"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "hätte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "hättest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "hätte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "hätte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "hätte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "hätten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "hättet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "hätten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "hätten"));

    return res.build();
  }

  private Collection<IWordForm> habenImp(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    addStdImpIfNotNul(res, lexeme, pos, valenz, SINGULAR, "habe");
    addStdImpIfNotNul(res, lexeme, pos, valenz, SINGULAR, "hab");
    addStdImpIfNotNul(res, lexeme, pos, valenz, PLURAL, "habt");
    addStdImpIfNotNul(res, lexeme, pos, valenz, PLURAL, "habet");

    return res.build();
  }

  private void addStdImpIfNotNul(final ImmutableList.Builder<IWordForm> res, final Lexeme lexeme,
      final String pos, final Valenz valenz, final Numerus numerus, final String string) {
    final Wortform imp = stdImp(lexeme, pos, valenz, numerus, string);
    if (imp != null) {
      res.add(imp);
    }
  }

  private Collection<IWordForm> werdenFin(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "werd"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "werde"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "wirst"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "wird"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "wird"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "wird"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "werden"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "werdet"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "werden")); // sie
                                                                                   // werden
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "werden")); // Sie
                                                                                  // werden

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "wurde"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "wurd"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "ward"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "wurdest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "wardest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "wardst"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "wurde"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "wurde"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "wurde"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "wurd")); // er
    // wurd geboren
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "wurd"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "wurd"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "ward")); // er
    // ward
    // geboren
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "ward"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "ward"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "wurden"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "wurdet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "wurden"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "wurden"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "werde"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "werdest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "werde"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "werde"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "werde"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "werden"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "werdet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "werden"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "werden"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "würd"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "würde"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "würdest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "würd"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "würd"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "würd"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "würde"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "würde"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "würde"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "würden"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "würdet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "würden"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "würden"));

    // putHaben\(lexeme, (.*)\)
    // res.add\(haben\(lexeme, pos, \1\)\)

    return res.build();
  }

  private Collection<IWordForm> werdenImp(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    addStdImpIfNotNul(res, lexeme, pos, valenz, SINGULAR, "werde");
    addStdImpIfNotNul(res, lexeme, pos, valenz, SINGULAR, "werd");
    addStdImpIfNotNul(res, lexeme, pos, valenz, PLURAL, "werdet");

    return res.build();
  }

  private final Collection<IWordForm> duerfenFin(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "darf"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "darfst"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "darf"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "darf"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "darf"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "dürfen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "dürft"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "dürfen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "dürfen"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "durfte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "durfest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "durfte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "durfte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "durfte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "durften"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "durftet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "durften"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "durften"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "dürfe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "dürfest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "dürfe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "dürfe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "dürfe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "dürften"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "dürfet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "dürften"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "dürften"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "dürfte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "dürftest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "dürfte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "dürfte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "dürfte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "dürften"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "dürftet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "dürften"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "dürften"));

    return res.build();
  }

  private final Collection<IWordForm> wollenFin(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "will"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "willst"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "will"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "will"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "will"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "wollen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "wollt"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "wollen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "wollen"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "wollte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "wolltest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "wollte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "wollte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "wollte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "wollten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "wolltet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "wollten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "wollten"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "wolle"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "wollest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "wolle"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "wolle"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "wolle"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "wollen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "wollet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "wollen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "wollen"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "wollte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "wolltest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "wollte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "wollte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "wollte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "wollten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "wolltet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "wollten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "wollten"));

    return res.build();
  }

  private final Collection<IWordForm> koennenFin(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "kann"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "kannst"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "kann"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "kann"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "kann"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "können"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "könnt"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "können"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "können"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "konnte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "konntest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "konnte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "konnte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "konnte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "konnten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "konntet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "konnten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "konnten"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "könne"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "könnest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "könne"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "könne"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "könne"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "können"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "könnet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "können"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "können"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "könnte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "könntest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "könnte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "könnte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "könnte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "könnten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "könntet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "könnten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "könnten"));

    return res.build();
  }

  private final Collection<IWordForm> sollenFin(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "soll"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "sollst"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "soll"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "soll"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "soll"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "sollen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "sollt"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "sollen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "sollen"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "sollte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "sollt")); // ich
    // sollt
    // besser
    // sagen...
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "solltest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "sollte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "sollte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "sollte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "sollt"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "sollt"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "sollt"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "sollten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "solltet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "sollten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "sollten"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "solle"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "sollest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "solle"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "solle"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "solle"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "sollen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "sollet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "sollen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "sollen"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "sollte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "sollt"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "solltest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "sollte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "sollte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "sollte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "sollt"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "sollt"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "sollt"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "sollten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "solltet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "sollten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "sollten"));

    return res.build();
  }

  private final Collection<IWordForm> muessenFin(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    // IDEA Alte Rechtschreibung?
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "muss"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "musst"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "muss"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "muss"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "muss"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "müssen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "müsst"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "müssen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "müssen"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "musste"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "musstest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "musste"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "musste"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "musste"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "mussten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "musstet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "mussten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "mussten"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "müsse"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "müssest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "müsse"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "müsse"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "müsse"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "müssten"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "müsset"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "müssten"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "müssten"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "müsste"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "müsstest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "müsste"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "müsste"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "müsste"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "müssten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "müsstet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "müssten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "müssten"));

    return res.build();
  }

  private final Collection<IWordForm> moegenFin(final Valenz valenz, final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "mag"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "magst"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "mag"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "mag"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "mag"));
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "mögen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "mögt"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "mögen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "mögen"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "mochte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "mochtest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "mochte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "mochte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "mochte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "mochten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "mochtet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "mochten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "mochten"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "möge"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "mögest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "möge"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "möge"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "möge"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "mögen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "möget"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "mögen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "mögen"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "möchte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "möchtest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "möchte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "möchte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "möchte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "möchten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "möchtet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "möchten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "möchten"));

    return res.build();
  }

  public Wortform stdPraesInd(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String person, final Genus genus, final Numerus numerus,
      final boolean hoeflichkeitsform, final String string) {
    return stdFin(lexeme, pos, valenz, person, genus, numerus, hoeflichkeitsform, PRAESENS,
        INDIKATIV, string);
  }

  public Wortform stdPraetInd(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String person, final Genus genus, final Numerus numerus,
      final boolean hoeflichkeitsform, final String string) {
    return stdFin(lexeme, pos, valenz, person, genus, numerus, hoeflichkeitsform, PRAETERITUM,
        INDIKATIV, string);
  }

  public IWordForm stdPraesKonj(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String person, final Genus genus, final Numerus numerus,
      final boolean hoeflichkeitsform, final String string) {
    return stdKonj(lexeme, pos, valenz, person, genus, numerus, hoeflichkeitsform, PRAESENS,
        string);
  }

  public IWordForm stdPraetKonj(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String person, final Genus genus, final Numerus numerus,
      final boolean hoeflichkeitsform, final String string) {
    return stdKonj(lexeme, pos, valenz, person, genus, numerus, hoeflichkeitsform, PRAETERITUM,
        string);
  }

  public Collection<IWordForm> stdKonj(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String person, final Numerus numerus, final String tempus,
      final Collection<String> strings) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();
    for (final String string : strings) {
      if (person.equals("3") && numerus.equals(SINGULAR)) {
        res.add(stdKonj(lexeme, pos, valenz, person, MASKULINUM, numerus, false, tempus, string));
        res.add(stdKonj(lexeme, pos, valenz, person, FEMININUM, numerus, false, tempus, string));
        res.add(stdKonj(lexeme, pos, valenz, person, NEUTRUM, numerus, false, tempus, string));

      } else {
        res.add(stdKonj(lexeme, pos, valenz, person, null, numerus, false, tempus, string));
        if (person.equals("3") && numerus.equals(PLURAL)) {
          // "Sie"-Form
          res.add(stdKonj(lexeme, pos, valenz, person, null, numerus, true, tempus, string));
        }
      }
    }

    return res.build();
  }

  public IWordForm stdKonj(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String person, final Genus genus, final Numerus numerus,
      final boolean hoeflichkeitsform, final String tempus, final String string) {
    return stdFin(lexeme, pos, valenz, person, genus, numerus, hoeflichkeitsform, tempus,
        KONJUNKTIV, string);
  }

  public Collection<IWordForm> stdFin(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String person, final Genus genus, final Numerus numerus,
      final boolean hoeflichkeitsform, final String tempus, final String modus,
      final Collection<String> strings) {
    return strings.stream().map(string -> stdFin(lexeme, pos, valenz, person, genus, numerus,
        hoeflichkeitsform, tempus, modus, string)).collect(toImmutableList());
  }

  /**
   * @return Eine Imperativ-Wortform dieser Valenz mit diesen Strings - oder eine leere
   *         <code>Collection</code>, falls die Valenz kein Subjekt vorsieht (also keinen Imperativ
   *         ermöglicht)
   */
  public Collection<IWordForm> stdImp(final Lexeme lexeme, final String pos, final Valenz valenz,
      final Numerus numerus, final Collection<String> strings) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    for (final String string : strings) {
      addStdImpIfNotNul(res, lexeme, pos, valenz, numerus, string);
    }

    return res.build();
  }

  public Wortform stdFin(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String person, final Genus genus, final Numerus numerus,
      final boolean hoeflichkeitsform, final String tempus, final String modus,
      final String string) {
    return stdFin(lexeme, pos, tempus, modus,
        valenz.buildErgaenzungenUndAngabenSlots(person, genus, // Er
            // gedenkt
            // SEINER
            // selbst.
            // -
            // Nicht aber: *Ihr gedenkt IHRER selbst. !
            numerus, StringFeatureLogicUtil.booleanToString(hoeflichkeitsform), false),
        string);
  }

  /**
   * @param numerusDesImplizitenSubjekts Numerus des impliziten Subjekts - hat Auswirkungen auf
   *        Partizipien von Verben, die ein Prädikativ fordern (z.B. sein):
   *        <ul>
   *        <li>Ist das (implizite) Subjekt im Singular, so kann das Prädikatsnomen auch nur im
   *        Singular stehen: Der KOMPONIST werdende Mann, jedoch nicht der *KOMPONISTEN werdende
   *        Mann!
   *        <li>Ist das (implizite) Subjekt im Plural, so kann das Prädikatsnomen im Plural - oder
   *        auch im Singular stehen: Die STANDARDS werdenden Vorgaben. Die STANDARD werdenden
   *        Vorgaben.
   *        </ul>
   *        <code>null</code> erlaubt, falls diese Valenz kein Subjekt vorsieht (also keinen
   *        Infinitiv erlaubt)
   * @return Eine Infinitiv-Wortform dieser Valenz mit diesem String - oder <code>null</code>, falls
   *         die Valenz kein Subjekt vorsieht (also keinen Infinitiv ermöglicht)
   */
  public Wortform stdInf(final Lexeme lexeme, final String pos, final Valenz valenz,
      final String personDesImplizitenSubjekts, final Genus genusDesImplizitenSubjekts,
      final @Nullable Numerus numerusDesImplizitenSubjekts,
      final boolean hoeflichkeitsformDesImplizitenSubjekts) {
    final Valenz valenzBeiImplizitemSubjekt = valenz.beiImplizitemSubjekt();

    if (valenzBeiImplizitemSubjekt == null) {
      return null;
    }

    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlots =
        valenzBeiImplizitemSubjekt.buildErgaenzungenUndAngabenSlots(personDesImplizitenSubjekts,
            // Die Person ist offenbar für REFLEXIVE
            // Verben relevant:
            // (Peter will) SICH bewerben.
            // (Du willst) DICH bewerben...
            genusDesImplizitenSubjekts, // Peter will SEINER selbst
            // gedenken - aber nicht
            // *Peter will IHRER SELBST gedenken !
            numerusDesImplizitenSubjekts,
            StringFeatureLogicUtil.booleanToString(hoeflichkeitsformDesImplizitenSubjekts), false);

    return stdInf(lexeme, pos, ergaenzungenUndAngabenSlots);
  }

  /**
   * @return Eine Imperativ-Wortform dieser Valenz mit diesem String - oder <code>null</code>, falls
   *         die Valenz kein Subjekt vorsieht (also keinen Imperativ ermöglicht)
   */
  public @Nullable Wortform stdImp(final Lexeme lexeme, final String pos, final Valenz valenz,
      final Numerus numerus, final String string) {
    final Valenz valenzBeiImplizitemSubjekt = valenz.beiImplizitemSubjekt();

    if (valenzBeiImplizitemSubjekt == null) {
      return null;
    }

    final Collection<RoleFrameSlot> ergaenzungenUndAngabenSlots =
        valenzBeiImplizitemSubjekt.buildErgaenzungenUndAngabenSlots("2", // vgl. "Zeig DICH!",
                                                                         // "Zeigt EUCH!"!!
            null, numerus, StringFeatureLogicUtil.FALSE, false);

    return stdImp(lexeme, pos, numerus, ergaenzungenUndAngabenSlots, string);
  }

  public Wortform stdFin(final Lexeme lexeme, final String pos, final String tempus,
      final String modus, final Collection<RoleFrameSlot> ergaenzungenUndAngaben,
      final String string) {
    return WortformUtil.buildVerbFormFin(lexeme, pos, tempus, modus, string,
        ergaenzungenUndAngaben.toArray(new RoleFrameSlot[] {}));
  }

  public Wortform stdImp(final Lexeme lexeme, final String pos, final Numerus numerus,
      final Collection<RoleFrameSlot> ergaenzungenUndAngaben, final String string) {
    return WortformUtil.buildVerbFormImp(lexeme, pos, numerus, string,
        ergaenzungenUndAngaben.toArray(new RoleFrameSlot[] {}));
  }

  public Wortform stdInf(final Lexeme lexeme, final String pos,
      final Collection<RoleFrameSlot> ergaenzungenUndAngabenFuerInf) {
    return WortformUtil.buildVerbFormInf(lexeme, pos, ergaenzungenUndAngabenFuerInf.toArray(
        new RoleFrameSlot[ergaenzungenUndAngabenFuerInf.size()]));
  }

  private static enum Perfektbildung {
    HABEN, SEIN
  }

  /**
   * Die Basisformen für die Flexion eines Verbs (Stammformen und einige weitere) - relevant für
   * starke Verben und andere &quot;Ausnahmen&quot;
   * <p>
   * Vgl. Duden 704
   */
  @Immutable
  private static final class Basisformen {
    /**
     * Infinitiv (1. Stammform)
     */
    private final String infinitiv;

    /**
     * Alternativen für 2. Person Praesens Indikativ
     */
    private final ImmutableCollection<String> p2SgPraesIndAltern;
    /**
     * Alternativen für 3. Person Praesens Indikativ
     */
    private final ImmutableCollection<String> p3SgPraesIndAltern;
    /**
     * Alternativen für den Imperativ Singular
     */
    private final ImmutableCollection<String> impSgAltern;
    /**
     * 1. Person Praeteritum Indikativ (2. Stammform)
     */
    private final ImmutableCollection<String> p1SgPraetIndAltern;
    /**
     * 1. Person Praeteritum Konjunktiv (Konjunktiv II)
     */
    private final ImmutableCollection<String> p1SgPraetKonjAltern;
    /**
     * Partizip Perfekt (Partizip II; 3. Stammform)
     */
    private final ImmutableCollection<String> partPerfAltern;

    /**
     * Ob das Perfekt mit haben oder mit sein gebildet wird
     */
    private final Perfektbildung perfektbildung;

    private Basisformen(final String infinitiv, final String p2SgPraesInd,
        final String p3SgPraesInd, final String impSg, final String p1SgPraetInd,
        final String p1SgPraetKonj, final String partPerf, final Perfektbildung perfektbildung) {
      this(infinitiv, ImmutableList.of(p2SgPraesInd), ImmutableList.of(p3SgPraesInd),
          ImmutableList.of(impSg), ImmutableList.of(p1SgPraetInd), ImmutableList.of(p1SgPraetKonj),
          ImmutableList.of(partPerf), perfektbildung);
    }

    public Basisformen(final String infinitiv, final ImmutableCollection<String> p2SgPraesIndAltern,
        final String p3SgPraesInd, final String impSg, final String p1SgPraetInd,
        final String p1SgPraetKonj, final String partPerf, final Perfektbildung perfektbildung) {
      this(infinitiv, p2SgPraesIndAltern, ImmutableList.of(p3SgPraesInd), ImmutableList.of(impSg),
          ImmutableList.of(p1SgPraetInd), ImmutableList.of(p1SgPraetKonj),
          ImmutableList.of(partPerf), perfektbildung);
    }

    private Basisformen(final String infinitiv, final String p2SgPraesInd,
        final String p3SgPraesInd, final String impSg, final String p1SgPraetInd,
        final String p1SgPraetKonj, final ImmutableCollection<String> partPerfAltern,
        final Perfektbildung perfektbildung) {
      this(infinitiv, ImmutableList.of(p2SgPraesInd), ImmutableList.of(p3SgPraesInd),
          ImmutableList.of(impSg), ImmutableList.of(p1SgPraetInd), ImmutableList.of(p1SgPraetKonj),
          partPerfAltern, perfektbildung);
    }

    private Basisformen(final String infinitiv,
        final ImmutableCollection<String> p2SgPraesIndAltern,
        final ImmutableCollection<String> p3SgPraesIndAltern, final String impSg,
        final String p1SgPraetInd, final String p1SgPraetKonj, final String partPerf,
        final Perfektbildung perfektbildung) {
      this(infinitiv, p2SgPraesIndAltern, p3SgPraesIndAltern, ImmutableList.of(impSg),
          ImmutableList.of(p1SgPraetInd), ImmutableList.of(p1SgPraetKonj),
          ImmutableList.of(partPerf), perfektbildung);
    }

    private Basisformen(final String infinitiv, final String p2SgPraesInd,
        final String p3SgPraesInd, final ImmutableCollection<String> impSgAltern,
        final String p1SgPraetInd, final String p1SgPraetKonj, final String partPerf,
        final Perfektbildung perfektbildung) {
      this(infinitiv, ImmutableList.of(p2SgPraesInd), ImmutableList.of(p3SgPraesInd), impSgAltern,
          ImmutableList.of(p1SgPraetInd), ImmutableList.of(p1SgPraetKonj),
          ImmutableList.of(partPerf), perfektbildung);
    }

    private Basisformen(final String infinitiv, final String p2SgPraesInd,
        final String p3SgPraesInd, final String impSg, final String p1SgPraetInd,
        final ImmutableCollection<String> p1SgPraetKonjAltern, final String partPerf,
        final Perfektbildung perfektbildung) {
      this(infinitiv, ImmutableList.of(p2SgPraesInd), ImmutableList.of(p3SgPraesInd),
          ImmutableList.of(impSg), ImmutableList.of(p1SgPraetInd), p1SgPraetKonjAltern,
          ImmutableList.of(partPerf), perfektbildung);
    }

    private Basisformen(final String infinitiv, final String p2SgPraesInd,
        final String p3SgPraesInd, final ImmutableCollection<String> impSgAltern,
        final String p1SgPraetInd, final ImmutableCollection<String> p1SgPraetKonjAltern,
        final String partPerf, final Perfektbildung perfektbildung) {
      this(infinitiv, ImmutableList.of(p2SgPraesInd), ImmutableList.of(p3SgPraesInd), impSgAltern,
          ImmutableList.of(p1SgPraetInd), p1SgPraetKonjAltern, ImmutableList.of(partPerf),
          perfektbildung);
    }

    private Basisformen(final String infinitiv, final String p2SgPraesInd,
        final String p3SgPraesInd, final ImmutableCollection<String> impSgAltern,
        final ImmutableCollection<String> p1SgPraetIndAltern,
        final ImmutableCollection<String> p1SgPraetKonjAltern, final String partPerf,
        final Perfektbildung perfektbildung) {
      this(infinitiv, ImmutableList.of(p2SgPraesInd), ImmutableList.of(p3SgPraesInd), impSgAltern,
          p1SgPraetIndAltern, p1SgPraetKonjAltern, ImmutableList.of(partPerf), perfektbildung);
    }

    public Basisformen(final String infinitiv, final String p2SgPraesInd, final String p3SgPraesInd,
        final ImmutableCollection<String> impSgAltern,
        final ImmutableCollection<String> p1SgPraetIndAltern,
        final ImmutableCollection<String> p1SgPraetKonjAltern,
        final ImmutableCollection<String> partPerfAltern, final Perfektbildung perfektbildung) {
      this(infinitiv, ImmutableList.of(p2SgPraesInd), ImmutableList.of(p3SgPraesInd), impSgAltern,
          p1SgPraetIndAltern, p1SgPraetKonjAltern, partPerfAltern, perfektbildung);
    }

    private Basisformen(final String infinitiv,
        final ImmutableCollection<String> p2SgPraesIndAltern,
        final ImmutableCollection<String> p3SgPraesIndAltern, final String impSg,
        final ImmutableCollection<String> p1SgPraetIndAltern,
        final ImmutableCollection<String> p1SgPraetKonjAltern, final String partPerf,
        final Perfektbildung perfektbildung) {
      this(infinitiv, p2SgPraesIndAltern, p3SgPraesIndAltern, ImmutableList.of(impSg),
          p1SgPraetIndAltern, p1SgPraetKonjAltern, ImmutableList.of(partPerf), perfektbildung);
    }

    private Basisformen(final String infinitiv,
        final ImmutableCollection<String> p2SgPraesIndAltern,
        final ImmutableCollection<String> p3SgPraesIndAltern,
        final ImmutableCollection<String> impSgAltern,
        final ImmutableCollection<String> p1SgPraetIndAltern,
        final ImmutableCollection<String> p1SgPraetKonjAltern, final String partPerf,
        final Perfektbildung perfektbildung) {
      this(infinitiv, p2SgPraesIndAltern, p3SgPraesIndAltern, impSgAltern, p1SgPraetIndAltern,
          p1SgPraetKonjAltern, ImmutableList.of(partPerf), perfektbildung);
    }

    public Basisformen(final String infinitiv, final ImmutableCollection<String> p2SgPraesIndAltern,
        final ImmutableCollection<String> p3SgPraesIndAltern,
        final ImmutableCollection<String> impSgAltern, final String p1SgPraetInd,
        final String p1SgPraetKonj, final String partPerf, final Perfektbildung perfektbildung) {
      this(infinitiv, p2SgPraesIndAltern, p3SgPraesIndAltern, impSgAltern,
          ImmutableList.of(p1SgPraetInd), ImmutableList.of(p1SgPraetKonj),
          ImmutableList.of(partPerf), perfektbildung);
    }

    private Basisformen(final String infinitiv,
        final ImmutableCollection<String> p2SgPraesIndAltern,
        final ImmutableCollection<String> p3SgPraesIndAltern,
        final ImmutableCollection<String> impSgAltern,
        final ImmutableCollection<String> p1SgPraetIndAltern,
        final ImmutableCollection<String> p1SgPraetKonjAltern,
        final ImmutableCollection<String> partPerfAltern, final Perfektbildung perfektbildung) {
      super();
      this.infinitiv = infinitiv;
      this.p2SgPraesIndAltern = p2SgPraesIndAltern;
      this.p3SgPraesIndAltern = p3SgPraesIndAltern;
      this.impSgAltern = impSgAltern;
      this.p1SgPraetIndAltern = p1SgPraetIndAltern;
      this.p1SgPraetKonjAltern = p1SgPraetKonjAltern;
      this.partPerfAltern = partPerfAltern;
      this.perfektbildung = perfektbildung;
    }

    /**
     * @return Basisform für diesen Infinitiv - sofern er zu dieser Basisform passt ("geben" und
     *         "vergeben" passen zu "geben") - oder <code>null</code>, wenn die der
     *         Eingabe-Infinitiv <i>nicht</i> zur diesen Basisformen passt ("nehmen" passt nicht zu
     *         "geben")
     */
    public Basisformen erzeugeKopieMitPraefix(final String inputInfinitiv) {
      if (inputInfinitiv.equals(infinitiv)) {
        // z.B. "geben" zu "geben"
        return this;
      }

      if (!inputInfinitiv.endsWith(infinitiv)) {
        // z.B. "nehmen" zu "geben"
        return null;
      }

      // z.B. "vergeben" zu "geben"

      final String prefix =
          inputInfinitiv.substring(0, inputInfinitiv.length() - infinitiv.length()); // ver, ge

      return new Basisformen(inputInfinitiv,
          // FIXME (geben / vergeben) du vergibst, ABER: du gibst ...
          // weiter!
          StringUtil.prepend(prefix, p2SgPraesIndAltern),
          StringUtil.prepend(prefix, p3SgPraesIndAltern), StringUtil.prepend(prefix, impSgAltern),
          // FIXME (er) brachte (das Regal) an !
          StringUtil.prepend(prefix, p1SgPraetIndAltern),
          StringUtil.prepend(prefix, p1SgPraetKonjAltern),
          erzeugePartPerfAlternativenGgfMitPraefix(prefix), perfektbildung);
    }

    /**
     * Erzeugt zu einer Basisform wie geben / denken Partizip-Perfekt-Formen wie auf-gegeben oder
     * gedacht (von gedenken)
     */
    private ImmutableCollection<String> erzeugePartPerfAlternativenGgfMitPraefix(
        final String prefix) {
      if (prefix.equals("ge")) {
        // (Ich habe ihrer) gedacht
        return erzeugePartPerfAlternativenFuerPraefixGe();
      }

      if (StringUtil.equals(prefix, "ver", "hinter", "ob", "verab", "be", "er")) {
        // vernommen
        // hintergangen
        // obsiegt, verabreicht, betragen
        // ergeben
        return erzeugePartPerfAlternativenOhneGeMitPraefix(prefix);
      }

      if (StringUtil.equals(prefix, "unter", "über", "um")) {
        // FIXME teilweise haben "verschiedene Wörter" (verschiedene
        // Valenzen) verschiedene Partizipien:
        // (Ich habe mich ihm) unterstellt vs. (ich habe mich wegen des
        // Regens) untergestellt.
        // (Ich habe das Bett) überzogen vs. (ich habe den Pullover)
        // übergezogen.
        // (Ich habe es) übersetzt vs. (der Fährmann hat) übergesetzt.

        // FIXME Andere Verben gibt es nur in einer Version:
        // (Ich habe mich) übernommen vs. (ich habe es) übergestülpt
        // (Ich habe mich) untergehakt
        // (Ich habe mich) umgesehen
        // (Ich habe es) umfahren
        // (Das Meer hat die Stadt) umgeben

        final ImmutableList.Builder<String> res = ImmutableList.builder();
        res.addAll(erzeugePartPerfAlternativenOhneGeMitPraefix(prefix));
        res.addAll(StringUtil.prepend(prefix, partPerfAltern));
        return res.build();
      }

      // weiter-geben: Ich habe es weitergegeben. Ich habe es mir
      // vorgenommen.
      return StringUtil.prepend(prefix, partPerfAltern);
    }

    private ImmutableCollection<String> erzeugePartPerfAlternativenFuerPraefixGe() {
      // (Ich habe ihrer) gedacht (von gedenken), nicht
      // *gegedacht
      return erzeugePartPerfAlternativenOhneGeMitPraefix("ge");
    }

    /**
     * @return z.B. <i>vernommen</i> (aus nehmen / genommen und dem Präfix <i>ver</i>)
     */
    private ImmutableCollection<String> erzeugePartPerfAlternativenOhneGeMitPraefix(
        final String prefix) {
      return partPerfAltern.stream().map(
          partPerf -> prefix + StringUtil.stripPrefixIfAny("ge", partPerf)).collect(
              toImmutableList());
    }

    public final Collection<String> getP2SgPraesIndAltern() {
      return p2SgPraesIndAltern;
    }

    public final Collection<String> getP3SgPraesIndAltern() {
      return p3SgPraesIndAltern;
    }

    public ImmutableCollection<String> getImpSgAltern() {
      return impSgAltern;
    }

    public final Collection<String> getP1SgPraetIndAltern() {
      return p1SgPraetIndAltern;
    }

    public final Collection<String> getP1SgPraetKonjAltern() {
      return p1SgPraetKonjAltern;
    }

    public final Collection<String> getPartPerfAltern() {
      return partPerfAltern;
    }

    public Perfektbildung getPerfektbildung() {
      return perfektbildung;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((impSgAltern == null) ? 0 : impSgAltern.hashCode());
      result = prime * result + ((infinitiv == null) ? 0 : infinitiv.hashCode());
      result = prime * result + ((p1SgPraetIndAltern == null) ? 0 : p1SgPraetIndAltern.hashCode());
      result =
          prime * result + ((p1SgPraetKonjAltern == null) ? 0 : p1SgPraetKonjAltern.hashCode());
      result = prime * result + ((p2SgPraesIndAltern == null) ? 0 : p2SgPraesIndAltern.hashCode());
      result = prime * result + ((p3SgPraesIndAltern == null) ? 0 : p3SgPraesIndAltern.hashCode());
      result = prime * result + ((partPerfAltern == null) ? 0 : partPerfAltern.hashCode());
      result = prime * result + ((perfektbildung == null) ? 0 : perfektbildung.hashCode());
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
      final Basisformen other = (Basisformen) obj;
      if (impSgAltern == null) {
        if (other.impSgAltern != null) {
          return false;
        }
      } else if (!impSgAltern.equals(other.impSgAltern)) {
        return false;
      }
      if (infinitiv == null) {
        if (other.infinitiv != null) {
          return false;
        }
      } else if (!infinitiv.equals(other.infinitiv)) {
        return false;
      }
      if (p1SgPraetIndAltern == null) {
        if (other.p1SgPraetIndAltern != null) {
          return false;
        }
      } else if (!p1SgPraetIndAltern.equals(other.p1SgPraetIndAltern)) {
        return false;
      }
      if (p1SgPraetKonjAltern == null) {
        if (other.p1SgPraetKonjAltern != null) {
          return false;
        }
      } else if (!p1SgPraetKonjAltern.equals(other.p1SgPraetKonjAltern)) {
        return false;
      }
      if (p2SgPraesIndAltern == null) {
        if (other.p2SgPraesIndAltern != null) {
          return false;
        }
      } else if (!p2SgPraesIndAltern.equals(other.p2SgPraesIndAltern)) {
        return false;
      }
      if (p3SgPraesIndAltern == null) {
        if (other.p3SgPraesIndAltern != null) {
          return false;
        }
      } else if (!p3SgPraesIndAltern.equals(other.p3SgPraesIndAltern)) {
        return false;
      }
      if (partPerfAltern == null) {
        if (other.partPerfAltern != null) {
          return false;
        }
      } else if (!partPerfAltern.equals(other.partPerfAltern)) {
        return false;
      }
      if (perfektbildung != other.perfektbildung) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return (infinitiv != null ? infinitiv : p3SgPraesIndAltern) + ", "
          + CollectionUtil.toShortString(p1SgPraetIndAltern) + ", "
          + CollectionUtil.toShortString(partPerfAltern);
    }
  }

  private static <E> ImmutableCollection<E> alt(final E... alternatives) {
    return ImmutableList.copyOf(alternatives);
  }

}
