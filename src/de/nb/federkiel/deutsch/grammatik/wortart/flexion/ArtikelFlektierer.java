package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;

/**
 * Kann Artikel flektieren
 *
 * @author nbudzyn 2009
 */
@ThreadSafe
public class ArtikelFlektierer extends AbstractArtikelUndPronomenFlektierer {
  public static final String TYP = "Artikel";

  public ArtikelFlektierer() {
    super();
  }

  /**
   * @param generateVorgabeFuerNachfolgendesAdjektiv
   *          ob die Merkmale erzeugt werden sollen, welche St‰rke / Schw‰che
   *          das nachfolgende Adjektiv haben soll
   */
  public Collection<IWordForm> definit(final Lexeme lexeme, final String pos,
      final boolean nurNominativ,
      final boolean generateVorgabeFuerNachfolgendesAdjektiv) {
    final Collection<IWordForm> res = new ArrayList<>(nurNominativ ? 4
        : 16);

    final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv = generateVorgabeFuerNachfolgendesAdjektiv ? VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH// definite
        // Artikel
        // tragen
        // alle eine Flexionsendung: d-er, d-ie etc. ("d-er groﬂe Mann")
        : VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN;

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, "der"));
    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, "die"));
    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, "das"));
    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
        vorgabeFuerNachfolgendesAdjektiv, PLURAL, null, "die"));

    if (!nurNominativ) {
      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, "des"));
      res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, "dem"));
      res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, "den"));

      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, "der"));
      res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, "der"));
      res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, "die"));

      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, "des"));
      res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, "dem"));
      res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, "das"));

      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_R,
          vorgabeFuerNachfolgendesAdjektiv, PLURAL, null, "der"));
      res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
          vorgabeFuerNachfolgendesAdjektiv, PLURAL, null, "den"));
      res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
          vorgabeFuerNachfolgendesAdjektiv, PLURAL, null, "die"));
    }

    return res;
  }

  public Collection<IWordForm> indefinit(final Lexeme lexeme, final String pos) {
    return einKeinUnser(lexeme, pos, false, // kein Plural
        true, // erzeugt wortartTraegtEndung-Merkmal
        false); // keine "St‰rke"
  }
}
