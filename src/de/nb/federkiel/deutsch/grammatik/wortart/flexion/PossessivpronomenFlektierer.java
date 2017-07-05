package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

import static de.nb.federkiel.deutsch.grammatik.wortart.flexion.GermanUtil.KOMPARATION_POSITIV;

import java.util.Collection;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;

import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.deutsch.grammatik.valenz.Valenz;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.lexikon.Lexeme;

/**
 * Kann Possesivpronomen flektieren.
 *
 * @author nbudzyn 2010
 */
@ThreadSafe
public class PossessivpronomenFlektierer
		extends AbstractPronomenFlektierer {
	public static final String TYP = "Possessivpronomen";

	public PossessivpronomenFlektierer() {
		super();
	}

	public Collection<IWordForm> possessivAttributiv(final Lexeme lexeme, final String pos) {
		return possessiv(
				lexeme,
				NomSgMaskUndNomAkkSgNeutrModus.ENDUNGSLOS, pos,
				VorgabeFuerNachfolgendesAdjektiv.ERLAUBT_NUR_SCHWACH); // "sein (Auto)"
	}

	public Collection<IWordForm> possessivSubstituierend(final Lexeme lexeme,
			final String pos, final String stamm) {
		return possessiv(
				lexeme,
				NomSgMaskUndNomAkkSgNeutrModus.MIT_ENDUNG_UND_NOM_AKK_AUCH_NUR_MIT_S_STATT_ES
				// "(Es ist) seines.", "(Es ist) seins."
				, pos, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN);
	}

	/**
	 * @param nomSgMaskUndNomAkkSgNeutrModus
	 *            legt fest, welche Formen im Nominativ Singular Maskulinum
	 *            sowie im Nominativ und Akkusativ Singular Neutrum erzeugt
	 *            werden sollen (z.B. endungslos oder mit Endungen)
	 */
	private Collection<IWordForm> possessiv(
			final Lexeme lexeme,
			final NomSgMaskUndNomAkkSgNeutrModus nomSgMaskUndNomAkkSgNeutrModus,
			final String pos,
			final VorgabeFuerNachfolgendesAdjektiv vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung) {
		final ImmutableList.Builder<IWordForm> res =
				ImmutableList.builder();

		final String stamm = lexeme.getCanonicalizedForm();
		res.addAll(adjStark(
				lexeme,
				Valenz.LEER,
				pos,
				vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
				KOMPARATION_POSITIV, // "seines (Autos)", nicht *seins
				stamm,
				GenMaskNeutrSgModus.NUR_ES, // (Autos)
				nomSgMaskUndNomAkkSgNeutrModus));

		// e-Tilgung im Stamm (unser -> unsr)
		final String stammNachETilgung =
				GermanUtil.tilgeEAusStammWennMoeglich(stamm); // ggf. null

		if (stammNachETilgung != null) {
			res.addAll(adjStark(
					lexeme,
					Valenz.LEER,
					pos,
					vorgabeFuerNachfolgendesAdjektivBeiFormenMitEndung,
					KOMPARATION_POSITIV, // "unsres (Autos)", nicht
					stammNachETilgung,
					GenMaskNeutrSgModus.NUR_ES
					, // "*unsren (Autos)"
					NomSgMaskUndNomAkkSgNeutrModus.NICHT // !! "*unsr Auto" soll
															// nicht gebildet
															// werden
			));
		}
		return res.build();
	}

}
