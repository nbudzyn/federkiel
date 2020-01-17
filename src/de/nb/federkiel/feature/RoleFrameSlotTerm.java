package de.nb.federkiel.feature;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.Variable;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * An term that builds up a slot for a role frame term, using a name, some slot
 * requirements and potentially a term for fillingTerms.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class RoleFrameSlotTerm implements IPlurivalTerm<RestrictedFSSet, FeatureAssignment> {
	/**
	 * The name of the slot. (Shall be unique within the role frame.)
	 */
	private final String name;

	/**
	 * Requirements to an element that could fill this slot. These are alternatives:
	 * The element only needs to fulfill <i>one</i> of these.
	 * <p>
	 * This collection has to be immutable!
	 */
	private final ImmutableCollection<SlotRequirements> alternativeRequirements;

	/**
	 * The minimal number of fillingTerms, that this slot needs. Would be 0 for an
	 * optional slot, 1 for a mandatory slot.
	 */
	private final int minFillings;

	/**
	 * The maximal number of fillingTerms, that this slot accepts, or -1, if there
	 * is no maximum.
	 */
	private final int maxFillings;

	/**
	 * The (only) filling TERM of the slot. Can be null (<i>empty slot term</i>).
	 * <p>
	 * Fillings are TERMS, because filling can already take place at grammar level:
	 * <code>Verb : vafin</code>, so the filling will be a
	 * <code>SymbolReferenceVariable</code>.
	 */
	@Nullable
	private final IPlurivalTerm<FeatureStructure, FeatureAssignment> fillingTerm;

	/**
	 * Creates a frame slot with one filling, that only accepts one filling.
	 */
	public RoleFrameSlotTerm(final String name,
			@Nullable final IPlurivalTerm<FeatureStructure, FeatureAssignment> fillingTerm,
			final SlotRequirements... requirementAlternatives) {
		this.name = name;
		alternativeRequirements = ImmutableList.copyOf(requirementAlternatives);
		this.fillingTerm = fillingTerm;
		minFillings = 1;
		maxFillings = 1;
	}

	/**
	 * Creates a mandatory frame slot, that only accepts one filling.
	 *
	 * @param name
	 * @param requirementAlternatives
	 */
	public RoleFrameSlotTerm(final String name, final SlotRequirements... requirementAlternatives) {
		this.name = name;
		alternativeRequirements = ImmutableList.copyOf(requirementAlternatives);
		fillingTerm = null;
		minFillings = 1;
		maxFillings = 1;
	}

	/**
	 * Creates a role frame slot.
	 *
	 * @param minFillings
	 *          the minimal number of fillings that this slot needs. Would be 0 for
	 *          an optional slot, 1 for a mandatory slot.
	 * @param maxFillings
	 *          the maximal number of fillingTerms, that this slot accepts, or -1,
	 *          if there is no maximum.
	 */
	public RoleFrameSlotTerm(final String name, final int minFillings, final int maxFillings,
			final SlotRequirements... requirementAlternatives) {
		this.name = name;
		alternativeRequirements = ImmutableList.copyOf(requirementAlternatives);
		fillingTerm = null;
		this.minFillings = minFillings;
		this.maxFillings = maxFillings;
	}

	/*
	 * private RoleFrameSlotTerm(final String name, final
	 * ImmutableCollection<SlotRequirements> alternativeRequirements, final
	 * ImmutableCollection<ITerm<ParseAlternatives, FeatureAssignment>> fillings,
	 * final int minFillings, final int maxFillings) { this.name = name;
	 * this.alternativeRequirements = alternativeRequirements; this.fillingTerms =
	 * fillings; this.minFillings = minFillings; this.maxFillings = maxFillings; }
	 */

	/**
	 * @return <code>true</code>, if the realization matches the requirements, that
	 *         is, it matches any of the requirements alternatives
	 *
	 *         public boolean matchesRequirements( final String
	 *         actualGrammarSymbolName, final List<ParseAlternatives>
	 *         actualSymbolRealizations, final IFeatureProvider actualFeatures) {
	 *         for (final SlotRequirements slotRequirementsAlternative :
	 *         this.alternativeRequirements) { if
	 *         (slotRequirementsAlternative.match( actualGrammarSymbolName,
	 *         actualSymbolRealizations, actualFeatures)) { return true; } }
	 *
	 *         return false; }
	 */

	/**
	 * Evaluates the filling terms - the result only contains Constants in the
	 * fillingTerms.
	 */
	@Override
	public Plurival<RestrictedFSSet> evaluate(final FeatureAssignment variableAssignment)
			throws UnassignedVariableException {
		// apply the evaluate() function to the fillingTerm
		if (fillingTerm == null) {
			return Plurival.of(RestrictedFSSet.of(alternativeRequirements, ImmutableSet.of(), minFillings, maxFillings));
		}

		final Plurival<FeatureStructure> fillingPlurival = fillingTerm.evaluate(variableAssignment);
		// UnassignedVariableException

		final ImmutableSet.Builder<RestrictedFSSet> roleFrameSlotAlternatives = ImmutableSet.builder();

		for (final FeatureStructure filling : fillingPlurival) {
			roleFrameSlotAlternatives
					.add(RestrictedFSSet.of(alternativeRequirements, ImmutableSet.of(filling), minFillings, maxFillings));
		}

		return Plurival.of(roleFrameSlotAlternatives.build());
	}

	@Override
	public ImmutableSet<Variable<?, FeatureAssignment>> getAllVariables() {
		if (fillingTerm == null) {
			return ImmutableSet.of();
		}

		return fillingTerm.getAllVariables();
	}

	/**
	 * Checks whether this (additional) filling term would be acceptable for this
	 * slot. If the filling would be acceptable, the methode returns a copy of this
	 * slot with this filling added. Otherwise, the method returns
	 * <code>null</code>.
	 *
	 * not needed? public RoleFrameSlotTerm addFillingTermIfAccepted(final
	 * ParseAlternatives freeFilling) { if (this.maxFillings != -1 &&
	 * this.fillingTerms.size() + 1 > this.maxFillings) { // no more fillingTerms
	 * allowed return null; }
	 *
	 * boolean fillingMatchesRequirements = false; for (final SlotRequirements
	 * slotRequirementsAlternative : this.alternativeRequirements) { if
	 * (slotRequirementsAlternative.match( actualGrammarSymbolName,
	 * actualSymbolRealizations, actualFeatures)) { fillingMatchesRequirements =
	 * true; } }
	 *
	 * if (! fillingMatchesRequirements) { return null; }
	 *
	 * final Collection<ParseAlternatives> resFillings = new
	 * LinkedList<ParseAlternatives>(this.fillingTerms);
	 * resFillings.add(freeFilling);
	 *
	 * // accept filling and return new Role Frame Slot return new
	 * RoleFrameSlotTerm(this.name, this.alternativeRequirements, resFillings,
	 * this.minFillings, this.maxFillings); }
	 */

	public String getName() {
		return name;
	}

	public int getMinFillings() {
		return minFillings;
	}

	public int getMaxFillings() {
		return maxFillings;
	}

	public ImmutableCollection<SlotRequirements> getAlternativeRequirements() {
		return alternativeRequirements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + alternativeRequirements.hashCode();
		result = prime * result + java.util.Objects.hash(fillingTerm);
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
		final RoleFrameSlotTerm other = (RoleFrameSlotTerm) obj;
		if (!name.equals(other.name)) {
			return false;
		}
		if (!Objects.equal(fillingTerm, other.fillingTerm)) {
			return false;
		}
		if (!alternativeRequirements.equals(other.alternativeRequirements)) {
			return false;
		}
		if (minFillings != other.minFillings) {
			return false;
		}
		if (maxFillings != other.maxFillings) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final IPlurivalTerm<? extends Object, ? extends IAssignment> o) {
		final int classNameCompared = this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
		if (classNameCompared != 0) {
			return classNameCompared;
		}

		final RoleFrameSlotTerm other = (RoleFrameSlotTerm) o;

		final int namesCompared = name.compareTo(other.name);
		if (namesCompared != 0) {
			return namesCompared;
		}

		final int reqsCompared = CollectionUtil.compareCollections(alternativeRequirements, other.alternativeRequirements);
		if (reqsCompared != 0) {
			return reqsCompared;
		}

		if (fillingTerm == null) {
			if (other.fillingTerm != null) {
				return -1;
			}
		} else {
			if (other.fillingTerm == null) {
				return 1;
			}

			final int fillingsCompared = fillingTerm.compareTo(other.fillingTerm);
			if (fillingsCompared != 0) {
				return fillingsCompared;
			}
		}

		if (minFillings < other.minFillings) {
			return -1;
		}

		if (minFillings > other.minFillings) {
			return 1;
		}

		if (maxFillings < other.maxFillings) {
			return -1;
		}

		if (maxFillings > other.maxFillings) {
			return 1;
		}

		return 0;
	}

	@Override
	public String toString() {
		return toString(false);
	}

	/**
	 * @param forceShowRequirements
	 *          if <code>true</code>, slot requirements are shown, even if the slot
	 *          is filled.
	 */
	@Override
	public String toString(final boolean forceShowRequirements) {
		final StringBuilder res = new StringBuilder();

		res.append(name);

		res.append(" : ");

		if (fillingTerm != null) {
			res.append(" [");
			res.append(fillingTerm.toString(true));
			res.append("] ");
		} else {
			res.append(" ?");
		}

		if (fillingTerm == null || forceShowRequirements) {
			boolean first = true;
			for (final SlotRequirements requirements : alternativeRequirements) {
				if (first == true) {
					res.append(" (");
					first = false;
				} else {
					res.append(" | ");
				}
				res.append(requirements.toString());
			}

			if (first != true) {
				res.append(")");
			}
		}
		return res.toString();
	}

}
