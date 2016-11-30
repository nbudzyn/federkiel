package de.nb.federkiel.feature;

/**
 * Utility class for logical reasoning with String features
 *
 * @author nbudzyn 2009
 */
public final class StringFeatureLogicUtil {
	public static final String TRUE = "j";
	public static final String FALSE = "n";

	private StringFeatureLogicUtil() {
		super();
	}

	public static boolean stringToBoolean(final String string) {
		if (string.equals(TRUE)) {
			return true;
		}
		if (string.equals(FALSE)) {
			return false;
		}

		throw new IllegalArgumentException("Boolean feature string was " + string +
				". Expected \"" + TRUE + "\" or \"" + FALSE + "\".");
	}

	public static String booleanToString(final boolean b) {
		return b ? TRUE : FALSE;
	}

}
