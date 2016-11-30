package de.nb.federkiel.feature;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.nb.federkiel.interfaces.ISemantics;

/**
 * A collection containing several alternative parses
 * (realizations for a grammar symbol) - what the grammar symbol
 * (up to the dotPosition) could stand for -
 * the realizations are words or (saturated) ParsingEdges, or
 * <code>NullGrammarSymbolRepresentation</code>s
 * (for optional symbol references, that are left out). THEY ALL HAVE THE SAME
 * FEATURES AND (at least equivalent) SEMANTICS (so they are
 * <i>homogenous</i>).
 * <p>
 * (By using these collections, we try to avoid an exponentially
 * growing number of edges, when several combinations are possible.)
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public interface IHomogeneousConstituentAlternatives
 extends
		IConstituentAlternatives, IFeatureAndSemanticsCarrier {
	public int getFrom();

	public int getTo();

	@Override
	public FeatureStructure getFeatures();

	@Override
	public ISemantics getSemantics();

}