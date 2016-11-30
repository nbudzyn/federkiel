package de.nb.federkiel.deutsch.lexikon;

import de.nb.federkiel.interfaces.ILexemeType;

/**
 * German lexeme types: Noun, Adjective etc.
 *
 * @author nbudzyn 2011
 */
public enum GermanLexemeType implements ILexemeType {
	// flektierbar
	NORMALES_NOMEN("normales Nomen"),
	EIGENNAME("Eigenname"),
	ADJEKTIV("Adjektiv"),
	ARTIKEL("Artikel"),
	VOLLVERB("Vollverb"),
	MODALVERB("Modalverb"),
	HILFSVERB("Hilfsverb"),
	DEMONSTRATIVPRONOMEN("Demonstrativpronomen"),
	INDEFINITPRONOMEN("Indefinitpronomen"),
	PERSONALPRONOMEN("Personalpronomen"),
	REFLEXIVPRONOMEN("Reflexivepronomen"),
	ATTRIBUIERENDES_POSSESSIVPRONOMEN("Possesivpronomen, attribuierend"),
	SUBSTITUIERENDES_POSSESSIVPRONOMEN("Possesivpronomen, substituierend"),
	ATTRIBUIERENDES_RELATIVPRONOMEN("Relativpronomen, attribuierend"),
	SUBSTITUIERENDES_RELATIVPRONOMEN("Relativpronomen, substituierend"),
	ATTRIBUIERENDES_INTERROGATIVPRONOMEN("Interrogativpronomen, attribuierend"),
	SUBSTITUIERENDES_INTERROGATIVPRONOMEN("Interrogativpronomen, substituierend"),
	// nicht flektierbar
	ZU_VOR_INFINITIV("\"zu\" vor Infinitiv"),
	ECHTES_ADVERB("echtes Adverb"),
	PRONOMIALADVERB("Pronomialadverb"),
	POSTPOSITION("Postposition"),
	PRAEPOSITION_ZIRKUMPOSITION("Pr‰position/Zirkumposition"),
	PRAEPOSITION_MIT_INKORPORIERTEM_ARTIKEL("Pr‰position mit inkorporiertem Artikel"),
	INTERJEKTION("Interjektion"),
	VERGLEICHSPARTIKEL_NICHT_SATZEINLEITEND("Vergleichspartikel, nicht satzeinleitend"),
	NEBENORDNENDE_KONJUNKTION("Nebenordnende Konjunktion"),
	UNTERORDNENDE_KONJUNKTION_INFINITIV("Unterordnende Konjunktion mit Infinitiv"),
	UNTERORDNENDE_KONJUNKTION_NEBENSATZ("Unterordnende Konjunition mit Nebensatz"),
	ANTWORTPARTIKEL("Antwortpartikel"),
	PARTIKEL_BEI_ADJEKTIV_ODER_ADVERB("Partikel bei Adjektiv oder Adverb"),
	NEGATIONSPARTIKEL("Negationspartikel"),
	ADVERBIALES_INTERROGATIV_ODER_RELATIV_PRONOMEN("adverbiales Interrogativ- oder Relativpronomen"),
	KOMPOSITIONSERSTGLIED("Kompositionserstglied"),
	/**
	 * eins
	 */
	KARDINALZAHL("Kardinalzahl"),
	// Sonstiges
	FREMDSRACHLICHES_MATERIAL("Fremdsprachliches Material"),
	NICHTWORT_SONDERZEICHEN("Nichtwort / Sonderzeichen"),
	// Satzzeichen
	KOMMA("Komma"),
	SATZINTERNES_SATZZEICHEN_AUSSER_KOMMA("satzinternes Satzzeichen auﬂer Komma"),
	SATZBEENDENDES_SATZZEICHEN("satbeendendes Satzzeichen");

	/**
	 * User-friendly german description
	 */
	private String description;

	private GermanLexemeType(final String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getKey() {
		return name();
	}
}
