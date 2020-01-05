package de.nb.federkiel.feature;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.logic.AbstractTermBounds;
import de.nb.federkiel.logic.CannotFulfillTermException;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A &quot;bound&quot; to a term, that says, that this term <i>can only have
 * one, fixed value</i>
 * <p>
 * T is the value type.
 *
 * @author nbudzyn 2010
 */
@Immutable
@Deprecated
public class OnlyValueBound<T> extends AbstractTermBounds implements
ITermBounds {
	/**
	 * the only allowed value, might even be <code>null</code>
	 */
	private final T onlyValue;

	public OnlyValueBound(final T onlyValue) {
		this.onlyValue = onlyValue;
	}

	@Override
	public ITermBounds combineBounds(final OnlyValueBound<?> otherOnlyValueBound)
	throws CannotFulfillTermException {
    if (Objects.equals(this.onlyValue, otherOnlyValueBound.onlyValue)) {
			return this;
		} else {
			// no solution possible
			throw new CannotFulfillTermException();
		}
	}

	@Override
	public ITermBounds combineBounds(final RoleFrame roleFrameBounds)
	throws CannotFulfillTermException {
		if (valueMeetsRoleFrameRestrictions(roleFrameBounds)) {
			return this;
		}

		throw new CannotFulfillTermException();
	}

	@Override
	public Plurival<RoleFrame> mergeBounds(final ITermBounds other) {
		/*
		if (this.onlyValue == null) {
			return new Plurival<RoleFrame>();
		}

		if (this.onlyValue instanceof RoleFrame) {
			final RoleFrame onlyRoleFrame = (RoleFrame) this.onlyValue;

			return null; // onlyRoleFrame.mergeBounds(other);
		}

		if (this.onlyValue instanceof RoleFrame) {
			final RoleFrame onlyRoleFrame = (RoleFrame) this.onlyValue;

			return null; // onlyRoleFrame.mergeBounds(other);
		}

		if (this.onlyValue instanceof RoleFrameSlot) {
			...
		}

		 */
		return Plurival.empty();
	}

	/*
	private static Plurival<RoleFrame> mergeBounds(
			final RoleFrameCollection boundsGivenAsRoleFrameCollection,
			final ITermBounds otherBounds) {
		if (otherBounds instanceof OnlyValueBound<?>) {
			final Object otherOnlyValue = ((OnlyValueBound<?>) otherBounds).onlyValue;

			if (otherOnlyValue == null) {
				return new Plurival<RoleFrame>();
			}
			if (otherOnlyValue instanceof RoleFrame) {
				final RoleFrame otherOnlyRoleFrame = (RoleFrame) otherOnlyValue;

				return null;
				/*boundsGivenAsRoleFrameCollection
				.mergeBoundsGivenAsRoleFrameCollections(
						new RoleFrameCollection(otherOnlyRoleFrame)); *
			}
			if (otherOnlyValue instanceof RoleFrameSlot) {
				...
			}

			if (otherOnlyValue instanceof RoleFrameCollection) {
				final RoleFrameCollection otherOnlyRoleFrameCollection = (RoleFrameCollection) otherOnlyValue;

				return null;/* boundsGivenAsRoleFrameCollection
							.mergeBoundsGivenAsRoleFrameCollections(
							otherOnlyRoleFrameCollection); *
			}
			return new Plurival<RoleFrame>();
		}

		if (otherBounds instanceof RoleFrame) {
			// TODO
		}

		throw new IllegalStateException("Unexpected bounds type: "
				+ otherBounds);
	}
	*/


	/**
	 * @return <code>true</code>, iff the only allowed value meets these role
	 *         frame restrictions
	 */
	public boolean valueMeetsRoleFrameRestrictions(
			final RoleFrame roleFrameBounds)
	throws CannotFulfillTermException {
		if (this.onlyValue == null) {
			return false;
		}
		if (this.onlyValue instanceof RoleFrame) {
			/* FIXME (zurzeit nicht verwendet)
			if (((RoleFrame) this.onlyValue).meetsRestrictions(roleFrameBounds)) {
				return true;
			}
			return false;
			 */
		}
		if (this.onlyValue instanceof RoleFrameSlot) {
			// ...
		}
		if (this.onlyValue instanceof RoleFrameCollection) {
			/* FIXME (zurzeit nicht verwendet)
			if (((RoleFrameCollection) this.onlyValue)
					.meetsRestrictions(roleFrameBounds)) {
				return true;
			}
			return false;
			 */
		}

		return false;
	}

	public T getOnlyValue() {
		return this.onlyValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((this.onlyValue == null) ? 0 : this.onlyValue.hashCode());
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
		final OnlyValueBound<?> other = (OnlyValueBound<?>) obj;
		if (this.onlyValue == null) {
			if (other.onlyValue != null) {
				return false;
			}
		} else if (!this.onlyValue.equals(other.onlyValue)) {
			return false;
		}
		return true;
	}

}
