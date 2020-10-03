package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.AKKUSATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.DATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.GENITIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.NOMINATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;

/**
 * Kann Personlapronomen flektieren.
 *
 * @author nbudzyn 2009
 */
@ThreadSafe
public class PersonalpronomenFlektierer extends AbstractPronomenFlektierer {
  public static final String TYP = "Personalpronomen";

  public PersonalpronomenFlektierer() {
    super();
  }

  public ImmutableCollection<IWordForm> irreflexiv(final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList
        .<IWordForm> builder();

    res.add(buildWortformPersPronP1(lexeme, pos, NOMINATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "ich"));
    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "meiner"));
    // "Sie gedenken mein."
    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "mein"));
    res.add(buildWortformPersPronP1(lexeme, pos, DATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "mir"));
    res.add(buildWortformPersPronP1(lexeme, pos, AKKUSATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "mich"));

    res.add(buildWortformPersPronP2(lexeme, pos, NOMINATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "du"));
    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "deiner"));
    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "dein"));
    res.add(buildWortformPersPronP2(lexeme, pos, DATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "dir"));
    res.add(buildWortformPersPronP2(lexeme, pos, AKKUSATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "dich"));

    res.add(buildWortformPersPronP3(lexeme, pos, NOMINATIV, SINGULAR, false,
        MASKULINUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "er"));
    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        MASKULINUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "seiner"));
    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        MASKULINUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "sein"));
    res.add(buildWortformPersPronP3(lexeme, pos, DATIV, SINGULAR, false,
        MASKULINUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "ihm"));
    res.add(buildWortformPersPronP3(lexeme, pos, AKKUSATIV, SINGULAR, false,
        MASKULINUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "ihn"));

    res.add(buildWortformPersPronP3(lexeme, pos, NOMINATIV, SINGULAR, false,
        FEMININUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "sie"));
    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        FEMININUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "ihrer"));
    res.add(buildWortformPersPronP3(lexeme, pos, DATIV, SINGULAR, false,
        FEMININUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "ihr"));
    res.add(buildWortformPersPronP3(lexeme, pos, AKKUSATIV, SINGULAR, false,
        FEMININUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "sie"));

    res.add(buildWortformPersPronP3(lexeme, pos, NOMINATIV, SINGULAR, false,
        NEUTRUM, PseudoaktantMoeglichkeit.MOEGLICH, "es"));
    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        NEUTRUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "seiner"));
    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        NEUTRUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "sein"));
    res.add(buildWortformPersPronP3(lexeme, pos, DATIV, SINGULAR, false,
        NEUTRUM, PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "ihm"));
    res.add(buildWortformPersPronP3(lexeme, pos, AKKUSATIV, SINGULAR, false,
        NEUTRUM, PseudoaktantMoeglichkeit.MOEGLICH, "es"));

    res.add(buildWortformPersPronP1(lexeme, pos, NOMINATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "wir"));
    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "unser"));
    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "unsrer")); // Duden
    // 363
    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "unserer"));
    res.add(buildWortformPersPronP1(lexeme, pos, DATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "uns"));
    res.add(buildWortformPersPronP1(lexeme, pos, AKKUSATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "uns"));

    res.add(buildWortformPersPronP2(lexeme, pos, NOMINATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "ihr"));
    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "euer"));
    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "eurer"));
    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "euerer"));
    res.add(buildWortformPersPronP2(lexeme, pos, DATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "euch"));
    res.add(buildWortformPersPronP2(lexeme, pos, AKKUSATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "euch"));

    res.add(buildWortformPersPronPluralP3(lexeme, pos, NOMINATIV, false,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "sie"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, GENITIV, false,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "ihrer"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, DATIV, false,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "ihnen"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, AKKUSATIV, false,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "sie"));

    // Höflichkeitsform
    res.add(buildWortformPersPronPluralP3(lexeme, pos, NOMINATIV, true,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "Sie"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, GENITIV, true,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "Ihrer"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, DATIV, true,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "Ihnen"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, AKKUSATIV, true,
        PseudoaktantMoeglichkeit.NICHT_MOEGLICH, "Sie"));

    return res.build();
  }

  public ImmutableCollection<IWordForm> reflexiv(final Lexeme lexeme,
      final String pos) {
    final ImmutableList.Builder<IWordForm> res = ImmutableList
        .<IWordForm> builder();

    // FIXME Ich bin mir nicht sicher, ob das so sinnvoll ist...
    // Duden 2006 nennt AUSSCHLIESSLICH "sich" Reflexivpronomen.
    // Die anderen Fällen heißen "reflexiv gebrauchte Personalpronomen",
    // das Wort "einander" heißt "Reziprokpronomen".

    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "meiner"));
    // "Sie gedenken mein."
    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "mein"));
    res.add(buildWortformPersPronP1(lexeme, pos, DATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "mir"));
    res.add(buildWortformPersPronP1(lexeme, pos, AKKUSATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "mich"));

    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "deiner"));
    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "dein"));
    res.add(buildWortformPersPronP2(lexeme, pos, DATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "dir"));
    res.add(buildWortformPersPronP2(lexeme, pos, AKKUSATIV, SINGULAR,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "dich"));

    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        MASKULINUM, PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "seiner"));
    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        MASKULINUM, PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "sein"));
    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        FEMININUM, PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "ihrer"));
    res.add(buildWortformPersPronP3(lexeme, pos, DATIV, SINGULAR, false,
        FEMININUM, PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "ihr"));
    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        NEUTRUM, PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "seiner"));
    res.add(buildWortformPersPronP3(lexeme, pos, GENITIV, SINGULAR, false,
        NEUTRUM, PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "sein"));

    res.add(buildWortformPersPronP3(lexeme, pos, DATIV, SINGULAR, false, null,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "sich"));
    res.add(buildWortformPersPronP3(lexeme, pos, AKKUSATIV, SINGULAR, false,
        null, PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "sich"));

    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "unser"));
    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "unsrer")); // Duden
    // 363
    res.add(buildWortformPersPronP1(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "unserer"));
    res.add(buildWortformPersPronP1(lexeme, pos, DATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "uns"));
    res.add(buildWortformPersPronP1(lexeme, pos, DATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "einander"));
    res.add(buildWortformPersPronP1(lexeme, pos, AKKUSATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "uns"));
    res.add(buildWortformPersPronP1(lexeme, pos, AKKUSATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "einander"));

    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "euer"));
    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "eurer"));
    res.add(buildWortformPersPronP2(lexeme, pos, GENITIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "euerer"));
    res.add(buildWortformPersPronP2(lexeme, pos, DATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "euch"));
    res.add(buildWortformPersPronP2(lexeme, pos, DATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "einander"));
    res.add(buildWortformPersPronP2(lexeme, pos, AKKUSATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "euch"));
    res.add(buildWortformPersPronP2(lexeme, pos, AKKUSATIV, PLURAL,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "einander"));

    res.add(buildWortformPersPronPluralP3(lexeme, pos, GENITIV, false,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "ihrer"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, DATIV, false,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "sich"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, DATIV, false,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "einander"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, AKKUSATIV, false,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "sich"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, AKKUSATIV, false,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "einander"));

    // Höflichkeitsform
    res.add(buildWortformPersPronPluralP3(lexeme, pos, GENITIV, true,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "Ihrer"));
    res.add(buildWortformPersPronPluralP3(lexeme, pos, DATIV, true,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "sich")); // auch
    // beim
    // Siezen
    // klein!
    res.add(buildWortformPersPronPluralP3(lexeme, pos, AKKUSATIV, true,
        PseudoaktantMoeglichkeit.NICHT_VORGESEHEN, "sich")); // auch
    // beim
    // Siezen
    // klein!

    return res.build();
  }
}
