/*
 * Preposition
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Created on 21.03.2003
 *
 */
package de.nb.federkiel.grammatik.wortart.praeposition;

import de.nb.federkiel.grammatik.kategorie.Kasus;

/**
 * Eine Präposition.
 * 
 * @author nikolaj
 */
public enum Praeposition {
  // @formatter:off
  AUF("auf", Kasus.DATIV), 
  AUS("aus", Kasus.DATIV),
  MIT("mit", Kasus.DATIV),
  NACH("nach", Kasus.DATIV);
  // @formatter:on
	
	private final String name;
	private final Kasus kasus;

	private Praeposition(final String name, final Kasus kasus) {
		this.name = name;
		this.kasus = kasus;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString () {
		return getName () + " (+ " + getKasus() + ")";
	}
	
  public Kasus getKasus() {
		return this.kasus; 
	}
}
