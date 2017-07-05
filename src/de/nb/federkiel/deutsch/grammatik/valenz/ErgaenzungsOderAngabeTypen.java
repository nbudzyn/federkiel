package de.nb.federkiel.deutsch.grammatik.valenz;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.AKKUSATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.DATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.GENITIV;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.reflection.ReflectionUtil;

/**
 * Enthält die Typen von Ergänzung oder Angabe zu einem Verb - z.B. das Subjekt, das Akkusativobjekt
 * oder adverbiale Angaben.
 *
 * @author nbudzyn 2011
 */
public final class ErgaenzungsOderAngabeTypen {
  public static final AbstractErgaenzungsOderAngabenTyp
  // Ergänzungen (zwingend)
  // - im Nominativ
  SUBJEKT = new SubjektTyp("Subjekt", 1, false), // min (and max) 1
                                                 // filling, kein
                                                 // Pseudoaktant

      PRAEDIKATIVUM = PraedikativumTyp.INSTANCE,

      /**
       * das "es" in "es regnet" (Duden Bd. 4 2006, 560)
       */
      OBLIG_PSEUDOAKTANT_FORMALES_SUBJ = new SubjektTyp("PseudoaktantFormalesSubj", 1, true), // min
      // (and
      // max)
      // 1
      // filling, Pseudoaktant

      /**
       * Das "es" (bzw. das fehlende es!) in <i>() Mich graut. / *Es* graut mich.</i> (Duden Bd. 4
       * 2006, 563)
       */
      OPT_PSEUDOAKTANT_FORMALES_SUBJ = new SubjektTyp("PseudoaktantFormalesSubj", 0, true), // min 0
                                                                                            // filling
                                                                                            // (max
                                                                                            // 1
      // filling), Pseudoaktant

      // - im Genitiv
      GENITIVOBJEKT = new ObjektTyp("Genitivobjekt", GENITIV),

      // - im Dativ
      DATIVOBJEKT = new ObjektTyp("Dativobjekt", DATIV),

      /**
       * Ein Dativobjekt, das zwingend reflexiv sein muss
       */
      REFL_DATIVOBJEKT = new ObjektTyp("ReflDativobjekt", DATIV, 1, 1, true, false),
      // TODO Genauer spezifizieren (muss reflexiv sein) - reflexiven
      // Gebrauch von Verben in die Grammatik einführen

      // - im Akkusativ
      AKKUSATIVOBJEKT = new ObjektTyp("Akkusativobjekt", AKKUSATIV),
      /**
       * z.B. bei "jdn. etw. lehren" das "jdn." (weil es "beinahe ein Dativ geworden wäre")
       */
      ZUS_PERSON_AKK_OBJEKT = new ObjektTyp("ZusPersonAkkObjekt", AKKUSATIV),

      /**
       * Ein Akkusativobjekt, das zwingend reflexiv sein muss
       */
      REFL_AKKUSATIVOBJEKT = new ObjektTyp("ReflAkkusativobjekt", AKKUSATIV, 1, 1, true, false),

      /**
       * das "es" bei "es gut haben", "es schlecht haben", "es auf jdn. anlegen" etc. (Duden Bd. 4
       * 2006, 539)
       */
      OBLIG_PSEUDOAKTANT_AKK = new ObjektTyp("PseudoaktantAkk", AKKUSATIV, 1, 1, false, true),

      // Angaben (fakultativ)
      ADVERBIALE_ANGABEN_FUER_VERBFORM = new AdverbialeAngabenTyp("AdverbialeAngabe", false),

      ADVERBIALE_ANGABEN_FUER_ADJEKTIVISCHE_FORM =
          new AdverbialeAngabenTyp("AdverbialeAngabe", true),

      /**
       * "Seiner Mutter [wird er] das Geld geben"[.]
       * <p>
       * Ein reiner Infinitiv (also ohne "zu") mit seinen Ergänzungen und Angaben - allerdings nicht
       * als (kontinuierliche) Phrase, sondern als Rollenrahmen.
       */
      REINER_INFINITIV_RF = ReinerInfinitivTyp.INSTANCE;

  /**
   * Alle Ergaenzungs-oder-Angabe-Typen
   */
  public static final Collection<AbstractErgaenzungsOderAngabenTyp> ALL =
      ReflectionUtil.getConstantFields(ErgaenzungsOderAngabeTypen.class,
          AbstractErgaenzungsOderAngabenTyp.class);

  private ErgaenzungsOderAngabeTypen() {
    super();
  }

  public static Collection<RoleFrameSlot> buildAngabenSlots(final String person,
      final Genus genusDesSubjekts, final Numerus numerusDesSubjekts,
      final String hoeflichkeitsformDesSubjekts, final boolean fuerAdjektivischeForm) {
    return ImmutableList.of(getAdverbialeAngabenTyp(fuerAdjektivischeForm).buildSlot(person,
        genusDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts));
  }

  private static AbstractErgaenzungsOderAngabenTyp getAdverbialeAngabenTyp(
      final boolean fuerAdjektivischeForm) {
    if (fuerAdjektivischeForm) {
      return ADVERBIALE_ANGABEN_FUER_ADJEKTIVISCHE_FORM;
    }

    return ADVERBIALE_ANGABEN_FUER_VERBFORM;
  }

  /**
   * Erzeugt die {@link RoleFrameSlot}s, die angeben, welche Angaben (unabhängig von Person und
   * Numerus des Subjekts) möglich sind.
   */
  public static Collection<RoleFrameSlot> buildAngabenRestrictionSlots() {
    // ADVERBIALE_ANGABEN_FUER_VERBFORM ist weniger restritiv als
    // ADVERBIALE_ANGABEN_FUER_ADJEKTIVISCHE_FORM
    return ImmutableList.of(ADVERBIALE_ANGABEN_FUER_VERBFORM.buildRestrictionSlot());
  }

}
