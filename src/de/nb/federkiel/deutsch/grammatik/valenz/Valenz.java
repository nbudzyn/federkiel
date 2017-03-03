package de.nb.federkiel.deutsch.grammatik.valenz;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.feature.RoleFrame;
import de.nb.federkiel.feature.RoleFrameSlot;
import de.nb.federkiel.reflection.ReflectionUtil;

/**
 * Valenz eines Verbs - ist es transitiv, intransitiv, erfordert es zwei Akkusative...
 * <p>
 * Präpositionalobjekte und adverbiale Ergänzungen werden nicht berücksichtig, sondern mit den
 * adverbialen Angaben zusammengefasst (die jederzeit möglich sind).
 * <p>
 * Valenzen lassen sich ähnlich auch bei Adjektiven beobachten (<i>des Lärms schon lange
 * überdrüssigen</i>) - in diesem Fall ist das "Subjekt" jeweils implizit gegeben.
 *
 * @author nbudzyn 2011
 */
public final class Valenz {
  // TRANSITIV (i.w.S.)
  /**
   * (jemanden) kennen, (etwas) essen
   */
  public static final Valenz TRANSITIV_IES = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);

  /**
   * (jemandem etwas) stehlen
   */
  public static final Valenz DITRANSITIV = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.DATIVOBJEKT,
      ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);

  /**
   * (jemanden einer Sache) verdächtigen
   */
  public static final Valenz AKKUSATIV_UND_GENITIV_OBJEKT = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.GENITIVOBJEKT,
      ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);

  /**
   * (jemanden etwas) lehren
   */
  public static final Valenz ZWEI_AKKUSATIVOBJEKTE = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT,
      ErgaenzungsOderAngabeTypen.ZUS_PERSON_AKK_OBJEKT);

  /**
   * "Die Zeitungen nannten den Schiedsrichter einen Trottel." -
   * "Der Schiedsrichter wurde von den Zeitungen ein Trottel genannt."
   */
  public static final Valenz AKK_OBJEKT_UND_OBJEKTSPRAEDIKAT_IM_AKK = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);
  // TODO Objektsprädikat im Akkusativ, siehe Duden Bd.4 2006, 538

  /**
   * "Man betrachtet seinen Rücktritt als einen großen Fehler." -
   * "Sein Rücktritt wurde als ein großer Fehler betrachtet."
   */
  public static final Valenz AKK_OBJEKT_UND_OBJEKTSPRAEDIKAT_MIT_ALS = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);
  // TODO Objektsprädikat im Akkusativ, siehe Duden Bd.4 2006, 538

  // INTRANSITIV
  /**
   * laufen
   */
  public static final Valenz NUR_SUBJEKT = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT);

  /**
   * (jemandem) helfen
   */
  public static final Valenz DATIV_OBJEKT = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.DATIVOBJEKT);

  /**
   * (einer Sache) bedürfen
   */
  public static final Valenz GENITIV_OBJEKT = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.GENITIVOBJEKT);

  /**
   * (etwas / irgendwie) sein, (etwas / irgendwie) werden
   */
  public static final Valenz SUBJEKT_PRAEDIKATIVUM = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.PRAEDIKATIVUM);

  // SONSTIGES

  /**
   * blau (nur ein implizites Subjekt)
   */
  public static final Valenz LEER = new Valenz();

  /**
   * sich schämen.
   * <p>
   * Diese Verben gelten NICHT als transitiv!
   */
  public static final Valenz MIT_REFLEXIVEM_AKKUSATIVOBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.REFL_AKKUSATIVOBJEKT);

  /**
   * sich jdm. anvertrauen
   * <p>
   * Diese Verben gelten NICHT als transitiv, auch nicht als ditransitiv!
   */
  public static final Valenz MIT_REFLEXIVEM_AKKUSATIVOBJ_UND_MIT_DATIVOBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.REFL_AKKUSATIVOBJEKT,
      ErgaenzungsOderAngabeTypen.DATIVOBJEKT);

  /**
   * sich einer Sache bemächtigen
   * <p>
   * Diese Verben gelten NICHT als transitiv.
   */
  public static final Valenz MIT_REFLEXIVEM_AKKUSATIVOBJ_UND_MIT_GENITIVOBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.REFL_AKKUSATIVOBJEKT,
      ErgaenzungsOderAngabeTypen.GENITIVOBJEKT);

  /**
   * Diese Verben gelten NICHT als intransitiv.
   */
  public static final Valenz MIT_REFLEXIVEM_DATIVOBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.REFL_DATIVOBJEKT);

  /**
   * sich etwas einprägen: Ich präge mir etwas ein.
   * <p>
   * Diese Verben gelten NICHT als intransitiv und nicht als transitiv.
   */
  public static final Valenz MIT_REFLEXIVEM_DATIVOBJ_UND_MIT_AKKUSATIVOBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.REFL_DATIVOBJEKT,
      ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);

  /**
   * Es regnet.
   * <p>
   * (Hierunter fallen die "Wetterverben".)
   */
  public static final Valenz IMPERSONALIA_NUR_PSEUDOAKTANT_ALS_FORMALES_SUBJEKT = new Valenz(
      ErgaenzungsOderAngabeTypen.OBLIG_PSEUDOAKTANT_FORMALES_SUBJ);

  /**
   * Es regnet rote Rosen.
   */
  public static final Valenz IMPERSONALIA_PSEUDOAKTANT_ALS_FORMALES_SUBJEKT_UND_AKK_OBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.OBLIG_PSEUDOAKTANT_FORMALES_SUBJ,
      ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);

  /**
   * Mir graut (vor...) / Es graut mir (vor...)
   */
  public static final Valenz IMPERSONALIA_OPT_PSEUDOAKTANT_ALS_FORMALES_SUBJEKT_UND_DAT_OBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.OPT_PSEUDOAKTANT_FORMALES_SUBJ,
      ErgaenzungsOderAngabeTypen.DATIVOBJEKT);

  /**
   * Mich graut (vor...) / Es graut mich (vor...)
   */
  public static final Valenz IMPERSONALIA_OPT_PSEUDOAKTANT_ALS_FORMALES_SUBJEKT_UND_AKK_OBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.OPT_PSEUDOAKTANT_FORMALES_SUBJ,
      ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);

  /**
   * Es bedarf eines Auswegs.
   */
  public static final Valenz IMPERSONALIA_PSEUDOAKTANT_ALS_FORMALES_SUBJEKT_UND_GEN_OBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.OBLIG_PSEUDOAKTANT_FORMALES_SUBJ,
      ErgaenzungsOderAngabeTypen.GENITIVOBJEKT);

  /**
   * Es handelt sich um Peter.
   */
  public static final Valenz IMPERSONALIA_PSEUDOAKTANT_ALS_FORMALES_SUBJEKT_UND_REFL_AKK_OBJ = new Valenz(
      ErgaenzungsOderAngabeTypen.OBLIG_PSEUDOAKTANT_FORMALES_SUBJ,
      ErgaenzungsOderAngabeTypen.REFL_AKKUSATIVOBJEKT);

  /**
   * Nicht transitive Verben mit einem obligatorischen Pseudoaktanten im
   * Akkusativ (und keinen weiteren Objekten): "es gut haben",
   * "es auf etwas anlegen"
   */
  public static final Valenz NICHT_TRANSITIV_MIT_OBLIG_PSEUDOAKTANT_IM_AKK = new Valenz(
      ErgaenzungsOderAngabeTypen.SUBJEKT,
      ErgaenzungsOderAngabeTypen.OBLIG_PSEUDOAKTANT_AKK);
  // TODO Ist nicht "gut" bei "es gut haben" eine Art praedikatives Adjektiv
  // (wie bei "Peter ist nett."?)

  /**
   * Alle möglichen Valenzen
   */
  public static final Collection<Valenz> ALL = ReflectionUtil
      .getConstantFields(Valenz.class, Valenz.class);

  /**
   * Die Ergänzungen, die ein solches Verb verlangt - nicht jedoch die Angaben!
   */
  private final AbstractErgaenzungsOderAngabenTyp[] ergaenzungstypen;

  /**
   * Gecacheter wert
   */
  private final RoleFrame restrictions;

  private Valenz(final AbstractErgaenzungsOderAngabenTyp... ergaenzungstypen) {
    this.ergaenzungstypen = ergaenzungstypen;
    restrictions = calcRestrictions();
  }

  /**
   * Erzeugt aus dieser Valenz eine weitere, die davon ausgeht, dass das Subjekt
   * IMPLIZIT gegeben ist. Sollte das gar nicht möglich sein (weil diese Valenz
   * gar kein Subjekt vorsah), wird <code>null</code> zurückgegeben.
   */
  public Valenz beiImplizitemSubjekt() {
    return beiDiesenImplizitenErgaenzungen(ErgaenzungsOderAngabeTypen.SUBJEKT);
  }

  public Valenz beiImplizitemSubjektUndAkkusativObjekt() {
    return beiDiesenImplizitenErgaenzungen(ErgaenzungsOderAngabeTypen.SUBJEKT,
        ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);
  }

  public Valenz beiImplizitemSubjektUndZusPersonAkkusativObjekt() {
    return beiDiesenImplizitenErgaenzungen(ErgaenzungsOderAngabeTypen.SUBJEKT,
        ErgaenzungsOderAngabeTypen.ZUS_PERSON_AKK_OBJEKT);
  }

  public Valenz beiImplizitemSubjektUndReflAkkObj() {
    return beiDiesenImplizitenErgaenzungen(ErgaenzungsOderAngabeTypen.SUBJEKT,
        ErgaenzungsOderAngabeTypen.REFL_AKKUSATIVOBJEKT);
  }

  // TODO: "Dass-Sätze besetzen in der Regel Stellen, die auch von nominalen Ergänzungen besetzt
  // werden können,
  // insbesondere die Stelle des Subjekts und des direkten Objekts. Umgekehrt kann aber nicht
  // überall dort [...]
  // auch ein dass-Satz stehen. [...] Damit ist ein syntaktischer Unterschied [...] festgestellt
  // [...]" (Mehr in "Der Satz" S.59.)

  // TODO "Neben den dass-Stätzen spielen die indirekten Fragesätze als Ergänzungen die wichtigste
  // Rolle.
  // [Darunter] verstehen wir einen Nebensatz, der mit ob oder einen Fragewort (wie, wer, was [...])
  // eingeleitet
  // ist." (Das Wort S.59f.)

  // TODO "Viele Verben können - oft als Alternative von dass-Sätzen - zu-Infinitive als Ergänzungen
  // regieren. [...]
  // Karl hofft zu gewinnen. Der zu-Infinitiv kann in erste Näherung als eine Art verkürzter Satz
  // aufgefasst werden, in dem die Subjektstelle nicht besetzt ist."
  // (Beispiele / Gegenbeispiele: "Helga verspricht, dass sie wartet / zu warten." Aber nur
  // "Helga merkt, dass sie träumt." und "Helga versucht zu lesen." (Das Wort S. 60.)

  // TODO "Als weitere Form von Ergänzung spielen Sätze eine Rolle, die wie Hauptsätze aussehen
  // [...]
  // Helga behauptet, Karl spiele / spielt Saxophon." (Das Wort S. 60.)

  // TODO "Als weitere [Konstruktion] kommt der reine Infinitiv im sog. AcI vor (Helga sieht ihn
  // Kartoffeln schälen [...]). (Das Wort S. 60.)

  /**
   * Erzeugt aus dieser Valenz eine weitere, die davon ausgeht, dass das gewisse Ergänzungen
   * IMPLIZIT gegeben ist. Diese Ergänzungen werden aus der Valenz entfernt. Sollte eine oder
   * mehrere dieser Ergänzungen gar nicht vorgesehen sein, wird <code>null</code> zurückgegeben.
   * <p>
   * Für adverbiale Akkkusative und adverbiale Genitive gibt es einige Einschränkungen gegebenüber
   * der ursprünglichen Valenz. (
   * <p>
   * Diese (Woche) haben wir Peter gefeiert.
   * </p>
   * , aber nicht
   * <p>
   * *Den diese gefeierten Peter.
   * </p>
   * )
   */
  public Valenz beiDiesenImplizitenErgaenzungen(
      final AbstractErgaenzungsOderAngabenTyp... impliziteAngabenOderErgaenzungstypen) {
    final ImmutableList.Builder<AbstractErgaenzungsOderAngabenTyp> res = ImmutableList
        .builder();

    final Collection<AbstractErgaenzungsOderAngabenTyp> angabenOderErgaenzungenDieNochEntferntWerdenSollen = new LinkedList<>(
        asList(impliziteAngabenOderErgaenzungstypen));

    for (final AbstractErgaenzungsOderAngabenTyp ergaenzungstyp : ergaenzungstypen) {
      // ergänzungstyp - wenn möglich - aus den noch nicht gefundenen
      // entfernen
      boolean sollDieserTypEntferntWerden = false;
      for (final Iterator<AbstractErgaenzungsOderAngabenTyp> innerIter = angabenOderErgaenzungenDieNochEntferntWerdenSollen
          .iterator(); innerIter.hasNext();) {
        final AbstractErgaenzungsOderAngabenTyp typDerNochEntferntWerdenSoll = innerIter
            .next();
        if (ergaenzungstyp.equals(typDerNochEntferntWerdenSoll)) {
          sollDieserTypEntferntWerden = true;
          innerIter.remove();
          break;
        }
      }

      if (!sollDieserTypEntferntWerden) {
          res.add(ergaenzungstyp);
      }
    }

    if (!angabenOderErgaenzungenDieNochEntferntWerdenSollen.isEmpty()) {
      // Diese Valenzvariante sah gar nicht alle Typen vor, die entfernt
      // werden sollten!
      // (Beispielsweise sieht die
      // Variante, die bei
      // "Mich dürstet." vorliegt, gar kein Subjekt vor.) Diese
      // Valenzvariante passt also nicht
      // dazu, dass angeblich diese Ergänzungstypen (z.B. ein Subjekt)
      // implizit gegeben sein sollen. ->
      return null;
    }

    return new Valenz(res.build().toArray(
        new AbstractErgaenzungsOderAngabenTyp[ergaenzungstypen.length
            - impliziteAngabenOderErgaenzungstypen.length]));
  }

  /**
   * Erzeugt {@link RoleFrameSlot}s, für die Ergänzungen und Angaben
   * entsprechend dieser Valenz
   *
   * @param personDesSubjekts
   *          Person des (ggf. impliziten) Subjekts - <code>null</code> erlaubt,
   *          wenn es kein Subjekt - auch kein implizites(!!) - gibt
   * @param genusDesSubjekts
   *          Genus des (ggf. impliziten) Subjekts - <code>null</code> erlaubt,
   *          wenn es kein Subjekt - auch kein implizites(!!) - gibt - oder wenn
   *          das Genus unklar ist (z.B. im Plural)
   * @param numerusDesSubjekts
   *          Numerus des (ggf. impliziten) Subjekts - <code>null</code>
   *          erlaubt, wenn es kein Subjekt - auch kein implizites(!!) - gibt
   * @param hoeflichkeitsformDesSubjekts
   *          Höflichkeitsform (sie vs. Sie) des (ggf. impliziten) Subjekts -
   *          <code>null</code> erlaubt, wenn es kein Subjekt - auch kein
   *          implizites(!!) - gibt
   */
  public Collection<RoleFrameSlot> buildErgaenzungenUndAngabenSlots(
      final @Nullable String personDesSubjekts,
      final @Nullable Genus genusDesSubjekts,
      final @Nullable Numerus numerusDesSubjekts,
      final @Nullable String hoeflichkeitsformDesSubjekts, final boolean fuerAdjektivischeForm) {
    final ImmutableList.Builder<RoleFrameSlot> res = ImmutableList.builder();

    for (final AbstractErgaenzungsOderAngabenTyp ergaenzungstyp : ergaenzungstypen) {
      res.add(ergaenzungstyp.buildSlot(personDesSubjekts, genusDesSubjekts,
          numerusDesSubjekts, hoeflichkeitsformDesSubjekts));
    }

    res.addAll(ErgaenzungsOderAngabeTypen.buildAngabenSlots(personDesSubjekts,
        genusDesSubjekts, numerusDesSubjekts, hoeflichkeitsformDesSubjekts, fuerAdjektivischeForm));

    return res.build();
  }

  /**
   * Erzeugt einen {@link RoleFrame} (mit freien Slots), der angibt, wie diese
   * Valenz generell (unabhängig von Person und Numerus des Subjekts) die
   * Verb-Ergänzungen und Angaben einschränkt.
   * <p>
   * Beispiel: Ein IM ENGEREN SINNE TRANSITIVES Verb steht nur mit einem Subjekt
   * und einem Akkusativobjekt, ein Dativobjekt wäre verboten.
   */
  public RoleFrame buildRestrictions() {
    return restrictions;
  }

  private RoleFrame calcRestrictions() {
    final ImmutableList.Builder<RoleFrameSlot> restrictionSlots = ImmutableList
        .builder();

    for (final AbstractErgaenzungsOderAngabenTyp ergaenzungstyp : ergaenzungstypen) {
      restrictionSlots.add(ergaenzungstyp.buildRestrictionSlot());
    }

    restrictionSlots.addAll(ErgaenzungsOderAngabeTypen
        .buildAngabenRestrictionSlots());

    return RoleFrame.of(restrictionSlots.build());
  }

  /**
   * Dies hier ist eher eine Heuristik, die im Zweifel eher "ja" sagt.
   */
  public boolean istTransitivUndBildetWerdenOderSeinPassiv() {
    if (!isTransitiv()) {
      return false;
    }

    // Ein werden-Passiv ist beim transitiven Verben letztlich wohl
    // immer möglich, vgl. Duden Bd.4 2006, 799, 800.
    // (Damit ist es unerheblich, ob ein seinPassiv möglich ist.)

    return true;
  }

  /**
   * Dies hier ist eher eine Heuristik, die im Zweifel eher "ja" sagt.
   *
   * @return ob es sich um eine reflexive Verbvariante handelt, zu der es eine
   *         (nicht konverse) Zustandskonstruktion mit <i>sein</i> gibt
   *         (<i>Zustandsreflexiv</i>). Siehe Duden 2006 831, 814, 678.
   *         Beispiele sind laut Duden: sich erkälten (ich bin erkältet), sich
   *         verliebt (ich bin verliebt), sich verfeinden (wir sind verfeindet),
   *         sich bewähren (die Methode ist bewährt).
   */
  public boolean bildetZustandsreflexiv() {
    // Der Duden spricht zwar nur allgemein von "reflexiven" Verben -
    // allerdings habe ich nur Zustandsreflexive von Verben mit
    // reflexivem AKKUSATIVOBJEKT gesehen (z.B. ich erkälte mich -> der
    // erkältete Mann), aber nicht von Verben mit reflexivem DATIVOBJEKT
    // (ich maße mir das Amt an -> *der angemaßte Mann)
    // (nur: das angemaßte Amt, aber das ist schon dadurch erklärt,
    // dass sich ETWAS anmaßen transitiv ist, dies ist keine
    // Zustandsreflexiv.)
    if (!fordertSubjekt()
        || !fordertErgaenzung(ErgaenzungsOderAngabeTypen.REFL_AKKUSATIVOBJEKT)) {
      return false;
    }

    // Jetzt wäre - nach dem Reflexivitätstest - zu prüfen:
    // Verbindet sich bei dieser Verbvariante das Partitizip II des Verbs
    // mit "sein"? (Siehe Duden 814.)
    // (Das heißt dann "Zustandsreflexiv", Duden 811.)

    if (fordertErgaenzung(ErgaenzungsOderAngabeTypen.GENITIVOBJEKT)) {
      // Duden 2006 811: "Von [...] Reflexivverben mit
      // Genitivobjekt wird [kein Zustandsreflexiv] gebildet."
      return false;
    }

    // Der Duden sagt außerdem:
    // "Von atelischen Reflexivverben wie sich schämen, sich beeilen [...]
    // wird [kein Zustandsreflexiv] gebildet." (Duden 2006
    // 814). Ob das Verb telisch ist oder nicht, ist mir hier allerdings
    // nicht bekannt.

    // "Nicht konvers" bedeutet, dass sich Aktiv und Passiv NICHT
    // "erschöpfend als
    // Konversen voneinander beschreiben lassen" (Duden 796), dass also
    // zwischen Aktiv und Passiv
    // ein größerer Unterschied besteht als in der "'Umschichtung' der
    // Zuordnung von semantischen Rollen
    // und Satzgliedfunktionen".
    // Wenn ich es richtig verstehe, gilt zumindest bei ECHT REFLEXIVEN
    // VERBEN
    // (also solchen, die GAR NICHT in einer z.B. transitiven Form
    // vorkommen), dass
    // JEDE Verbindung des Partizips II
    // mit dem Verb "sein" als
    // "nicht konvers" angesehen wird. Beispiel: Ich bin erkältet.
    // Wie das mit denjenigen Verben ist, die zwar eine reflexive Variante
    // habe, aber
    // auch (z.B.) transitiv vorkommen, weiß ich nicht.

    return true;
  }

  /**
   * @return ob diese Valenz eine echtes Subjekt erlaubt-und-fordert (nicht
   *         Pseudoaktant!)
   */
  public boolean fordertSubjekt() {
    return fordertErgaenzung(ErgaenzungsOderAngabeTypen.SUBJEKT);
  }

  public boolean isTransitiv() {
    // vgl. Duden Bd. 4 2006, 525
    return fordertSubjekt()
        && fordertErgaenzung(ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT);
    // Verben mit reflexivem Akkusativobjekt und Verben mit Pseudoaktant im
    // Akkusativ
    // gelten NICHT als transitiv
  }

  public boolean isIntransitiv() {
    // vgl. Duden Bd.4 2006, 540
    return fordertSubjekt()
        && !fordertErgaenzung(ErgaenzungsOderAngabeTypen.AKKUSATIVOBJEKT)
        &&
        // TODO - das folgende weiß ich nicht
        !fordertErgaenzung(ErgaenzungsOderAngabeTypen.OBLIG_PSEUDOAKTANT_AKK)
        && !fordertErgaenzung(ErgaenzungsOderAngabeTypen.REFL_AKKUSATIVOBJEKT);
  }

  /**
   * @return ob es sich um reflexives Verb (genauer: eine reflexive
   *         Verbvariante) handelt
   */
  public boolean isReflexiv() {
    return fordertSubjekt()
        && (fordertErgaenzung(ErgaenzungsOderAngabeTypen.REFL_DATIVOBJEKT) || fordertErgaenzung(ErgaenzungsOderAngabeTypen.REFL_AKKUSATIVOBJEKT));
  }

  private boolean fordertErgaenzung(
      final AbstractErgaenzungsOderAngabenTyp ergaenzungsOderAngabenTyp) {
    for (final AbstractErgaenzungsOderAngabenTyp element : ergaenzungstypen) {
      if (element.equals(ergaenzungsOderAngabenTyp)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (!this.getClass().equals(obj.getClass())) {
      return false;
    }

    final Valenz other = (Valenz) obj;

    return Arrays.equals(ergaenzungstypen, other.ergaenzungstypen);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(ergaenzungstypen);
  }

}
