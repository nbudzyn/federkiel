package de.nb.federkiel.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * Reflection utility methods
 *
 * @author nbudzyn 2011
 */
public class ReflectionUtil {
	private ReflectionUtil() {
		super();
	}

	/**
	 * Returns all values of public-static-final fields of the given
	 * <code>clazz</code>, that have the <code>constantType</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> getConstantFields(
			final Class<?> clazz, final Class<T> constantType) {
		try {
			final ImmutableList.Builder<T> res = ImmutableList.builder();

			for (final Field field : clazz.getDeclaredFields()) {
				if (constantType.isAssignableFrom(field.getType())) {
					final int modifiers = field.getModifiers();
					if (Modifier.isPublic(modifiers) &&
							Modifier.isStatic(modifiers) &&
							Modifier.isFinal(modifiers)) {
						res.add((T) field.get(null));
					}
				}
			}

			return res.build();
		} catch (final SecurityException e) {
			throw new RuntimeException(e);
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
