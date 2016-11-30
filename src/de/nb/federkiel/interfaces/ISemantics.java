package de.nb.federkiel.interfaces;

import javax.annotation.concurrent.Immutable;

/**
 * The semantics (of a sequence of Words, e.g.)
 * <p>
 * All implementations MUST BE IMMUTABLE.
 *
 * @author nbudzyn 2009
 */
@Immutable
public interface ISemantics extends Comparable<ISemantics> {
}
