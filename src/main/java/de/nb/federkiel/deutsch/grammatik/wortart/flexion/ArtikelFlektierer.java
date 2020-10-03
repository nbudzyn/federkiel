package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
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
   * @param generateVorgabeFuerNachfolgendesAdjektiv ob die Merkmale erzeugt werden sollen, welche
   *        St‰rke / Schw‰che das nachfolgende Adjektiv haben soll
   */
  public Collection<IWordForm> definit(final Lexeme lexeme, final String pos,
      final boolean nurNominativ, final boolean generateVorgabeFuerNachfolgendesAdjektiv) {
    return Stream.of(Numerus.values()).flatMap(numerus -> definit(lexeme, pos, nurNominativ,
        generateVorgabeFuerNachfolgendesAdjektiv, numerus).stream())
        .collect(Collectors.toCollection(ArrayList::new));

    // final Collection<IWordForm> res = new ArrayList<>(16);
    //
    // Stream.of(Kasus.values()).forEach(
    // kasus -> res.addAll(definit(lexeme, pos, generateVorgabeFuerNachfolgendesAdjektiv, kasus)));
    //
    // return res;
  }

  /**
   * @param generateVorgabeFuerNachfolgendesAdjektiv
   *          ob die Merkmale erzeugt werden sollen, welche St‰rke / Schw‰che
   *          das nachfolgende Adjektiv haben soll
   */
  private Collection<IWordForm> definit(final Lexeme lexeme, final String pos,
      final boolean nurNominativ, final boolean generateVorgabeFuerNachfolgendesAdjektiv,
      Numerus numerus) {
    switch (numerus) {
      case SINGULAR:
        return definitSg(lexeme, pos, nurNominativ, generateVorgabeFuerNachfolgendesAdjektiv);
      case PLURAL:
        return definitPl(lexeme, pos, nurNominativ, generateVorgabeFuerNachfolgendesAdjektiv);
      default:
        throw new IllegalStateException("Unerwarteter Numerus: " + numerus);
    }
  }

  /**
   * @param generateVorgabeFuerNachfolgendesAdjektiv ob die Merkmale erzeugt werden sollen, welche
   *        St‰rke / Schw‰che das nachfolgende Adjektiv haben soll
   */
  public IWordForm definit(final Lexeme lexeme, final String pos,
      final boolean generateVorgabeFuerNachfolgendesAdjektiv, Kasus kasus, Numerus numerus,
      Genus genus) {
    switch (numerus) {
      case SINGULAR:
        return definitSg(lexeme, pos, generateVorgabeFuerNachfolgendesAdjektiv, kasus, genus);
      case PLURAL:
        return definitPl(lexeme, pos, generateVorgabeFuerNachfolgendesAdjektiv, kasus);
      default:
        throw new IllegalStateException("Unerwarteter Numerus: " + numerus);
    }
  }

  /**
   * @param generateVorgabeFuerNachfolgendesAdjektiv ob die Merkmale erzeugt werden sollen, welche
   *        St‰rke / Schw‰che das nachfolgende Adjektiv haben soll
   */
  private Collection<IWordForm> definitSg(final Lexeme lexeme, final String pos,
      final boolean nurNominativ, final boolean generateVorgabeFuerNachfolgendesAdjektiv) {
    if (nurNominativ) {
      return definitSg(lexeme, pos, generateVorgabeFuerNachfolgendesAdjektiv, Kasus.NOMINATIV);
    }

    return Stream.of(Kasus.values())
        .flatMap(
            kasus -> definitSg(lexeme, pos, generateVorgabeFuerNachfolgendesAdjektiv, kasus)
                .stream())
        .collect(Collectors.toList());
  }

  /**
   * @param generateVorgabeFuerNachfolgendesAdjektiv ob die Merkmale erzeugt werden sollen, welche
   *        St‰rke / Schw‰che das nachfolgende Adjektiv haben soll
   */
  private Collection<IWordForm> definitSg(final Lexeme lexeme, final String pos,
      final boolean generateVorgabeFuerNachfolgendesAdjektiv, Kasus kasus) {
    return Stream.of(Genus.values())
        .map(
            genus -> definitSg(lexeme, pos, generateVorgabeFuerNachfolgendesAdjektiv, kasus, genus))
        .collect(Collectors.toList());
  }

  /**
   * @param generateVorgabeFuerNachfolgendesAdjektiv ob die Merkmale erzeugt werden sollen, welche
   *        St‰rke / Schw‰che das nachfolgende Adjektiv haben soll
   */
  private IWordForm definitSg(final Lexeme lexeme, final String pos,
      final boolean generateVorgabeFuerNachfolgendesAdjektiv,
      Kasus kasus, Genus genus) {
    final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv = generateVorgabeFuerNachfolgendesAdjektiv ? VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH // definite
        // Artikel
        // tragen
        // alle eine Flexionsendung: d-er, d-ie etc. ("d-er groﬂe Mann")
        : VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN;

    switch(kasus) {
      case NOMINATIV:
        switch(genus) {
          case MASKULINUM:
            return buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, "der");
          case FEMININUM:
            return buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, "die");
          case NEUTRUM:
            return buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, "das");
          default:
            throw new IllegalStateException("Unerwartetes Genus: " + genus);
        }
      case GENITIV:
        switch(genus) {
          case MASKULINUM:
            return buildWortform(lexeme, pos, KasusInfo.GEN_S,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, "des");
          case FEMININUM:
            return buildWortform(lexeme, pos, KasusInfo.GEN_R,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, "der");
          case NEUTRUM:
            return buildWortform(lexeme, pos, KasusInfo.GEN_S,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, "des");
          default:
            throw new IllegalStateException("Unerwartetes Genus: " + genus);
        }

      case DATIV:
        switch(genus) {
          case MASKULINUM:
            return buildWortform(lexeme, pos, KasusInfo.DAT,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, "dem");
          case FEMININUM:
            return buildWortform(lexeme, pos, KasusInfo.DAT,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, "der");
          case NEUTRUM:
            return buildWortform(lexeme, pos, KasusInfo.DAT,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, "dem");
          default:
            throw new IllegalStateException("Unerwartetes Genus: " + genus);
        }

      case AKKUSATIV:
        switch(genus) {
          case MASKULINUM:
            return buildWortform(lexeme, pos, KasusInfo.AKK,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, "den");
          case FEMININUM:
            return buildWortform(lexeme, pos, KasusInfo.AKK,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, "die");
          case NEUTRUM:
            return buildWortform(lexeme, pos, KasusInfo.AKK,
                vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, "das");
          default:
            throw new IllegalStateException("Unerwartetes Genus: " + genus);
        }

      default:
        throw new IllegalStateException("Unerwarteter Kasus: " + kasus);
    }
  }

  /**
   * @param generateVorgabeFuerNachfolgendesAdjektiv ob die Merkmale erzeugt werden sollen, welche
   *        St‰rke / Schw‰che das nachfolgende Adjektiv haben soll
   */
  private Collection<IWordForm> definitPl(final Lexeme lexeme, final String pos,
      final boolean nurNominativ, final boolean generateVorgabeFuerNachfolgendesAdjektiv) {
    if (nurNominativ) {
      return ImmutableList
          .of(definitPl(lexeme, pos, generateVorgabeFuerNachfolgendesAdjektiv, Kasus.NOMINATIV));
    }

    return Stream.of(Kasus.values()).map(
        kasus -> definitPl(lexeme, pos, generateVorgabeFuerNachfolgendesAdjektiv, kasus))
        .collect(Collectors.toList());
  }

  /**
   * @param generateVorgabeFuerNachfolgendesAdjektiv ob die Merkmale erzeugt werden sollen, welche
   *        St‰rke / Schw‰che das nachfolgende Adjektiv haben soll
   */
  private IWordForm definitPl(final Lexeme lexeme, final String pos,
      final boolean generateVorgabeFuerNachfolgendesAdjektiv, Kasus kasus) {
    final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv = generateVorgabeFuerNachfolgendesAdjektiv ? VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH// definite
        // Artikel
        // tragen
        // alle eine Flexionsendung: d-er, d-ie etc. ("d-er groﬂe Mann")
        : VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN;

    switch (kasus) {
      case NOMINATIV:
        return buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
            vorgabeFuerNachfolgendesAdjektiv, PLURAL, null, "die");
      case GENITIV:
        return buildWortform(lexeme, pos, KasusInfo.GEN_R, vorgabeFuerNachfolgendesAdjektiv, PLURAL,
            null, "der");
      case DATIV:
        return buildWortform(lexeme, pos, KasusInfo.DAT, vorgabeFuerNachfolgendesAdjektiv, PLURAL,
            null, "den");
      case AKKUSATIV:
        return buildWortform(lexeme, pos, KasusInfo.AKK, vorgabeFuerNachfolgendesAdjektiv, PLURAL,
            null, "die");
      default:
        throw new IllegalStateException("Unerwarteter Kasus " + kasus);
    }
  }

  public Collection<IWordForm> indefinit(final Lexeme lexeme, final String pos) {
    return Stream.of(Kasus.values()).flatMap(kasus -> indefinit(lexeme, pos, kasus).stream())
        .collect(Collectors.toList());
  }

  private Collection<IWordForm> indefinit(final Lexeme lexeme, final String pos, Kasus kasus) {
    return Stream.of(Genus.values()).flatMap(genus -> indefinit(lexeme, pos, kasus, genus).stream())
        .collect(Collectors.toList());
  }

  public ImmutableList<IWordForm> indefinit(final Lexeme lexeme, final String pos, Kasus kasus,
      Genus genus) {
    return einKeinUnser(lexeme, pos,
        true, // erzeugt wortartTraegtEndung-Merkmal
        false, // keine "St‰rke"
        Numerus.SINGULAR, // keine Plural
        kasus, genus);
  }
}
