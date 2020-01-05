package de.nb.federkiel.feature;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A part of the <i>surface</i> (the parsed String).
 *
 * @author nbudzyn, 2019
 */
@Immutable
public class SurfacePart implements Comparable<SurfacePart> {
	/**
	 * the </i>whole input</i> (of which this object is some part)
	 */
	private final String surfaceOfAll;

	/**
	 * TOKEN index (in the input), where this sequence starts (0 is before the
	 * firstTerm word)
	 */
	private final int from;

	/**
	 * @return TOKEN index (in the input), where this sequence ends (0 is before the
	 *         firstTerm word)
	 */
	private final int to;

	/**
	 * the CHARACTER index (in the input), where this sequence starts (0 is the
	 * first character)
	 */
	private final int characterIndexFrom;

	private final int characterIndexTo;

	public SurfacePart(@Nonnull final String surfaceOfAll, final int from, final int to, final int characterIndexFrom,
			final int characterIndexTo) {
		checkNotNull(surfaceOfAll, "surfaceOfAll is null");

		if (from > to) {
			throw new IllegalArgumentException("from (" + from + ") > to (" + to + ")");
		}

		if (characterIndexFrom > characterIndexTo) {
			throw new IllegalArgumentException(
					"characterIndexFrom (" + characterIndexFrom + ") > characterIndexTo (" + characterIndexTo + ")");
		}

		this.surfaceOfAll = surfaceOfAll;
		this.from = from;
		this.to = to;
		this.characterIndexFrom = characterIndexFrom;
		this.characterIndexTo = characterIndexTo;
	}

	public SurfacePart join(final SurfacePart other) {
		if (!surfaceOfAll.equals(other.surfaceOfAll)) {
			throw new IllegalArgumentException("Not the same surface of all! - Cannot join.");
		}

		final int resFrom = Math.min(from, other.from);
		final int resTo = Math.max(to, other.to);
		final int resCharacterIndexFrom = Math.min(characterIndexFrom, other.characterIndexFrom);
		final int resCharacterIndexTo = Math.max(characterIndexTo, other.characterIndexTo);

		return new SurfacePart(surfaceOfAll, resFrom, resTo, resCharacterIndexFrom, resCharacterIndexTo);
	}

	@Nonnull
	public String getSurface() {
		return surfaceOfAll.substring(characterIndexFrom, characterIndexTo);
	}

	/**
	 * @return The number of words (from the input), that this realization
	 *         encompasses.
	 */
	public int getLength() {
		return to - from;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public int getCharacterIndexFrom() {
		return characterIndexFrom;
	}

	public int getCharacterIndexTo() {
		return characterIndexTo;
	}

	public String getSurfaceOfAll() {
		return surfaceOfAll;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + to;
		result = prime * result + from;
		// We do not check the other values - they will be equal in most cases
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
		final SurfacePart other = (SurfacePart) obj;
		if (from != other.from) {
			return false;
		}
		if (to != other.to) {
			return false;
		}
		if (characterIndexFrom != other.characterIndexFrom) {
			return false;
		}
		if (characterIndexTo != other.characterIndexTo) {
			return false;
		}
		if (!surfaceOfAll.equals(other.surfaceOfAll)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final SurfacePart o) {
		final int classNameCompared = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final SurfacePart other = o;

		if (getFrom() < other.getFrom()) {
			return -1;
		}

		if (getFrom() > other.getFrom()) {
			return 1;
		}

		if (getTo() < other.getTo()) {
			return -1;
		}

		if (getTo() > other.getTo()) {
			return 1;
		}

		if (characterIndexFrom < other.characterIndexFrom) {
			return -1;
		}

		if (characterIndexFrom > other.characterIndexFrom) {
			return 1;
		}

		if (characterIndexTo < other.characterIndexTo) {
			return -1;
		}

		if (characterIndexTo > other.characterIndexTo) {
			return 1;
		}

		return surfaceOfAll.compareTo(other.surfaceOfAll);
	}

	@Override
	public String toString() {
		return getSurface();
	}
}
