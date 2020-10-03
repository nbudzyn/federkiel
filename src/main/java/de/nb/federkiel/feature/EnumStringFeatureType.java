package de.nb.federkiel.feature;

import com.google.common.collect.ImmutableSet;

import de.nb.federkiel.interfaces.IFeatureValue;

/**
 * The type of a String feature, that can have one of a distinct (enumerated) number of String
 * values.
 *
 * @author nbudzyn
 */
public class EnumStringFeatureType extends StringFeatureType {
  public static final EnumStringFeatureType BOOLEAN =
      new EnumStringFeatureType(StringFeatureLogicUtil.TRUE, StringFeatureLogicUtil.FALSE);

  private final ImmutableSet<String> values;

  public EnumStringFeatureType(final String... values) {
    this(ImmutableSet.copyOf(values));
  }

  public EnumStringFeatureType(final ImmutableSet<String> values) {
    this.values = values;
  }

  @Override
  public ImmutableSet<IFeatureValue> getAllPossibleValues() {
    // @formatter:off
    return values.stream()
      .map(s -> StringFeatureValue.of(s))
      .collect(ImmutableSet.toImmutableSet());
    // @formatter:on
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((values == null) ? 0 : values.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final EnumStringFeatureType other = (EnumStringFeatureType) obj;
    if (values == null) {
      if (other.values != null) {
        return false;
      }
    } else if (!values.equals(other.values)) {
      return false;
    }
    return true;
  }
}
