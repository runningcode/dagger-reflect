package dagger.reflect;

import javax.inject.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

final class LinkedMapProviderBinding<K, V> extends Binding.LinkedBinding<Map<K, Provider<V>>> {
  private final Map<K, LinkedBinding<Provider<V>>> entryBindings;

  LinkedMapProviderBinding(Map<K, LinkedBinding<Provider<V>>> entryBindings) {
    this.entryBindings = entryBindings;
  }

  @Override public Map<K, Provider<V>> get() {
    Map<K, Provider<V>> map = new LinkedHashMap<>(entryBindings.size());
    for (Map.Entry<K, LinkedBinding<Provider<V>>> elementBinding : entryBindings.entrySet()) {
      map.put(elementBinding.getKey(), elementBinding.getValue().get());
    }
    return map;
  }
}
