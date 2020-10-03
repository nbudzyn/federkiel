package de.nb.federkiel.feature;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import de.nb.federkiel.interfaces.IFeatureType;

/**
 * Ask this class whenever you know a feature by name (<code>istSatzanfang</code>) and want to know
 * its type (<code>BOOLEAN</code>). The type will reveal the possible values for this feature.
 *
 * @author nbudzyn
 */
public class FeatureTypeDictionary {
  private final ConcurrentHashMap<String, IFeatureType> featureTypesByFeatureName =
      new ConcurrentHashMap<>();

  public FeatureTypeDictionary() {}

  public void put(final String featureName, final IFeatureType featureType) {
    featureTypesByFeatureName.put(featureName, featureType);
  }

  public @Nullable IFeatureType get(final String featureName) {
    return featureTypesByFeatureName.get(featureName);
  }
}
