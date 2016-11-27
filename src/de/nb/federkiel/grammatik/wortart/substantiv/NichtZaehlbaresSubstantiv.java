/*
 * UncountableNoun
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Created on 22.03.2003
 *
 */
package de.nb.federkiel.grammatik.wortart.substantiv;

import de.nb.federkiel.grammatik.kategorie.Genus;
import de.nb.federkiel.grammatik.kategorie.Kasus;

/**
 * Ein Substantiv, das nicht zählbar ist.
 */
class NichtZaehlbaresSubstantiv extends Substantiv {

  public NichtZaehlbaresSubstantiv(final String nominativSingular,
      final String genitivSingular, final String dativSingular,
      final String akkusativSingular, final Genus genus) {
    super(nominativSingular, genitivSingular, dativSingular, akkusativSingular,
        genus);
  }

  // --------------- PACKAGE PRIVATE --------------

  @Override
  String getWortformPlural(final Kasus kasus) {
    throw new RuntimeException("Tried to retrieve plural of uncountable noun "
        + this);
  }
}
