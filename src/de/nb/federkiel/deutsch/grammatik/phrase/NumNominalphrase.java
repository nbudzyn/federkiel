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
package de.nb.federkiel.deutsch.grammatik.phrase;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.IDeterminativ;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.substantiv.Aufzaehlbar;
import de.nb.federkiel.grammatik.wortart.verb.VerbFlexionstyp;

/**
 * Eine Nominalphrase mit spezifiziertem Numerus.
 * 
 * @author nikolaj
 */
public class NumNominalphrase implements Aufzaehlbar {
	private final Nominalphrase nominalphrase;
	private final Numerus numerus;

  public NumNominalphrase(final Nominalphrase nominalphrase,
      final Numerus numerus) {
		this.nominalphrase = nominalphrase;
		this.numerus     = numerus;		
	}

  public String getFlektiert(final Kasus kasus) {
    return this.nominalphrase.getFlektiert(kasus, this.numerus);
	}

  public String getFlektiertMitDeterminativ(final Kasus kasus,
      final IDeterminativ determinativ) {
    return this.nominalphrase.getFlektiertMitDeterminativ(kasus, numerus,
        determinativ);
  }

  @Override
  public String getFlektiert(final @Nullable Flexionstyp flexionstyp,
      final Kasus kasus, final @Nullable Numerus numerus,
      final @Nullable Genus genus, final @Nullable VerbFlexionstyp object) {
		// most of the attributes are only valid for adjectives/adverbs!
    return getFlektiert(kasus);
	}

  public Nominalphrase getNominalphrase() {
    return nominalphrase;
  }

  public Numerus getNumerus() {
    return numerus;
  }
}
