package de.nb.federkiel.deutsch.grammatik.wortart.adjektiv;

public enum AdjektivFlexionsklasse {
  /** <i>schön</i>, <i>anders</i> */
  FLEKTIERBAR_KEIN_ZAHLADJEKTIV(true, false),
  /** <i>drei</i> */
  FLEKTIERBAR_ZAHLADJEKTIV(true, false),
  /** <i>lila</i> */
  NICHT_FLEKTIERBAR_KEINE_ABLEITUNG_AUF_ER(false, false),
  /** <i>Kieler</i> */
  ABLEITUNG_AUF_ER(false, true);

  final boolean flektierbar;
  final boolean ableitungAufEr;

  private AdjektivFlexionsklasse(boolean flektierbar, boolean ableitungAufEr) {
    this.flektierbar = flektierbar;
    this.ableitungAufEr = ableitungAufEr;
  }

  public boolean isFlektierbar() {
    return flektierbar;
  }

  public boolean isAbleitungAufEr() {
    return ableitungAufEr;
  }
}
