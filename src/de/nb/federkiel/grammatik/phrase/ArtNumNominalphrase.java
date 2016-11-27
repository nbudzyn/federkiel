/*
 * ArtNumNounPhrase
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Created on 21.03.2003
 *
 */
package de.nb.federkiel.grammatik.phrase;

import javax.annotation.Nullable;

import de.nb.federkiel.grammatik.kategorie.Aufzaehlbar;
import de.nb.federkiel.grammatik.kategorie.Flexionstyp;
import de.nb.federkiel.grammatik.kategorie.Genus;
import de.nb.federkiel.grammatik.kategorie.IDeterminativ;
import de.nb.federkiel.grammatik.kategorie.Kasus;
import de.nb.federkiel.grammatik.kategorie.Numerus;
import de.nb.federkiel.grammatik.kategorie.VerbFlexionstyp;

/**
 * Eine Nominalphrase mit spezifiziertem Artikel-Typ und Numerus.
 * 
 * @author nikolaj
 */
public class ArtNumNominalphrase implements Aufzaehlbar {
  private final NumNominalphrase numNominalphrase;
  private final @Nullable IDeterminativ determinativ;

  public ArtNumNominalphrase(final @Nullable IDeterminativ determinativ,
      final Nominalphrase nominalphrase, final Numerus numerus) {
    this(determinativ, new NumNominalphrase(nominalphrase, numerus));
	}

  public ArtNumNominalphrase(final @Nullable IDeterminativ determinativ,
      final NumNominalphrase numNominalphrase) {
    this.determinativ = determinativ;
    this.numNominalphrase = numNominalphrase;
  }

  public String getFlektiert(final Kasus kasus) {
    return this.numNominalphrase.getFlektiertMitDeterminativ(kasus,
        this.determinativ);
	}

  @Override
  public String getFlektiert(final @Nullable Flexionstyp flexionstyp,
      final Kasus kasus, final @Nullable Numerus numerus,
      final @Nullable Genus genus, final @Nullable VerbFlexionstyp object) {
		// most of the attributes are only valid for adjectives/adverbs!
    return getFlektiert(kasus);
	}

  @Override
  public String toString() {
    return getFlektiert(Kasus.NOMINATIV);
  }
}
