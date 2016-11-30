package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

/**
 * Gibt an, welche Verwendungen eine Substantiv-Wortform zulässt: Verlangt sie
 * ein Artikelwort, kann sie nur ohne Artikelwort stehen oder ist beides
 * möglich?
 *
 * @author nbudzyn 2010
 */
public enum Artikelwortbezug {
	/**
	 * z.B. "Annas" (Gen), vgl. "*der Annas Hund", aber "Annas Hund" Hierzu
	 * gehören vor allem einige Formen der NE.
	 */
	ZWINGEND_OHNE_ARTIKELWORT(true, true, false),
	/**
	 * z.B. "Anna" (Gen), vgl. "der Anna Hund", nicht aber "*Anna Hund" (nicht einmal im
	 * Telegrammstil).
	 * <p>
	 * Hierzu gehören vor allem einige Formen der NE.
	 */
	ZWINGEND_MIT_ARTIKELWORT_AUCH_IM_TELEGRAMMSTIL(false, false, true),
	/**
	 * z.B. "Schweiz" (Dativ), etwa bei "Leben in Schweiz" (Telegrammstil),
	 * bei heißen / taufen ("Das Land heißt Schweiz") und in der 
	 * Anrede ("Schweiz, was hast du mir angetan!").
	 * Vgl. Duden 400.
	 * <p>
	 * Hierzu gehören viele Formen derjenigen Eigennamen, die mit
	 * Artikel gebraucht werden (z.B. "die Schweiz", "die Tschechoslowakei",
	 * "die USA").
	 */
	ZWINGEND_MIT_ARTIKELWORT_AUSSER_IM_TELEGRAMMSTIL(false, true, true),
	/**
	 * z.B. "Komponist" (Nom), vgl. "der Komponist war [...]",
	 * "[...] war Komponist"
	 * <p>
	 * Hierzu gehören fast alle Formen der NN.
	 */
	IM_NORMALSTIL_MIT_ODER_OHNE_ARTIKELWORT(true, true, true);

	private final boolean imNormalstilOhneArtikelwortMoeglich;
	private final boolean imTelegrammstilOhneArtikelwortMoeglich;
	private final boolean mitArtikelwortMoeglich;
	
	private Artikelwortbezug(boolean imNormalstilOhneArtikelwortMoeglich,
			boolean imTelegrammstilOhneArtikelwortMoeglich,
			boolean mitArtikelwortMoeglich) {
		this.imNormalstilOhneArtikelwortMoeglich = imNormalstilOhneArtikelwortMoeglich;
		this.imTelegrammstilOhneArtikelwortMoeglich = imTelegrammstilOhneArtikelwortMoeglich;
		this.mitArtikelwortMoeglich = mitArtikelwortMoeglich;
	}

	public boolean isImNormalstilOhneArtikelwortMoeglich() {
		return imNormalstilOhneArtikelwortMoeglich;
	}

	public boolean isImTelegrammstilOhneArtikelwortMoeglich() {
		return imTelegrammstilOhneArtikelwortMoeglich;
	}

	public boolean isMitArtikelwortMoeglich() {
		return mitArtikelwortMoeglich;
	}
}
