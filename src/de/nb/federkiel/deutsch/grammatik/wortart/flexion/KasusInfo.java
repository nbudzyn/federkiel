package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.AKKUSATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.DATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.GENITIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.NOMINATIV;

import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;

/**
 * Gibt für eine <i>Wortform</i> an, wie es um ihren Kasus steht - insbesondere
 * bei einer Genitiv-Form. Dies wird in der Grammatik benötigt, um gewisse
 * Entscheidungen zu treffen (z.B. für die Genitivregel).
 *
 * @author nbudzyn 2010
 */
public enum KasusInfo {
  //@formatter:off
  NOM_KEIN_NOMEN(NOMINATIV, false, false, false),
  NOM_MGLW_DAT_AKK_MIT_UNTERL_KASUSFLEX(NOMINATIV, true, false, false),
  NOM_NICHT_ETWA_DAT_AKK_MIT_UNTERL_KASUSFLEX(NOMINATIV, false, false, false),
  GEN_S(GENITIV, false, true, false),
  GEN_R(GENITIV, false, false, true),
  GEN_OHNE_S_UND_R(GENITIV, false, false, false),
  DAT(DATIV, false, false, false),
  AKK(AKKUSATIV, false, false, false);
  //@formatter:on

  private final Kasus kasus;
  private final boolean genitivSichtbarDurchS;
  private final boolean genitivSichtbarDurchR;
  private final boolean mglwDatOderAkkMitUnterlassenerKasusflexion;

  private KasusInfo(final Kasus kasus,
      final boolean mglwDatOderAkkMitUnterlassenerKasusflexion,
      final boolean genitivSichtbarDurchS, final boolean genitivSichtbarDurchR) {
    this.kasus = kasus;
    this.mglwDatOderAkkMitUnterlassenerKasusflexion = mglwDatOderAkkMitUnterlassenerKasusflexion;
    this.genitivSichtbarDurchS = genitivSichtbarDurchS;
    this.genitivSichtbarDurchR = genitivSichtbarDurchR;
  }

  public Kasus getKasus() {
    return kasus;
  }

  public boolean isGenitivSichtbarDurchR() {
    return genitivSichtbarDurchR;
  }

  public boolean isGenitivSichtbarDurchS() {
    return genitivSichtbarDurchS;
  }

  public boolean isMglwDatOderAkkMitUnterlassenerKasusflexion() {
    return mglwDatOderAkkMitUnterlassenerKasusflexion;
  }

  public final static KasusInfo nomNomen(
      final boolean mglwDatOderAkkMitUnterlassenerKasusflexion) {
    return mglwDatOderAkkMitUnterlassenerKasusflexion ? NOM_MGLW_DAT_AKK_MIT_UNTERL_KASUSFLEX
        : NOM_NICHT_ETWA_DAT_AKK_MIT_UNTERL_KASUSFLEX;
  }
}
