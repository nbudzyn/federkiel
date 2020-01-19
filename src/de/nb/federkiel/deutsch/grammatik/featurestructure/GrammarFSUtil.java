package de.nb.federkiel.deutsch.grammatik.featurestructure;

import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.FeatureStructure;
import de.nb.federkiel.feature.SlotRequirements;
import de.nb.federkiel.feature.StringFeatureValue;
import de.nb.federkiel.feature.ThreeStateFeatureEqualityFormula;
import de.nb.federkiel.logic.AndFormula;
import de.nb.federkiel.logic.IFormula;

/**
 * Static Utility methods for feature structures inside a grammar.
 * 
 * @author nbudzyn 2020
 */
public class GrammarFSUtil {
	/**
	 * Used as a name (key) for the grammar symbol in {@link FeatureStructure}s.
	 */
	public static final String GRAMMAR_SYMBOL_NAME = "_SYMBOL";

	public static FeatureStructure addGrammarSymbol(FeatureStructure features, String grammarSymbol) {
		return features.disjunctUnionWithoutFreeFillings(FeatureStructure.fromValues(features.getSurfacePart(),
				GRAMMAR_SYMBOL_NAME, StringFeatureValue.of(grammarSymbol)), features.getSemantics());
	}

	public static SlotRequirements buildSlotRequirements(String expectedGrammarSymbol,
			IFormula<FeatureAssignment> featureCondition) {
		ThreeStateFeatureEqualityFormula grammarSymbolFormula = grammarSymbolFormula(expectedGrammarSymbol);
		final IFormula<FeatureAssignment> slotFormula = new AndFormula<FeatureAssignment>(
				grammarSymbolFormula, featureCondition);

		return SlotRequirements.of(expectedGrammarSymbol, slotFormula);
	}

	public static SlotRequirements buildSlotRequirements(String expectedGrammarSymbol) {
		final IFormula<FeatureAssignment> slotFormula = grammarSymbolFormula(expectedGrammarSymbol);

		return SlotRequirements.of(expectedGrammarSymbol, slotFormula);
	}
	
	private static ThreeStateFeatureEqualityFormula grammarSymbolFormula(String expectedGrammarSymbol) {
		return ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(GRAMMAR_SYMBOL_NAME, expectedGrammarSymbol);
	}
}
