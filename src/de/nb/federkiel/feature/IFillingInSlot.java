package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.interfaces.IFeatureValue;

/**
 * A filling inside a slot (as opposed to a <i>free filling</i>). A part surface
 * with some features and semantics
 *
 * @author nbudzyn 2019
 */
@Immutable
public interface IFillingInSlot extends Comparable<IFeatureValue>, IFeatureAndSemanticsCarrier, IFeatureValue {
}