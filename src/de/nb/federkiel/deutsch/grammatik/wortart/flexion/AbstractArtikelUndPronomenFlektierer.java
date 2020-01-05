package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.STAERKE_STARK;

import java.util.Collection;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.deutsch.grammatik.valenz.Valenz;
import de.nb.federkiel.feature.EnumStringFeatureType;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.LexiconFeatureStructureUtil;
import de.nb.federkiel.feature.StringFeatureLogicUtil;
import de.nb.federkiel.interfaces.IFeatureType;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;
import de.nb.federkiel.lexikon.Wortform;
import de.nb.federkiel.semantik.NothingInParticularSemantics;

/**
 * Sammelt einige Gemeinsamkeiten von Artikel- und Pronomen- Flektierern
 *
 * @author nbudzyn 2009
 */
@ThreadSafe()
abstract class AbstractArtikelUndPronomenFlektierer extends AbstractArtikelPronomenAdjektivFlektierer {
	AbstractArtikelUndPronomenFlektierer() {
		super();
	}

	public Collection<IWordForm> einKeinUnser(final Lexeme lexeme, final String pos, final boolean auchPlural,
			final boolean generateFeatureWortartTraegtFlexionsendung, final boolean generateStaerke) {
		return einKeinUnser(lexeme, pos, lexeme.getCanonicalizedForm().substring(0, lexeme.getCanonicalizedForm().length()),
				auchPlural, // "ein"
				generateFeatureWortartTraegtFlexionsendung, generateStaerke);
	}

	public ImmutableList<IWordForm> einKeinUnser(final Lexeme lexeme, final String pos,
			final boolean generateFeatureWortartTraegtFlexionsendung, final boolean generateStaerke, final Numerus numerus,
			final Kasus kasus, final Genus genus) {
		return einKeinUnser(lexeme, pos, lexeme.getCanonicalizedForm().substring(0, lexeme.getCanonicalizedForm().length()),
				generateFeatureWortartTraegtFlexionsendung, generateStaerke, numerus, kasus, genus);
	}

	private Collection<IWordForm> einKeinUnser(final Lexeme lexeme, final String pos, final String stamm,
			final boolean auchPlural, final boolean generateFeatureWortartTraegtFlexionsendung,
			final boolean generateStaerke) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.addAll(einKeinUnserSg(lexeme, pos, stamm, generateFeatureWortartTraegtFlexionsendung, generateStaerke));

		if (auchPlural) {
			res.addAll(einKeinUnserPl(lexeme, pos, stamm, generateFeatureWortartTraegtFlexionsendung, generateStaerke));
		}

		return res.build();
	}

	private Collection<IWordForm> einKeinUnserSg(final Lexeme lexeme, final String pos, final String stamm,
			final boolean generateFeatureWortartTraegtFlexionsendung, final boolean generateStaerke) {
		// @formatter:off
    return Stream.of(Kasus.values())
        .map(
            kasus -> einKeinUnserSg(lexeme, pos, stamm, generateFeatureWortartTraegtFlexionsendung,
                generateStaerke, kasus))
        .flatMap(Collection::stream)
        .collect(toImmutableList());
    // @formatter:on
	}

	private Collection<IWordForm> einKeinUnserSg(final Lexeme lexeme, final String pos, final String stamm,
			final boolean generateFeatureWortartTraegtFlexionsendung, final boolean generateStaerke, final Kasus kasus) {
		return Stream.of(Genus.values()).map(genus -> einKeinUnserSg(lexeme, pos, stamm,
				generateFeatureWortartTraegtFlexionsendung, generateStaerke, kasus, genus)).flatMap(Collection::stream)
				.collect(toImmutableList());
	}

	private Collection<IWordForm> einKeinUnserPl(final Lexeme lexeme, final String pos, final String stamm,
			final boolean generateFeatureWortartTraegtFlexionsendung, final boolean generateStaerke) {
		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		Stream.of(Kasus.values()).map(
				kasus -> einKeinUnserPl(lexeme, pos, stamm, generateFeatureWortartTraegtFlexionsendung, generateStaerke, kasus))
				.forEach(e -> res.addAll(e));

		return res.build();
	}

	public ImmutableList<IWordForm> einKeinUnser(final Lexeme lexeme, final String pos, final String stamm,
			final boolean generateFeatureWortartTraegtFlexionsendung, final boolean generateStaerke, final Numerus numerus,
			final Kasus kasus, final Genus genus) {
		switch (numerus) {
		case SINGULAR:
			return einKeinUnserSg(lexeme, pos, stamm, generateFeatureWortartTraegtFlexionsendung, generateStaerke, kasus,
					genus);
		case PLURAL:
			return einKeinUnserPl(lexeme, pos, stamm, generateFeatureWortartTraegtFlexionsendung, generateStaerke, kasus);
		default:
			throw new IllegalStateException("Unerwarteter Numerus: " + numerus);
		}
	}

	public ImmutableList<IWordForm> einKeinUnserSg(final Lexeme lexeme, final String pos, final String stamm,
			final boolean generateFeatureWortartTraegtFlexionsendung, final boolean generateStaerke, final Kasus kasus,
			final Genus genus) {
		final String staerke = generateStaerke ? STAERKE_STARK : null;

		return adjStarkSg(lexeme, pos, stamm, // nur "eines (Autos)", nicht
				GenMaskNeutrSgModus.NUR_ES, // "ein (Auto)",
				// "*einen (Autos)"
				NomSgMaskUndNomAkkSgNeutrModus.ENDUNGSLOS, // nicht
				// "*einer (Auto)"
				generateFeatureWortartTraegtFlexionsendung ? VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH
						: VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN,
				Valenz.LEER, buildFeatureMap(staerke), buildFeatureTypeMap(staerke), kasus, genus);
	}

	public ImmutableList<IWordForm> einKeinUnserPl(final Lexeme lexeme, final String pos, final String stamm,
			final boolean generateFeatureWortartTraegtFlexionsendung, final boolean generateStaerke, final Kasus kasus) {
		final String staerke = generateStaerke ? STAERKE_STARK : null;

		final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv = generateFeatureWortartTraegtFlexionsendung
				? VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH
				: VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN;

		return adjStarkPl(lexeme, vorgabeFuerNachfolgendesAdjektiv, pos, stamm,
				buildFeatureMap(staerke, Valenz.LEER.buildErgaenzungenUndAngabenSlots("3", null,
						// (IHRER selbst gedenkende) Männer /
						// Frauen / Kinder,
						// -> alle Genera möglich!
						// (und außerdem macht es bei LEERER Valenz
						// ohnehin keinen Unterschied!)
						PLURAL, StringFeatureLogicUtil.FALSE, true)),
				buildFeatureTypeMap(staerke), kasus);
		// Die ihrer selbst gedenkenden Männer, aber nicht
		// *die Ihrer selbst gedenkenden Männer!
	}

	/**
	 * Erzeugt eine attribuierende Pronomen- oder Artikel-Wortform. Sie besitzt (da
	 * sie attribuierend ist) <i>keine</i> Person.
	 *
	 * @param numerus
	 *          for an unspecified value use
	 *          UnspecifiedFeatureValue.UNSPECIFIED_STRING!
	 */
	Wortform buildWortform(final Lexeme lexeme, final String pos, final KasusInfo kasusInfo, final String staerke,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final Numerus numerus,
			final @Nullable Genus genus, final String string) {
		final ImmutableMap<String, IFeatureValue> additionalFeatures = buildFeatureMap(staerke,
				Valenz.LEER.buildErgaenzungenUndAngabenSlots("3", genus, numerus, StringFeatureLogicUtil.FALSE, true));
		// Die ihrer selbst gedenkenden Männer, aber nicht
		// *die Ihrer selbst gedenkenden Männer!

		final ImmutableMap<String, IFeatureType> additionalFeatureTypes = buildFeatureTypeMap(staerke);

		return buildWortform(lexeme, pos, kasusInfo, vorgabeFuerNachfolgendesAdjektiv, numerus, genus, additionalFeatures,
				additionalFeatureTypes, string);
	}

	/**
	 * Erzeugt eine attribuierende Pronomen- oder Artikel-Wortform. Sie besitzt (da
	 * sie attribuierend ist) <i>keine</i> Person.
	 *
	 * @param numerus
	 *          for an unspecified value use
	 *          UnspecifiedFeatureValue.UNSPECIFIED_STRING!
	 */
	Wortform buildWortform(final Lexeme lexeme, final String pos, final KasusInfo kasusInfo,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv, final Numerus numerus,
			final @Nullable Genus genus, final String string) {
		final ImmutableMap<String, IFeatureValue> additionalFeatures = buildFeatureMap(
				Valenz.LEER.buildErgaenzungenUndAngabenSlots("3", genus, numerus, StringFeatureLogicUtil.FALSE, true));
		// Die ihrer selbst gedenkenden Männer, aber nicht
		// *die Ihrer selbst gedenkenden Männer!

		return buildWortform(lexeme, pos, kasusInfo, vorgabeFuerNachfolgendesAdjektiv, numerus, genus, additionalFeatures,
				ImmutableMap.of(), string);
	}

	/**
	 * Erzeugt eine Personalpronomen-Wortform für die erste Person.
	 */
	Wortform buildWortformPersPronP1(final Lexeme lexeme, final String pos, final Kasus kasus, final Numerus numerus,
			final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit, final String string) {
		return buildWortformPersPron(lexeme, pos, kasus, "1", numerus, false, // Keine Höflichkeitsform
				null, pseudoaktantMoeglichkeit, string);
	}

	/**
	 * Erzeugt eine Personalpronomen-Wortform für die zweite Person.
	 *
	 */
	Wortform buildWortformPersPronP2(final Lexeme lexeme, final String pos, final @Nullable Kasus kasus,
			final Numerus numerus, final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit, final String string) {
		return buildWortformPersPron(lexeme, pos, kasus, "2", numerus, false, // Keine Hoeflichkeitsform
				null, pseudoaktantMoeglichkeit, string);
	}

	/**
	 * Erzeugt eine <i>substituierende</i> Pronomen-Wortform. Sie besitzt (da sie
	 * substituierend ist) das Merkmal "Person", hier dritte person.
	 *
	 * @param kasus
	 *          for an unspecified value use
	 *          UnspecifiedFeatureValue.UNSPECIFIED_STRING!
	 * @param genus
	 *          for an unspecified value use
	 *          UnspecifiedFeatureValue.UNSPECIFIED_STRING!
	 * @param hoeflichkeitsform
	 *          ob es sich um die Höflichkeitsform ("Sie", "Ihrer", "Ihnen", "Sie")
	 *          handelt
	 */
	Wortform buildWortformPersPronP3(final Lexeme lexeme, final String pos, final @Nullable Kasus kasus,
			final @Nullable Numerus numerus, final boolean hoeflichkeitsform, final Genus genus,
			final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit, final String string) {
		return buildWortformPersPron(lexeme, pos, kasus, "3", numerus, hoeflichkeitsform, genus, pseudoaktantMoeglichkeit,
				string);
	}

	/**
	 * Builds a plural word form with a person feature. The genus will be
	 * unspecified.
	 *
	 * @param hoeflichkeitsform
	 *          ob es sich um die Höflichkeitsform ("Sie", "Ihrer", "Ihnen", "Sie")
	 *          handelt
	 */
	Wortform buildWortformPersPronPluralP3(final Lexeme lexeme, final String pos, final @Nullable Kasus kasus,
			final boolean hoeflichkeitsform, final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit, final String string) {
		return buildWortformPersPronP3(lexeme, pos, kasus, PLURAL, hoeflichkeitsform, null, pseudoaktantMoeglichkeit,
				string);
	}

	/**
	 * Builds a plural word form - without a person feature. The genus will be
	 * unspecified.
	 */
	Wortform buildWortformPlural(final Lexeme lexeme, final String pos, final KasusInfo kasusInfo, final String staerke,
			final String string, final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv) {
		return buildWortform(lexeme, pos, kasusInfo, staerke, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null, string);
	}

	/**
	 * Builds a plural word form - without a person feature. The genus will be
	 * unspecified.
	 */
	Wortform buildWortformPlural(final Lexeme lexeme, final String pos, final KasusInfo kasusInfo, final String string,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektiv) {
		return buildWortform(lexeme, pos, kasusInfo, vorgabeFuerNachfolgendesAdjektiv, PLURAL, null, string);
	}

	/**
	 * Erzeugt eine Personlapronomen-Wortform. Sie besitzt insbesondere das Merkmal
	 * "Person".
	 *
	 * @param hoeflichkeitsform
	 *          ob es sich um die Höflichkeitsform ("Sie", "Ihrer", "Ihnen", "Sie")
	 *          handelt
	 * @param pseudoaktantMoeglichkeit
	 *          ob dieses Personalpronomen-Form als Pseudoaktant geeignet ist. (Nur
	 *          "es" ist - im Nominativ und Akkusativ - als Pseudoaktant geeignet -
	 *          vgl. "Es regnet".)
	 */
	private static Wortform buildWortformPersPron(final Lexeme lexeme, final String pos, final @Nullable Kasus kasus,
			final String person, final @Nullable Numerus numerus, final boolean hoeflichkeitsform,
			final @Nullable Genus genus, final PseudoaktantMoeglichkeit pseudoaktantMoeglichkeit, final String string) {

		// @formatter:off
    final Builder<String, String> featureBuilder = ImmutableMap
        .<String, String> builder()
        .put("kasus", FeatureStringConverter.toFeatureString(kasus))
        .put("numerus", FeatureStringConverter.toFeatureString(numerus))
        .put("genus", FeatureStringConverter.toFeatureString(genus))
        .put("person", person)
        .put("hoeflichkeitsform",
            StringFeatureLogicUtil.booleanToString(hoeflichkeitsform));

    final Builder<String, IFeatureType> featureTypeBuilder = ImmutableMap
        .<String, IFeatureType> builder()
        .put("kasus",   FeatureStringConverter.KASUS_FEATURE_TYPE)
        .put("numerus", FeatureStringConverter.NUMERUS_FEATURE_TYPE)
        .put(GermanUtil.GENUS_KEY, GermanUtil.GENUS_FEATURE_TYPE)
        .put("person", GermanUtil.PERSON_FEATURE_TYPE)
        .put("hoeflichkeitsform", EnumStringFeatureType.BOOLEAN);

    if (pseudoaktantMoeglichkeit.isMerkmalVorgesehen()) {
      featureBuilder.put(GermanUtil.GEEIGNET_ALS_PSEUDOAKTANT_ES_KEY,
          StringFeatureLogicUtil.booleanToString(pseudoaktantMoeglichkeit
              .isMoeglich()));

      featureTypeBuilder.put(GermanUtil.GEEIGNET_ALS_PSEUDOAKTANT_ES_KEY, EnumStringFeatureType.BOOLEAN);
    }

    final ImmutableMap<String, String> featureMap = featureBuilder.build();

    final FeatureStructure features = LexiconFeatureStructureUtil.fromStringValues(featureMap);

    final Wortform res = new Wortform(lexeme, pos, string, features, NothingInParticularSemantics.INSTANCE);
    // @formatter:on

		return res;
	}

}
