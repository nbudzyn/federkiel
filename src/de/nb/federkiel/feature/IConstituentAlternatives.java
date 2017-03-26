package de.nb.federkiel.feature;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.interfaces.IConstituent;
import de.nb.federkiel.interfaces.IGuessedWordForm;
import de.nb.federkiel.interfaces.IWordFormVisitor;

/**
 * A collection containing several alternative constituents (realizations for a grammar symbol in a
 * rule) - what the grammar symbol could stand for - the realizations are word forms, or passive
 * parsing edges, or <code>NullGrammarSymbolRepresentation</code>s (for optional symbol references,
 * that are left out).
 * <p>
 * (By using these alternatives, we try to avoid an exponentially growing number of edges, when
 * several combinations are possible.)
 * <p>
 * All implementations must be IMMUTABLE
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public interface IConstituentAlternatives
extends Iterable<IConstituent>, Comparable<IConstituentAlternatives> {

	String getGrammarSymbol();

	@Override
	Iterator<IConstituent> iterator();

	long getNumParsesEffectively();

	String getSurface();

	int size();

	void visitWordForms(IWordFormVisitor visitor);

	public Set<IGuessedWordForm> getGuessedWordForms();

	// THIS would be a BAD idea - because, if AFTER THE EDGE has been processed,
	// producing some other edges, a NEW CHILD EDGE with DIFFERENT
	// FEATURES is inserted, the features will be MISSING IN THE edges
	// above!! So it is IMPOSSIBLE to NOT SEPARATE edges with different
	// features.
	/*
	 * Split the parse alternative collection into several collections, each of
	 * which is homogeneous.
	 *
	public abstract ImmutableCollection<IHomogeneousConstituentAlternatives> splitHomogeneously();
	 */
}