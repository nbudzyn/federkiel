package de.nb.federkiel.grammatik.kategorie;

/**
 * Etwas, das als "Determinativ" (zu Beginn einer Nominalphrase) dienen kann:
 * <ul>
 * <li>ein Artikel: der (Hund)
 * <ul>
 *
 * @author Nikolaj Budzyn
 */
public interface IDeterminativ {
  String getFlektiertAlsDeterminativFuer(Kasus kasusBezugsphrase,
      Numerus numerusBezugsphrase, Genus genusBezugsphrase, boolean negiert);

  /**
   * Gibt zur�ck, ob das Wort eine Flexionsendung tr�gt (Duden 488) oder nicht.
   */
  boolean hatFlexionsendung(Kasus kasusBezugsphrase, Numerus numerusBezugsphrase,
      Genus genusBezugsphrase);
}
