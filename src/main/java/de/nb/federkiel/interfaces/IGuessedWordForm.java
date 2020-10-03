package de.nb.federkiel.interfaces;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A word form for a lexeme, also knowing the other inflected forms of this
 * lexeme
 *
 * @author nbudzyn 2011
 */
@Immutable
@ThreadSafe
public interface IGuessedWordForm extends IWordForm {
	public IInflexionList getLexemeInflexions();

	public Object getLexemAndInflectionsDescription();
}
