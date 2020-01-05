package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Something that has features.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public interface IFeatureCarrier {
	FeatureStructure getFeatures();
}
