package de.nb.federkiel.cache;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A cache, holding weak references to the cached objects. Very useful for
 * immutable objects, to prevent having several equal immutable objects at the
 * some time. The weak references lead to cleaning the cache automatically.
 * <p>
 * For using this cache - it is essential that the hashCode() does not change
 * and that the values used for equals() do not change either.
 *
 * @author nbudzyn 2011
 */
@ThreadSafe
public final class WeakCache<E extends Object> {
	@GuardedBy("this")
	private final WeakHashMap<E, WeakReference<E>> map =
			new WeakHashMap<>();

	// Macht keinen Sinn - weil man das readLock nicht zu einem writeLock
	// upgraden kann
	// (ansonsten müsste man außerdem ohnehin ein double-checked locking
	// machen...)
	// private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

	public WeakCache() {
		super();
	}

	/**
	 * Returns a value equals to given one, if existing. If not, stores the
	 * given value and returns it.
	 */
	public synchronized <F extends E> F findOrInsert(final F value) {
		final F res = tryAndGet(value);
		if (res != null) {
			return res;
		}
		this.map.put(value, new WeakReference<E>(value));
		return value;
	}

	private synchronized <F extends E> F tryAndGet(final F value) {
		final WeakReference<E> res = this.map.get(value);

		if (res != null) {
			// There seem to be cases, where res != null, but
			// res.get() == null! Hm.
			@SuppressWarnings("unchecked")
			final F resRef = (F) res.get();
			if (resRef != null) {
				return resRef;
			}
		}

		return null;
	}
}
