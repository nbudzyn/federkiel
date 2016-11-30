package de.nb.federkiel.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Utility methods for Collections
 *
 * @author nbudzyn 2009
 */
@Immutable
@ThreadSafe
final public class CollectionUtil {
  private CollectionUtil() {
    super();
  }

  /*
   * public static <T extends Object> void addIfNotNull( final Collection<? super T> collection,
   * final T element) { if (element != null) { collection.add(element); } }
   */

  public static <T extends Object> void addIfNotNull(
      final ImmutableList.Builder<? super T> immutableListBuilder, final T element) {
    if (element != null) {
      immutableListBuilder.add(element);
    }
  }

  /**
   * @return <code>true</code>, iff the collection contains something of the stuff.
   */
  @SafeVarargs
  public static <E extends Object> boolean containsAny(final Collection<E> col, final E... stuff) {
    for (final E something : stuff) {
      if (col.contains(something)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Might be helpful for implementing the Comparable interface, if there are collection members
   */
  public static <T extends Comparable<? super T>, S1 extends T, S2 extends T> int compareCollections(
      final Collection<S1> one, final Collection<S2> other) {
    final Iterator<S1> oneIt = one.iterator();
    final Iterator<S2> otherIt = other.iterator();

    while (oneIt.hasNext() && otherIt.hasNext()) {
      final S1 oneElement = oneIt.next();
      final S2 otherElement = otherIt.next();

      final int elementsCompared = oneElement.compareTo(otherElement);
      if (elementsCompared != 0) {
        return elementsCompared;
      }
    }
    // one Collction was at its end
    if ((!oneIt.hasNext()) && otherIt.hasNext()) {
      return -1; // the first is shorter
    } else if (oneIt.hasNext() && !otherIt.hasNext()) {
      return 1; // second is shorter
    }

    return 0;
  }

  public static <K extends Comparable<? super K>, V extends Comparable<? super V>, K1 extends K, K2 extends K, V1 extends V, V2 extends V> int compareMaps(
      final Map<K1, V1> one, final Map<K2, V2> other) {
    final Iterator<Entry<K1, V1>> oneIt = one.entrySet().iterator();

    while (oneIt.hasNext()) {
      final Entry<K1, V1> oneEntry = oneIt.next();
      final V2 otherValue = other.get(oneEntry.getKey());

      if (otherValue == null) {
        return -1;
      }

      final int valuesCompared = oneEntry.getValue().compareTo(otherValue);
      if (valuesCompared != 0) {
        return valuesCompared;
      }
    }

    // Are there more keys in the second Map?
    if (one.size() < other.size()) {
      return 1;
    }

    return 0;
  }

  public static String toLines(final Iterable<?> iterable) {
    final StringBuilder res = new StringBuilder(2000);
    boolean first = true;
    for (final Object element : iterable) {
      if (first) {
        first = false;
      } else {
        res.append("\n");
      }
      res.append(element.toString());
    }
    return res.toString();
  }

  public static <E extends Object> Collection<E> noNull(final Collection<E> collection) {
    if (collection != null) {
      return collection;
    }

    return ImmutableList.of();
  }

  public static String toShortString(final Collection<?> col) {
    if (col.size() == 1) {
      return col.iterator().next().toString();
    }

    return Iterables.toString(col);
  }

  @SafeVarargs
  public static <E extends Object> ImmutableList<E> immmutableListSkipNulls(E... elements) {
    return immmutableListCopySkipNulls(Arrays.asList(elements));
  }

  public static <E extends Object> ImmutableList<E> immmutableListCopySkipNulls(Collection<E> col) {
    return ImmutableList.copyOf(Collections2.filter(col, arg -> arg != null));
  }
}
