package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;

/**
 * Kann Demonstrativpronomen flektieren.
 * <p>
 * Für <i>der</i> siehe {@link ArtikelFlektierer}.
 *
 * @author nbudzyn 2009
 */
@ThreadSafe()
public class DemonstrativpronomenFlektierer extends AbstractPronomenFlektierer {
  public static final String TYP = "Demonstrativpronomen";

  public DemonstrativpronomenFlektierer() {
    super();
  }

  public Collection<IWordForm> derSubstituierend(final Lexeme lexeme,
      final String pos) {
    final Collection<IWordForm> res = new ArrayList<>(16);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        "der"));
    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, FEMININUM,
        "die"));
    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, NEUTRUM,
        "das"));
    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, PLURAL,
 null, "die"));

    res.add(buildWortform(lexeme,
        pos, // ? Wir bedürfen dessen.
        KasusInfo.GEN_S, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
        SINGULAR, MASKULINUM, "dessen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        "dem"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        "den"));

    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, FEMININUM,
        "derer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, FEMININUM,
        "deren"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, FEMININUM,
        "der"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, FEMININUM,
        "die"));

    res.add(buildWortform(lexeme,
        pos, // fraglich
        KasusInfo.GEN_S, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
        SINGULAR, NEUTRUM, "dessen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, NEUTRUM,
        "dem"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, NEUTRUM,
        "das"));

    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, PLURAL,
 null, "derer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, PLURAL,
 null, "deren"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, PLURAL,
 null, "denen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, PLURAL,
 null, "die"));

    return res;
  }

  public Collection<IWordForm> derjenige(final Lexeme lexeme, final String pos) {
    final Collection<IWordForm> res = new ArrayList<>(16);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, // "derjenige große Mann",
        // "*derjenige großer Mann"
        SINGULAR, MASKULINUM, "derjenige"));
    res.add(buildWortform(lexeme,
        pos, // desjenigen Studenten
        KasusInfo.GEN_S, VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH,
        SINGULAR, MASKULINUM, "desjenigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        MASKULINUM,
        "demjenigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        MASKULINUM,
        "denjenigen"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "diejenige"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "derjenigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "derjenigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "diejenige"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "dasjenige"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "desjenigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "demjenigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "dasjenige"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "diejenigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "derjenigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "denjenigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "diejenigen"));

    return res;
  }

  public Collection<IWordForm> derselbe(final Lexeme lexeme, final String pos) {
    final Collection<IWordForm> res = new ArrayList<>(16);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, // *derselbe
        // großer
        // Mann
        SINGULAR, MASKULINUM, "derselbe"));
    res.add(buildWortform(lexeme,
        pos, // desseben Studenten
        KasusInfo.GEN_S, VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH,
        SINGULAR, MASKULINUM, "desselben"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        MASKULINUM,
        "demselben"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        MASKULINUM,
        "denselben"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "dieselbe"));
    res.add(buildWortform(lexeme,
        pos, // derselben Stadt
        KasusInfo.GEN_R, VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH,
        SINGULAR, FEMININUM, "derselben"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "derselben"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "dieselbe"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "dasselbe"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "desselben"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "demselben"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "dasselbe"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "dieselben"));
    res.add(buildWortform(lexeme,
        pos, // derselben Städte
        KasusInfo.GEN_R, VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH,
        PLURAL, null, "derselben"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "denselben"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "dieselben"));

    return res;
  }

  public Collection<IWordForm> derselbige(final Lexeme lexeme, final String pos) {
    final Collection<IWordForm> res = new ArrayList<>(16);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        MASKULINUM,
        "derselbige"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        MASKULINUM,
        "desselbigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        MASKULINUM,
        "demselbigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        MASKULINUM,
        "denselbigen"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "dieselbige"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "derselbigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "derselbigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        FEMININUM,
        "dieselbige"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "dasselbige"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "desselbigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "demselbigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "dasselbige"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "dieselbigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "derselbigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "denselbigen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, PLURAL,
 null,
        "dieselbigen"));

    return res;
  }

  public Collection<IWordForm> dergleichen(final Lexeme lexeme, final String pos) {
    final Collection<IWordForm> res = new ArrayList<>(4);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "dergleichen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "dergleichen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        "dergleichen"));

    return res;
  }

  public Collection<IWordForm> dieserEbendieser(final Lexeme lexeme,
      final String pos, final boolean auchGenitivMaskNeutr) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.addAll(typDieser(lexeme, pos,
        false, // keine "Stärke"
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH,
        auchGenitivMaskNeutr));

    final String nennform = lexeme.getCanonicalizedForm(); // dieser
    final String stamm = nennform.substring(0, nennform.length() - 2); // dies

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        stamm)); // dies
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH, SINGULAR,
        NEUTRUM,
        stamm)); // dies

    return res.build();
  }

  public Collection<IWordForm> ersterer(final Lexeme lexeme, final String pos) {
    final Collection<IWordForm> res = new ArrayList<>(16);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        // ersterer großer Mann, ?ersterer große Mann
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        MASKULINUM, "ersterer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        MASKULINUM, "ersteres")); // sehr ungewöhnlich
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        MASKULINUM, "ersterem"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        MASKULINUM, "ersteren"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        FEMININUM, "erstere"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        FEMININUM, "ersterer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        FEMININUM, "ersterer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        FEMININUM, "erstere"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        NEUTRUM, "ersteres"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        NEUTRUM, "ersteres")); // sehr ungewöhnlich
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        NEUTRUM, "ersterem"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        NEUTRUM, "ersteres"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, PLURAL,
        null, "erstere"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, PLURAL,
        null, "ersterer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, PLURAL,
        null, "ersteren"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, PLURAL,
        null, "erstere"));

    return res;
  }

  public Collection<IWordForm> letzterer(final Lexeme lexeme, final String pos) {
    final Collection<IWordForm> res = new ArrayList<>(16);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        MASKULINUM, "letzterer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        MASKULINUM, "letzteres")); // sehr ungewöhnlich
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        MASKULINUM, "letzterem"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        MASKULINUM, "letzteren"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        FEMININUM, "letztere"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        FEMININUM, "letzterer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        FEMININUM, "letzterer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        FEMININUM, "letztere"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        NEUTRUM, "letzteres"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        NEUTRUM, "letzteres")); // sehr ungewöhnlich
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        NEUTRUM, "letzterem"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, SINGULAR,
        NEUTRUM, "letzteres"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, PLURAL,
        null, "letztere"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, PLURAL,
        null, "letzterer"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, PLURAL,
        null, "letzteren"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_STARK_UND_SCHWACH, PLURAL,
        null, "letztere"));

    return res;
  }

}
