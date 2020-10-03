package de.nb.federkiel.collection;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Objects;

@Immutable
@ThreadSafe
final public class Pair<A, B> {
	/**
	 * <code>null</code> erlaubt
	 */
	private final A first;

	/**
	 * <code>null</code> erlaubt
	 */
	private final B second;

	public static <A, B> Pair<A, B> of(final A first, final B second) {
		return new Pair<>(first, second);
	}

	private Pair(final A first, final B second) {
		super();
		this.first = first;
		this.second = second;
	}

	public A first() {
		return this.first;
	}

	public B second() {
		return this.second;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (! this.getClass().equals(obj.getClass())) {
			return false;
		}

		final Pair<A, B> other = (Pair<A, B>) obj;

		return
		Objects.equal(this.first, other.first) &&
		Objects.equal(this.second, other.second);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.first) * 31 +
		Objects.hashCode(this.second);
	}

	@Override
	public String toString() {
		return "<" + this.first + ", " + this.second + ">";
	}

}
