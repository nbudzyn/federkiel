package de.nb.federkiel.lexikon;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.LexiconFeatureStructureUtil;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.feature.UnspecifiedFeatureValue;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.ILexeme;
import de.nb.federkiel.interfaces.ILexemeType;
import de.nb.federkiel.interfaces.ISemantics;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.string.StringUtil;

/**
 * Eine Wortform (syntaktisches Wort, Flexionsform), d.h. ein Wort in einer
 * Form, wie es im Satz erscheinen könnte einschließlich seiner (grammatischen)
 * Merkmale.
 * <p>
 * It is important to understand that objects of this class are value objects!
 * There can be arbitrarily many objects that "mean the same word form".
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class Wortform implements IWordForm {
	/**
	 * Das zu Grunde liegende Lexeme.
	 */
	private final ILexeme lexeme;

	/**
	 * Der POS- (part-of-speech-) -Typ dieser Wortform.
	 */
	private final String pos;

	/**
	 * Die eigentliche Wortform (als String)
	 * <p>
	 * Not empty.
	 */
	private final String string;

	/**
	 * Die (grammatischen) Merkmale, jeweils mit Name und Wert - EINSCHLIESSLICH der
	 * (grammatischen) Merkmale des Lexems!
	 */
	private final FeatureStructure features;

	/**
	 * Semantics (<i>meaning</i>) of the word.
	 */
	private final ISemantics semantics;

	/**
	 * Erzeugt eine neue Wortform, die der Nennform eines Lexems entspricht,
	 * <i>ohne</i> Wortform-spezifische (grammatische) Merkmale.
	 */
	public Wortform(final ILexeme lexeme, final String pos, final ISemantics semantics) {
		this(lexeme, pos, lexeme.getCanonicalizedForm(), semantics);
	}

	/**
	 * Erzeugt eine neue Wortform <i>ohne</i> Wortform-spezifische (grammatische)
	 * Merkmale.
	 *
	 * @param string
	 *          must not be empty
	 */
	public Wortform(final ILexeme lexeme, final String pos, final String string, final ISemantics semantics) {
		this.lexeme = lexeme;
		this.string = string;
		this.pos = pos;
		features = lexeme.getFeatures();
		this.semantics = semantics;
	}

	/**
	 * Erzeugt eine neue Wortform <i>mit</i> Wortform-spezifischen (grammatischen)
	 * Merkmalen.
	 *
	 * @param string
	 *          must not be empty
	 */
	public Wortform(final ILexeme lexeme, final String pos, final String string, final FeatureStructure specificFeatures,
			final ISemantics semantics) {
		this(lexeme, pos, string, specificFeatures, semantics, true);
	}

	/**
	 * Creates a new word form as a copy from the original.
	 *
	 * @param additionalSpecificFeatures
	 *          additional specific features
	 */
	private Wortform(final Wortform original, final FeatureStructure additionalSpecificFeatures) {
		this(original, original.string, additionalSpecificFeatures);
	}

	/**
	 * Creates a new word form as a copy from the original.
	 *
	 * @param string
	 *          the new string
	 * @param additionalSpecificFeatures
	 *          additional specific features
	 */
	private Wortform(final Wortform original, final String string, final FeatureStructure additionalSpecificFeatures) {
		this(original.lexeme, original.pos, string, FeatureStructure.disjunctUnion(original.features, // lexeme
																																																	// features
																																																	// are
																																																	// already
																																																	// included!
				additionalSpecificFeatures), original.semantics, false); // (lexeme
																																	// features
																																	// are
																																	// already
		// included!)
	}

	/**
	 * @param string
	 *          must not be empty
	 */
	private Wortform(final ILexeme lexeme, final String pos, final String string, final FeatureStructure features,
			final ISemantics semantics, final boolean addFeaturesFromLexeme) {
		this.lexeme = lexeme;
		this.pos = pos;
		this.string = string;
		this.features = addFeaturesFromLexeme ? FeatureStructure.disjunctUnion(this.lexeme.getFeatures(), features)
				: features;
		this.semantics = semantics;
	}

	/**
	 * @param string
	 *          must not be empty
	 */
	public static Wortform lexemeFeaturesAlreadyIntegrated(final ILexeme lexeme, final String pos, final String string,
			final FeatureStructure allWordformFeatures, final ISemantics semantics) {
		return new Wortform(lexeme, pos, string, allWordformFeatures, semantics, false);
	}

	/**
	 * Returns a new word from with this feature generalized (from its original
	 * value to JOKER).
	 */
	@Override
	public IWordForm generalizeFeature(final String featureName) {
		final FeatureStructure generalizedFeatures = features.generalizeFeature(featureName);
		return new Wortform(lexeme, pos, string, generalizedFeatures, // lexeme
				// features are already included!
				semantics, false); // (lexeme features are already
	}

	/**
	 * @param strings
	 *          none of them may be empty
	 */
	public static IWordForm[] unflektMitTyp(final ILexemeType lexemeType, final String pos, final String typ,
			final ISemantics semantics, final String... strings) {
		final IWordForm[] res = new IWordForm[strings.length];

		final FeatureStructure features = LexiconFeatureStructureUtil.fromStringValues(ImmutableMap.of("typ", typ));

		for (int i = 0; i < strings.length; i++) {
			final Lexeme lexeme = new Lexeme(lexemeType, strings[i], features);
			res[i] = new Wortform(lexeme, pos, semantics);
		}

		return res;
	}

	@Override
	public String getPos() {
		return pos;
	}

	@Override
	public String getString() {
		return string;
	}

	@Override
	public ILexeme getLexem() {
		return lexeme;
	}

	@Override
	public FeatureStructure getFeatures() {
		return features;
	}

	/**
	 * Gibt den Wert des (grammatischen) Merkmals zurück.
	 */
	@Override
	public IFeatureValue getFeatureValue(final String featureName) {
		return features.getFeatureValue(featureName);
	}

	/**
	 * Gibt den Wert des (grammatischen) Merkmals zurück.
	 */
	@Override
	public IFeatureValue getFeatureValue(final String featureName, final IFeatureValue defaultValue) {
		return features.getFeatureValue(featureName, defaultValue);
	}

	public boolean areAllFeaturesCompleted() {
		return features.isCompleted();
	}

	@Override
	public ISemantics getSemantics() {
		return semantics;
	}

	@Override
	public Collection<IWordForm> expandToUpperLowerCaseForms(final boolean alsoAllowLowerCaseAtSentenceStart,
			final Locale locale) {
		if (Character.isLowerCase(getString().codePointAt(0))) {
			// starts with a lower-case letter
			return expandToUpperLowerCaseFormsForLowerCase(alsoAllowLowerCaseAtSentenceStart, locale);
		}

		return addUpperCaseFeatures();
	}

	/**
	 * Erzeugt zu dieser Wortform - die KLEINgeschrieben sein muss! - zwei
	 * Wortformen - eine kleingeschriebene für das Vorkommen im Satz (i.A.) und eine
	 * Großgeschriebene für das Vorkommen am Satzbeginn.
	 *
	 * @param alsoAllowLowerCaseAtSentenceStart
	 *          ob die kleingeschriebene Wortform auch am Satzbeginn stehen darf
	 *          (wie z.B. bei "von Papen sagte zu.")
	 */
	private Collection<IWordForm> expandToUpperLowerCaseFormsForLowerCase(final boolean alsoAllowLowerCaseAtSentenceStart,
			final Locale locale) {
		return ImmutableList.<IWordForm>of(
				// am Satzbeginn
				new Wortform(this, StringUtil.capitalize(string, locale), buildCaseFeature(true)),
				// im Satz
				new Wortform(this, buildCaseFeature(alsoAllowLowerCaseAtSentenceStart ? null : false)));
	}

	private Collection<IWordForm> addUpperCaseFeatures() {
		return ImmutableList.<IWordForm>of(new Wortform(this, buildCaseFeature(null)));
	}

	private static FeatureStructure buildCaseFeature(@Nullable final Boolean istSatzanfang) {
		// @formatter:off
    return LexiconFeatureStructureUtil.fromStringValues(
        ImmutableMap.of(GermanUtil.IST_SATZANFANG_KEY,
            istSatzanfang != null ?
                StringFeatureLogicUtil.booleanToString(istSatzanfang) :
                  UnspecifiedFeatureValue.UNSPECIFIED_STRING));
    // @formatter:on
	}

	@Override
	public String toRealizationString() {
		return getString();
	}

	/*
	 * public boolean featuresEqual(final Wortform other) { return
	 * this.features.equals(other.features); }
	 */

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}

		final Wortform other = (Wortform) obj;

		if (!string.equals(other.string)) {
			return false;
		}

		if (!lexeme.equalsWithoutCheckingFeatures(other.lexeme)) {
			return false;
		}

		if (!features.equals(other.features)) {
			return false;
		}

		if (!semantics.equals(other.semantics)) {
			return false;
		}

		if (!pos.equals(other.pos)) {
			return false;
		}

		return true;
	}

	@Override
	public int compareTo(final IWordForm o) {
		// This method shall be consistent with equals().
		final int classNameCompared = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final Wortform other = (Wortform) o;

		final int stringsCompared = string.compareTo(other.string);
		if (stringsCompared != 0) {
			return stringsCompared;
		}

		final int posCompared = pos.compareTo(other.pos);
		if (posCompared != 0) {
			return posCompared;
		}

		final int lexemsCompared = lexeme.compareTo(other.lexeme);
		if (lexemsCompared != 0) {
			return lexemsCompared;
		}

		final int featuresCompared = features.compareTo(other.features);
		if (featuresCompared != 0) {
			return featuresCompared;
		}

		return semantics.compareTo(other.semantics);
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder res = new StringBuilder();

		if (string.equals(lexeme.getCanonicalizedForm())) {
			res.append("\"");
			res.append(lexeme.getCanonicalizedForm());
			res.append("\"/");
			res.append(getPos());
		} else {
			res.append("\"");
			res.append(string);
			res.append("\"(");
			res.append(lexeme.getCanonicalizedForm());
			res.append("/");
			res.append(getPos());
			res.append(")");
		}

		if (!features.isEmpty()) {
			res.append("(");
			res.append(features.toString());
			res.append(")");
		}

		return res.toString();
	}
}
