package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.FEMININUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.MASKULINUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Genus.NEUTRUM;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.AKKUSATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.DATIV;
import static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;
import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.REG_KASUS_KEY;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Kasus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Numerus;
import de.nb.federkiel.deutsch.lexikon.GermanLexemeType;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.LexiconFeatureStructureUtil;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;
import de.nb.federkiel.lexikon.Wortform;

public class PraepositionsartikelverschmelzungFlektierer implements IFlektierer {
	private static final String PRAEPOSITION_KEY = "praeposition";

	public static final String TYP = "mitArtikelVerschmolzenePraeposition";

	public PraepositionsartikelverschmelzungFlektierer() {
		super();
	}

	public ImmutableCollection<IWordForm> anDatVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("an", DATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "am"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "am"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> anAkkVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("an", AKKUSATIV);

		return ImmutableList.of(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "ans"));
	}

	public ImmutableCollection<IWordForm> aufAkkVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("auf", AKKUSATIV);

		return ImmutableList.of(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "aufs"));
	}

	public ImmutableCollection<IWordForm> beiDatVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("bei", DATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "beim"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "beim"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> durchVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("durch", AKKUSATIV);

		return ImmutableList.of(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "durchs"));
	}

	public ImmutableCollection<IWordForm> fuerVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("für", AKKUSATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "fürn"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "fürs"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> hinterDatVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("hinter", DATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "hinterm"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "hinterm"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> hinterAkkVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("hinter", AKKUSATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "hintern"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "hinters"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> inDatVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("in", DATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "im"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "im"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> inAkkVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("in", AKKUSATIV);

		return ImmutableList.of(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "ins"));
	}

	public ImmutableCollection<IWordForm> umAkkVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("um", DATIV);

		return ImmutableList.of(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "ums"));
	}

	public ImmutableCollection<IWordForm> unterDatVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("unter", DATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "unterm"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "unterm"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> unterAkkVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("unter", AKKUSATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "untern"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "unters"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> vonDatVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("von", DATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "vom"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "vom"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> vorDatVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("vor", DATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "vorm"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "vorm"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> vorAkkVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("vor", AKKUSATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "vorn"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "vors"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> zuVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("zu", DATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "zum"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, FEMININUM, "zur"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "zum"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> ueberDatVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("ueber", DATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "ueberm"));
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "ueberm"));

		return res.build();
	}

	public ImmutableCollection<IWordForm> ueberAkkVerschmelzung(final String pos) {
		final Lexeme lexeme = buildAPPARTLexem("ueber", AKKUSATIV);

		final ImmutableList.Builder<IWordForm> res = ImmutableList.builder();

		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, MASKULINUM, "uebern")); // (?)
		res.add(buildAPPRARTWortform(lexeme, pos, SINGULAR, NEUTRUM, "uebers"));

		return res.build();
	}

	// ----------------
	// FOR PRIVATE USE
	// ----------------
	private static Lexeme buildAPPARTLexem(final String praeposition, final Kasus regKasus) {
		// @formatter:off
    final FeatureStructure features = LexiconFeatureStructureUtil
        .fromStringValues(ImmutableMap.<String, String> builder()
              .put(REG_KASUS_KEY, FeatureStringConverter.toFeatureString(regKasus))
              .put(PRAEPOSITION_KEY, praeposition)
              .build());
    // @formatter:on

		return new Lexeme(GermanLexemeType.PRAEPOSITION_MIT_INKORPORIERTEM_ARTIKEL, praeposition + "-", features);
	}

	private static Wortform buildAPPRARTWortform(final Lexeme lexeme, final String pos, final Numerus numerus,
			final Genus genus, final String string) {

		final FeatureStructure features = LexiconFeatureStructureUtil
		// @formatter:off
        .fromStringValues(ImmutableMap.<String, String> builder()
            .put("numerus", FeatureStringConverter.toFeatureString(numerus))
            .put("genus", FeatureStringConverter.toFeatureString(genus))
            .build());
    // @formatter:on

		return new Wortform(lexeme, pos, string, features);
	}

}
