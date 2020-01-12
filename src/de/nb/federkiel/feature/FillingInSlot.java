package de.nb.federkiel.feature;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.interfaces.IFeatureValue;
import net.jcip.annotations.Immutable;

/**
 * A filling inside a slot (as opposed to a <i>free filling</i>). A part of the
 * surface with some features and semantics
 *
 * @author nbudzyn 2019
 */
@Immutable
@org.checkthread.annotations.ThreadSafe
public class FillingInSlot implements IFeatureCarrier, IFeatureValue {
	/**
	 * The features
	 */
	private final FeatureStructure features;

	public FillingInSlot(final FeatureStructure features) {
		this.features = features;
	}

	@Override
	public IFeatureValue addFillingIfAccepted(IHomogeneousConstituentAlternatives freeFilling,
			int keepPlaceFreeForHowManyFillings) {
		return null;
	}

	@Override
	public Collection<FillingInSlot> getFillings() {
		return ImmutableList.of();
	}

	@Override
	public SurfacePart getSurfacePart() {
		return features.getSurfacePart();
	}

	@Override
	public FeatureStructure getFeatures() {
		return features;
	}

	@Override
	public int howManyFillingsAreMissingUntilCompletion() {
		return 0;
	}

	@Override
	public int howManyAdditionalFillingsAreAllowed() {
		return 0;
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
		return true;
	}

	@Override
	public int compareTo(final IFeatureValue o) {
		final int classNameCompared = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final FillingInSlot other = (FillingInSlot) o;

		final int featuresCompared = features.compareTo(other.getFeatures());
		if (featuresCompared != 0) {
			return featuresCompared;
		}

		return 0;
	}

	@Override
	public String toString() {
		return toString(true, false);
	}

	@Override
	public String toString(boolean neverShowRequirements, boolean forceShowRequirements) {
		return getFeatures().toString(neverShowRequirements, forceShowRequirements);
	}
}
