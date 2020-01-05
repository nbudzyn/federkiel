package de.nb.federkiel.lexikon;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.LexiconFeatureStructureUtil;
import de.nb.federkiel.feature.StringFeatureValue;
import de.nb.federkiel.feature.UnspecifiedFeatureValue;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.ILexeme;
import de.nb.federkiel.interfaces.ILexemeType;

/**
 * Ein Lexeme (lexikalisches Wort), also ein Wort, wie es im Lexikon auftreten
 * könnte - und von dem sich ggf. flektierte Formen (<code>Wortform</code>en)
 * bilden lassen.
 * <p>
 * It is important to understand that objects of this class are value objects!
 * There can be arbitrarily many objects that "mean the same lexem".
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class Lexeme implements ILexeme {
	private final ILexemeType type;

	/**
	 * not empty
	 */
	private final String nennform;

	/**
	 * Die (im Grundsatz festen) (grammatischen) Merkmale des Lexems, jeweils mit
	 * Name und Wert, z.B. das Genus bei einem Nomen.
	 */
	final FeatureStructure features;

	/**
	 * Erzeugt ein neues Lexeme <i>ohne</i> (grammatische) Merkmale.
	 *
	 * @param nennform
	 *          must not be empty
	 */
	public Lexeme(final ILexemeType type, final String nennform) {
		this.type = type;
		this.nennform = nennform;
		features = LexiconFeatureStructureUtil.EMPTY_FEATURE_STRUCTURE;
	}

	/**
	 * Erzeugt ein neues Lexeme <i>mit</i> (grammatischen) Merkmalen.
	 * 
	 * @param nennform
	 *          must not be empty
	 */
	public Lexeme(final ILexemeType type, final String nennform, final FeatureStructure featureStructure) {
		this.type = type;
		this.nennform = nennform;
		features = featureStructure;
	}

	@Override
	public ILexemeType getType() {
		return type;
	}

	@Override
	public String getCanonicalizedForm() {
		return nennform;
	}

	@Override
	public FeatureStructure getFeatures() {
		return features;
	}

	@Override
	public IFeatureValue getFeatureValue(final String featureName) {
		return features.getFeatureValue(featureName);
	}

	@Override
	public String getStringFeatureValue(final String featureName) {
		final IFeatureValue featureValue = getFeatureValue(featureName);
		if (featureValue instanceof UnspecifiedFeatureValue) {
			return null;
		}

		return ((StringFeatureValue) featureValue).getString();
	}

	public IFeatureValue getFeatureValue(final String featureName, final IFeatureValue defaultValue) {
		return features.getFeatureValue(featureName, defaultValue);
	}

	public Iterator<String> featureNameIterator() {
		return features.orderedFeatureNameIterator();
	}

	public boolean areAllFeaturesCompleted() {
		return features.isCompleted();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		// Hier EXTRA kein instanceof! Subklasse Substantiv können ebensogut
		// equal sein!
		if (!(obj instanceof Lexeme)) {
			return false;
		}

		final Lexeme other = (Lexeme) obj;

		if (!equalsWithoutCheckingFeatures(other)) {
			return false;
		}

		if (!features.equals(other.features)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean equalsWithoutCheckingFeatures(final ILexeme other) {
		if (!type.equals(other.getType())) {
			return false;
		}

		if (!nennform.equals(other.getCanonicalizedForm())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return nennform.hashCode();
	}

	@Override
	public int compareTo(final ILexeme o) {
		// This method shall be consistent with equals().
		final int classNameCompared = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final Lexeme other = (Lexeme) o;

		final int typesCompared = type.getDescription().compareTo(other.type.getDescription());
		if (typesCompared != 0) {
			return typesCompared;
		}

		final int nennformenCompared = nennform.compareTo(other.nennform);
		if (nennformenCompared != 0) {
			return nennformenCompared;
		}

		return features.compareTo(other.features);
	}

	@Override
	public String toString() {
		final StringBuilder res = new StringBuilder();

		res.append(nennform);

		/*
		 * (nicht so spannend) if (! this.features.isEmpty()) { res.append("(");
		 * 
		 * boolean firstTerm = true;
		 * 
		 * for (final Map.Entry<String, String> feature : this.features.entrySet()) { if
		 * (firstTerm) { firstTerm = false; } else { res.append(", "); }
		 * 
		 * res.append(feature.getKey()); res.append("=\"");
		 * res.append(feature.getValue()); res.append("\""); }
		 * 
		 * res.append(")"); }
		 */

		res.append(" (");
		res.append(type);
		res.append(")");

		return res.toString();
	}

}
