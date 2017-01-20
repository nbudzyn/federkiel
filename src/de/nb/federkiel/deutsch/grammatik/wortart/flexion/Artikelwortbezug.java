package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

/**
 * Gibt an, welche Verwendungen eine Substantiv-Wortform zul�sst: Verlangt sie ein Artikelwort, kann
 * sie nur ohne Artikelwort stehen oder ist beides m�glich?
 *
 * @author nbudzyn 2010
 */
public enum Artikelwortbezug {
  /**
   * z.B. "Annas" (Gen), vgl. "*der Annas Hund", aber "Annas Hund" Hierzu geh�ren vor allem einige
   * Formen der NE.
   */
  ZWINGEND_OHNE_ARTIKELWORT(true, true, true, false),
  /**
   * z.B. "Anna" (Gen), vgl. "der Anna Hund", nicht aber "*Anna Hund" (nicht einmal im
   * Telegrammstil).
   * <p>
   * Hierzu geh�ren vor allem einige Formen der NE.
   */
  ZWINGEND_MIT_ARTIKELWORT_AUCH_IM_TELEGRAMMSTIL(false, false, false, true),
  /**
   * z.B. "Schweiz" (Dativ), etwa bei "Leben in Schweiz" (Telegrammstil), bei hei�en / taufen ("Das
   * Land hei�t Schweiz") und in der Anrede ("Schweiz, was hast du mir angetan!"). Vgl. Duden 400.
   * <p>
   * Hierzu geh�ren viele Formen derjenigen Eigennamen, die mit Artikel gebraucht werden (z.B. "die
   * Schweiz", "die Tschechoslowakei", "die USA").
   */
  ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL(false, false, true, true),
  /**
   * z.B. "Komponist" (Nom), vgl. "der Komponist war [...]", "[...] war Komponist"
   * <p>
   * Hierzu geh�ren fast Pluralformen der NN, au�erdem Singularformen von "sozial etablierten und
   * anerkannten" Gruppen ("Nationalit�t, Herkunf, Beruf, Funktion, Weltanschauung, Religion,
   * gesellschaftlicher Status usw." (Duden 445).
   */
  IM_NORMALSTIL_ALS_SUBJEKT_MIT_ARTIKELWORT(false, true, true, true),
  /**
   * z.B. "Teil" (Nom), vgl. "Teil der �bung war [...]", "Ein Teil war [...]", "[...] war (ein) Teil
   * davon."
   * <p>
   * Hierzu geh�ren fast alle Formen der NN.
   */
  IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT(true, true, true, true);

  private final boolean imNormalstilAlsSubjektOhneArtikelwortMoeglich;
  private final boolean imNormalstilOhneArtikelwortMoeglich;
  private final boolean imTelegrammstilOhneArtikelwortMoeglich;
  private final boolean mitArtikelwortMoeglich;

  private Artikelwortbezug(boolean imNormalstilAlsSubjektOhneArtikelwortMoeglich,
      boolean imNormalstilOhneArtikelwortMoeglich,
      boolean imTelegrammstilOhneArtikelwortMoeglich, boolean mitArtikelwortMoeglich) {
    this.imNormalstilAlsSubjektOhneArtikelwortMoeglich =
        imNormalstilAlsSubjektOhneArtikelwortMoeglich;
    this.imNormalstilOhneArtikelwortMoeglich = imNormalstilOhneArtikelwortMoeglich;
    this.imTelegrammstilOhneArtikelwortMoeglich = imTelegrammstilOhneArtikelwortMoeglich;
    this.mitArtikelwortMoeglich = mitArtikelwortMoeglich;
  }

  public boolean isImNormalstilAlsSubjektOhneArtikelwortMoeglich() {
    return imNormalstilAlsSubjektOhneArtikelwortMoeglich;
  }

  public boolean isImNormalstilOhneArtikelwortMoeglich() {
    return imNormalstilOhneArtikelwortMoeglich;
  }

  public boolean isImTelegrammstilOhneArtikelwortMoeglich() {
    return imTelegrammstilOhneArtikelwortMoeglich;
  }

  public boolean isMitArtikelwortMoeglich() {
    return mitArtikelwortMoeglich;
  }
}
