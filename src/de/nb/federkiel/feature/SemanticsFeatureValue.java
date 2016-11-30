package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.cache.WeakCache;
import de.nb.federkiel.interfaces.IFeatureValue;

/**
 * A String feature value
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public abstract class SemanticsFeatureValue implements IFeatureValue {
	/**
	 * All generated semantics feature values shall be cached - to minimize
	 * memory use. The cache consists of weak references, so it will be cleared
	 * automatically, when a value is no longer (strongly) referenced.
	 */
	final protected static WeakCache<SemanticsFeatureValue> cache = new WeakCache<>();

	/**
	 * Do not forget to cache all created values! Every constructed value shall
	 * be cached by using <code>cache.findOrInsert()</code>
	 */
	protected SemanticsFeatureValue() {
		super();
	}

	@Override
	public int hashCode() {
		return 1;
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
		return true;
	}
}
