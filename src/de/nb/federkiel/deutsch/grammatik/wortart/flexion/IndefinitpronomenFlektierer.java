package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_UNFLEKTIERT;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.deutsch.grammatik.valenz.Valenz;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;

/**
 * Kann substituierende Indefinitpronomen flektieren (relevant für die Tags PIS,
 * PIAT, PIDAT).
 *
 * @author nbudzyn 2009
 */
@ThreadSafe
public class IndefinitpronomenFlektierer extends AbstractPronomenFlektierer {
  public static final String TYP = "Indefinitpronomen";

  public IndefinitpronomenFlektierer() {
    super();
  }

  /**
   * Returns the flection forms of <i>anderes</i>.
   *
   * @param lexeme
   *          <i>anderes</i>
   */
  public ImmutableCollection<IWordForm> anderes(final Lexeme lexeme,
      final String pos, final boolean auchGenitivMaskNeutr) {
    return ImmutableList.copyOf(typDieser(lexeme, pos, false, // keine "Stärke"
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, auchGenitivMaskNeutr));
  }

  /**
   * @param lexeme
   *          <i>einer</i>, <i>irgendeiner</i>
   */
  public Collection<IWordForm> einerIrgendeiner(final Lexeme lexeme,
      final String pos) {
    final String stem = lexeme.getCanonicalizedForm().substring(0,
        lexeme.getCanonicalizedForm().length() - 2); // "ein"

    return adjStarkSg(
        lexeme,
        pos,
        stem, // nur "eines" (Gen), nicht
        GenMaskNeutrSgModus.NUR_ES,
        // "*einen" (Gen)
        NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES, // "einer",
        // "eines",
        // "eins"
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, Valenz.LEER,
        ImmutableMap.of(), ImmutableMap.of());
  }

  /**
   * @param lexeme
   *          <i>unsereiner</i>, ...
   */
  public Collection<IWordForm> unsereinerEuereiner(final Lexeme lexeme,
      final String pos) {
    final String stem = lexeme.getCanonicalizedForm().substring(0,
        lexeme.getCanonicalizedForm().length() - 2);
    // "unserein"

    return adjStarkSg(
        lexeme,
        pos,
        stem, // nur "unsereines" (Gen), nicht
        GenMaskNeutrSgModus.NUR_ES,
        // "*unsereinen" (Gen)
        NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES, // "unsereiner",
        // "unsereines",
        // "unsereins"
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, Valenz.LEER,
        ImmutableMap.of(), ImmutableMap.of());
  }

  /**
   * @param lexeme
   *          <i>jemand</i>, <i>niemand</i>, ...
   */
  public Collection<IWordForm> jemandNiemand(final Lexeme lexeme,
      final String pos) {
    final String stem = lexeme.getCanonicalizedForm(); // "jemand"

    final Collection<IWordForm> res = new ArrayList<>(16);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        stem));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        stem
            + "es"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        stem
            + "em"));
    // FIXME: "mit jemand anderem"
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        stem
            + "en"));

    return res;
  }

  /**
   * @param lexeme
   *          <i>jedermann</i>
   */
  public Collection<IWordForm> jedermann(final Lexeme lexeme, final String pos) {
    final String stem = lexeme.getCanonicalizedForm(); // "jedermann"

    final Collection<IWordForm> res = new ArrayList<>(16);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        stem));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        stem
            + "s"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        stem));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        stem));

    return res;
  }

  public Collection<IWordForm> werIrgendwer(final Lexeme lexeme,
      final String pos) {
    final String prefix = lexeme.getCanonicalizedForm().substring(0,
        lexeme.getCanonicalizedForm().length() - 3); // "",
    // "irgend"

    final Collection<IWordForm> res = new ArrayList<>(16);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        prefix
            + "wer"));
    res.add(buildWortform(lexeme, pos, // da bin ich nicht sicher
        KasusInfo.GEN_S, // - ist aber wohl
        // auch egal
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        prefix
            + "wessen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        prefix
            + "wem"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        prefix
            + "wen"));

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, NEUTRUM,
        prefix + "was"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, NEUTRUM,
        prefix + "wessen"));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, NEUTRUM,
        prefix + "wem"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, NEUTRUM,
        prefix + "was"));

    res.add(buildWortformPlural(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, prefix
        + "welche", VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN));
    res.add(buildWortformPlural(lexeme, pos, KasusInfo.GEN_R, prefix
        + "welcher", VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN));
    res.add(buildWortformPlural(lexeme, pos, KasusInfo.DAT, prefix + "welchen",
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN));
    res.add(buildWortformPlural(lexeme, pos, KasusInfo.AKK, prefix + "welche",
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN));

    return res;
  }

  /**
   * Generates word forms for a word like <i>meinesgleichen</i> for all genera
   *
   * @param lexeme
   *          <i>meinesgleichen</i>, <i>ihresgleichen</i>, ...
   */
  public Collection<IWordForm> meinesgleichenDeinesgleichen(
      final Lexeme lexeme, final String pos) {
    final Collection<IWordForm> res = new ArrayList<>(12);

    res.addAll(meinesgleichenIhresgleichen(lexeme, pos, MASKULINUM));
    res.addAll(meinesgleichenIhresgleichen(lexeme, pos, FEMININUM));
    res.addAll(meinesgleichenIhresgleichen(lexeme, pos, NEUTRUM));

    return res;
  }

  /**
   * @param lexeme
   *          <i>unseresgleichen</i>, ...
   */
  public Collection<IWordForm> unseresgleichenEuresgleichenPl(
      final Lexeme lexeme, final String pos) {
    return meinesgleichenIhresgleichen(lexeme, pos, null);
  }

  /**
   * @param lexeme
   *          <i>meinesgleichen</i>, <i>ihresgleichen</i>, ...
   */
  public Collection<IWordForm> meinesgleichenIhresgleichen(final Lexeme lexeme,
      final String pos, final Genus genus) {
    final Collection<IWordForm> res = new ArrayList<>(4);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, genus,
        lexeme.getCanonicalizedForm()));
    // Genitiv ist in Duden 429 nicht genannt...
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, genus,
        lexeme.getCanonicalizedForm()));
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, genus,
        lexeme.getCanonicalizedForm()));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, genus,
        lexeme.getCanonicalizedForm()));

    return res;
  }

  /**
   * Substantivische Deklination eines Artikelworts oder Pronomens
   *
   * z.B. etwas, irgendetwas, mehr
   *
   * @param alleGeneraUndKasus
   *          ob alle Genera und Kasus erzeugt werden sollen (nur relevant für
   *          Artikelwörter, nicht für echte Pronomen)
   */
  public Collection<IWordForm> substantivisch(final Lexeme lexeme,
      final String pos, final boolean generateStaerke,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv,
      final boolean alleGeneraUndKasus, final boolean auchPlural) {
    final String stem = lexeme.getCanonicalizedForm(); // "etwas"

    final Collection<IWordForm> res = new ArrayList<>(16);

    final String staerke = generateStaerke ? STAERKE_UNFLEKTIERT : null;

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, staerke,
        vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, stem));
    if (alleGeneraUndKasus) {
      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, // *wir
                                                                     // bedürfen
                                                                     // etwas
                                                                     // Wasser
                                                                     // ->
          // wir bedürfen etwas WasserS!
          staerke, vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, stem)); // etwas
      // Wassers
    }
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT, staerke,
        vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, stem));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK, staerke,
        vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, stem));

    if (alleGeneraUndKasus) {
      res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, stem));
      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, stem)); // etwas
      // guter
      // Sauce
      res.add(buildWortform(lexeme, pos, KasusInfo.DAT, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, stem));
      res.add(buildWortform(lexeme, pos, KasusInfo.AKK, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, stem));

      res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, stem));
      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, stem)); // etwas
      // Specks
      res.add(buildWortform(lexeme, pos, KasusInfo.DAT, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, stem));
      res.add(buildWortform(lexeme, pos, KasusInfo.AKK, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, stem));
    }

    if (auchPlural) {
      // all (die Bücher)

      res.add(buildWortformPlural(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
          staerke, stem, vorgabeFuerNachfolgendesAdjektiv));
      if (alleGeneraUndKasus) {
        res.add(buildWortformPlural(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
            staerke, stem, vorgabeFuerNachfolgendesAdjektiv)); // all
        // (der
        // Bücher);
        // *wir
        // bedürfen
        // mehr
        // Bücher

      }
      res.add(buildWortformPlural(lexeme, pos, KasusInfo.DAT, staerke, stem,
          vorgabeFuerNachfolgendesAdjektiv));
      res.add(buildWortformPlural(lexeme, pos, KasusInfo.AKK, staerke, stem,
          vorgabeFuerNachfolgendesAdjektiv));
    }
    return res;
  }

  public Collection<IWordForm> man(final Lexeme lexeme, final String pos) {
    final Collection<IWordForm> res = new ArrayList<>(1);

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, SINGULAR, MASKULINUM,
        lexeme.getCanonicalizedForm())); // "man"

    return res;
  }
}
