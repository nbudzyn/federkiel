/**
 * Created: Sun Jan  5 20:35:42 2003
 *
 * @author Nikolaj Budzyn
 */
package de.nb.federkiel.deutsch.grammatik.wortart.substantiv;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.NOMINATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.artikelwort.Artikel;
import de.nb.federkiel.deutsch.grammatik.wortart.artikelwort.Artikeltyp;

public class Substantiv {
  private final Genus genus;
  private final boolean zaehlbar;

  private final String nominativSingular;
  private final String genitivSingular;
  private final String dativSingular;
  private final String akkusativSingular;

  private final @Nullable String nominativPlural;
  private final @Nullable String genitivPlural;
  private final @Nullable String dativPlural;
  private final @Nullable String akkusativPlural;

  Substantiv(final String nominativSingular, final String genitivSingular,
      final String dativSingular, final String akkusativSingular,
      final Genus genus, final boolean zaehlbar) {
    this(nominativSingular, genitivSingular, dativSingular, akkusativSingular, null, null, null,
        null, genus, zaehlbar);
  }

  public Substantiv(final String nominativSingular, final String genitivSingular,
      final String dativSingular, final String akkusativSingular,
      final @Nullable String nominativPlural,
      final @Nullable String genitivPlural, final @Nullable String dativPlural,
      final @Nullable String akkusativPlural,
      final Genus genus, final boolean zaehlbar) {
    this.genus = genus;
    this.zaehlbar = zaehlbar;
    this.nominativSingular = nominativSingular;
    this.genitivSingular = genitivSingular;
    this.dativSingular = dativSingular;
    this.akkusativSingular = akkusativSingular;
    this.nominativPlural = nominativPlural;
    this.genitivPlural = genitivPlural;
    this.dativPlural = dativPlural;
    this.akkusativPlural = akkusativPlural;
  }

  /**
   * Gibt ein zählbares Substantiv mit dieser Deklination zurück: der Ork, des
   * Orks, dem Ork, den Ork die Orks, der Orks, den Orks, die Orks
   */
  public static Substantiv imSingularStarkDekliniert(
      final String nominativSingular, final String genitivSingular,
      final String nominativPlural, final Genus genus) {
    return new Substantiv(nominativSingular, genitivSingular,
        nominativSingular, nominativSingular, nominativPlural, nominativPlural,
        nominativPlural, nominativPlural, genus, true);
  }

  /**
   * Gibt ein nicht zählbares Substantiv mit dieser Deklination zurück: das
   * Getreide, des Getreides, dem Getreide, das Getreide
   */
  public static Substantiv imSingularStarkDekliniert(
      final String nominativSingular, final String genitivSingular,
      final Genus genus) {
    return new Substantiv(nominativSingular, genitivSingular, nominativSingular, nominativSingular,
        genus, false);
  }

  /**
   * Gibt ein zählbares Substantiv mit dieser Deklination zurück: der Geist, des
   * Geistes, dem Geist, den Geist die Geister, der Geister, den Geistern, die
   * Geister
   */
  public static Substantiv imSingularStarkDekliniert(
      final String nominativSingular, final String genitivSingular,
      final String nominativPlural, final String dativPlural, final Genus genus) {
    return new Substantiv(nominativSingular, genitivSingular,
        nominativSingular, nominativSingular, nominativPlural, nominativPlural,
        dativPlural, nominativPlural, genus, true);
  }

  /**
   * Gibt ein zählbares Substantiv mit dieser Deklination zurück: die Frau, der
   * Frau, der Frau, die Frau die Frauen, der Frauen, die Frauen, der Frauen
   * Oder: der Mensch, des Menschen, dem Menschen, den Menschen die Menschen,
   * der Menschen, den Menschen, die Menschen
   */
  public static Substantiv schwachDekliniert(
      final String nominativSingular, final String genitivSingular,
      final String nominativPlural, final Genus genus) {

    return new Substantiv(nominativSingular, genitivSingular,
        genitivSingular, genitivSingular, nominativPlural, nominativPlural,
        nominativPlural, nominativPlural, genus, true);
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
    return genus;
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
    if (numerus == Numerus.SINGULAR) {
      return getWortformSingular(kasus);
    }

    return getWortformPlural(kasus);
  }

  public String getWortformMitArtikel(final Kasus kasus) {
    return getWortformMitArtikel(kasus, Numerus.SINGULAR);
  }

  public String getWortformMitArtikel(final Kasus kasus, final Numerus numerus) {
    return getWortformMitArtikel(kasus, numerus, Artikeltyp.BESTIMMT);
  }

  public String getWortformMitArtikel(final Kasus kasus, final Artikeltyp artikeltyp) {
    return getWortformMitArtikel(kasus, Numerus.SINGULAR, artikeltyp);
  }

  public String getWortformMitArtikel(final Kasus kasus, final Numerus numerus,
      final Artikeltyp artikeltyp) {
    if (numerus == Numerus.PLURAL && kasus == Kasus.GENITIV
        && artikeltyp == Artikeltyp.SG_UNBESTIMMT_PL_OHNE) {
      return "von " + getWortform(Kasus.DATIV, Numerus.PLURAL);
    }

    final StringBuilder res = new StringBuilder();

    final Artikel artikel = Artikel.createArtikel(artikeltyp, numerus);

    if (artikel != null) {
      res.append(artikel.getFlektiertAlsDeterminativFuer(kasus, numerus, genus, false));
      res.append(" ");
    }

    return getWortform(kasus, numerus);
  }

  // --------------- PACKAGE PRIVATE --------------------

  public String getWortformPlural(Kasus kasus) {
    if (!zaehlbar) {
      throw new RuntimeException("Tried to retrieve plural of uncountable noun " + this);
    }

    if (kasus == Kasus.NOMINATIV) {
      return nominativPlural;
    }
    if (kasus == Kasus.GENITIV) {
      return genitivPlural;
    }
    if (kasus == Kasus.DATIV) {
      return dativPlural;
    }

    return akkusativPlural;
  }

  // --------------- PRIVATE --------------

  private String getWortformSingular(final Kasus kasus) {
    if (kasus == Kasus.NOMINATIV) {
      return nominativSingular;
    }
    if (kasus == Kasus.GENITIV) {
      return genitivSingular;
    }
    if (kasus == Kasus.DATIV) {
      return dativSingular;
    }
    if (kasus == Kasus.AKKUSATIV) {
      return akkusativSingular;
    }

    throw new RuntimeException("Unexpected case: " + kasus);
  }
}
