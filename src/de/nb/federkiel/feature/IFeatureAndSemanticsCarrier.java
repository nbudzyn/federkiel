package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.interfaces.ISemantics;

/**
 * Something that has features and semantics.
 *
 * @author nbudzyn 2016
 */
@Immutable
@ThreadSafe
public interface IFeatureAndSemanticsCarrier extends IFeatureCarrier {
	public ISemantics getSemantics();
}
