package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_STARK;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_UNFLEKTIERT;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.deutsch.grammatik.valenz.Valenz;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;

/**
 * Sammelt einige Gemeinsamkeiten verschiedener Pronomenflektierer
 *
 * @author nbudzyn 2009
 */
@ThreadSafe()
abstract class AbstractPronomenFlektierer extends
    AbstractArtikelUndPronomenFlektierer {
  public AbstractPronomenFlektierer() {
    super();
  }

  /**
   * @param lexeme
   *          z.B. <i>dieser</i>
   * @param stamm
   *          z.B. <i>dies</i>
   *          <p>
   *          Darf nicht auf -e enden!
   * @param auchGenitivMaskNeutr
   *          ob auch der Genitiv Singular maskulinum und neutrum erzeut werden
   *          soll z.B. "alles" - diese Form kann im heutigen Deutsch meist
   *          nicht mehr ohne folgendes flektiertes Wort im gleichen Kasus
   *          verwendet werden (Duden 356): "der Verbrauch alles Fleischs", aber
   *          nicht "*der Verbrauch alles".
   * @return die Wortformen, auch unter Berücksichtigung möglicher e-Tilgung im
   *         Suffix
   */
  private Collection<IWordForm> typDieser(final Lexeme lexeme,
      final String pos,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv,
      final boolean generateStaerke, final String stamm,
      final boolean auchGenitivMaskNeutr) {
    return typDieser(lexeme, pos, stamm, vorgabeFuerNachfolgendesAdjektiv,
        generateStaerke, auchGenitivMaskNeutr, false); // Statt
    // -es
    // darf
    // in
    // Nominativ
    // und
    // Akkusativ
    // Singular
    // Neutrum nicht -s stehen. (*diess)
  }

  /**
   * @param lexeme
   *          z.B. <i>dieser</i>
   * @param stamm
   *          z.B. <i>dies</i>
   *          <p>
   *          Darf nicht auf -e enden!
   * @param auchGenitivMaskNeutr
   *          ob auch der Genitiv Singular maskulinum und neutrum erzeut werden
   *          soll z.B. "alles" - diese Form kann im heutigen Deutsch meist
   *          nicht mehr ohne folgendes flektiertes Wort im gleichen Kasus
   *          verwendet werden (Duden 356): "der Verbrauch alles Fleischs", aber
   *          nicht "*der Verbrauch alles".
   * @param nomAkkSgNeutrumAuchSStattEs
   *          ob im Nominativ und Akkusativ Singular Neutrum statt der Endung
   *          -es auch die Endung -s stehen kann ("seins" statt "seines" - aber
   *          z.B. nicht "*manchs" statt "manches")
   * @return die Wortformen, auch unter Berücksichtigung möglicher e-Tilgung im
   *         Suffix
   */
  private Collection<IWordForm> typDieser(final Lexeme lexeme,
      final String pos, final String stamm,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv,
      final boolean generateStaerke, final boolean auchGenitivMaskNeutr,
      final boolean nomAkkSgNeutrumAuchSStattEs) {

    final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

    res.addAll(typDieserSg(lexeme, pos, stamm,
        vorgabeFuerNachfolgendesAdjektiv, generateStaerke,
        auchGenitivMaskNeutr, nomAkkSgNeutrumAuchSStattEs));

    res.addAll(typDieserPl(lexeme, pos, stamm,
        vorgabeFuerNachfolgendesAdjektiv, generateStaerke));

    return res.build();
  }

  /**
   * @param stamm
   *          z.B. <i>dies</i>
   *          <p>
   *          Darf nicht auf -e enden!
   * @param vorgabeFuerNachfolgendesAdjektiv
   *          TODO
   * @param generateStaerke
   *          TODO
   * @param auchGenitivMaskNeutr
   *          ob auch der Genitiv Singular maskulinum und neutrum erzeut werden
   *          soll z.B. "alles" - diese Form kann im heutigen Deutsch meist
   *          nicht mehr ohne folgendes flektiertes Wort im gleichen Kasus
   *          verwendet werden (Duden 356): "der Verbrauch alles Fleischs", aber
   *          nicht "*der Verbrauch alles".
   * @param nomAkkSgNeutrumAuchSStattEs
   *          ob im Nominativ und Akkusativ Singular Neutrum statt der Endung
   *          -es auch die Endung -s stehen kann ("seins" statt "seines" - aber
   *          z.B. nicht "*manchs" statt "manches")
   * @return die Wortformen, auch unter Berücksichtigung möglicher e-Tilgung im
   *         Suffix
   */
  public Collection<IWordForm> typDieserSg(final Lexeme lexeme,
      final String pos, final String stamm,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv,
      final boolean generateStaerke, final boolean auchGenitivMaskNeutr,
      final boolean nomAkkSgNeutrumAuchSStattEs) {
    @Nullable
    final String staerke = generateStaerke ? STAERKE_STARK : null;
    return adjStarkSg(
        lexeme,
        pos,
        stamm,
        auchGenitivMaskNeutr ? GenMaskNeutrSgModus.ES_UND_EN // nicht immer
                                                             // anerkannt
            // (diesen Hauses)
            : GenMaskNeutrSgModus.NICHT,
        nomAkkSgNeutrumAuchSStattEs ? NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES
            : NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG,
        vorgabeFuerNachfolgendesAdjektiv, Valenz.LEER,
        buildFeatureMap(staerke), buildFeatureTypeMap(staerke));
  }

  public Collection<IWordForm> typDieserPl(final Lexeme lexeme,
      final String pos, final String stamm,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv,
      final boolean generateStaerke) {
    @Nullable
    final String staerke = generateStaerke ? STAERKE_STARK : null;
    return adjStarkPl(
        lexeme,
        vorgabeFuerNachfolgendesAdjektiv,
        pos,
        stamm,
        buildFeatureMap(staerke, Valenz.LEER
            .buildErgaenzungenUndAngabenSlots("3", null, PLURAL, StringFeatureLogicUtil.FALSE,
                true)),
        buildFeatureTypeMap(staerke));
    // Die ihrer selbst gedenkenden Männer, aber nicht
    // *die Ihrer selbst gedenkenden Männer!
  }

  /**
   * @param auchGenitivMaskNeutr
   *          ob auch der Genitiv Singular maskulinum und neutrum erzeut werden
   *          soll z.B. "alles" - diese Form kann im heutigen Deutsch meist
   *          nicht mehr ohne folgendes flektiertes Wort im gleichen Kasus
   *          verwendet werden (Duden 356): "der Verbrauch alles Fleischs", aber
   *          nicht "*der Verbrauch alles".
   */
  public Collection<IWordForm> typDieser(final Lexeme lexeme, final String pos,
      final boolean generateStaerke,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv,
      final boolean auchGenitivMaskNeutr) {
    final String nennform = lexeme.getCanonicalizedForm(); // dieser
    final String stamm = nennform.substring(0, nennform.length() - 2); // dies
    return typDieser(lexeme, pos, vorgabeFuerNachfolgendesAdjektiv,
        generateStaerke, stamm, auchGenitivMaskNeutr);
  }

  /**
   * Builds flection forms of <i>derlei</i>, <i>allerlei</i> etc.
   *
   * @param alleGeneraUndKasus
   *          ob alle Genera und Kasus erzeugt werden sollen (relevant für
   *          Artikelwörter, nicht für eigentliche Pronomen)
   * @param auchPlural
   *          ob auch Pluralformen erzeugt werden sollen (relevant für
   *          Artikelwörter, nicht für eigentliche Pronomen)
   */
  public ImmutableCollection<IWordForm> derleiAllerlei(final Lexeme lexeme,
      final String pos, final boolean generateStaerke,
      final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv,
      final boolean alleGeneraUndKasus, final boolean auchPlural) {
    final String stem = lexeme.getCanonicalizedForm().substring(0,
        lexeme.getCanonicalizedForm().length() - 5); // "all"

    final String staerke = generateStaerke ? STAERKE_UNFLEKTIERT : null;

    final ImmutableList.Builder<IWordForm> res = ImmutableList
        .<IWordForm> builder();

    res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, staerke,
        vorgabeFuerNachfolgendesAdjektiv,
        // vgl. Duden 1524
        SINGULAR, NEUTRUM, stem + "erlei"));
    res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, staerke,
        vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, stem + "erlei")); // mancherlei
    // Wassers
    res.add(buildWortform(lexeme, pos, KasusInfo.DAT, staerke,
        vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, stem + "erlei"));
    res.add(buildWortform(lexeme, pos, KasusInfo.AKK, staerke,
        vorgabeFuerNachfolgendesAdjektiv, SINGULAR, NEUTRUM, stem + "erlei"));

    if (auchPlural) {
      res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, staerke,
          vorgabeFuerNachfolgendesAdjektiv, PLURAL, NEUTRUM, stem + "erlei"));
      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, staerke,
          vorgabeFuerNachfolgendesAdjektiv, PLURAL, NEUTRUM, stem + "erlei"));
      res.add(buildWortform(lexeme, pos, KasusInfo.DAT, staerke,
          vorgabeFuerNachfolgendesAdjektiv, PLURAL, NEUTRUM, stem + "erlei"));
      res.add(buildWortform(lexeme, pos, KasusInfo.AKK, staerke,
          vorgabeFuerNachfolgendesAdjektiv, PLURAL, NEUTRUM, stem + "erlei"));
    }

    if (alleGeneraUndKasus) {
      res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, stem + "erlei"));
      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, stem + "erlei"));
      res.add(buildWortform(lexeme, pos, KasusInfo.DAT, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, stem + "erlei"));
      res.add(buildWortform(lexeme, pos, KasusInfo.AKK, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, FEMININUM, stem + "erlei"));
      if (auchPlural) {
        res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, staerke,
            vorgabeFuerNachfolgendesAdjektiv, PLURAL, FEMININUM, stem + "erlei"));
        res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, staerke,
            vorgabeFuerNachfolgendesAdjektiv, PLURAL, FEMININUM, stem + "erlei"));
        res.add(buildWortform(lexeme, pos, KasusInfo.DAT, staerke,
            vorgabeFuerNachfolgendesAdjektiv, PLURAL, FEMININUM, stem + "erlei"));
        res.add(buildWortform(lexeme, pos, KasusInfo.AKK, staerke,
            vorgabeFuerNachfolgendesAdjektiv, PLURAL, FEMININUM, stem + "erlei"));
      }

      res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, stem
              + "erlei"));
      res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, stem
              + "erlei"));
      res.add(buildWortform(lexeme, pos, KasusInfo.DAT, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, stem
              + "erlei"));
      res.add(buildWortform(lexeme, pos, KasusInfo.AKK, staerke,
          vorgabeFuerNachfolgendesAdjektiv, SINGULAR, MASKULINUM, stem
              + "erlei"));
      if (auchPlural) {
        res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN, staerke,
            vorgabeFuerNachfolgendesAdjektiv, PLURAL, MASKULINUM, stem
                + "erlei"));
        res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R, staerke,
            vorgabeFuerNachfolgendesAdjektiv, PLURAL, MASKULINUM, stem
                + "erlei"));
        res.add(buildWortform(lexeme, pos, KasusInfo.DAT, staerke,
            vorgabeFuerNachfolgendesAdjektiv, PLURAL, MASKULINUM, stem
                + "erlei"));
        res.add(buildWortform(lexeme, pos, KasusInfo.AKK, staerke,
            vorgabeFuerNachfolgendesAdjektiv, PLURAL, MASKULINUM, stem
                + "erlei"));
      }
    }

    return res.build();
  }

}
