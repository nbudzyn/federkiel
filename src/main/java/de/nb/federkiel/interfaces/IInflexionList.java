package de.nb.federkiel.interfaces;

import java.util.Collection;
import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A collection of all (inflected) word forms for a (guessed) lexeme.
 *
 * @author nbudzyn 2011
 */
@Immutable
@ThreadSafe
public interface IInflexionList extends Comparable<IInflexionList> {
	int size();

	IWordForm get(int index);

	/**
	 * This method can be used to retrieve the dative singular forms from this
	 * list, e.g.
	 *
	 * @return a string showing all inflexions matching theses feature
	 *         restrictions, might be empty
	 */
	String getAlternativeString(Map<String, String> featureRestrictions);

	/**
	 * This method can be used to retrieve all dative singular forms from this
	 * list, e.g.
	 *
	 * @return all inflexions matching theses feature restrictions, might be
	 *         empty
	 */
	Collection<String> getAlternatives(Map<String, String> featureRestrictions);

}
