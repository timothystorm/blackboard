package org.storm.abseil.supplier;

import java.util.function.Supplier;

public abstract class DecoratedSupplier<T> implements Supplier<T> {

  private final Supplier<T> _supplier;

  protected DecoratedSupplier(Supplier<T> supplier) {
    _supplier = supplier;
  }

  protected Supplier<T> decorated() {
    return _supplier;
  }

  protected T decoratedGet() {
    return _supplier.get();
  }
}
