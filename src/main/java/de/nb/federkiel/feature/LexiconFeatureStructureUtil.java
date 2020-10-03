package de.nb.federkiel.feature;

import com.google.common.collect.ImmutableMap;

import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.ISemantics;

/**
 * Utility methode for feature structures in a lexicon.
 * <p>
 * In a lexicon, a feature structure does not correspond to a part of the
 * parsing input, obviously.
 */
public class LexiconFeatureStructureUtil {
	public static final FeatureStructure EMPTY_FEATURE_STRUCTURE = FeatureStructure.empty(null);

	/**
	 * Builds a feature Structure WITHOUT a surface part - for a lexicon.
	 *
	 * @param stringFeatures
	 *          The key - string-value- pairs for the feature structure; an
	 *          <code>UnspecifiedFeatureValue.UNSPECIFIED_STRING</code> leads to an
	 *          <code>UnspecifiedFeatureValue</code>.
	 */
	public static FeatureStructure fromStringValues(final ImmutableMap<String, String> stringFeatures) {
		return FeatureStructure.fromStringValues(null, stringFeatures);
	}

	public static FeatureStructure fromValues(final String key, final IFeatureValue value) {
		return FeatureStructure.fromValues(null, key, value);
	}

	public static FeatureStructure fromValues(final String key1, final IFeatureValue value1, final String key2,
			final IFeatureValue value2) {
		return FeatureStructure.fromValues(null, key1, value1, key2, value2);
	}

	public static FeatureStructure fromValues(final String key1, final IFeatureValue value1, final String key2,
			final IFeatureValue value2, final String key3, final IFeatureValue value3) {
		return FeatureStructure.fromValues(null, key1, value1, key2, value2, key3, value3);
	}

	public static FeatureStructure fromValues(final String key1, final IFeatureValue value1, final String key2,
			final IFeatureValue value2, final String key3, final IFeatureValue value3, final String key4,
			final IFeatureValue value4) {
		return FeatureStructure.fromValues(null, key1, value1, key2, value2, key3, value3, key4, value4);
	}

	/**
	 * Builds a feature Structure WITHOUT a surface part - for a lexicon.
	 */
	public static FeatureStructure fromValues(final ImmutableMap<String, IFeatureValue> features, ISemantics semantics) {
		return FeatureStructure.fromValues(null, features, semantics);
	}
}
