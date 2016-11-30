package de.nb.federkiel.deutsch.grammatik.kategorie;

/**
 * Die Vorgabe durch einen Artikel oder ein Pronomen, ob das nachfolgende
 * Adjektiv
 * <ul>
 * <li>schwach sein muss (<i>der (groﬂe Mann)</i>)
 * <li>stark sein muss (<i>ein (groﬂer Mann)</i>)
 * <li>schwach oder stark sein darf (<i>beide (groﬂe M‰nner)</i>, <i>beide
 * (groﬂen M‰nner)</i>)
 * <li>oder ob keine entsprechenden Merkmale erzeugt werden sollen
 * </ul>
 * <p>
 * Grunds‰tzlich h‰ngt dies hier davon ab, ob das Artikelwort eine
 * Flexionsendung tr‰gt - allerdings gibt es (siehe Duden 1526) diverse
 * Sonderf‰lle, in denen beides erlaubt ist.
 *
 * @author nbudzyn 2011
 */
public enum VorgabeFuerNachfolgendesAdjektiv {
	ERLAUBT_NUR_SCHWACH(true, true, false), ERLAUBT_NUR_STARK(true, false, true),
	ERLAUBT_STARK_UND_SCHWACH(true, true, true),
	NICHT_ERZEUGEN(false, false, false);

	private VorgabeFuerNachfolgendesAdjektiv(final boolean erzeugen, final boolean erlaubtSchwach,
			final boolean erlaubtStark) {
		this.erzeugen = erzeugen;
		this.erlaubtSchwach = erlaubtSchwach;
		this.erlaubtStark = erlaubtStark;
	}

	private final boolean erzeugen;

	/**
	 * Nicht definiert, falls erzeugen == false;
	 */
	private final boolean erlaubtSchwach;

	/**
	 * Nicht definiert, falls erzeugen == false;
	 */
	private final boolean erlaubtStark;

	public boolean isErzeugen() {
		return this.erzeugen;
	}

	public boolean isErlaubtSchwach() {
		return this.erlaubtSchwach;
	}

	public boolean isErlaubtStark() {
		return this.erlaubtStark;
	}
}
