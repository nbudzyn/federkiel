package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import java.util.Collection;

/**
 * Flexionsklassen für Substantive
 *
 * @author nbudzyn 2010
 */
public enum SubstantivFlexionsklasse {
	ENDUNGSLOS_I(false),
	STARK_II(false),
	STARK_EIGENNAME_III(false),
	SCHWACH_IV(false),
	/** Duden 337-339 */
	GEMISCHT_TYP_FRIEDE(true),
	/** Duden 340 */
	GEMISCHT_TYP_BUCHSTABE(true),
	/** Duden 339 */
	GEMISCHT_TYP_DRACHE(true),
	/** Duden 339 */
	GEMISCHT_TYP_FELS(true),
	/** Duden 340 */
	GEMISCHT_TYP_HERZ(true),
	PLURAL_V(false);

	private final boolean gemischt;

	private SubstantivFlexionsklasse(final boolean gemischt) {
		this.gemischt = gemischt;
	}

	public boolean isGemischt() {
		return gemischt;
	}

	public static boolean anyGemscht(
			final Collection<SubstantivFlexionsklasse> klassen) {
		return klassen.stream().anyMatch(klasse -> klasse.isGemischt());
//
//		for (final SubstantivFlexionsklasse klasse : klassen) {
//			if (klasse.isGemischt()) {
//				return true;
//			}
//		}
//
//		return false;
	}
}
