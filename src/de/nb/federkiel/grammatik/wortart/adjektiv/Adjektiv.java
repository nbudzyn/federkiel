/**
 * Ein Adjektiv.
 *
 * @author Nikolaj Budzyn
 */

package de.nb.federkiel.grammatik.wortart.adjektiv;

import javax.annotation.Nullable;

import de.nb.federkiel.grammatik.kategorie.AdjektivFlexionsklasse;
import de.nb.federkiel.grammatik.kategorie.Flexionstyp;
import de.nb.federkiel.grammatik.kategorie.Genus;
import de.nb.federkiel.grammatik.kategorie.Kasus;
import de.nb.federkiel.grammatik.kategorie.Numerus;
import de.nb.federkiel.grammatik.wortart.substantiv.Substantiv;
import de.nb.federkiel.string.StringUtil;

public class Adjektiv {
  /**
   * (ein) hoh(es) Regal
   */
  private final String stamm;

  /**
   * (Das Regal ist) hoch
   */
  private final String unflektiert;

  private final AdjektivFlexionsklasse flexionsklasse;

  /**
   * Die Substantivierung des Adjektivs. Bei <code>null</code> wird die
   * Substantivierung geraten.
   */
  private final @Nullable Substantiv substantivierung;

  public Adjektiv(final String stamm) {
    this(stamm, stamm);
  }

  public Adjektiv(final String stamm,
      final AdjektivFlexionsklasse flexionsklasse) {
    this(stamm, stamm, flexionsklasse, null);
  }

  public Adjektiv(final String stamm, final String unflektiert) {
    this(stamm, unflektiert, AdjektivFlexionsklasse.FLEKTIERT, null);
  }

  public Adjektiv(final String stamm, final String unflektiert,
      final AdjektivFlexionsklasse flexionsklasse,
      final @Nullable Substantiv substantiv) {
    this.stamm = stamm;
    this.flexionsklasse = flexionsklasse;
    this.unflektiert = unflektiert;
    substantivierung = substantiv;
  }

  /**
   * Schwache Flexion: der groﬂe Krieger die gute Frau das weiﬂe Einhorn des
   * groﬂen Kriegers der guten Frau des weiﬂen Einhorns dem groﬂen Krieger der
   * guten Frau dem weiﬂen Einhorn den groﬂen Krieger die gute Frau das weiﬂe
   * Einhorn die groﬂen Krieger die guten Frauen die weiﬂen Einhˆrner der groﬂen
   * Krieger der guten Frauen der weiﬂen Einhˆrner den groﬂen Kriegern den guten
   * Frauen den weiﬂen Einhˆrnern die groﬂen Krieger die guten Frauen die weiﬂen
   * Einhˆrner
   *
   * Starke Flexion: ein groﬂer Krieger eine gute Frau ein weiﬂes Einhorn eines
   * groﬂen Kriegers einer guten Frau eines weiﬂen Einhorns einem groﬂen Krieger
   * einer guten Frau einem weiﬂen Einhorn einen groﬂen Krieger eine gute Frau
   * ein weiﬂes Einhorn groﬂe Krieger gute Frauen weiﬂe Einhˆrner groﬂer Krieger
   * guter Frauen weiﬂer Einhˆrner groﬂen Kriegern guten Frauen weiﬂen
   * Einhˆrnern groﬂe Krieger gute Frauen weiﬂe Einhˆrner
   */
  public String getWortform(final Flexionstyp inflexionType, final Kasus kasus,
      final Numerus numerus, final Genus genus) {
    if (inflexionType == Flexionstyp.UNFLEKTIERT) {
      return getUnflektiert(); // hoch
    }

    if (flexionsklasse == AdjektivFlexionsklasse.NIE_FLEKTIERT) {
      return stamm; // lila
    }

    if (inflexionType == Flexionstyp.SCHWACHE_FLEXION) {
      return getWortformSchwacheFlexion(kasus, numerus, genus);
    }
    if (inflexionType == Flexionstyp.STARKE_FLEXION) {
      return getWortformStarkeFlexion(kasus, numerus, genus);
    }
    throw new RuntimeException("Unexpected InflexionType: " + inflexionType);
  }

  public String getStamm() {
    return stamm;
  }

  public String getUnflektiert() {
    return unflektiert;
  }

  public Substantiv toSubstantiv() {
    if (substantivierung != null) {
      return substantivierung;
    }
    return rateSubstantivierung();
  }

  /*
   * public String getWortform(InflexionType inflexionType, kasus kasus, Numerus
   * numerus, Genus genus, Object object) { return getWortform (inflexionType,
   * kasus, numerus, genus); }
   */

  // ------ PRIVATE ---------

  private String getWortformSchwacheFlexion(final Kasus kasus,
      final Numerus numerus, final Genus genus) {
    if (flexionsklasse == AdjektivFlexionsklasse.FLEKTIERTES_ZAHLADJEKTIV) {
      return stamm;
    }

    if (numerus == Numerus.SINGULAR
        && (kasus == Kasus.NOMINATIV || (kasus == Kasus.AKKUSATIV && genus != Genus.MASKULINUM))) {
      return stamm + "e";
    }
    return stamm + "en";
  }

  private String getWortformStarkeFlexion(final Kasus kasus,
      final Numerus numerus, final Genus genus) {
    if (flexionsklasse == AdjektivFlexionsklasse.FLEKTIERTES_ZAHLADJEKTIV) {
      if (kasus == Kasus.GENITIV && numerus == Numerus.PLURAL) {
        return stamm + "er";
      }

      return stamm;
    }

    if ((kasus == Kasus.NOMINATIV && numerus == Numerus.SINGULAR && genus == Genus.MASKULINUM)
        || (kasus == Kasus.GENITIV && numerus == Numerus.PLURAL)) {
      return stamm + "er";
    }
    if (((kasus == Kasus.NOMINATIV || kasus == Kasus.AKKUSATIV)
        && numerus == Numerus.SINGULAR && genus == Genus.FEMININUM)
        || (numerus == Numerus.PLURAL && (kasus == Kasus.NOMINATIV || kasus == Kasus.AKKUSATIV))) {
      return stamm + "e";
    }
    if ((kasus == Kasus.NOMINATIV || kasus == Kasus.AKKUSATIV)
        && numerus == Numerus.SINGULAR && genus == Genus.NEUTRUM) {
      return stamm + "es";
    }
    return stamm + "en";
  }

  private Substantiv rateSubstantivierung() {
    // IDEA...
    final String unflektiertesSubstantiv = StringUtil.capitalize(stamm
        + "lichkeit");
    return Substantiv.schwachDekliniert(unflektiertesSubstantiv,
        unflektiertesSubstantiv, unflektiertesSubstantiv + "en", Genus.FEMININUM);
  }

}
