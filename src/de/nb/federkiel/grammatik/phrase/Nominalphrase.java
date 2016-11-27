/*
 * NounPhrase
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Created on 20.03.2003
 *
 */
package de.nb.federkiel.grammatik.phrase;

import static de.nb.federkiel.grammatik.kategorie.Kasus.GENITIV;
import static de.nb.federkiel.string.StringUtil.concatSpacedTrim;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.grammatik.kategorie.ArtikelTyp;
import de.nb.federkiel.grammatik.kategorie.Flexionstyp;
import de.nb.federkiel.grammatik.kategorie.Genus;
import de.nb.federkiel.grammatik.kategorie.IDeterminativ;
import de.nb.federkiel.grammatik.kategorie.Kasus;
import de.nb.federkiel.grammatik.kategorie.Numerus;
import de.nb.federkiel.grammatik.wortart.adjektiv.Adjektiv;
import de.nb.federkiel.grammatik.wortart.artikelwort.Artikel;
import de.nb.federkiel.grammatik.wortart.substantiv.Substantiv;

/**
 * Eine Nominalphrase, also eine Phrase, die wie ein Nomen verwendet werden
 * kann.
 * <p>
 * Beispiele:
 * <ul>
 * <li>&quot;(eine) geoeffnete, mit goldener Farbe angestrichene und einladend
 * aussehende Tuer aus Eichenholz nach Norden&quot;
 * </ul>
 *
 * @author nikolaj
 */
public class Nominalphrase implements Cloneable {
  /**
   * &quot;geoeffnet&quot;, &quot;mit goldener Farbe angestrichen&quot;,
   * &quot;einladend aussehend&quot;
   */
  private AdjektivphrasenAufzaehlung adjektivphrasen;

  /** &quot;Tuer&quot; */
  private final Substantiv substantiv;

  /** "des Raums" */
  private Aufzaehlung<ArtNumNominalphrase> genitivattribute;

  /** &quot;aus Eichenholz&quot;,&quot;nach Norden&quot; */
  private Aufzaehlung<Praepositionalphrase> adverbialeAngaben;

  /** "(eine Kanne) schwarzer Kaffee" */
  private @Nullable NumNominalphrase partitiveApposition;

  /** kein..., nicht der ... */
  private final boolean negiert;

  // IDEA: Nebensatz: die Tuer, die ....; die Wand, an der ...

  public Nominalphrase(final Substantiv substantiv) {
    this(new AdjektivphrasenAufzaehlung(), substantiv);
  }

  public Nominalphrase(final Adjektiv adjektiv, final Substantiv substantiv) {
    this(new Adjektivphrase(adjektiv), substantiv);
  }

  public Nominalphrase(final Adjektivphrase adjektivphrase,
      final Substantiv substantiv) {
    this(new AdjektivphrasenAufzaehlung(ImmutableList.of(adjektivphrase)),
        substantiv);
  }

  public Nominalphrase(final AdjektivphrasenAufzaehlung adjektivphrasen,
      final Substantiv substantiv) {
    this(adjektivphrasen, substantiv, new Aufzaehlung<ArtNumNominalphrase>(),
        new Aufzaehlung<Praepositionalphrase>());
  }

  public Nominalphrase(final Substantiv substantiv,
      final ArtNumNominalphrase genitivattribut) {
    this(new AdjektivphrasenAufzaehlung(), substantiv,
        new Aufzaehlung<>(ImmutableList.of(genitivattribut)),
        new Aufzaehlung<Praepositionalphrase>());
  }

  public Nominalphrase(final Substantiv substantiv,
      final Aufzaehlung<ArtNumNominalphrase> genitivattribute,
      final Aufzaehlung<Praepositionalphrase> adverbialeAngaben) {
    this(new AdjektivphrasenAufzaehlung(), substantiv, genitivattribute,
        adverbialeAngaben);
  }

  public Nominalphrase(final AdjektivphrasenAufzaehlung adjektivphrasen,
      final Substantiv substantiv,
      final Aufzaehlung<ArtNumNominalphrase> genitivattribute,
      final Aufzaehlung<Praepositionalphrase> adverbialeAngaben) {
    this(adjektivphrasen, substantiv, genitivattribute, adverbialeAngaben,
        false); // nicht
                                                                 // negiert
  }

  public Nominalphrase(final AdjektivphrasenAufzaehlung adjektivphrasen,
      final Substantiv substantiv,
      final Aufzaehlung<ArtNumNominalphrase> genitivattribute,
      final Aufzaehlung<Praepositionalphrase> adverbialeAngaben,
      final boolean negiert) {
    this(adjektivphrasen, substantiv, genitivattribute,
        adverbialeAngaben, null, negiert);
  }

  public Nominalphrase(final AdjektivphrasenAufzaehlung adjektivphrasen,
      final Substantiv substantiv,
      final Aufzaehlung<ArtNumNominalphrase> genitivattribute,
      final Aufzaehlung<Praepositionalphrase> adverbialeAngaben,
      final @Nullable NumNominalphrase partitiveApposition) {
    this(adjektivphrasen, substantiv, genitivattribute, adverbialeAngaben,
        partitiveApposition,
        false);
  }

  public Nominalphrase(final AdjektivphrasenAufzaehlung adjektivphrasen,
      final Substantiv substantiv,
      final Aufzaehlung<ArtNumNominalphrase> genitivattribute,
      final Aufzaehlung<Praepositionalphrase> adverbialeAngaben,
      final @Nullable NumNominalphrase partitiveApposition,
      final boolean negiert) {
    this.adjektivphrasen = adjektivphrasen;
    this.substantiv = substantiv;
    this.genitivattribute = genitivattribute;
    this.adverbialeAngaben = adverbialeAngaben;
    this.partitiveApposition = partitiveApposition;
    this.negiert = negiert;
  }

  public void addFirstAdjektivphrase(final Adjektiv adjektiv) {
    addFirstAdjektivphrase(new Adjektivphrase(adjektiv));
  }

  public void addFirstAdjektivphrase(final Adjektivphrase adjektivphrase) {
    adjektivphrasen.addFirst(adjektivphrase);
  }

  public void addPartitiveApposition(
      final @Nullable NumNominalphrase partitiveApposition) {
    if (partitiveApposition != null && this.partitiveApposition != null) {
      throw new IllegalStateException(
          "Nominalphrase besitzt bereits eine partitive Apposition, kann keine weitere erhalten!");
    }

    this.partitiveApposition = partitiveApposition;
  }

  /**
   * Ob die Nominalphrase einen Singular besitzt. ("Leute" besitzt z.B. keinen
   * Singular.)
   */
  public boolean hatSingular() {
    return substantiv.hatSingular();
  }

  public boolean hatPlural() {
    return substantiv.hatPlural();
  }

  public Genus getGenus() {
    return substantiv.getGenus();
  }

  @Override
  public String toString() {
    return getFlektiert();
  }

  public String getFlektiert() {
    return getFlektiert(Kasus.NOMINATIV);
  }

  public String getFlektiert(final Kasus kasus) {
    return getFlektiert(kasus, Numerus.SINGULAR);
  }

  /**
   * Ohne Artikel (&quot;freundlicher Ork (zu verkaufen)&quot;)
   */
  public String getFlektiert(final Kasus kasus, final Numerus numerus) {
    return getFlektiertMitDeterminativ(kasus, numerus, null);
  }

  public String getFlektiertMitArtikel(final Kasus kasus) {
    return getFlektiertMitArtikel(kasus, Numerus.SINGULAR);
  }

  public String getFlektiertMitArtikel(final Kasus kasus, final Numerus numerus) {
    return getFlektiertMitDeterminativ(kasus, numerus, Artikel.BESTIMMT);
  }

  public String getFlektiertMitArtikel(final Kasus kasus,
      final ArtikelTyp artikelTyp) {
    return getFlektiertMitDeterminativ(kasus, Numerus.SINGULAR,
        Artikel.fuerTyp(artikelTyp));
  }

  /**
   * Gibt die flektierte Form zurück, ggf. mit Determinativum.
   */
  public String getFlektiertMitDeterminativ(final Kasus kasus,
      final Numerus numerus, final @Nullable IDeterminativ determinativ) {
    // FIXME: von großen Orks mit einem grimmigen Lächeln does not work?!
    String res = determinativ != null ? determinativ
        .getFlektiertAlsDeterminativFuer(kasus, numerus, getGenus(),
            isNegiert()) : "";



    final Flexionstyp flexionstypAdjektivphrasen =
        determinativ != null && determinativ.hatFlexionsendung(kasus, numerus, getGenus())
            ? Flexionstyp.SCHWACHE_FLEXION
            : Flexionstyp.STARKE_FLEXION;

    res = concatSpacedTrim(res, adjektivphrasen.getFlektiert(
        flexionstypAdjektivphrasen, kasus, numerus, getGenus(), null));
    res = concatSpacedTrim(res,
        substantiv.getWortform(kasus, numerus));
    res = concatSpacedTrim(res,
        genitivattribute.getFlektiert(null, GENITIV, null, null, null));
    res = concatSpacedTrim(res,
        adverbialeAngaben.getFlektiert(null, null, null, null, null));
    if (partitiveApposition != null) {
      res = concatSpacedTrim(res, partitiveApposition.getFlektiert(kasus));
    }

    return res;
  }

  @Override
  public Nominalphrase clone() {
    try {
      final Nominalphrase res = (Nominalphrase) super.clone();
      res.adjektivphrasen = adjektivphrasen.clone();
      res.genitivattribute = genitivattribute.clone();
      res.adverbialeAngaben = adverbialeAngaben.clone();
      return res;
    } catch (final CloneNotSupportedException e) {
      throw new RuntimeException("Clone not supported for (cloneable) "
          + this.getClass() + " object??", e);
    }
  }

  public Nominalphrase getNegierteNominalphrase() {
    return new Nominalphrase(adjektivphrasen, substantiv,
        genitivattribute, adverbialeAngaben,
        partitiveApposition, true);
  }

  /**
   * Gibt das Relativpronomen für diese Phrase zurück: der, den, die o.Ä.
   */
  public String getRelativpronomen(final Kasus kasus, final Numerus numerus) {
    return ArtikelTyp.getArtikel(ArtikelTyp.BESTIMMTER_ARTIKEL, kasus, numerus,
        getGenus(), false); // nicht negiert
  }

  public AdjektivphrasenAufzaehlung getAdjektivphrasen() {
    return adjektivphrasen;
  }

  public Substantiv getSubstantiv() {
    return substantiv;
  }

  public Aufzaehlung<Praepositionalphrase> getAdverbialeAngaben() {
    return adverbialeAngaben;
  }

  public @Nullable NumNominalphrase getPartitiveApposition() {
    return partitiveApposition;
  }

  // -------------- PRIVATE ----------------

  private boolean isNegiert() {
    return negiert;
  }

}
