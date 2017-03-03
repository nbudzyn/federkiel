package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

/**
 * Gibt f�r ein Lexeme an, ob es zaehlbar ist oder auch ein Plurale Tantum.
 *
 * @author nbudzyn 2011
 */
public enum NumerabilitaetsInfo {
  // TODO Wird zurzeit gar nicht verwendet. W�re aber vielleicht nicht schlecht f�r den
  // Einstieg in den Flektierer?

  // TODO k�nnte man auch als eine Art AdditionalNELexemeInfo aus dem NEGuesser �bergeben!

  /**
   * z.B. Hunde
   */
  ZAEHLBAR("zaehlbar"),

  /**
   * Woerter die definitiv nicht z�hlbar sind (vielleicht bei Eigennamen)
   */
  NICHT_ZAEHLBAR("nichtZaehlbar"),

  /**
   * z.B. Eltern
   */
  PLURALE_TANTUM("pluraleTantum");

  private String string;

  private NumerabilitaetsInfo(final String string) {
    this.string = string;
  }

  public String getString() {
    return string;
  }
}
