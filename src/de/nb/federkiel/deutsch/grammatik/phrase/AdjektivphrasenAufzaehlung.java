/*
 * GramEnumeration
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Created on 21.03.2003
 *
 */
package de.nb.federkiel.deutsch.grammatik.phrase;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.deutsch.grammatik.wortart.adjektiv.Adjektiv;

/**
 * Eine Aufzählung von Adjektivphrasen.
 * 
 * @author nikolaj
 */
public class AdjektivphrasenAufzaehlung extends Aufzaehlung<Adjektivphrase> {
  public AdjektivphrasenAufzaehlung() {
    this(ImmutableList.of());
  }

  public AdjektivphrasenAufzaehlung(
      final Collection<? extends Adjektivphrase> elements) {
    super(elements);
  }

  @Override
  public AdjektivphrasenAufzaehlung add(final Adjektivphrase element) {
    super.add(element);
    return this;
  }

  @Override
  public AdjektivphrasenAufzaehlung addFirst(final Adjektivphrase element) {
    super.addFirst(element);
    return this;
  }

  @Override
  public AdjektivphrasenAufzaehlung delete(final int index) {
    super.delete(index);
    return this;
  }

  public AdjektivphrasenAufzaehlung add(final Adjektiv adjective) {
    return add(new Adjektivphrase(adjective));
  }

  @Override
  public AdjektivphrasenAufzaehlung clone() {
    return (AdjektivphrasenAufzaehlung) super.clone();
  }
}
