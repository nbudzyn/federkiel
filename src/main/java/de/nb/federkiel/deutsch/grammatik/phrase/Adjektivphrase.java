/*
 * AdjectivePhrase
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Created on 20.03.2003
 *
 */
package de.nb.federkiel.deutsch.grammatik.phrase;

import static de.nb.federkiel.string.StringUtil.concatSpacedTrim;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.adjektiv.Adjektiv;
import de.nb.federkiel.deutsch.grammatik.wortart.substantiv.Aufzaehlbar;
import de.nb.federkiel.grammatik.wortart.verb.VerbFlexionstyp;

/**
 * Eine Adjektivphrase, also eine Phrase, die im Satz wie ein einzelnes Adjektiv
 * verwendet werden kann.
 * <p>
 * Beispiele:
 * <ul>
 * <li>&quot;mit goldener Farbe angestrichen&quot;
 * <li>&quot;freundlich, erstaunlich unauffaellig und einladend aussehend&quot;
 * </ul>
 *
 * @author nikolaj
 */
public class Adjektivphrase implements Aufzaehlbar {
  /**
   * &quot;mit goldener Farbe&quot;
   */
  private final @Nullable Praepositionalphrase praepositionalphrase;

  /**
   * &quot;nett und freundlich&quot;, &quot;erstaunlich unauffaellig&quot; Leer
   * oder enthält Adjektiv-Phrasen.
   */
  private final AdjektivphrasenAufzaehlung adverbphrasen;

  /** Z.B. &quot;angestrichen&quot; */
  private final Adjektiv adjektiv;

  public Adjektivphrase(final Adjektiv adjektiv) {
    this(new AdjektivphrasenAufzaehlung(), adjektiv);
  }

  public Adjektivphrase(final Adjektiv adverb, final Adjektiv adjektiv) {
    this(new Adjektivphrase(adverb), adjektiv);
  }

  public Adjektivphrase(final Adjektivphrase adverbphrase, final Adjektiv adjektiv) {
    this(new AdjektivphrasenAufzaehlung().add(adverbphrase), adjektiv);
  }

  public Adjektivphrase(final AdjektivphrasenAufzaehlung adverbphrasen,
      final Adjektiv adjektiv) {
    this(null, adverbphrasen, adjektiv);
  }

  public Adjektivphrase(final Praepositionalphrase prepositionalphrase,
      final Adjektiv adjektiv) {
    this(prepositionalphrase, new AdjektivphrasenAufzaehlung(), adjektiv);
  }

  private Adjektivphrase(final Praepositionalphrase praepositionalphrase,
      final AdjektivphrasenAufzaehlung adverbphrasen, final Adjektiv adjektiv) {
    this.praepositionalphrase = praepositionalphrase;
    this.adverbphrasen = adverbphrasen;
    this.adjektiv = adjektiv;
  }

  public String getFlektiert(final Flexionstyp flexionstyp,
 final Kasus kasus,
      final Numerus numerus, final Genus genus) {
    if (praepositionalphrase != null) {
      if (!adverbphrasen.isEmpty()) {
        throw new RuntimeException("Both Prepositional phrase ("
            + praepositionalphrase + ") AND adverb phrase(s) ("
            + adverbphrasen + ") in the same AdjectivePhrase (Adjective "
            + adjektiv + "??");
      }
      return praepositionalphrase.getFlektiert() + " "
          + adjektiv.getWortform(flexionstyp, kasus, numerus, genus);
    }



    return concatSpacedTrim(
        adverbphrasen.getFlektiert(Flexionstyp.UNFLEKTIERT, null, null, null, null),
        adjektiv.getWortform(flexionstyp, kasus, numerus, genus));
  }

  @Override
  public String getFlektiert(final Flexionstyp flexionstyp,
      final Kasus kasus, final Numerus numerus, final Genus genus,
      final VerbFlexionstyp object) {
    return getFlektiert(flexionstyp, kasus, numerus, genus);
  }
}
