package org.storm.abseil.runnable;

import java.util.function.Supplier;

import org.storm.abseil.Abseil;

/**
 * Factory that builds {@link Runnable}s to be processed by an {@link Abseil}.
 * 
 * @author Timothy Storm
 * @see AbstractRunnableFactory
 */
public interface RunnableSupplier extends Supplier<Runnable>{
  /**
   * Creates runnable(s) to be processed by an {@link Abseil}. A null Runnable begins a graceful shutdown of the
   * {@link Abseil}.
   * 
   * @return {@link Runnable} to be executed or null to gracefully shutdown the {@link Abseil}
   */
  Runnable get();
}
