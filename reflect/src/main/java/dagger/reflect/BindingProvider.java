package dagger.reflect;

import dagger.reflect.Binding.LinkedBinding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Provider;

final class BindingProvider<T> implements Provider<T> {
  private LinkedBinding<T> binding;

  BindingProvider(@NotNull LinkedBinding<T> binding) {
    this.binding = binding;
  }

  @Override public @Nullable T get() {
    return binding.get();
  }
}
