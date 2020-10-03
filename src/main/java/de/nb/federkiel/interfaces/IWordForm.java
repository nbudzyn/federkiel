package de.nb.federkiel.interfaces;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import de.nb.federkiel.feature.FeatureStructure;

/**
 * All implementations must be immutable.
 *
 * @author nbudzyn 2009
 */
@Immutable
public interface IWordForm extends Comparable<IWordForm> {
  public ILexeme getLexem();

  public String getPos();

  public String getString();

  public FeatureStructure getFeatures();

  /**
   * Gibt den Wert des (grammatischen) Merkmals zurück. Dabei hat der spezifische Wert dieser
   * Wortform Vorrang vor dem Wert des Lexems.
   */
  public IFeatureValue getFeatureValue(final String featureName);

  /**
   * Gibt den Wert des (grammatischen) Merkmals zurück. Dabei hat der spezifische Wert dieser
   * Wortform Vorrang vor dem Wert des Lexems.
   */
  public IFeatureValue getFeatureValue(final String featureName, final IFeatureValue defaultValue);

  public IWordForm generalizeFeature(String featureName);

  /**
   * @return one or two word forms:
   *         <ul>
   *         <li>If the word starts with an <i>uppercase letter</i>, then this wordform is return,
   *         with two additional features, meaning: This word form can start a sentence and can also
   *         be part inside a sentence. (E.g. "Haus".)
   *         <li>If the word starts with a <i>lowercase letter</i>, two wordforms are returned: A
   *         lowercase one (with additional features, telling that this word form can only be used
   *         <i>inside</i> a sentence) and a capitalized one (with additional features, telling that
   *         this word form can only be used <i>at the beginning of a sentence</i>). (E.g. "abends",
   *         "Abends".)
   *         </ul>
   * @param alsoAllowLowerCaseAtSentenceStart ob die kleingeschriebene Wortform auch am Satzbeginn
   *        stehen darf (wie z.B. bei "von Papen sagte zu.")
   */
  public Collection<IWordForm> expandToUpperLowerCaseForms(
      boolean alsoAllowLowerCaseAtSentenceStart, Locale locale);

  public String toRealizationString();
}
