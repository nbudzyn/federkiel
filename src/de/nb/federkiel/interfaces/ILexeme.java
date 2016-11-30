package de.nb.federkiel.interfaces;

import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.feature.FeatureStructure;

/**
 * All implementations must be immutable.
 *
 * @author nbudzyn 2009
 */
@Immutable
public interface ILexeme extends Comparable<ILexeme> {
	public ILexemeType getType();

	public String getCanonicalizedForm();

	public FeatureStructure getFeatures();

	public IFeatureValue getFeatureValue(final String featureName);

	/**
	 * @return the String value of a String feature - or <code>null</code>, if
	 *         the feature was unspecified
	 */
	public String getStringFeatureValue(final String featureName);

	public boolean equalsWithoutCheckingFeatures(final ILexeme other);
}