/*
 * PrepositionalPhrase
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Created on 21.03.2003
 *
 */
package de.nb.federkiel.deutsch.grammatik.phrase;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.praeposition.Praeposition;
import de.nb.federkiel.deutsch.grammatik.wortart.substantiv.Aufzaehlbar;
import de.nb.federkiel.grammatik.wortart.verb.VerbFlexionstyp;
import de.nb.federkiel.string.StringUtil;

/**
 * Eine Präpositionalphrase. Eine Präpositionalphrase besteht aus einer
 * Präposition und einer Nominalphrase, z.B.: &quot;aus Eichenholz&quot;,
 * &quot;mit goldener Farbe&quot; &quot;nach einem ausgiebigen
 * Fruehstueck&quot;.
 * 
 * @author nikolaj
 */
public class Praepositionalphrase implements Aufzaehlbar {

  private final Praeposition praeposition;
  private final ArtNumNominalphrase artNumNominalphrase;

  public Praepositionalphrase(final Praeposition praeposition,
      final ArtNumNominalphrase artNumNominalphrase) {
    this.praeposition = praeposition;
    this.artNumNominalphrase = artNumNominalphrase;
  }

  public String getFlektiert() {
    return StringUtil.concatSpacedTrim(this.praeposition.getName(),
        this.artNumNominalphrase.getFlektiert(this.praeposition.getKasus()));
  }

  @Override
  public String getFlektiert(final @Nullable Flexionstyp flexionstyp,
      final @Nullable Kasus kasus, final @Nullable Numerus numerus,
      final @Nullable Genus genus,
      final @Nullable VerbFlexionstyp verbFlexionstyp) {
    // most parameters only apply for adjective enumerations!
    return getFlektiert();
  }
}
