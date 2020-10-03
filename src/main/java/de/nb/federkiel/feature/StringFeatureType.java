package de.nb.federkiel.feature;

import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.interfaces.IFeatureType;
import de.nb.federkiel.interfaces.IFeatureValue;

/**
 * A feature type for string feature values.
 *
 * @author nbudzyn
 */
public class StringFeatureType implements IFeatureType {
  @Override
  public ImmutableSet<IFeatureValue> getAllPossibleValues() {
    return null;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return true;
  }
}
