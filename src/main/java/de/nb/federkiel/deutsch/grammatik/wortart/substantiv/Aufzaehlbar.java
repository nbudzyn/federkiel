/*
 * Enumeratable
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Created on 21.03.2003
 *
 */
package de.nb.federkiel.deutsch.grammatik.wortart.substantiv;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.phrase.Flexionstyp;
import de.nb.federkiel.grammatik.wortart.verb.VerbFlexionstyp;


/**
 * Grammatikalische Elemente, die Bestandteil einer Aufzählung sein können.
 *
 * @author nikolaj
 */
public interface Aufzaehlbar {

	  /**
   * Je nach konkretem Typ der Aufzählung können einzelne Parameter
   * <code>null</code> sein.
   */
  String getFlektiert(@Nullable Flexionstyp inflexionType,
      @Nullable Kasus kasus, Numerus numerus, @Nullable Genus genus,
      @Nullable VerbFlexionstyp verbInflexionMode);
}
