/*
 * ArtNumNounPhrase
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * Created on 21.03.2003
 *
 */
package de.nb.federkiel.deutsch.grammatik.phrase;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.artikelwort.Artikel;
import de.nb.federkiel.deutsch.grammatik.wortart.substantiv.Aufzaehlbar;
import de.nb.federkiel.grammatik.wortart.verb.VerbFlexionstyp;

/**
 * Eine Nominalphrase mit spezifiziertem Artikel und Numerus.
 *
 * @author nikolaj
 */
public class ArtNumNominalphrase implements Aufzaehlbar {
  private final NumNominalphrase numNominalphrase;
  private final @Nullable Artikel artikel;

  public ArtNumNominalphrase(final @Nullable Artikel artikel, final Nominalphrase nominalphrase,
      final Numerus numerus) {
    this(artikel, new NumNominalphrase(nominalphrase, numerus));
  }

  public ArtNumNominalphrase(final @Nullable Artikel artikel,
      final NumNominalphrase numNominalphrase) {
    Preconditions.checkArgument(
        numNominalphrase.getNumerus() == SINGULAR || artikel == Artikel.BESTIMMT || artikel == null,
        "Im Plural gibt es keine unbestimmten Artikel!");

    this.artikel = artikel;
    this.numNominalphrase = numNominalphrase;
  }

  public String getFlektiert(final Kasus kasus) {
    return numNominalphrase.getFlektiertMitArtikel(kasus, artikel);
  }

  @Override
  public String getFlektiert(final @Nullable Flexionstyp flexionstyp, final Kasus kasus,
      final @Nullable Numerus numerus, final @Nullable Genus genus,
      final @Nullable VerbFlexionstyp object) {
    // most of the attributes are only valid for adjectives/adverbs!
    return getFlektiert(kasus);
  }

  @Override
  public String toString() {
    return getFlektiert(Kasus.NOMINATIV);
  }
}
