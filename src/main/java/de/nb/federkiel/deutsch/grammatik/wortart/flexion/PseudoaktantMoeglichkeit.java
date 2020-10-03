package de.nb.federkiel.deutsch.grammatik.wortart.flexion;

/**
 * Angabe, ob eine Personalpronomen-Form als Pseudoaktant geeignet ist (oder ob
 * kein solches Merkmal vorgesehen ist - bei Reflexivpronomen).
 *
 * @author nbudzyn 2012
 */
public enum PseudoaktantMoeglichkeit {
	MOEGLICH(true, true), NICHT_MOEGLICH(true, false), NICHT_VORGESEHEN(false, false);

	private boolean merkmalVorgesehen;

	/**
	 * <code>true</code> = Personalpronomen-Form ist als Pseudoaktant moeglich
	 */
	private boolean moeglich;

	private PseudoaktantMoeglichkeit(final boolean merkmalVorgesehen, final boolean moeglich) {
		this.merkmalVorgesehen = merkmalVorgesehen;
		this.moeglich = moeglich;
	}

	public boolean isMoeglich() {
		return this.moeglich;
	}

	public boolean isMerkmalVorgesehen() {
		return this.merkmalVorgesehen;
	}
}
