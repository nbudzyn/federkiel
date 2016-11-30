package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.RoleFrame;
import de.nb.federkiel.feature.RoleFrameCollection;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.feature.StringFeatureValue;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;
import de.nb.federkiel.lexikon.Wortform;
import de.nb.federkiel.semantik.NothingInParticularSemantics;

/**
 * Utility-Methoden f�r Wortformen
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public final class WortformUtil {
	public static final String ROLE_FRAME_COLLECTION_NAME_VERB = "verb";

	private static final String NUMERUS = "numerus";

	private static final String MODUS = "modus";

	private static final String TEMPUS = "tempus";

	private WortformUtil() {
		super();
	}

	/**
	 * @param slots
	 *            will NOT be copied (hand-over)
	 */
	public static Wortform buildVerbFormFin(final Lexeme lexeme,
			final String pos,
			final String tempus, final String modus,
			final String string, final RoleFrameSlot... slots) {
		final RoleFrame verbFrame = RoleFrame.of(slots);

		final FeatureStructure features =
				FeatureStructure.fromValues(
							ROLE_FRAME_COLLECTION_NAME_VERB,
								RoleFrameCollection.of(verbFrame),
								TEMPUS, StringFeatureValue.of(tempus),
								MODUS, StringFeatureValue.of(modus));

		return new Wortform(lexeme, pos, string, features,
				NothingInParticularSemantics.INSTANCE);
	}

	/**
	 * @param slots
	 *            will NOT be copied (hand-over)
	 */
	public static Wortform buildVerbFormImp(final Lexeme lexeme,
      final String pos, final Numerus numerus,
			final String string, final RoleFrameSlot... slots) {
		final RoleFrame verbFrame = RoleFrame.of(slots);

		final FeatureStructure features =
				FeatureStructure.fromValues(
							ROLE_FRAME_COLLECTION_NAME_VERB,
								RoleFrameCollection.of(verbFrame),
        NUMERUS,
        StringFeatureValue.of(FeatureStringConverter.toFeatureString(numerus)));

		return new Wortform(lexeme, pos, string, features,
				NothingInParticularSemantics.INSTANCE);
	}

	/**
	 * @param slots
	 *            will NOT be copied (hand-over)
	 */
	public static Wortform buildVerbFormInf(final Lexeme lexeme,
			final String pos, final RoleFrameSlot... slots) {
		final RoleFrame verbFrame = RoleFrame.of(slots);

		final FeatureStructure features =
				FeatureStructure.fromValues(
								ROLE_FRAME_COLLECTION_NAME_VERB,
								RoleFrameCollection.of(verbFrame));

		return new Wortform(lexeme, pos, lexeme.getCanonicalizedForm(),
				features,
				NothingInParticularSemantics.INSTANCE);
	}

	public static Collection<IWordForm> expandToUpperLowerCaseForms(
			final Iterable<IWordForm> wordForms,
			final boolean alsoAllowLowerCaseAtSentenceStart,
			final Locale locale) {
		final ImmutableList.Builder<IWordForm> res =
				ImmutableList.<IWordForm> builder();

		// "abends" / "Haus"
		for (final IWordForm wordForm : wordForms) {
			res.addAll(wordForm
					.expandToUpperLowerCaseForms(
							alsoAllowLowerCaseAtSentenceStart,
							locale));
		}

		return res.build();
	}
}