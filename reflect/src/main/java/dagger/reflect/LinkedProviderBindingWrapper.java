package dagger.reflect;

import dagger.reflect.Binding.LinkedBinding;

import javax.inject.Provider;

final class LinkedProviderBindingWrapper<T> extends LinkedBinding<Provider<T>> {

  private final LinkedBinding<T> linkedBinding;

  LinkedProviderBindingWrapper(LinkedBinding<T> linkedBinding) {
    this.linkedBinding = linkedBinding;
  }

  @Override public Provider<T> get() {
    return new BindingProvider<>(linkedBinding);
  }
}
