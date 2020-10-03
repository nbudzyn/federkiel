/**
 * Statische Hilsmethoden für den Umgang mit Strings.
 *
 * @author Nikolaj Budzyn
 */
package de.nb.federkiel.string;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableCollection;

@Immutable
@ThreadSafe
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
   * Trims the two strings and concats two strings; also adds a space in between them, iff (after
   * the trimming) none of the strings is empty.
   */
  public static String concatSpacedTrim(final String string1, final String string2) {
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

  public static boolean contains(final String string, final CharSequence... ss) {
    for (final CharSequence s : ss) {
      if (string.contains(s)) {
        return true;
      }
    }

    return false;
  }

  public static boolean containsOneChar(final String string, final CharSequence s) {
    for (int i = 0; i < s.length(); i++) {
      if (string.indexOf(s.charAt(i)) != -1) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the (firstTerm) index of any of the Strings in the string - or -1, if none of them
   * occurs in the string.
   */
  public static int indexOf(final String string, final String... ss) {
    int res = -1;
    for (final String s : ss) {
      final int indexOfS = string.indexOf(s);
      if (indexOfS != -1) {
        // found
        if (res == -1) {
          res = indexOfS;
        } else if (indexOfS < res) {
          res = indexOfS;
        }
      }
    }

    return res;
  }

  public static boolean endsWith(final String string, final String... possibleEndings) {
    for (final String possibleEnding : possibleEndings) {
      if (string.endsWith(possibleEnding)) {
        return true;
      }
    }

    return false;
  }

  public static boolean startsWith(final String string, final String... possibleBeginnings) {
    for (final String possibleBeginning : possibleBeginnings) {
      if (string.startsWith(possibleBeginning)) {
        return true;
      }
    }

    return false;
  }

  /**
   * @return <code>true</code>, if <code>string</code> equals one of the <code>otherStrings</code>.
   */
  public static boolean equals(final String string, final String... otherStrings) {
    for (final String anotherString : otherStrings) {
      if (string.equals(anotherString)) {
        return true;
      }
    }

    return false;
  }

  public static ImmutableCollection<String> prepend(final String prefix,
      final Collection<String> strings) {
    return strings.stream().map(string -> prefix + string).collect(toImmutableList());
  }

  public static String replaceRegion(final String string, final int startIndex,
      final String replacement) {
    final StringBuilder res = new StringBuilder(string.length());
    if (startIndex >= 0) {
      if (startIndex > string.length()) {
        return string;
      }

      res.append(string.substring(0, startIndex));
    }

    int lengthToReplace = replacement.length();
    if (lengthToReplace > string.length() - startIndex) {
      lengthToReplace = string.length() - startIndex;
    }

    res.append(replacement.subSequence(0, lengthToReplace));

    if (startIndex + lengthToReplace < string.length()) {
      res.append(string.substring(startIndex + lengthToReplace));
    }

    return res.toString();
  }

  /**
   * @return <code>true</code>, iff the strings contains any whitespace
   */
  public static boolean containsWhitespace(final String string) {
    for (int i = 0; i < string.length(); i++) {
      if (Character.isWhitespace(string.charAt(i))) {
        return true;
      }
    }

    return false;
  }

  /**
   * @return <code>true</code>, iff the strings contains only letters
   */
  public static boolean containsOnlyLetters(final String string) {
    for (int i = 0; i < string.length(); i++) {
      if (!Character.isLetter(string.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Splits the string at the occurences of the splitter, trims the single strings and returns those
   * that are not empty.
   */
  protected static String[] splitAndTrim(final String string, final String splitter) {
    return splitAndTrim(string, new String[] {splitter});
  }

  /**
   * Splits the string at the occurences of any of the splitters, trims the single strings and
   * returns those that are not empty.
   * <p>
   *
   * @param splitters The splitters shall <i>not</i> contain each other!
   */
  public static String[] splitAndTrim(final String string, final String[] splitters) {
    final Collection<String> res = new LinkedList<>();

    int sectionStart = 0;
    int i = 0;
    while (i < string.length()) {
      boolean splitterFoundThisTime = false;

      for (final String splitter : splitters) {
        if (i + splitter.length() <= string.length()
            && string.substring(i, i + splitter.length()).equals(splitter)) {
          // a splitter found!
          splitterFoundThisTime = true;
          if (i > sectionStart) {
            final String sub = string.substring(sectionStart, i).trim();
            if (!sub.isEmpty()) {
              res.add(sub);
            }
          } // else ("...<br><br>...", e.g., if splitter is "<br>")
          sectionStart = i + splitter.length();
          i = sectionStart;
          break; // for
        }
      } // for

      if (!splitterFoundThisTime) {
        i++;
      }
    }

    // last section
    final String sub = string.substring(sectionStart).trim();
    if (!sub.isEmpty()) {
      res.add(sub);
    }

    final String[] resArray = res.toArray(new String[] {});
    return resArray;
  }

  public static String removeSpacesAroundACharacter(final String string, final String character) {
    String tmp = string;

    final String spacePlusChar = " " + character;

    int occur1 = tmp.indexOf(spacePlusChar);
    while (occur1 != -1) {
      if (occur1 > 0) {
        tmp = tmp.substring(0, occur1).trim() + character + tmp.substring(occur1 + 2).trim();
      } else {
        // spaceSlash == 0
        tmp = character + tmp.substring(occur1 + 2).trim();
      }
      occur1 = tmp.indexOf(spacePlusChar);
    }

    final String charPlusSpace = character + " ";

    int occur2 = tmp.indexOf(charPlusSpace);
    while (occur2 != -1) {
      if (occur2 > 0) {
        tmp = tmp.substring(0, occur2).trim() + character + tmp.substring(occur2 + 2).trim();
      } else {
        // spaceSlash == 0
        tmp = character + tmp.substring(occur2 + 2).trim();
      }
      occur2 = tmp.indexOf(charPlusSpace);
    }
    return tmp;
  }

  public static boolean startsWithUpperCase(final String string) {
    if (string.equals("")) {
      return false;
    }

    return Character.isUpperCase(string.codePointAt(0));
  }

  /**
   * Turns the first letter of the string to upper-case.
   */
  public static String capitalize(final String string, final Locale locale) {
    return changeCapitalization(string, true, locale);
  }

  /**
   * Turns the first letter of the string to lower-case.
   */
  public static String decapitalize(final String string, final Locale locale) {
    return changeCapitalization(string, false, locale);
  }

  private static String changeCapitalization(final String string, final boolean toUpperCase,
      final Locale locale) {
    if (string.length() == 0) {
      return "";
    }

    final StringBuilder res = new StringBuilder();

    final String startString = new String(new int[] {string.codePointAt(0)}, 0, 1);

    String handledStart;
    if (toUpperCase) {
      handledStart = startString.toUpperCase(locale);
    } else {
      // toLowerCase
      handledStart = startString.toLowerCase(locale);
    }
    res.append(handledStart);

    if (string.length() > 1) {
      res.append(string.substring(1));
    }

    return res.toString();
  }

  /**
   * Entfernt ein Präfix von einem String - wenn es überhaupt vorkam.
   */
  public static String stripPrefixIfAny(final String prefix, final String string) {
    if (string.startsWith(prefix)) {
      return string.substring(prefix.length());
    }

    return string;
  }
}
