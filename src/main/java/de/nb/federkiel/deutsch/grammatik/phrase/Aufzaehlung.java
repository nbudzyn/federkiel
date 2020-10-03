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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.wortart.substantiv.Aufzaehlbar;
import de.nb.federkiel.grammatik.wortart.verb.VerbFlexionstyp;

/**
 * Eine Aufzählung (von Nominalphrasen, Adjektivphrasen etc.)
 * 
 * @author nikolaj
 */
public class Aufzaehlung<E extends Aufzaehlbar> implements Cloneable {
  private List<E> elements = new ArrayList<E>();

  // IDEA: Verknuepfung oder, entweder oder (nicht nur, sondern auch, sowohl
  // als auch)

  public Aufzaehlung() {
    this(ImmutableList.of());
  }

  public Aufzaehlung(final Collection<? extends E> elements) {
    this.elements.addAll(elements);
  }

  public boolean isEmpty() {
    return this.elements.isEmpty();
  }

  public Aufzaehlung<E> add(final E element) {
    this.elements.add(element);
    return this;
  }

  public Aufzaehlung<E> addFirst(final E element) {
    this.elements.add(0, element);
    return this;
  }

  public Aufzaehlung<E> delete(final int index) {
    this.elements.remove(index);
    return this;
  }

  /**
   * Beachte, dass die Parameter die Aufzählung als Ganzes spezifizieren! Die
   * einzelnen Elemente mögen unterschiedliche Attribute haben.
   * <p>
   * Parameter könnten <code>null</code> sein, je nach konkret aufgezählten
   * Elementen.
   */
  public String getFlektiert(final @Nullable Flexionstyp inflexionType,
      final @Nullable Kasus gramCase, final Numerus numerus,
      final @Nullable Genus genus,
      final @Nullable VerbFlexionstyp verbInflexionMode) {
    String res = "";
    for (int i = 0; i < this.elements.size(); i++) {
      if (i == 0)
        res = res
            + this.elements.get(i).getFlektiert(inflexionType, gramCase,
                numerus, genus, verbInflexionMode);
      else if (i < this.elements.size() - 1)
        res = res
            + ", "
            + this.elements.get(i).getFlektiert(inflexionType, gramCase,
                numerus, genus, verbInflexionMode);
      else
        // i == this.adverbPhrases.size() - 1
        res = res
            + " und "
            + this.elements.get(i).getFlektiert(inflexionType, gramCase,
                numerus, genus, verbInflexionMode);
    }
    return res;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Aufzaehlung<E> clone() {
    try {
      final Aufzaehlung<E> res = (Aufzaehlung<E>) super.clone();
      res.elements = new ArrayList<E>(this.elements);
      return res;
    } catch (final CloneNotSupportedException e) {
      throw new RuntimeException("Clone not supported for (cloneable) "
          + this.getClass() + " object??", e);
    }
  }
}
