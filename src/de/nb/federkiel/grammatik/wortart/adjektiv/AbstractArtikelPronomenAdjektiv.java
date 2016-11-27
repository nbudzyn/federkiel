package de.nb.federkiel.grammatik.wortart.adjektiv;

public class AbstractArtikelPronomenAdjektiv {
  private AbstractArtikelPronomenAdjektiv() {}

  // public Wortformen<ArtikelPronomenAdjektivWortform> getWortformenStarkeFlexion(final Kasus
  // kasus,
  // final Numerus numerus, final Genus genus) {
  // switch (numerus) {
  // case SINGULAR:
  // getWortformenStarkSg(kasus, genus);
  // case PLURAL:
  // getWortformenStarkPl(kasus, genus);
  // default:
  // throw new IllegalArgumentException("Unerwarteter Kasus: " + kasus);
  // }
  // }
  //
  // private Wortformen<ArtikelPronomenAdjektivWortform> getWortformenStarkSg(Kasus kasus,
  // Genus genus) {
  // switch (genus) {
  // case FEMININUM:
  // return getWortformenStarkSgMask(kasus);
  // case MASKULINUM:
  // return getWortformenStarkSgFem(kasus);
  // case NEUTRUM:
  // return getWortformenStarkSgNeutr(kasus);
  // default:
  // throw new IllegalArgumentException("Unerwartetes Genus: " + genus);
  // }
  // }
  //
  // private Wortformen<ArtikelPronomenAdjektivWortform> getWortformenStarkSgMask(Kasus kasus) {
  // if (nomSgMaskUndNomAkkSgNeutrModus.equals(NomSgMaskUndNomAkkSgNeutrModus.ENDUNGSLOS)) {
  // res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
  // VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_STARK, SINGULAR, MASKULINUM,
  // additionalFeaturesSgMitVerbFrame, stammInKomparation));
  //
  // }
  // if (nomSgMaskUndNomAkkSgNeutrModus.equals(NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG)
  // || nomSgMaskUndNomAkkSgNeutrModus.equals(
  // NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES)) {
  // res.add(buildWortform(lexeme, pos, KasusInfo.NOM_KEIN_NOMEN,
  // vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM,
  // additionalFeaturesSgMitVerbFrame, stammInKomparation + "er"));
  // }
  // // TODO schön Heinrich, schön Heinrichs (generell unflektiert)
  //
  // if (genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.NUR_ES)
  // || genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.ES_UND_EN)) {
  // res.add(buildWortform(lexeme, pos, KasusInfo.GEN_S,
  // vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM,
  // additionalFeaturesSgMitVerbFrame, stammInKomparation + "es"));
  // }
  //
  // if (genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.NUR_EN)
  // || genMaskNeutrSgModus.equals(GenMaskNeutrSgModus.ES_UND_EN)) {
  // res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
  // vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM,
  // additionalFeaturesSgMitVerbFrame, stammInKomparation + "en"));
  // if (eTilgungImSuffixEnUndEmErlaubt) {
  // res.add(buildWortform(lexeme, pos, KasusInfo.GEN_OHNE_S_UND_R,
  // vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM,
  // additionalFeaturesSgMitVerbFrame, stammInKomparation + "n")); // dunkeln
  // }
  // }
  //
  // res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
  // vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM,
  // additionalFeaturesSgMitVerbFrame, stammInKomparation + "em"));
  // if (eTilgungImSuffixEnUndEmErlaubt) {
  // res.add(buildWortform(lexeme, pos, KasusInfo.DAT,
  // vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM,
  // additionalFeaturesSgMitVerbFrame, stammInKomparation + "m"));
  // }
  //
  // res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
  // vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM,
  // additionalFeaturesSgMitVerbFrame, stammInKomparation + "en"));
  // if (eTilgungImSuffixEnUndEmErlaubt) {
  // res.add(buildWortform(lexeme, pos, KasusInfo.AKK,
  // vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung, SINGULAR, MASKULINUM,
  // additionalFeaturesSgMitVerbFrame, stammInKomparation + "n"));
  // }
  // }
  //
  // private Wortformen<ArtikelPronomenAdjektivWortform> getWortformenStarkSgFem(Kasus kasus) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // private Wortformen<ArtikelPronomenAdjektivWortform> getWortformenStarkSgNeutr(Kasus kasus) {
  // // TODO Auto-generated method stub
  // return null;
  // }
}
