/*
 * CountableNoun
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Created on 22.03.2003
 *
 */
package de.nb.federkiel.deutsch.grammatik.wortart.substantiv;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.artikelwort.ArtikelTyp;

/**
 * Ein zählbares Substantiv.
 * 
 * @author nikolaj
 */
class ZaehlbaresSubstantiv extends Substantiv {
  private final String nominativPlural;
  private final String genitivPlural;
  private final String dativPlural;
  private final String akkusativPlural;

  public ZaehlbaresSubstantiv(final String nominativSingular,
      final String genitivSingular, final String dativSingular,
      final String akkusativSingular, final String nominativPlural,
      final String genitivPlural, final String dativPlural,
      final String akkusativPlural, final Genus genus) {
    super(nominativSingular, genitivSingular, dativSingular, akkusativSingular,
        genus);
    this.nominativPlural = nominativPlural;
    this.genitivPlural = genitivPlural;
    this.dativPlural = dativPlural;
    this.akkusativPlural = akkusativPlural;
  }

  @Override
  public String getWortformMitArtikel(final Kasus kasus, final Numerus numerus,
      final ArtikelTyp artikelTyp) {
    if (numerus == Numerus.PLURAL && kasus == Kasus.GENITIV
        && artikelTyp == ArtikelTyp.UNBESTIMMTER_ARTIKEL)
      return "von " + getWortform(Kasus.DATIV, Numerus.PLURAL);

    return super.getWortformMitArtikel(kasus, numerus, artikelTyp);
  }

  // --------------- PACKAGE PRIVATE -----------

  @Override
  String getWortformPlural(final Kasus kasus) {
    if (kasus == Kasus.NOMINATIV)
      return this.nominativPlural;
    if (kasus == Kasus.GENITIV)
      return this.genitivPlural;
    if (kasus == Kasus.DATIV)
      return this.dativPlural;

    return this.akkusativPlural;
  }

  // --------------- PRIVATE --------------

}
