/*
 * Verb
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Created on 16.03.2003
 *
 */
package de.nb.federkiel.grammatik.wortart.verb;

import javax.annotation.Nullable;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.phrase.Flexionstyp;
import de.nb.federkiel.deutsch.grammatik.wortart.substantiv.Aufzaehlbar;
import de.nb.federkiel.string.StringUtil;

/**
 * Ein Verb.
 * 
 * @author nikolaj
 */
public class Verb implements Aufzaehlbar {
  /** laufen */
  private final String infinitive;
  /** (er, sie, es) läuft */
  private final String drittePersonSingularIndikativPraesensAktiv;
  /** (sie, Sie) laufen */
  private final String drittePersonPluralIndikativPraesensAktiv;

  public Verb(final String infinitiv,
      final String drittePersonSingularIndikativPraesensAktiv,
      final String drittePersonPluralIndikativPraesensAktiv) {
    this.infinitive = infinitiv;
    this.drittePersonSingularIndikativPraesensAktiv = drittePersonSingularIndikativPraesensAktiv;
    this.drittePersonPluralIndikativPraesensAktiv = drittePersonPluralIndikativPraesensAktiv;
  }

  /**
   * Erzeugt eine neues Verb, basiserend auf dem "umgelauteten Stamm". Für viele
   * Verben ist der "umgelautete Stamm" einfach der Stamm (&quot;steh&quot;).
   * Für werden, die Umlaute verwende, ist der "umgelautete Stamm" der Stamm,
   * jedoch mit Umlaut. (&quot;läuf&quot; statt &quot;lauf&quot;).
   */
  public static Verb create(final String umgelauteterStamm) {
    final String stem = StringUtil.makeUmlautsPlain(umgelauteterStamm);

    return new Verb(stem + "en", umgelauteterStamm + "t", stem + "en");
  }

  @Override
  public String toString() {
    return getInfinitiv();
  }

  public String getInfinitiv() {
    return this.infinitive;
  }

  public String getWortform(final Numerus numerus) {
    if (numerus == Numerus.SINGULAR)
      return this.drittePersonSingularIndikativPraesensAktiv;

    if (numerus == Numerus.PLURAL)
      return this.drittePersonPluralIndikativPraesensAktiv;

    throw new RuntimeException("Unexpected numerus: " + numerus);
  }

  /**
   * @param numerus
   *          Darf <code>null</code> sein, je nach Flexionstyp
   */
  public String getVoice(final VerbFlexionstyp flexionstyp,
      final @Nullable Numerus numerus) {
    if (flexionstyp == VerbFlexionstyp.INFINITIV)
      return getInfinitiv();
    if (flexionstyp == VerbFlexionstyp.FLEKTIERT)
      return getWortform(numerus);
    throw new RuntimeException("Unexpected verb inflexion mode: "
        + flexionstyp);
  }

  @Override
  public String getFlektiert(final @Nullable Flexionstyp flexsionstyp,
      final @Nullable Kasus kasus, final Numerus numerus,
      final @Nullable Genus genus,
      final VerbFlexionstyp verbFlexionstyp) {
    // most parameters only apply for adjectives!
    return getVoice(verbFlexionstyp, numerus);
  }
}
