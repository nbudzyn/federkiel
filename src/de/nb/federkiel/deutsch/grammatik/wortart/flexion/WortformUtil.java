package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;

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
  private WortformUtil() {
    super();
  }

  /**
   * @param slots will NOT be copied (hand-over)
   */
  public static Wortform buildVerbFormFin(final Lexeme lexeme, final String pos,
      final String tempus, final String modus, final String string, final RoleFrameSlot... slots) {
    final RoleFrame verbFrame = RoleFrame.of(slots);

    final FeatureStructure features =
        FeatureStructure.fromValues(GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB,
            RoleFrameCollection.of(verbFrame), GermanUtil.TEMPUS, StringFeatureValue.of(tempus),
            GermanUtil.MODUS_KEY,
            StringFeatureValue.of(modus));

    return new Wortform(lexeme, pos, string, features,
        NothingInParticularSemantics.INSTANCE);
  }

  /**
   * @param slots will NOT be copied (hand-over)
   */
  public static Wortform buildVerbFormImp(final Lexeme lexeme, final String pos,
      final Numerus numerus, final String string, final RoleFrameSlot... slots) {
    final RoleFrame verbFrame = RoleFrame.of(slots);

    final FeatureStructure features = FeatureStructure.fromValues(
        GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB, RoleFrameCollection.of(verbFrame), GermanUtil.NUMERUS_KEY,
        StringFeatureValue.of(FeatureStringConverter.toFeatureString(numerus)));

    return new Wortform(lexeme, pos, string, features,
        NothingInParticularSemantics.INSTANCE);
  }

  /**
   * @param slots will NOT be copied (hand-over)
   */
  public static Wortform buildVerbFormInf(final Lexeme lexeme, final String pos,
      final RoleFrameSlot... slots) {
    final RoleFrame verbFrame = RoleFrame.of(slots);

    final FeatureStructure features = FeatureStructure.fromValues(
        GermanUtil.ROLE_FRAME_COLLECTION_NAME_VERB, RoleFrameCollection.of(verbFrame));

    return new Wortform(lexeme, pos, lexeme.getCanonicalizedForm(), features,
        NothingInParticularSemantics.INSTANCE);
  }

  public static Collection<IWordForm> expandToUpperLowerCaseForms(
      final Iterable<IWordForm> wordForms,
      final boolean alsoAllowLowerCaseAtSentenceStart, final Locale locale) {
    // "abends" / "Haus"
    // @formatter:off
	  return Streams.stream(wordForms)
	      .flatMap(wordForm -> wordForm
                    .expandToUpperLowerCaseForms(
                            alsoAllowLowerCaseAtSentenceStart,
                            locale).stream())
	      .collect(ImmutableList.toImmutableList());
      // @formatter:on
  }
}
