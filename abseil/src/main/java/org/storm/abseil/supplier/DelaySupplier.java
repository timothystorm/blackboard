package org.storm.abseil.supplier;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.storm.abseil.utils.Sleep;

public class DelaySupplier extends DecoratedSupplier<Runnable> {
  private final Long               _max, _min;

  public DelaySupplier(Supplier<Runnable> supplier, int minInclusive, int maxExclusive, TimeUnit unit) {
    super(supplier);
    _max = unit.toMillis(maxExclusive);
    _min = unit.toMillis(minInclusive);
  }

  public static Supplier<Runnable> decorate(Supplier<Runnable> supplier, int minInclusive, int maxExclusive,
      TimeUnit unit) {
    return new DelaySupplier(supplier, minInclusive, maxExclusive, unit);
  }

  @Override
  public Runnable get() {
    if (Sleep.random(_min, _max, TimeUnit.MILLISECONDS)) return decoratedGet();
    return null;
  }
}
