package de.nb.federkiel.feature;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.interfaces.IFeatureType;
import de.nb.federkiel.interfaces.IFeatureValue;

/**
 * A feature type for a role frame collection feature.
 *
 * @author nbudzyn
 */
public class RoleFrameCollectionFeatureType implements IFeatureType {
  public static final RoleFrameCollectionFeatureType INSTANCE =
      new RoleFrameCollectionFeatureType();

  private RoleFrameCollectionFeatureType() {};

  @Override
  public @Nullable ImmutableSet<IFeatureValue> getAllPossibleValues() {
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
