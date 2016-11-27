/**
 * Welcher Artikel gewünscht ist: kein Artikel, bestimmter Artikel, unbestimmter Artikel.
 *
 * @author Nikolaj Budzyn
 */
package de.nb.federkiel.grammatik.kategorie;

import static de.nb.federkiel.grammatik.kategorie.Kasus.AKKUSATIV;
import static de.nb.federkiel.grammatik.kategorie.Kasus.NOMINATIV;

public enum ArtikelTyp {
  KEIN_ARTIKEL, BESTIMMTER_ARTIKEL, UNBESTIMMTER_ARTIKEL;

  public static String getArtikel(final ArtikelTyp artikelTyp, final Kasus kasus,
      final Numerus numerus, final Genus genus, final boolean negiert) {
    switch (artikelTyp) {
      case BESTIMMTER_ARTIKEL:
        return getBestimmterArtikel(kasus, numerus, genus, negiert);
      case UNBESTIMMTER_ARTIKEL:
        return getUnbestimmterArtikel(kasus, numerus, genus, negiert);
      case KEIN_ARTIKEL:
        return getKeinArtikel(negiert);
      default:
        throw new RuntimeException("Unexpected article type: " + artikelTyp);
    }
  }

  /**
   * Gibt zurück, ob der Artikel eine Flexionsendung trägt (Duden 488) oder nicht.
   */
  public static boolean hatFlexionsendung(ArtikelTyp artikelTyp, final Kasus kasus,
      final Numerus numerus, final Genus genus) {
    switch (artikelTyp) {
      case BESTIMMTER_ARTIKEL:
        return true;
      case UNBESTIMMTER_ARTIKEL:
        return hatUnbestimmterArtikelFlexionsendung(kasus, numerus, genus);
      case KEIN_ARTIKEL:
        return false;
      default:
        throw new RuntimeException("Unexpected article type: " + artikelTyp);
    }
  }


  // ------------ PRIVATE --------------

  private static String getBestimmterArtikel(final Kasus kasus, final Numerus numerus,
      final Genus genus, final boolean negiert) {
    return negiert ? "nicht " + getBestimmterArtikel(kasus, numerus, genus)
        : getBestimmterArtikel(kasus, numerus, genus);
  }

  private static String getBestimmterArtikel(final Kasus kasus, final Numerus numerus,
      final Genus genus) {
    if (numerus == Numerus.SINGULAR) {
      return getBestimmterArtikelSingular(kasus, genus);
    }

    return getBestimmterArtikelPlural(kasus); // genus does not matter
  }

  private static String getBestimmterArtikelSingular(final Kasus kasus, final Genus genus) {
    if (genus == Genus.MASKULINUM) {
      return getBestimmterArtikelSingularMaskulinum(kasus);
    }
    if (genus == Genus.FEMININUM) {
      return getBestimmterArtikelSingularFemininum(kasus);
    }

    return getBestimmterArtikelSingularNeutrum(kasus);
  }

  private static String getBestimmterArtikelSingularMaskulinum(final Kasus kasus) {
    if (kasus == Kasus.NOMINATIV) {
      return "der";
    }
    if (kasus == Kasus.GENITIV) {
      return "des";
    }
    if (kasus == Kasus.NOMINATIV) {
      return "dem";
    }
    return "den";
  }

  private static String getBestimmterArtikelSingularFemininum(final Kasus kasus) {
    if (kasus == Kasus.NOMINATIV || kasus == Kasus.AKKUSATIV) {
      return "die";
    }
    return "der";
  }

  private static String getBestimmterArtikelSingularNeutrum(final Kasus kasus) {
    if (kasus == Kasus.NOMINATIV || kasus == Kasus.AKKUSATIV) {
      return "das";
    }
    if (kasus == Kasus.GENITIV) {
      return "des";
    }
    return "dem";
  }

  private static String getBestimmterArtikelPlural(final Kasus kasus) {
    if (kasus == Kasus.NOMINATIV || kasus == Kasus.AKKUSATIV) {
      return "die";
    }
    if (kasus == Kasus.GENITIV) {
      return "der";
    }
    return "den";
  }

  private static String getUnbestimmterArtikel(final Kasus kasus, final Numerus numerus,
      final Genus genus, final boolean negiert) {
    if (numerus == Numerus.SINGULAR && !negiert) {
      return getUnbestimmterArtikelSingular(kasus, genus);
    }
    if (numerus == Numerus.SINGULAR && negiert) {
      return "k" + getUnbestimmterArtikelSingular(kasus, genus);
    }

    return getUnbestimmterArtikelPlural(kasus, negiert);
  }


  private static boolean hatUnbestimmterArtikelFlexionsendung(Kasus kasus, Numerus numerus,
      Genus genus) {
    if (numerus == Numerus.SINGULAR) {
      return hatUnbestimmterArtikelSingularFlexionsendung(kasus, genus);
    }

    return false;
  }

  /**
   * Man beachte, dass es mit dem Genitiv Plural etwas Bestimmtes auf sich hat (&quot;von
   * Maennern&quot;?)
   */
  private static String getUnbestimmterArtikelPlural(final Kasus kasus, final boolean negiert) {
    if (!negiert) {
      return "";
    }
    if (kasus == Kasus.GENITIV) {
      return "keiner";
    }
    if (kasus == Kasus.DATIV) {
      return "keinen";
    }
    return "keine";
  }

  private static String getUnbestimmterArtikelSingular(final Kasus kasus, final Genus genus) {
    switch (genus) {
      case MASKULINUM:
        return getUnbestimmterArticleSingularMaskulinum(kasus);
      case FEMININUM:
        return getUnbestimmterArtikelSingularFemininum(kasus);
      case NEUTRUM:
        return getUnbestimmterArticleSingularNeutrum(kasus);
      default:
        throw new IllegalStateException("Unerwarterter Genus: " + genus);
    }
  }


  private static boolean hatUnbestimmterArtikelSingularFlexionsendung(Kasus kasus, Genus genus) {
    switch (genus) {
      case MASKULINUM:
        return kasus != NOMINATIV;
      case FEMININUM:
        return true;
      case NEUTRUM:
        return kasus != NOMINATIV && kasus != AKKUSATIV;
      default:
        throw new IllegalStateException("Unerwarterter Genus: " + genus);
    }
  }

  private static String getUnbestimmterArtikelSingularFemininum(final Kasus kasus) {
    if (kasus == Kasus.NOMINATIV || kasus == Kasus.AKKUSATIV) {
      return "eine";
    }
    return "einer";
  }

  private static String getUnbestimmterArticleSingularMaskulinum(final Kasus kasus) {
    switch (kasus) {
      case NOMINATIV:
        return "ein";
      case GENITIV:
        return "eines";
      case DATIV:
        return "einem";
      case AKKUSATIV:
        return "einen";
      default:
        throw new IllegalStateException("Unerwarterter Kasus: " + kasus);
    }
  }

  private static String getUnbestimmterArticleSingularNeutrum(final Kasus kasus) {
    if (kasus == Kasus.NOMINATIV || kasus == Kasus.AKKUSATIV) {
      return "ein";
    }
    if (kasus == Kasus.GENITIV) {
      return "eines";
    }
    return "einem";
  }

  private static String getKeinArtikel(final boolean negiert) {
    if (!negiert) {
      return "";
    }
    return "nicht";
  }
}
