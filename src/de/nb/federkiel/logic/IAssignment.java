package de.nb.federkiel.logic;


/**
 * A variable assignment in firstTerm-order-logic.
 * Each Variable is assigned a value.
 *
 * @author nbudzyn 2009
 */
public interface IAssignment {
	/**
	 * Retrieves the value for a variable.
	 *
	 * @param <T> The type of the variable (and the value)
	 */
	public <T extends Object> T getValue(Variable<T, ? extends IAssignment> variable)
	throws UnassignedVariableException;
}
