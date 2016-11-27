package de.nb.federkiel.grammatik.wortart.adjektiv;

import java.util.Iterator;
import java.util.Spliterator;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

@Immutable
public class Wortformen<WF extends Wortform> implements Iterable<WF> {
  private final ImmutableList<WF> list;

  public Wortformen(ImmutableList<WF> list) {
    this.list = list;
  }

  WF getStandard() {
    return list.get(0);
  }

  @Override
  public Spliterator<WF> spliterator() {
    return list.spliterator();
  }

  @Override
  public Iterator<WF> iterator() {
    return list.iterator();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((list == null) ? 0 : list.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Wortformen<?> other = (Wortformen<?>) obj;
    if (list == null) {
      if (other.list != null) {
        return false;
      }
    } else if (!list.equals(other.list)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return list.toString();
  }
}
