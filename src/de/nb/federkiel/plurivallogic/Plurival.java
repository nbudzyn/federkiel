package de.nb.federkiel.plurivallogic;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.federkiel.cache.WeakCache;

/**
 * Some alternative values (plurival is meant to be short for <i>plurivalent</i>).
 */
@Immutable
@ThreadSafe
public class Plurival<T extends Object> implements Iterable<T> {
	final private static WeakCache<Plurival<?>> cache =
			new WeakCache<>();

	private final ImmutableCollection<T> values;

	/**
	 * Cached hash code
	 */
	private int hashCode;

	@SuppressWarnings("unchecked")
	public static <T> Plurival<T> of(final T... values) {
		return cache.findOrInsert(new Plurival<>(values));
	}

	@SuppressWarnings("unchecked")
	public static <T> Plurival<T> of(final ImmutableCollection<T> values) {
		return cache.findOrInsert(new Plurival<>(values));
	}

	@SuppressWarnings("unchecked")
	public static <T> Plurival<T> empty() {
		return of();
	}

	private Plurival(final T... values) {
		this(ImmutableList.copyOf(values));
	}

	private Plurival(final ImmutableCollection<T> values) {
		this.values = values;
		this.hashCode = calcHash();
	}

	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return this.values.iterator();
	}

	public int size() {
		return this.values.size();
	}

	private final int calcHash() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.values == null) ? 0 : this.values.hashCode());
		return result;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
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

		final Plurival<?> other = (Plurival<?>) obj;

		if (this.hashCode != other.hashCode) {
			return false;
		}

		if (!this.values.equals(other.values)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		if (this.size() == 1) {
			return this.values.iterator().next().toString();
		}

		final StringBuilder res = new StringBuilder();
		res.append("{ ");

		boolean first = true;

		for (final T value : this.values) {
			if (first) {
				first = false;
			} else {
				res.append(" | ");
			}

			res.append(value.toString());
		}

		res.append(" }");

		return res.toString();
	}
}
