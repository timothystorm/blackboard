package org.storm.abseil.supplier;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class BoundSupplier extends DecoratedSupplier<Runnable> {
  private final AtomicLong _bound;

  public BoundSupplier(Supplier<Runnable> supplier, Long bound) {
    super(supplier);
    _bound = new AtomicLong(bound);
  }

  public static Supplier<Runnable> decorate(Supplier<Runnable> supplier, Long bound) {
    return new BoundSupplier(supplier, bound);
  }

  @Override
  public Runnable get() {
    if (_bound.decrementAndGet() == 0) return null;
    return decoratedGet();
  }
}
