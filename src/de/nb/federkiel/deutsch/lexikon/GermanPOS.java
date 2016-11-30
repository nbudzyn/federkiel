package de.nb.federkiel.deutsch.lexikon;

import java.util.Locale;

/**
 * All German POS types
 *
 * @author nbudzyn 2011
 */
public enum GermanPOS {
	ADJA, ADJD, ADV, APPO, APPRART,
	APPR, APZR, ART, CARD,
	DOLLAR_KOMMA("$,"), DOLLAR_PUNKT("$."),
	DOLLAR_RUNDE_KLAMMER("$("),
	FM, ITJ, KOKOM, KON, KOUI, KOUS, NE,
	NN, PAV, PDAT, PDS, PIAT, PIDAT, PIS, PPER, PPOSAT,
	PPOSS, PRELAT, PRELS, PRF, PTKANT, PTKA,
	PTKNEG, PTKVZ, PTKZU, PWAT, PWAV, PWS,
	TRUNC, VAFIN, VAIMP, VAINF, VAPP, VMFIN,
	VMINF, VMPP, VVFIN, VVIMP, VVINF, VVIZU,
	VVPP, XY;

	private String string;

	private GermanPOS() {
		this.string = name().toLowerCase(Locale.GERMAN);
	}

	private GermanPOS(final String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return this.string;
	}
}
