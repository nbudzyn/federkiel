package de.nb.federkiel.feature;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;

import de.nb.federkiel.collection.CollectionUtil;
import de.nb.federkiel.interfaces.IFeatureValue;
import de.nb.federkiel.logic.IAssignment;
import de.nb.federkiel.logic.ITerm;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.Variable;
import de.nb.federkiel.logic.YieldsNoResultException;
import de.nb.federkiel.plurivallogic.IPlurivalTerm;
import de.nb.federkiel.plurivallogic.Plurival;

/**
 * A TERM that builds up a role frame, based on some slot TERMS and some free
 * filling TERMS.
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
public class RoleFrameTerm implements IPlurivalTerm<FeatureStructure, FeatureAssignment> {
	final private static boolean CHECK_ASSERTIONS = false;

	private final ImmutableMap<String, RoleFrameSlotTerm> slotTerms;

	/**
	 * There can only be free fillings, if there are NO slotTerms at all!
	 */
	private final ImmutableCollection<ITerm<IHomogeneousConstituentAlternatives, FeatureAssignment>> freeFillingTerms;

	public RoleFrameTerm(final RoleFrameSlotTerm... slotTerms) {
		super();

		this.slotTerms = Maps.<String, RoleFrameSlotTerm>uniqueIndex(Arrays.asList(slotTerms),
				(Function<RoleFrameSlotTerm, String>) slotTerm -> slotTerm.getName());

		freeFillingTerms = ImmutableList.of();

		if (CHECK_ASSERTIONS) {
			check(this.slotTerms, freeFillingTerms);
		}
	}

	public RoleFrameTerm(final ImmutableMap<String, RoleFrameSlotTerm> slotTerms,
			final ImmutableCollection<ITerm<IHomogeneousConstituentAlternatives, FeatureAssignment>> freeFillingTerms) {
		if (CHECK_ASSERTIONS) {
			check(slotTerms, freeFillingTerms);
		}

		this.slotTerms = slotTerms;
		this.freeFillingTerms = freeFillingTerms;
	}

	/**
	 * Evaluates this role frame term - the result will be a feature structure.
	 */
	@Override
	public Plurival<FeatureStructure> evaluate(final FeatureAssignment variableAssignment)
			throws UnassignedVariableException {
		final ImmutableSet<IHomogeneousConstituentAlternatives> freeFillings;
		SurfacePart surfacePart = null;

		try {
			final Builder<IHomogeneousConstituentAlternatives> freeFillingsBuilder = ImmutableSet
					.<IHomogeneousConstituentAlternatives>builder();

			for (final ITerm<IHomogeneousConstituentAlternatives, FeatureAssignment> freeFillingTerm : freeFillingTerms) {
				IHomogeneousConstituentAlternatives freeFilling = freeFillingTerm.evaluate(variableAssignment);
				freeFillingsBuilder.add(freeFilling);
				// UnassignedVariableException, YieldsNoResultException
				surfacePart = SurfacePart.join(surfacePart, freeFilling.getSurfacePart());
			}

			freeFillings = freeFillingsBuilder.build();
		} catch (final YieldsNoResultException e) {
			return Plurival.empty();
		}

		ImmutableSet<ImmutableMap<String, IFeatureValue>> slotMapEntryAlternatives = ImmutableSet
				.of(ImmutableMap.<String, IFeatureValue>of());

		for (final Map.Entry<String, RoleFrameSlotTerm> slotMapEntry : slotTerms.entrySet()) {
			final String key = slotMapEntry.getKey();
			final Plurival<RoleFrameSlot> roleFrameSlotAlternatives = slotMapEntry.getValue().evaluate(variableAssignment);
			// UnassignedVariableException

			final ImmutableSet.Builder<ImmutableMap<String, IFeatureValue>> newMapEntryAlternatives = ImmutableSet.builder();
			for (final Map<String, IFeatureValue> oldMap : slotMapEntryAlternatives) {
				final ImmutableMap.Builder<String, IFeatureValue> newMap = ImmutableMap.builder();
				newMap.putAll(oldMap);
				for (final RoleFrameSlot roleFrameSlot : roleFrameSlotAlternatives) {
					newMap.put(key, roleFrameSlot);
				}

				newMapEntryAlternatives.add(newMap.build());
			}

			slotMapEntryAlternatives = newMapEntryAlternatives.build();
		}

		final ImmutableSet.Builder<FeatureStructure> roleFrameAlternatives = ImmutableSet.builder();
		for (final ImmutableMap<String, IFeatureValue> slotMapEntry : slotMapEntryAlternatives) {
			roleFrameAlternatives.add(FeatureStructure.fromValuesAndFreeFillings(
					SurfacePart.join(surfacePart, joinSurfaceParts(slotMapEntry.values())), slotMapEntry,
					freeFillings));
		}

		return Plurival.of(roleFrameAlternatives.build());
	}

	public static SurfacePart joinSurfaceParts(Iterable<IFeatureValue> features) {
		SurfacePart res = null;

		for (IFeatureValue feature : features) {
			res = SurfacePart.join(res, feature.getSurfacePart());
		}

		return res;
	}

	@Override
	public ImmutableSet<Variable<?, FeatureAssignment>> getAllVariables() {
		// @formatter:off
    final ImmutableSet.Builder<Variable<?, FeatureAssignment>> res = ImmutableSet.builder();

    freeFillingTerms.stream()
      .flatMap(t -> t.getAllVariables().stream())
      .forEach(res::add);

    slotTerms.values().stream()
      .flatMap(t -> t.getAllVariables().stream())
      .forEach(res::add);

    return res.build();
    // @formatter:on
	}

	/*
	 * As bounds for the term, this returns <ul> <li>a role frame containing all
	 * slots (they might be filled or not), or <li>all free fillings, or
	 * <li><code>null</code> </ul>
	 *
	 * @Override public BoundsForTermAndBoundsForVariable
	 * retrieveImplicitBoundsForTermAndBoundsForVariable( final Variable<RoleFrame,
	 * FeatureAssignment> variable, final FeatureAssignment assignment) { if
	 * (!this.slotTerms.isEmpty()) { // there are slot terms, so there are no free
	 * filling terms final ImmutableMap.Builder<String, RoleFrameSlot> slotBounds =
	 * ImmutableMap .<String, RoleFrameSlot> builder();
	 *
	 * for (final Map.Entry<String, RoleFrameSlotTerm> slotMapEntry : this.slotTerms
	 * .entrySet()) { RoleFrameSlot slotValue; try { // try to evaluate the slot
	 * filling term slotValue = slotMapEntry.getValue().evaluate(assignment); }
	 * catch (final UnassignedVariableException e) { // impossible -> build an empty
	 * slot slotValue = new RoleFrameSlot( slotMapEntry.getKey(),
	 * slotMapEntry.getValue().getAlternativeRequirements(),
	 * ImmutableSet.<IHomogeneousConstituentAlternatives>of(),
	 * slotMapEntry.getValue().getMinFillings(),
	 * slotMapEntry.getValue().getMaxFillings()); };
	 *
	 * slotBounds.put(slotMapEntry.getKey(), slotValue); } return new
	 * BoundsForTermAndBoundsForVariable(new RoleFrame( slotBounds.build()), null);
	 * } // no slot terms -> what about the free fillings?
	 *
	 * final Builder<IHomogeneousConstituentAlternatives> freeFillingsBounds =
	 * ImmutableSet .<IHomogeneousConstituentAlternatives> builder();
	 *
	 * try { for (final ITerm<IHomogeneousConstituentAlternatives,
	 * FeatureAssignment> freeFillingTerm : this.freeFillingTerms) {
	 * freeFillingsBounds.add(freeFillingTerm.evaluate(assignment)); //
	 * UnassignedVariableException }
	 *
	 * return new BoundsForTermAndBoundsForVariable(new RoleFrame(
	 * freeFillingsBounds.build()), null); } catch (final
	 * UnassignedVariableException e) { return
	 * BoundsForTermAndBoundsForVariable.NONE; } }
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + slotTerms.hashCode();
		result = prime * result + freeFillingTerms.hashCode();
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
		final RoleFrameTerm other = (RoleFrameTerm) obj;
		if (!slotTerms.equals(other.slotTerms)) {
			return false;
		}
		if (!freeFillingTerms.equals(other.freeFillingTerms)) {
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

		final RoleFrameTerm other = (RoleFrameTerm) o;

		final int slotTermsCompared = CollectionUtil.compareMaps(slotTerms, other.slotTerms);
		if (slotTermsCompared != 0) {
			return slotTermsCompared;
		}

		return CollectionUtil.compareCollections(freeFillingTerms, other.freeFillingTerms);

	}

	@Override
	public String toString() {
		return this.toString(false);
	}

	@Override
	public String toString(final boolean surroundWithBracketsIfApplicable) {
		// brackets are not applicable
		final StringBuilder res = new StringBuilder();

		res.append("{");

		boolean first = true;

		for (final RoleFrameSlotTerm emptySlot : slotTerms.values()) {
			if (first) {
				first = false;
			} else {
				res.append(", ");
			}

			res.append(emptySlot.toString());
		}

		for (final ITerm<? extends IConstituentAlternatives, FeatureAssignment> freeFilling : freeFillingTerms) {
			if (first) {
				first = false;
			} else {
				res.append(", ");
			}

			res.append("? : ");
			res.append(freeFilling.toString(false));
		}

		res.append("}");
		return res.toString();
	}

	/**
	 * Checks wether these values would make up a consistent role frame.
	 */
	private static void check(final Map<String, RoleFrameSlotTerm> slots,
			final Collection<ITerm<IHomogeneousConstituentAlternatives, FeatureAssignment>> freeFillings)
			throws IllegalArgumentException {
		// There can only be free fillings, if there are NO slotTerms AT ALL!
		if (!freeFillings.isEmpty()) {
			if (!slots.isEmpty()) {
				throw new IllegalArgumentException("There can only be free fillings, if "
						+ "there are NO empty slotTerms and NO filled slotTerms. But parameters were:\n" + " - Free fillings: "
						+ freeFillings + "\n - Slots: " + slots);
			}
		}
	}
}
