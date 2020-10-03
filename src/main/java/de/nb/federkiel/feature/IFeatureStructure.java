package de.nb.federkiel.feature;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.ISemantics;

/**
 * Something that has features.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public interface IFeatureStructure {
	@Nullable
	SurfacePart getSurfacePart();

	IFeatureValue getFeatureValue(final String name);

	ISemantics getSemantics();
}
