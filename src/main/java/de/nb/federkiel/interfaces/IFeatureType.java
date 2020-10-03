package de.nb.federkiel.interfaces;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.feature.UnspecifiedFeatureValue;

/**
 * A type a feature could have. Feature types are optional.
 * <p>
 * All implementations MUST BE IMMUTABLE.
 *
 * @author nbudzyn 2017
 */
@javax.annotation.concurrent.Immutable
public interface IFeatureType {
  public default boolean isAllowed(final IFeatureValue featureValue) {
    if (featureValue == UnspecifiedFeatureValue.INSTANCE) {
      return true;
    }

    final ImmutableSet<IFeatureValue> allPossibleValues = getAllPossibleValues();
    return allPossibleValues == null || allPossibleValues.contains(featureValue);
  }

  /**
   * Returns all possible values of which a feature of this type may choose one from - or
   * <code>null</code>, if the values are unknown or not to numbered.
   */
  @Nullable
  ImmutableSet<IFeatureValue> getAllPossibleValues();
}
