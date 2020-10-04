package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.GENUS_FEM;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.GENUS_MASK;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.GENUS_NEUT;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.feature.EnumStringFeatureType;
import de.nb.federkiel.feature.UnspecifiedFeatureValue;

/**
 * Static methods to convert enums etc. to feature values (and back).
 *
 * @author Nikolaj Budzyn
 */

public class FeatureStringConverter {
  private static final String KASUS_NOM = "nom";
  private static final String KASUS_GEN = "gen";
  private static final String KASUS_DAT = "dat";
  private static final String KASUS_AKK = "akk";

  public static final EnumStringFeatureType KASUS_FEATURE_TYPE =
      new EnumStringFeatureType(KASUS_NOM, KASUS_GEN, KASUS_DAT, KASUS_AKK);

  public static final String NUMERUS_SG = "sg";
  public static final String NUMERUS_PL = "pl";

  public static final EnumStringFeatureType NUMERUS_FEATURE_TYPE =
      new EnumStringFeatureType(NUMERUS_SG, NUMERUS_PL);

  private FeatureStringConverter() {}

	public static String toFeatureString(@Nullable final Kasus kasus) {
    if (kasus == null) {
      return UnspecifiedFeatureValue.UNSPECIFIED_STRING;
    }

    switch (kasus) {
    case NOMINATIV:
        return KASUS_NOM;
    case GENITIV:
        return KASUS_GEN;
    case DATIV:
        return KASUS_DAT;
    case AKKUSATIV:
        return KASUS_AKK;
    default:
      throw new IllegalStateException("Unexpected Kasus: " + kasus);
    }
  }

	public static String toFeatureString(@Nullable final Numerus numerus) {
    if (numerus == null) {
      return UnspecifiedFeatureValue.UNSPECIFIED_STRING;
    }

    switch (numerus) {
    case SINGULAR:
        return NUMERUS_SG;
    case PLURAL:
        return NUMERUS_PL;
    default:
      throw new IllegalStateException("Unexpected Numerus: " + numerus);
    }
  }

	public static String toFeatureString(final @Nullable Genus genus) {
    if (genus == null) {
      return UnspecifiedFeatureValue.UNSPECIFIED_STRING;
    }

    switch (genus) {
    case MASKULINUM:
        return GENUS_MASK;
    case FEMININUM:
        return GENUS_FEM;
    case NEUTRUM:
        return GENUS_NEUT;
    default:
      throw new IllegalStateException("Unexpected Genus: " + genus);
    }
  }

  public static @Nullable Genus toGenus(final @Nullable String string) {
    if (!UnspecifiedFeatureValue.notNullAndNotUnspecified(string)) {
      return null;
    }

    switch (string) {
      case GENUS_MASK:
      return MASKULINUM;
      case GENUS_FEM:
      return FEMININUM;
      case GENUS_NEUT:
      return NEUTRUM;
    default:
      throw new IllegalStateException("Unexpected Genus: " + string);
    }
  }
}
