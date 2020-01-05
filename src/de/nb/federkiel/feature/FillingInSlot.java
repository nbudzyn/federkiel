package de.nb.federkiel.feature;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.ISemantics;
import net.jcip.annotations.Immutable;

/**
 * A filling inside a slot (as opposed to a <i>free filling</i>). A part surface
 * with some features and semantics
 *
 * @author nbudzyn 2019
 */
@Immutable
@org.checkthread.annotations.ThreadSafe
public class FillingInSlot implements IFillingInSlot {
	/**
	 * The features
	 */
	private final FeatureStructure features;

	/**
	 * The semantics
	 */
	private final ISemantics semantics;

	public FillingInSlot(final FeatureStructure features, final ISemantics semantics) {
		this.features = features;
		this.semantics = semantics;
	}

	@Override
	public FeatureStructure getFeatures() {
		return features;
	}

	@Override
	public ISemantics getSemantics() {
		return semantics;
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (features == null ? 0 : features.hashCode());
		result = prime * result + (semantics == null ? 0 : semantics.hashCode());
		return result;
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
		final FillingInSlot other = (FillingInSlot) obj;
		if (features == null) {
			if (other.features != null) {
				return false;
			}
		} else if (!features.equals(other.features)) {
			return false;
		}
		if (semantics == null) {
			if (other.semantics != null) {
				return false;
			}
		} else if (!semantics.equals(other.semantics)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final IFeatureValue o) {
		final int classNameCompared = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final IFillingInSlot other = (IFillingInSlot) o;

		final int featuresCompared = features.compareTo(other.getFeatures());
		if (featuresCompared != 0) {
			return featuresCompared;
		}

		final int semanticsCompared = semantics.compareTo(other.getSemantics());
		if (semanticsCompared != 0) {
			return semanticsCompared;
		}

		return 0;
	}

	@Override
	public String toString() {
		return getFeatures().toString();
	}
}
