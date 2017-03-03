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
   * (Enden von) Ausnahmen. Hier m�ssen insbesondere alle (Enden von) starken Vollverben und von
   * unregelm��igen schwachen Vollverben aufgef�hrt werden (au�er <i>wissen</i> - das wird separat
   * behandelt) - au�erdem alle Verben, die ihr Perfekt mit sein bilden!
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
          alt("b�ckst", "backst"),
          // er
          alt("b�ckt", "backt"), "back", // !
          // ich
          alt("backte", "buk"),
          // dass ich
          alt("backte", "b�ke"),
          // ich habe
          "gebacken", Perfektbildung.HABEN),
          new Basisformen("befehlen", "befiehlst", "befiehlt", "befiehl", "befahl",
              alt("bef�hle", "bef�hle"), "befohlen", Perfektbildung.HABEN),
          new Basisformen("beflei�igen", "beflei�t", "beflei�t", alt("beflei�", "beflei�e"),
              "befliss", "beflisse", "beflissen", Perfektbildung.HABEN),
          new Basisformen("beginnen", "beginnst", "beginnt", alt("beginn", "beginne"), "begann",
              alt("beg�nne", "beg�nne"), "begonnen", Perfektbildung.HABEN),
          new Basisformen("bei�en", "bei�t", "bei�t", alt("bei�", "bei�e"), "biss", "bisse",
              "gebissen", Perfektbildung.HABEN),
          new Basisformen("bergen", "birgst", "birgt", "birg", "barg", "b�rge", "geborgen",
              Perfektbildung.HABEN),
          new Basisformen("bersten", alt("birst", "berstest"), alt("birst", "berstet"), "birst",
              "barst", "b�rste", "geborsten", Perfektbildung.SEIN),
          // FIXME teils VERANLASSEN, teils r�umlich bewegen!
          new Basisformen("bewegen", "bewegst", "bewegt", alt("beweg", "bewege"),
              alt("bewegte", "bewog"), alt("bewegte", "bew�ge"), alt("bewegt", "bewogen"),
              Perfektbildung.HABEN),
          new Basisformen("biegen", "biegst", "biegt", alt("bieg", "biege"), "bog", "b�ge",
              "gebogen", Perfektbildung.HABEN),
          new Basisformen("bieten", "bietest", "bietet", alt("biete", "biet"), "bot", "b�te",
              "geboten", Perfektbildung.HABEN),
          new Basisformen("binden", "bindest", "bindet", alt("binde", "bind"), "band", "b�nde",
              "gebunden", Perfektbildung.HABEN),
          new Basisformen("bitten", "bittest", "bittet", alt("bitte", "bitt"), "bat", "b�te",
              "gebeten", Perfektbildung.HABEN),
          new Basisformen("blasen", "bl�st", "bl�st", alt("blas", "blase"), "blies", "bliese",
              "geblasen", Perfektbildung.HABEN),
          new Basisformen("bleiben", "bleibst", "bleibt", alt("bleib", "bleibe"), "blieb", "bliebe",
              "geblieben", Perfektbildung.SEIN),
          new Basisformen("braten", "br�tst", "br�t", alt("brat", "brate"), "briet", "briete",
              "gebraten", Perfektbildung.HABEN),
          new Basisformen("brechen", "brichst", "bricht", "brich", "brach", "br�che", "gebrochen",
              Perfektbildung.SEIN),
          new Basisformen("brennen", "brennst", "brennt", alt("brenn", "brenne"), "brannte",
              "brennte", "gebrannt", Perfektbildung.HABEN),
          new Basisformen("bringen", "bringst", "bringt", alt("bring", "bringe"), "brachte",
              "br�chte", "gebracht", Perfektbildung.HABEN),
          new Basisformen("denken", "denkst", "denkt", alt("denke", "denk"), "dachte", "d�chte",
              "gedacht", Perfektbildung.HABEN),
          new Basisformen("dingen", "dingst", "dingt", alt("ding", "dinge"), alt("dingte", "dang"),
              alt("dingte", "d�nge"), alt("gedungen", "gedingt"), Perfektbildung.HABEN),
          new Basisformen("dreschen", alt("drischst", "dreschst"), alt("drischt", "drescht"),
              "drisch", alt("drosch", "drasch", "dreschte"), alt("dr�sche", "dr�sche", "dreschte"),
              "gedroschen", Perfektbildung.HABEN),
          new Basisformen("dringen", "dringst", "dringt", alt("dring", "dringe"), "drang", "dr�nge",
              "gedrungen", Perfektbildung.SEIN),
          new Basisformen("d�nken", alt("d�nkst", "deuchst"), alt("d�nkt", "deucht"),
              alt("d�nk", "d�nke", "deuch", "deuche"), alt("d�nkte", "deuchte"),
              alt("d�nkte", "deuchte"), alt("ged�nkt", "gedeucht"), Perfektbildung.HABEN),
          new Basisformen("empfangen", "empf�ngst", "empf�ngt", alt("empfang", "empfange"),
              "empfing", "empfinge", "empfangen", Perfektbildung.HABEN),
          new Basisformen("empfehlen", "empfiehlst", "empfiehlt", "empfiehl", "empfahl",
              alt("empf�hle", "empf�hle"), "empfohlen", Perfektbildung.HABEN),
          new Basisformen("empfingen", "empfindest", "empfindet", alt("empfinde", "empfind"),
              "empfand", "empf�nde", "empfunden", Perfektbildung.HABEN),
          new Basisformen("erbleichen", "erbleichst", "erbleicht", alt("erbleiche", "erbleich"),
              "erblich", "erbliche", "erblichen", Perfektbildung.SEIN),
          new Basisformen("erkiesen", "erkiest", "erkiest", alt("erkies", "erkiese"), "erkor",
              "erk�re", "erkoren", Perfektbildung.HABEN),
          new Basisformen("erk�re", "erk�rst", "erk�rt", alt("erk�r", "erk�re"), "erk�rte",
              "erk�rte", "erk�rt", Perfektbildung.HABEN),
          new Basisformen("erl�schen", "erlischst", "erlischt", "erlisch", "erlosch", "erl�sche",
              "erloschen", Perfektbildung.SEIN),
          new Basisformen("erschrecken", "erschrickst", "erschrickt", "erschrick", "erschrak",
              "erschr�ke", "erschrocken", Perfektbildung.SEIN),
          new Basisformen("essen", "isst", "isst", "iss", "a�", "��e", "gegessen",
              Perfektbildung.HABEN),
          new Basisformen("fahren", "f�hrst", "f�hrt", alt("fahr", "fahre"), "fuhr", "f�hre",
              "gefahren", Perfektbildung.SEIN),
          new Basisformen("fallen", "f�llst", "f�llt", alt("fall", "falle"), "fiel", "fiele",
              "gefallen", Perfektbildung.SEIN),
          new Basisformen("fangen", "f�ngst", "f�ngt", alt("fang", "fange"), "fing", "finge",
              "gefangen", Perfektbildung.HABEN),
          new Basisformen("fechten", alt("fichtst", "fechtest"), "ficht", "ficht", "focht",
              "f�chte", "gefochten", Perfektbildung.HABEN),
          new Basisformen("finden", "findest", "findet", alt("finde", "find"), "fand", "f�nde",
              "gefunden", Perfektbildung.HABEN),
          new Basisformen("flechten", alt("flichtst", "flechtest"), "flicht", "flicht", "flocht",
              "fl�chte", "geflochten", Perfektbildung.HABEN),
          new Basisformen("fliegen", "fliegst", "fliegt", alt("flieg", "fliege"), "flog", "fl�ge",
              "geflogen", Perfektbildung.SEIN),
          new Basisformen("fliehen", "fliehst", "flieht", alt("flieh", "fliehe"), "floh", "fl�he",
              "geflohen", Perfektbildung.SEIN),
          new Basisformen("flie�en", "flie�t", "flie�t", alt("flie�", "flie�e"), "floss", "fl�sse",
              "geflossen", Perfektbildung.SEIN),
          new Basisformen("fragen", alt("fragst", "fr�gst"), alt("fragt", "fr�gt"),
              alt("frag", "frage"), alt("fragte", "frug"), alt("fragte", "fr�ge"), "gefragt",
              Perfektbildung.HABEN),
          new Basisformen("fressen", "frisst", "frisst", "friss", "fra�", "fr��e", "gefressen",
              Perfektbildung.HABEN),
          new Basisformen("frieren", "frierst", "friert", alt("frier", "friere"), "fror", "fr�re",
              "gefroren", Perfektbildung.HABEN),
          new Basisformen("g�ren", "g�rst", "g�rt", alt("g�r", "g�re"), alt("gor", "g�rte"),
              alt("g�re", "g�rte"), alt("gegoren", "geg�rt"), Perfektbildung.SEIN),
          new Basisformen("geb�ren", "gebierst", "gebiert", "gebier", "gebar", "geb�re",
              alt("geboren", "*"), Perfektbildung.HABEN),
          new Basisformen("geben", "gibst", "gibt", "gib", "gab", "g�be", "gegeben",
              Perfektbildung.HABEN),
          new Basisformen("gedeihen", "gedeihst", "gedeiht", alt("gedeih", "gedeihe"), "gedieh",
              "gediehe", "gediehen", Perfektbildung.SEIN),
          new Basisformen("gehen", "gehst", "geht", alt("geh", "gehe"), "ging", "ginge", "gegangen",
              Perfektbildung.SEIN),
          new Basisformen("gelingen", "gelingst", "gelingt", alt("geling", "gelinge"), "gelang",
              "gel�nge", "gelungen", Perfektbildung.SEIN),
          new Basisformen("gelten", "giltst", "gilt", "gilt", "galt", alt("g�lte", "g�lte"),
              "gegolten", Perfektbildung.HABEN),
          new Basisformen("genesen", "genest", "genest", alt("genese", "genes"), "genas", "gen�se",
              "genesen", Perfektbildung.SEIN),
          new Basisformen("genie�en", "genie�t", "genie�t", alt("genie�", "genie�e"), "genoss",
              "gen�sse", "genossen", Perfektbildung.HABEN),
          new Basisformen("geschehen", "geschiehst", "geschieht", alt("gescheh", "geschehe"),
              "geschah", "gesch�he", "geschehen", Perfektbildung.SEIN),
          new Basisformen("gewinnen", "gewinnst", "gewinnt", alt("gewinn", "gewinne"), "gewann",
              alt("gew�nne", "gew�nne"), "gewonnen", Perfektbildung.HABEN),
          new Basisformen("gie�en", "gie�t", "gie�t", alt("gie�", "gie�e"), "goss", "g�sse",
              "gegossen", Perfektbildung.HABEN),
          new Basisformen("gleichen", "gleichst", "gleicht", alt("gleiche", "gleich"), "glich",
              "gliche", "geglichen", Perfektbildung.HABEN),
          new Basisformen("gleiten", "gleitest", "gleitet", alt("gleite", "gleit"), "glitt",
              "glitte", "geglitten", Perfektbildung.SEIN),
          new Basisformen("glimmen", "glimmst", "glimmt", alt("glimm", "glimme"),
              alt("glomm", "glimmte"), alt("gl�mme", "glimmte"), alt("geglommen", "geglimmt"),
              Perfektbildung.HABEN),
          new Basisformen("graben", "gr�bst", "gr�bt", alt("grab", "grabe"), "grub", "gr�be",
              "gegraben", Perfektbildung.HABEN),
          new Basisformen("greifen", "greifst", "greift", alt("greif", "greife"), "griff", "griffe",
              "gegriffen", Perfektbildung.HABEN),
          new Basisformen("halten", "h�ltst", "h�lt", alt("halt", "halte"), "hielt", "hielte",
              "gehalten", Perfektbildung.HABEN),
          new Basisformen("h�ngen", "h�ngst", "h�ngt", alt("h�ng", "h�nge"), alt("hing, h�ngte"),
              alt("hinge", "h�ngte"), alt("gehangen", "geh�ngt"), Perfektbildung.HABEN),
          new Basisformen("hauen", "haust", "haut", alt("hau", "haue"), alt("hieb", "haute"),
              alt("hiebe", "haute"), "gehauen", Perfektbildung.HABEN),
          new Basisformen("heben", "hebst", "hebt", alt("heb", "hebe"), alt("hob", "hub"),
              alt("h�be", "h�be"), "gehoben", Perfektbildung.HABEN),
          new Basisformen("hei�en", "hei�t", "hei�t", alt("hei�e", "hei�"), "hie�", "hie�e",
              "gehei�en", Perfektbildung.HABEN),
          new Basisformen("helfen", "hilfst", "hilft", "hilf", "half", alt("h�lfe", "h�lfe"),
              "geholfen", Perfektbildung.HABEN),
          new Basisformen("kennen", "kennst", "kennt", alt("kenne", "kenn"), "kannte", "kennte",
              "gekannt", Perfektbildung.HABEN),
          new Basisformen("klimmen", "klimmst", "klimmt", alt("klimm", "klimme"),
              alt("klomm", "klimmte"), alt("kl�mme", "klimmte"), "geklommen", Perfektbildung.SEIN),
          new Basisformen("klingen", "klingst", "klingt", alt("kling", "klinge"), "klang", "kl�nge",
              "geklungen", Perfektbildung.HABEN),
          new Basisformen("kneifen", "kniffst", "kniff", alt("kneif", "kneife"), "kniff", "kniffe",
              "gekniffen", Perfektbildung.HABEN),
          new Basisformen("knien", "kniest", "kniet", "knie", "kniete", "kniete", "gekniet",
              Perfektbildung.HABEN),
          new Basisformen("kommen", "kommst", "kommt", alt("komm"), "kam", "k�me", "gekommen",
              Perfektbildung.SEIN),
          new Basisformen("kriechen", "kriechst", "kriecht", alt("kriech", "krieche"), "kroch",
              "kr�che", "gekrochen", Perfektbildung.SEIN),
          // erk�ren steht oben und hat also Vorrang
          new Basisformen("k�ren", "k�rst", "k�rt", alt("k�r", "k�re"), alt("k�rte", "kor"),
              alt("k�rte", "k�re"), "gekoren", Perfektbildung.HABEN),
          new Basisformen("laden", alt("l�dst", "ladest"), alt("l�dt", "ladet"), alt("lad", "lade"),
              "lud", "l�de", "geladen", Perfektbildung.HABEN),
          new Basisformen("lassen", "l�sst", "l�sst", alt("lass", "lasse"), "lie�", "lie�e",
              "gelassen", Perfektbildung.HABEN),
          new Basisformen("laufen", "l�ufst", "l�uft", alt("lauf", "laufe"), "lief", "liefe",
              "gelaufen", Perfektbildung.SEIN),
          new Basisformen("leiden", "leidest", "leidet", alt("leide", "leid"), "litt", "litte",
              "gelitten", Perfektbildung.HABEN),
          new Basisformen("leihen", "leihst", "leiht", alt("leih", "leihe"), "lieh", "liehe",
              "geliehen", Perfektbildung.HABEN),
          new Basisformen("lesen", "liest", "liest", "lies", "las", "l�se", "gelesen",
              Perfektbildung.HABEN),
          new Basisformen("liegen", "liegst", "liegt", alt("leg", "lege"), "lag", "l�ge", "gelegen",
              Perfektbildung.HABEN),
          new Basisformen("l�gen", "l�gst", "l�gt", alt("l�g", "l�ge"), "log", "l�ge", "gelogen",
              Perfektbildung.HABEN),
          new Basisformen("mahlen", "mahlst", "mahlt", alt("mahl", "mahle"), "mahlte", "mahlte",
              "gemahlen", Perfektbildung.HABEN),
          new Basisformen("meiden", "meidest", "meidet", alt("meide", "meid"), "mied", "miede",
              "gemieden", Perfektbildung.HABEN),
          new Basisformen("melken", "melkst", "melkt", alt("melk", "melke"), "molk", "m�lke",
              "gemolken", Perfektbildung.HABEN),
          new Basisformen("messen", "misst", "misst", "miss", "ma�", "m��e", "gemessen",
              Perfektbildung.HABEN),
          new Basisformen("misslingen", "misslingst", "misslingt", alt("missling", "misslinge"),
              "misslang", "missl�nge", "misslungen", Perfektbildung.SEIN),
          new Basisformen("nehmen", "nimmst", "nimmt", "nimm", "nahm", "n�hme", "genommen",
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
                  "pfl�ge"),
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
          new Basisformen("sehen", "siehst", "sieht", alt("sieh", "siehe"), "sah", "s�he",
              "gesehen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("einschlafen", "schl�fst|ein", // FIXME
              "schl�ft|ein", alt("schlaf|ein", "schlafe|ein"), "schlief|ein", "schliefe|ein",
              "eingeschlafen", Perfektbildung.SEIN), // IST
          // eingeschlafen,
          // aber
          // HAT
          // geschlafen!
          new Basisformen("schlafen", "schl�fst", "schl�ft", alt("schlaf", "schlafe"), "schlief",
              "schliefe", "geschlafen", Perfektbildung.HABEN),
          new Basisformen("schlagen", "schl�gst", "schl�gt", alt("schlag", "schlage"), "schlug",
              "schl�ge", "geschlagen", Perfektbildung.HABEN),

      // TODO ...
          new Basisformen("schlie�en", "schlie�t", "schlie�t", alt("schlie�", "schlie�e"),
              "schloss", "schl�sse", "geschlossen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("sinnen", "sinnst", "sinnt", alt("sinn", "sinne"), "sann",
              alt("s�nne", "s�nne"), "gesonnen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("sprechen", "sprichst", "spricht", "sprich", "sprach", "spr�che",
              "gesprochen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("stehlen", "stiehlst", "stiehlt", "stiehl", "stahl",
              alt("st�hle", "st�hle"), "gestohlen", Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("sterben", "stirbst", "stirbt", "stirb", "starb", "st�rbe",
              alt("gestorben", "�"), Perfektbildung.SEIN),
          // TODO...
          new Basisformen("tragen", "tr�gst", "tr�gt", "trag", "trug", "tr�ge", "getragen",
              Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("treffen", "triffst", "trifft", "triff", "traf", "tr�fe", "getroffen",
              Perfektbildung.HABEN),
          // TODO ...
          new Basisformen("wachsen", "w�chst", "w�chst", alt("wachs", "wachse"), "wuchs", "w�chse",
              "gewachsen", Perfektbildung.SEIN),
          // TODO ...
          new Basisformen("ziehen", alt("ziehst", "zeuchts"), alt("zieht", "zeucht"),
              alt("zieh", "ziehe", "zeuch"), "zog", "z�ge", "gezogen", Perfektbildung.HABEN) // TODO
                                                                                             // Sie
      // SIND
      // nach
      // Osten
      // gezogen.
      // TODO ...

      // @formatter:on
      // TODO Alle Verben mit allen ihren Valenzen in
      // VerbLister �bertragen
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
   *        Partizipien von Verben, die ein Pr�dikativ fordern (z.B. sein):
   *        <ul>
   *        <li>Ist das (implizite) Subjekt im Singular, so kann das Pr�dikatsnomen auch nur im
   *        Singular stehen: <i>Der KOMPONIST werdende Mann</i>, jedoch nicht <i>der *KOMPONISTEN
   *        werdende Mann</i>!
   *        <li>Ist das (implizite) Subjekt im Plural, so kann das Pr�dikatsnomen im Plural - oder
   *        auch im Singular stehen: <i>Die STANDARDS werdenden Vorgaben. Die STANDARD werdenden
   *        Vorgaben.</i>
   *        </ul>
   *        <code>null</code> erlaubt, falls diese Valenz kein Subjekt vorsieht (also keinen
   *        Infinitiv erlaubt)
   * @return Eine Infinitiv-Wortform dieser Valenz mit diesem String - oder <code>null</code>, falls
   *         die Valenz kein Subjekt vorsieht (also keinen Infinitiv erm�glicht)
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
   * Standard-Deklination eines Verbs gem�� Duden - finite Formen
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
    if (infinitive.equals("d�rfen")) {
      return duerfenFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("k�nnen")) {
      return koennenFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("m�gen")) {
      return moegenFin(variante.getValenz(), lexeme, pos);
    }
    if (infinitive.equals("m�ssen")) {
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
    if (infinitive.equals("d�rfen")) {
      return ImmutableList.of("gedurft");
    }
    if (infinitive.equals("k�nnen")) {
      return ImmutableList.of("gekonnt");
    }
    if (infinitive.equals("m�gen")) {
      return ImmutableList.of("gemocht");
    }
    if (infinitive.equals("m�ssen")) {
      return ImmutableList.of("gemusst", "gemu�t");
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
   * @return Standard-Deklination eines Verbs gem�� Duden - unflektiertes Partizip Pr�sens mit
   *         Wortform-String und Valenz (Subjekt ist IMPLIZIT) - oder <code>null</code>, falls es
   *         kein Partizip Pr�sens gibt (z.B. bei der Verbvariante, die bei "Mich deucht." vorliegt)
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
   * Standard-Deklination eines Verbs gem�� Duden - Imperative Sg. und Plural
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
   * Experiment</i>), nicht aber <i>*die gekr�nkelten Kinder</i>).
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
      // Auch Perfekt-Partizipien von Verben wie lehren, abh�ren
      // etc.
      // k�nnen au�erhalb des Verbalkomplexes verwendet werden.

      // gelehrt
      final Valenz valenzBeiImplizitemSUndAkkObj =
          valenzvariante.getValenz().beiImplizitemSubjektUndAkkusativObjekt();
      if (valenzBeiImplizitemSUndAkkObj != null) {
        // jdn. etwas lehren - der Franz�sisch gelehrte Mann
        valenzenBeiImplizitenErgaenzungen.add(valenzBeiImplizitemSUndAkkObj);
      }

      final Valenz valenzBeiImplizitemSUndZusPersAkkObj =
          valenzvariante.getValenz().beiImplizitemSubjektUndZusPersonAkkusativObjekt();
      if (valenzBeiImplizitemSUndZusPersAkkObj != null) {
        // jdn. etwas lehren - das den Mann gelehrte Franz�sisch
        valenzenBeiImplizitenErgaenzungen.add(valenzBeiImplizitemSUndAkkObj);
      }
    }

    if (valenzvariante.bildetZustandsreflexiv()) {
      final Valenz valenzBeiImplizitemSUndReflAkkObj =
          valenzvariante.getValenz().beiImplizitemSubjektUndReflAkkObj();
      if (valenzBeiImplizitemSUndReflAkkObj != null) {
        // sich erk�lten -> das erk�ltete Kind
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
   * Standard-Deklination eines Verbs gem�� Duden - Partizip Perfekt (Partizip II)
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
      // trauer(!), l�chel(!) - zumindest "in der Alltagssprache"
      // (aber nicht atm!*, sondern atme!)
      addStdImpIfNotNul(res, lexeme, pos, valenz, SINGULAR, stammGemaessInfinitiv);
    }

    // Duden 609 meint auch: "Verben deren Stamm auf d oder t [...] endet,
    // erhalten im Imperativ Sg. regelm��ig -e" - allerdings
    // empfinde ich pers�nlich "bind" als korrekt (nicht nur "binde").

    // gehe(!), trauere(!), l�chele(!)

    res.addAll(stdImp(lexeme, pos, valenz, SINGULAR,
        // Duden 609
        // trauern -> traure(!), l�cheln -> l�chle(!)
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
    // ge-lach-t, ge-gr�nd-e-t
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
    // Duden 609: "Im Plural werden Pr�sensformen verwendet."
    // geht(!)
    return ImmutableList.copyOf(
        stdImp(lexeme, pos, valenz, SINGULAR, stdP2PlPraesIndStrings(stammGemaessInfinitiv)));
  }

  /**
   * @return Basisformen f�r diesen Infinitiv - wenn es sich um eine Ausnahme handelt - sonst
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

    // lachen, l�cheln
    res.addAll(
        stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, ImmutableList.of(infinitiv)));

    res.addAll(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false,
        stdP2PlPraesIndStrings(stammPraesInd)));

    // lachen, l�cheln
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
      return ImmutableList.of("wei�");
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
      return ImmutableList.of("wei�t");
    }

    if (ausnahmeformen != null) {
      return ausnahmeformen.getP2SgPraesIndAltern();
    }

    final ImmutableList.Builder<String> res = ImmutableList.builder();

    if (GermanUtil.endetAufSLaut(stammPraesIndGemaessInfinitiv)) {
      // Sonderfall: rasen, k�ssen
      // du ras-t, du k�ss-t
      res.addAll(tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(
          stammPraesIndGemaessInfinitiv, "t"));
    } else {
      // Regelfall: Du lachst, du l�chelst, du naschst
      res.addAll(tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(
          stammPraesIndGemaessInfinitiv, "st"));
    }

    // In jedem Fall:
    // Wenn der Stamm auf -s oder -sch endet...
    if (GermanUtil.endetAufSLaut(stammPraesIndGemaessInfinitiv)
        || GermanUtil.endetAufSchLaut(stammPraesIndGemaessInfinitiv)) {
      // ..., so
      // KANN auch ein e eingeschoben werden (Duden 4, 2006 618)
      // du rasest, du k�ssest, du naschest ("poetisch, veraltet")
      res.addAll(tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(
          stammPraesIndGemaessInfinitiv, "est"));
    }

    return res.build();
  }

  /**
   * F�gt einen Stamm und eine Endung zusammen - k�mmert sich au�erdem um e-Tilgung (im Stamm) und
   * um e-Einschub (vor der Endung):
   * <ul>
   * <li>Wenn der Stamm auf -el, -en oder auf -er endet, wird - sofern gem�� Endung m�glich - auch
   * die Form geliefert, bei der das e des Stamms getilgt ist (also sowohl <i>sammel-e</i> als auch
   * <i>samml-e</i>).
   * <li>In manchen F�llen wird zwischen Stammd und Endung ein e eingeschoben (teilweise als einzige
   * M�glichkeit, teilweise werden beide M�glichkeiten geliefert).
   * </ul>
   */
  private Collection<String> tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(
      final String stamm, final String endung) {
    final ImmutableList.Builder<String> res = ImmutableList.builder();

    // lache, lacht, l�chele, l�chelt
    res.addAll(fuegeGgfEVorEndungEinUndHaengeEndungAn(stamm, endung));
    if (endung.startsWith("e")) {
      final String stammNachETilgung = GermanUtil.tilgeEAusStammWennMoeglich(stamm);

      if (stammNachETilgung != null) {
        // l�chl -> ich l�chle, bedau -> ich bedaure
        res.addAll(fuegeGgfEVorEndungEinUndHaengeEndungAn(stammNachETilgung, endung));
      }
    }

    return res.build();
  }

  /**
   * F�gt einen (ggf. schon e-getilgten!) Stamm und eine Endung zusammen - k�mmert sich au�erdem um
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
    // Duden 4 2006 618: "Au�erhalb der 2. Pers. Sg. Ind. Pr�s. findet
    // hier normalerweise
    // kein e-Einschub statt."
    // return false;
    // }

    // Ebenfalls ergibt sich automatisch, dass bei Verben auf -el kein e
    // eingef�gt wird
    // (du wandelst, Duden 620).

    if (!StringUtil.endsWith(stamm, "d", "t") && !GermanUtil.endetAufObstruentPlusMOderN(stamm)) {
      return false;
    }

    if (endung.startsWith("t") // t (Pr�s), te, test, tet (Pr�t)
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
      return ImmutableList.of("wei�");
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
    // "Gelegentlich ist der e-Einschub �ber die genannten Regelf�lle hinaus
    // anzutreffen, besonders bei Imperativformen der Luther�bersetzung der
    // Bibel.
    // Diese gelten als veraltet und dichterisch:
    // Seid fruchtbar und mehret euch[...]"
    // Wegen des Sets w�hrend Duplikate hier unproblematisch
    res.addAll(tilgeGgfEAusStammUndFuegeGgfEVorEndungEinUndHaengeEndungAn(stammPraesInd, "et"));

    return res.build();
  }

  /**
   * @return der Stamm der Indikativ-Pr�sens-Formen, wie er sich aus dem Infinitiv ergeben m�sste
   *         (ber�cksichtigt KEINE Ausnahmen!)
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

    // f�cheln, sammeln
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
      // H�flichkeitsform
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
        // oder -ern eine e-Tilgung im Stamm g�be. Duden 620 sagt das
        // nicht deutlich... -
        // ich habe allerdings (auch in Duden 704) kein einziges starkes
        // Verb auf -eln oder -ern gefunden.
        // Klar ist jedoch, dass es eine e-Einf�gung vor der Endung
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

    // dass wir lachen?, dass wir l�chlen?
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
      return stdPraeteritumSchwach(lexeme, pos, valenz, "w�ss", KONJUNKTIV);
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

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "w�re"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "w�rst"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "w�rest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "w�re"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "w�re"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "w�re"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "w�ren"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "w�rt"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "w�ret"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "w�ren"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "w�ren"));

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

    // TODO ..., dass ich habe* -> dass ich h�tte
    // -> neues Merkmal "alsKonjZuErkennen"?
    // (Ist es so �hnlich wie die F�lle, wo der Nominativ einspringt?
    // Wie sind diese F�lle gel�st? Oder �hnliche wie Genitivregel?)
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "habe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "habest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "habe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "habe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "habe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "haben"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "habet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "haben"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "haben"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "h�tte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "h�ttest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "h�tte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "h�tte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "h�tte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "h�tten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "h�ttet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "h�tten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "h�tten"));

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

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "w�rd"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "w�rde"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "w�rdest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "w�rd"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "w�rd"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "w�rd"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "w�rde"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "w�rde"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "w�rde"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "w�rden"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "w�rdet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "w�rden"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "w�rden"));

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
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "d�rfen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "d�rft"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "d�rfen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "d�rfen"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "durfte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "durfest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "durfte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "durfte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "durfte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "durften"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "durftet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "durften"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "durften"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "d�rfe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "d�rfest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "d�rfe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "d�rfe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "d�rfe"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "d�rften"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "d�rfet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "d�rften"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "d�rften"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "d�rfte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "d�rftest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "d�rfte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "d�rfte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "d�rfte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "d�rften"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "d�rftet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "d�rften"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "d�rften"));

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
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "k�nnen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "k�nnt"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "k�nnen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "k�nnen"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "konnte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "konntest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "konnte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "konnte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "konnte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "konnten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "konntet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "konnten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "konnten"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "k�nne"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "k�nnest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "k�nne"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "k�nne"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "k�nne"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "k�nnen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "k�nnet"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "k�nnen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "k�nnen"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "k�nnte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "k�nntest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "k�nnte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "k�nnte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "k�nnte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "k�nnten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "k�nntet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "k�nnten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "k�nnten"));

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
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "m�ssen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "m�sst"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "m�ssen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "m�ssen"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "musste"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "musstest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "musste"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "musste"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "musste"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "mussten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "musstet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "mussten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "mussten"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "m�sse"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "m�ssest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "m�sse"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "m�sse"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "m�sse"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "m�ssten"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "m�sset"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "m�ssten"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "m�ssten"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "m�sste"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "m�sstest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "m�sste"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "m�sste"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "m�sste"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "m�ssten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "m�sstet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "m�ssten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "m�ssten"));

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
    res.add(stdPraesInd(lexeme, pos, valenz, "1", null, PLURAL, false, "m�gen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "2", null, PLURAL, false, "m�gt"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, false, "m�gen"));
    res.add(stdPraesInd(lexeme, pos, valenz, "3", null, PLURAL, true, "m�gen"));

    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, SINGULAR, false, "mochte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, SINGULAR, false, "mochtest"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "mochte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "mochte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "mochte"));
    res.add(stdPraetInd(lexeme, pos, valenz, "1", null, PLURAL, false, "mochten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "2", null, PLURAL, false, "mochtet"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, false, "mochten"));
    res.add(stdPraetInd(lexeme, pos, valenz, "3", null, PLURAL, true, "mochten"));

    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "m�ge"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "m�gest"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "m�ge"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "m�ge"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "m�ge"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "m�gen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "m�get"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "m�gen"));
    res.add(stdPraesKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "m�gen"));

    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, SINGULAR, false, "m�chte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, SINGULAR, false, "m�chtest"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", MASKULINUM, SINGULAR, false, "m�chte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", FEMININUM, SINGULAR, false, "m�chte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", NEUTRUM, SINGULAR, false, "m�chte"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "1", null, PLURAL, false, "m�chten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "2", null, PLURAL, false, "m�chtet"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, false, "m�chten"));
    res.add(stdPraetKonj(lexeme, pos, valenz, "3", null, PLURAL, true, "m�chten"));

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
   *         erm�glicht)
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
   *        Partizipien von Verben, die ein Pr�dikativ fordern (z.B. sein):
   *        <ul>
   *        <li>Ist das (implizite) Subjekt im Singular, so kann das Pr�dikatsnomen auch nur im
   *        Singular stehen: Der KOMPONIST werdende Mann, jedoch nicht der *KOMPONISTEN werdende
   *        Mann!
   *        <li>Ist das (implizite) Subjekt im Plural, so kann das Pr�dikatsnomen im Plural - oder
   *        auch im Singular stehen: Die STANDARDS werdenden Vorgaben. Die STANDARD werdenden
   *        Vorgaben.
   *        </ul>
   *        <code>null</code> erlaubt, falls diese Valenz kein Subjekt vorsieht (also keinen
   *        Infinitiv erlaubt)
   * @return Eine Infinitiv-Wortform dieser Valenz mit diesem String - oder <code>null</code>, falls
   *         die Valenz kein Subjekt vorsieht (also keinen Infinitiv erm�glicht)
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
            // Die Person ist offenbar f�r REFLEXIVE
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
   *         die Valenz kein Subjekt vorsieht (also keinen Imperativ erm�glicht)
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
   * Die Basisformen f�r die Flexion eines Verbs (Stammformen und einige weitere) - relevant f�r
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
     * Alternativen f�r 2. Person Praesens Indikativ
     */
    private final ImmutableCollection<String> p2SgPraesIndAltern;
    /**
     * Alternativen f�r 3. Person Praesens Indikativ
     */
    private final ImmutableCollection<String> p3SgPraesIndAltern;
    /**
     * Alternativen f�r den Imperativ Singular
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
     * @return Basisform f�r diesen Infinitiv - sofern er zu dieser Basisform passt ("geben" und
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

      if (StringUtil.equals(prefix, "unter", "�ber", "um")) {
        // FIXME teilweise haben "verschiedene W�rter" (verschiedene
        // Valenzen) verschiedene Partizipien:
        // (Ich habe mich ihm) unterstellt vs. (ich habe mich wegen des
        // Regens) untergestellt.
        // (Ich habe das Bett) �berzogen vs. (ich habe den Pullover)
        // �bergezogen.
        // (Ich habe es) �bersetzt vs. (der F�hrmann hat) �bergesetzt.

        // FIXME Andere Verben gibt es nur in einer Version:
        // (Ich habe mich) �bernommen vs. (ich habe es) �bergest�lpt
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
     * @return z.B. <i>vernommen</i> (aus nehmen / genommen und dem Pr�fix <i>ver</i>)
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
