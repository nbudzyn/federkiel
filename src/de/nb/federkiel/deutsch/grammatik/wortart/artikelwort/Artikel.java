package de.nb.federkiel.deutsch.grammatik.wortart.artikelwort;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.IDeterminativ;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;

public enum Artikel implements IDeterminativ {
  BESTIMMT(ArtikelTyp.BESTIMMTER_ARTIKEL), UNBESTIMMT(
      ArtikelTyp.UNBESTIMMTER_ARTIKEL);

  private ArtikelTyp artikelTyp;

  Artikel(final ArtikelTyp artikelTyp) {
    this.artikelTyp = artikelTyp;
  }

  @Override
  public String getFlektiertAlsDeterminativFuer(
      final Kasus kasusBezugsphrase, final Numerus numerusBezugsphrase,
      final Genus genusBezugsphrase, final boolean negiert) {
    return ArtikelTyp.getArtikel(artikelTyp, kasusBezugsphrase,
        numerusBezugsphrase, genusBezugsphrase, negiert);
  }

  @Override
  public boolean hatFlexionsendung(Kasus kasusBezugsphrase, Numerus numerusBezugsphrase,
      Genus genusBezugsphrase) {
    return ArtikelTyp.hatFlexionsendung(artikelTyp, kasusBezugsphrase, numerusBezugsphrase,
        genusBezugsphrase);
  }

  public static Artikel fuerTyp(final ArtikelTyp artikelTyp) {
    if (artikelTyp == ArtikelTyp.KEIN_ARTIKEL) {
      return null;
    }

    switch (artikelTyp) {
    case BESTIMMTER_ARTIKEL:
      return BESTIMMT;
    case UNBESTIMMTER_ARTIKEL:
      return UNBESTIMMT;
    default:
      throw new IllegalStateException("Unerwarteter Artikeltyp: " + artikelTyp);
    }
  }
}
