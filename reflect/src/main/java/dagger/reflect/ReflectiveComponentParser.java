package dagger.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static dagger.reflect.Reflection.findQualifier;

final class ReflectiveComponentParser implements Reflection.ClassVisitor {
  private static final Binding<?>[] NO_BINDINGS = new Binding<?>[0];

  static void parse(Class<?> moduleClass, Object instance, BindingGraph.Builder graphBuilder) {
    Reflection.walkHierarchy(moduleClass, new ReflectiveComponentParser(instance, graphBuilder));
  }

  private final Object instance;
  private final BindingGraph.Builder graphBuilder;

  private ReflectiveComponentParser(Object instance, BindingGraph.Builder graphBuilder) {
    this.instance = instance;
    this.graphBuilder = graphBuilder;
  }

  @Override public void visit(Class<?> cls) {
    if (cls == Object.class) {
      return;
    }

    for (Method method : cls.getDeclaredMethods()) {
      if (method.getParameterCount() != 0 || method.getReturnType() == void.class) {
        continue; // Not a provision method.
      }

      Annotation qualifier = findQualifier(method.getAnnotations());
      Type type = method.getGenericReturnType();
      Key key = Key.of(qualifier, type);

      Binding<?> binding = new Binding.LinkedProvides<>(instance, method, NO_BINDINGS);

      graphBuilder.add(key, binding);
    }
  }
}
