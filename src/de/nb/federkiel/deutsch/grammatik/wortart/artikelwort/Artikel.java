package de.nb.federkiel.deutsch.grammatik.wortart.artikelwort;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.ArtikelFlektierer;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.IndefinitpronomenFlektierer;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.SubstantivPronomenUtil;
import de.nb.federkiel.deutsch.lexikon.GermanLexemeType;
import de.nb.federkiel.deutsch.lexikon.GermanPOS;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;

public abstract class Artikel {
  public static final Artikel UNBESTIMMT = new UnbestimmterArtikel();
  public static final Artikel BESTIMMT = new BestimmterArtikel();

  final ArtikelFlektierer artikelFlekt = new ArtikelFlektierer();

  private Artikel() {}

  public static @Nullable Artikel createArtikel(Artikeltyp artikeltyp, Numerus numerus) {
    switch (artikeltyp) {
      case BESTIMMT:
        return BESTIMMT;
      case SG_UNBESTIMMT_PL_OHNE:
        return numerus == Numerus.SINGULAR ? UNBESTIMMT : null;
      case OHNE:
        return null;
      default:
        throw new IllegalStateException("Unerwarteter Artikeltyp: " + artikeltyp);
    }
  }

  public abstract String getFlektiertAlsDeterminativFuer(final Kasus kasusBezugsphrase,
      final Numerus numerusBezugsphrase, final Genus genusBezugsphrase, final boolean negiert);

  public abstract boolean hatFlexionsendung(Kasus kasusBezugsphrase, Numerus numerusBezugsphrase,
      Genus genusBezugsphrase);

  private static class UnbestimmterArtikel extends Artikel {
    private final Lexeme lexemEin = new Lexeme(GermanLexemeType.ARTIKEL, "ein", FeatureStructure
        .fromStringValues(ImmutableMap.of(GermanUtil.DEFINIT_KEY, StringFeatureLogicUtil.FALSE)));

    private final Lexeme lexemKein =
        SubstantivPronomenUtil.createIndefinitpronomen(GermanPOS.PIAT.toString(), "kein");

    private final IndefinitpronomenFlektierer indefinitpronomenFlekt =
        new IndefinitpronomenFlektierer();

    @Override
    public String getFlektiertAlsDeterminativFuer(final Kasus kasusBezugsphrase,
        final Numerus numerusBezugsphrase, final Genus genusBezugsphrase, final boolean negiert) {
      if (!negiert) {
        if (numerusBezugsphrase != SINGULAR) {
          throw new IllegalArgumentException("Plural des indefiniten Artikels gibt es nicht!");
        }

        return artikelFlekt
            .indefinit(lexemEin, GermanPOS.ART.toString(), kasusBezugsphrase, genusBezugsphrase)
            .get(0).getString();
      }

      // negiert

      return wortformNegativ(numerusBezugsphrase, kasusBezugsphrase, genusBezugsphrase).getString();
    }

    @Override
    public boolean hatFlexionsendung(Kasus kasusBezugsphrase, Numerus numerusBezugsphrase,
        Genus genusBezugsphrase) {
      final IWordForm wortform =
          wortformNegativ(numerusBezugsphrase, kasusBezugsphrase, genusBezugsphrase);;
      return FeatureStructure.toBoolean(
          wortform.getFeatureValue(GermanUtil.ERLAUBT_NACHGESTELLTES_SCHWACH_FLEKTIERTES_ADJEKTIV_KEY));
    }

    private IWordForm wortformNegativ(final Numerus numerus, final Kasus kasus, final Genus genus) {
      return indefinitpronomenFlekt.einKeinUnser(lexemKein, GermanPOS.PIAT.toString(), true, // Trotz
                                                                                             // Duden
                                                                                             // 1526
                                                                                             // -
                                                                                             // *keine
                                                                                             // bessere
                                                                                             // Nachrichten
                                                                                             // klingt
                                                                                             // falsch
          true, // kann als "stark" gelten
          numerus, kasus, genus).get(0);
    }
  }


  private static class BestimmterArtikel extends Artikel {
    private final Lexeme lexeme = new Lexeme(GermanLexemeType.ARTIKEL, "der", FeatureStructure
        .fromStringValues(ImmutableMap.of(GermanUtil.DEFINIT_KEY, StringFeatureLogicUtil.TRUE)));

    @Override
    public String getFlektiertAlsDeterminativFuer(final Kasus kasusBezugsphrase,
        final Numerus numerusBezugsphrase, final Genus genusBezugsphrase, final boolean negiert) {
      final StringBuilder res = new StringBuilder();
      if (negiert) {
        res.append("nicht ");
      }

      res.append(wortform(kasusBezugsphrase, numerusBezugsphrase, genusBezugsphrase).getString());

      return res.toString();
    }

    @Override
    public boolean hatFlexionsendung(Kasus kasusBezugsphrase, Numerus numerusBezugsphrase,
        Genus genusBezugsphrase) {
      return true;
    }

    private IWordForm wortform(Kasus kasus, Numerus numerus, Genus genus) {
      return artikelFlekt.definit(lexeme, GermanPOS.ART.toString(), true, kasus, numerus, genus);
    }
  }
}
