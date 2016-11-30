package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

/**
 * Gibt für ein Lexeme an, ob es zaehlbar ist oder auch ein Plurale Tantum.
 *
 * @author nbudzyn 2011
 */
public enum NumerabilitaetsInfo {
	/**
	 * z.B. Hunde
	 */
	ZAEHLBAR("zaehlbar"),

	/**
	 * Woerter die definitiv nicht zählbar sind (vielleicht bei Eigennamen)
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
		return this.string;
	}
}
