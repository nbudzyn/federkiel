/**
 * Statische Hilsmethoden für den Umgang mit Strings.
 *
 * @author Nikolaj Budzyn
 */
package de.nb.federkiel.string;

public class StringUtil {
  private StringUtil() {
    super();
  }

  // public static String deleteTrailing(final String aString, final char aChar)
  // {
  // String res = aString;
  //
  // while (res.length() > 0 && res.charAt(res.length() - 1) == aChar) {
  // res = res.substring(0, res.length() - 1);
  // }
  // return res;
  // }
  //
  public static String capitalize(final String aString) {
    if (aString.length() == 0) {
      return "";
    }

    return aString.substring(0, 1).toUpperCase() + aString.substring(1);
    // FIXME Locale?! Umlauts?
  }

  // public static boolean contains(final String string, final int aChar) {
  // return string.indexOf(aChar) >= 0;
  // }

  /**
   * Entfernt die Umlautung.
   */
  public static char makeUmlautsPlain(final char aChar) {
    if (aChar == 'ä') {
      return 'a';
    }
    if (aChar == 'ö') {
      return 'o';
    }
    if (aChar == 'ü') {
      return 'u';
    }
    if (aChar == 'Ä') {
      return 'A';
    }
    if (aChar == 'Ö') {
      return 'O';
    }
    if (aChar == 'Ü') {
      return 'U';
    }
    return aChar;
  }

  public static String makeUmlautsPlain(final String aString) {
    final StringBuffer resBuffer = new StringBuffer(aString);
    for (int i = 0; i < resBuffer.length(); i++) {
      resBuffer.setCharAt(i, makeUmlautsPlain(resBuffer.charAt(i)));
    }

    return resBuffer.toString();
  }

  // public static String transformToRealUmlauts(final String aString) {
  // final StringBuffer resBuffer = new StringBuffer();
  // for (int i = 0; i < aString.length(); i++) {
  // if (aString.charAt(i) == '$') {
  // if (i + 1 == aString.length()) {
  // throw new IllegalArgumentException(
  // "Invalid string for tranformationToRealUmlauts: ends with \'$\': "
  // + aString);
  // }
  // resBuffer.append(umlaut(aString.charAt(i + 1)));
  // i++;
  // } else {
  // resBuffer.append(aString.charAt(i));
  // }
  // }
  // return resBuffer.toString();
  // }
  //
  // public static char umlaut(final char aChar) {
  // if (aChar == 'a') {
  // return 'ä';
  // }
  // if (aChar == 'o') {
  // return 'ö';
  // }
  // if (aChar == 'u') {
  // return 'ü';
  // }
  // if (aChar == 'A') {
  // return 'Ä';
  // }
  // if (aChar == 'O') {
  // return 'Ö';
  // }
  // if (aChar == 'U') {
  // return 'Ü';
  // }
  // if (aChar == 's') {
  // return 'ß';
  // }
  // throw new IllegalArgumentException("Cannot create umlaut for char \'"
  // + aChar + "\'.");
  // }

  /**
   * Trims the two strings and concats two strings; also adds a space in between
   * them, iff (after the trimming) none of the strings is empty.
   */
  public static String concatSpacedTrim(final String string1,
      final String string2) {
    final String string1trimmed = string1.trim();
    final String string2trimmed = string2.trim();

    if (string1trimmed.length() == 0 || string2trimmed.length() == 0) {
      return string1trimmed + string2trimmed;
    }

    return string1trimmed + " " + string2trimmed;
  }

  // public static String fromSecondWordOn(final String aString) {
  // final StringBuffer resBuffer = new StringBuffer(aString);
  // for (int i = 0; i < resBuffer.length(); i++) {
  // if (resBuffer.charAt(0) == ' ' || resBuffer.charAt(0) == ','
  // || resBuffer.charAt(0) == ';' || resBuffer.charAt(0) == '.'
  // || resBuffer.charAt(0) == '!') {
  // return resBuffer.deleteCharAt(0).toString().trim();
  // }
  //
  // resBuffer.deleteCharAt(0);
  // }
  // return "";
  // }
}
