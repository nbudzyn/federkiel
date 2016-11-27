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
   * Schwache Flexion: der gro�e Krieger die gute Frau das wei�e Einhorn des
   * gro�en Kriegers der guten Frau des wei�en Einhorns dem gro�en Krieger der
   * guten Frau dem wei�en Einhorn den gro�en Krieger die gute Frau das wei�e
   * Einhorn die gro�en Krieger die guten Frauen die wei�en Einh�rner der gro�en
   * Krieger der guten Frauen der wei�en Einh�rner den gro�en Kriegern den guten
   * Frauen den wei�en Einh�rnern die gro�en Krieger die guten Frauen die wei�en
   * Einh�rner
   *
   * Starke Flexion: ein gro�er Krieger eine gute Frau ein wei�es Einhorn eines
   * gro�en Kriegers einer guten Frau eines wei�en Einhorns einem gro�en Krieger
   * einer guten Frau einem wei�en Einhorn einen gro�en Krieger eine gute Frau
   * ein wei�es Einhorn gro�e Krieger gute Frauen wei�e Einh�rner gro�er Krieger
   * guter Frauen wei�er Einh�rner gro�en Kriegern guten Frauen wei�en
   * Einh�rnern gro�e Krieger gute Frauen wei�e Einh�rner
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
