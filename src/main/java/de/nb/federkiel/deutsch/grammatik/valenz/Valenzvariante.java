package de.nb.federkiel.deutsch.grammatik.valenz;

import javax.annotation.concurrent.Immutable;

/**
 * Eine Valenzvariante: Ein Verb mit Angaben zur Valenz - relavant als Output des
 * <code>VerbLister</code>s <i> Analog kann man auch bei Adjektiven von Valenzen sprechen.
 *
 * @author nbudzyn 2011
 */
@Immutable
public class Valenzvariante {
	private final String canonicalForm;

	private final Valenz valenz;

	public Valenzvariante(final String infinitiv, final Valenz valenz) {
		super();
		canonicalForm = infinitiv;
		this.valenz = valenz;
	}

	public final String getCanonicalForm() {
		return canonicalForm;
	}

	public final Valenz getValenz() {
		return valenz;
	}

	public boolean istTransitivUndBildetWerdenOderSeinPassiv() {
		return valenz.istTransitivUndBildetWerdenOderSeinPassiv();
	}

	public boolean bildetZustandsreflexiv() {
		return valenz.bildetZustandsreflexiv();
	}

	public boolean isTransitiv() {
		return valenz.isTransitiv();
	}

	public boolean isIntransitiv() {
		return valenz.isIntransitiv();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
				prime
						* result
						+ ((canonicalForm == null) ? 0 : canonicalForm
								.hashCode());
		result =
				prime * result
						+ ((valenz == null) ? 0 : valenz.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Valenzvariante other = (Valenzvariante) obj;
		if (canonicalForm == null) {
			if (other.canonicalForm != null) {
				return false;
			}
		} else if (!canonicalForm.equals(other.canonicalForm)) {
			return false;
		}
		if (valenz == null) {
			if (other.valenz != null) {
				return false;
			}
		} else if (!valenz.equals(other.valenz)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder res = new StringBuilder();
		res.append(canonicalForm);
		res.append(" (");
		res.append(valenz);
		res.append(")");

		return res.toString();
	}
}
