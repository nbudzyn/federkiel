/**
 * Noun.java
 *
 *
 * Created: Sun Jan  5 20:35:42 2003
 *
 * $Log$
 *
 *
 * @author Nikolaj Budzyn
 * @version
 */

package de.nb.federkiel.grammatik.wortart.substantiv;

import static de.nb.federkiel.grammatik.kategorie.Kasus.NOMINATIV;
import static de.nb.federkiel.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.grammatik.kategorie.Numerus.SINGULAR;
import de.nb.federkiel.grammatik.kategorie.ArtikelTyp;
import de.nb.federkiel.grammatik.kategorie.Genus;
import de.nb.federkiel.grammatik.kategorie.Kasus;
import de.nb.federkiel.grammatik.kategorie.Numerus;

abstract public class Substantiv {
  private final Genus genus;
  private final String nominativSingular;
  private final String genitivSingular;
  private final String dativSingular;
  private final String akkusativSingular;

  Substantiv(final String nominativSingular, final String genitivSingular,
      final String dativSingular, final String akkusativSingular,
      final Genus genus) {
    this.genus = genus;
    this.nominativSingular = nominativSingular;
    this.genitivSingular = genitivSingular;
    this.dativSingular = dativSingular;
    this.akkusativSingular = akkusativSingular;
  }

  /**
   * Gibt ein zählbares Substantiv mit dieser Deklination zurück: der Ork, des
   * Orks, dem Ork, den Ork die Orks, der Orks, den Orks, die Orks
   */
  public static ZaehlbaresSubstantiv imSingularStarkDekliniert(
      final String nominativSingular, final String genitivSingular,
      final String nominativPlural, final Genus genus) {
    return new ZaehlbaresSubstantiv(nominativSingular, genitivSingular,
        nominativSingular, nominativSingular, nominativPlural, nominativPlural,
        nominativPlural, nominativPlural, genus);
  }

  /**
   * Gibt ein nicht zählbares Substantiv mit dieser Deklination zurück: das
   * Getreide, des Getreides, dem Getreide, das Getreide
   */
  public static NichtZaehlbaresSubstantiv imSingularStarkDekliniert(
      final String nominativSingular, final String genitivSingular,
      final Genus genus) {
    return new NichtZaehlbaresSubstantiv(nominativSingular, genitivSingular,
        nominativSingular, nominativSingular, genus);
  }

  /**
   * Gibt ein zählbares Substantiv mit dieser Deklination zurück: der Geist, des
   * Geistes, dem Geist, den Geist die Geister, der Geister, den Geistern, die
   * Geister
   */
  public static ZaehlbaresSubstantiv imSingularStarkDekliniert(
      final String nominativSingular, final String genitivSingular,
      final String nominativPlural, final String dativPlural, final Genus genus) {
    return new ZaehlbaresSubstantiv(nominativSingular, genitivSingular,
        nominativSingular, nominativSingular, nominativPlural, nominativPlural,
        dativPlural, nominativPlural, genus);
  }

  /**
   * Gibt ein zählbares Substantiv mit dieser Deklination zurück: die Frau, der
   * Frau, der Frau, die Frau die Frauen, der Frauen, die Frauen, der Frauen
   * Oder: der Mensch, des Menschen, dem Menschen, den Menschen die Menschen,
   * der Menschen, den Menschen, die Menschen
   */
  public static ZaehlbaresSubstantiv schwachDekliniert(
      final String nominativSingular, final String genitivSingular,
      final String nominativPlural, final Genus genus) {

    return new ZaehlbaresSubstantiv(nominativSingular, genitivSingular,
        genitivSingular, genitivSingular, nominativPlural, nominativPlural,
        nominativPlural, nominativPlural, genus);
  }

  /**
   * Ob das Substantiv einen Singular besitzt. ("Leute" besitzt z.B. keinen
   * Singular.)
   */
  public boolean hatSingular() {
    return getWortform(NOMINATIV, SINGULAR) != null;
  }

  public boolean hatPlural() {
    return getWortform(NOMINATIV, PLURAL) != null;
  }

  public Genus getGenus() {
    return this.genus;
  }

  public String getWortform() {
    return getWortform(Kasus.NOMINATIV);
  }

  @Override
  public String toString() {
    return getWortform();
  }

  public String getWortform(final Kasus kasus) {
    return getWortform(kasus, Numerus.SINGULAR);
  }

  public String getWortform(final Kasus kasus, final Numerus numerus) {
    if (numerus == Numerus.SINGULAR)
      return getWortformSingular(kasus);

    return getWortformPlural(kasus);
  }

  public String getWortformMitArtikel(final Kasus kasus) {
    return getWortformMitArtikel(kasus, Numerus.SINGULAR);
  }

  public String getWortformMitArtikel(final Kasus kasus, final Numerus numerus) {
    return getWortformMitArtikel(kasus, numerus, ArtikelTyp.BESTIMMTER_ARTIKEL);
  }

  public String getWortformMitArtikel(final Kasus kasus,
      final ArtikelTyp artikelType) {
    return getWortformMitArtikel(kasus, Numerus.SINGULAR, artikelType);
  }

  public String getWortformMitArtikel(final Kasus kasus, final Numerus numerus,
      final ArtikelTyp artikelType) {
    final String article = ArtikelTyp.getArtikel(artikelType, kasus, numerus,
        this.genus, false);
    if (article != "")
      return article + " " + getWortform(kasus, numerus);

    return getWortform(kasus, numerus);
  }

  // --------------- PACKAGE PRIVATE --------------------

  abstract String getWortformPlural(Kasus kasus);

  // --------------- PRIVATE --------------

  private String getWortformSingular(final Kasus kasus) {
    if (kasus == Kasus.NOMINATIV)
      return this.nominativSingular;
    if (kasus == Kasus.GENITIV)
      return this.genitivSingular;
    if (kasus == Kasus.DATIV)
      return this.dativSingular;
    if (kasus == Kasus.AKKUSATIV)
      return this.akkusativSingular;

    throw new RuntimeException("Unexpected case: " + kasus);
  }
}
