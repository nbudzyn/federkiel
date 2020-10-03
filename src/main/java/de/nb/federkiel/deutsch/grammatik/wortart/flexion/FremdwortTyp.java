package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

public enum FremdwortTyp {
	KEIN_FREMDWORT(false),
	FREMDWORT_AM_ENDE_BETONT(true),
	FREMDWORT_NICHT_AM_ENDE_BETONT(true);

	private final boolean fremdwort;

	private FremdwortTyp(final boolean fremdwort) {
		this.fremdwort = fremdwort;
	}

	public boolean isFremdwort() {
		return this.fremdwort;
	}
}