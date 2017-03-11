package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.feature.UnspecifiedFeatureValue;

/**
 * Static methods to convert enums etc. to feature values (and back).
 *
 * @author Nikolaj Budzyn
 */

public class FeatureStringConverter {
  private FeatureStringConverter() {
  }

  public static @Nullable String toFeatureString(final Kasus kasus) {
    if (kasus == null) {
      return UnspecifiedFeatureValue.UNSPECIFIED_STRING;
    }

    switch (kasus) {
    case NOMINATIV:
      return "nom";
    case GENITIV:
      return "gen";
    case DATIV:
      return "dat";
    case AKKUSATIV:
      return "akk";
    default:
      throw new IllegalStateException("Unexpected Kasus: " + kasus);
    }
  }

  public static @Nullable String toFeatureString(final Numerus numerus) {
    if (numerus == null) {
      return UnspecifiedFeatureValue.UNSPECIFIED_STRING;
    }

    switch (numerus) {
    case SINGULAR:
      return "sg";
    case PLURAL:
      return "pl";
    default:
      throw new IllegalStateException("Unexpected Numerus: " + numerus);
    }
  }

  public static @Nullable String toFeatureString(final @Nullable Genus genus) {
    if (genus == null) {
      return UnspecifiedFeatureValue.UNSPECIFIED_STRING;
    }

    switch (genus) {
    case MASKULINUM:
      return SubstantivPronomenUtil.GENUS_MASK;
    case FEMININUM:
      return SubstantivPronomenUtil.GENUS_FEM;
    case NEUTRUM:
      return SubstantivPronomenUtil.GENUS_NEUT;
    default:
      throw new IllegalStateException("Unexpected Genus: " + genus);
    }
  }

  public static @Nullable Genus toGenus(final @Nullable String string) {
    if (!UnspecifiedFeatureValue.notNullAndNotUnspecified(string)) {
      return null;
    }

    switch (string) {
    case SubstantivPronomenUtil.GENUS_MASK:
      return MASKULINUM;
    case SubstantivPronomenUtil.GENUS_FEM:
      return FEMININUM;
    case SubstantivPronomenUtil.GENUS_NEUT:
      return NEUTRUM;
    default:
      throw new IllegalStateException("Unexpected Genus: " + string);
    }
  }

}
