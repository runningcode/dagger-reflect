package dagger.reflect;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.jetbrains.annotations.Nullable;

import static dagger.reflect.DaggerReflect.notImplemented;
import static dagger.reflect.Reflection.findQualifier;
import static dagger.reflect.Reflection.findScope;
import static java.lang.reflect.Modifier.ABSTRACT;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;

final class ReflectiveModuleParser implements Reflection.ClassVisitor {
  static void parse(Class<?> moduleClass, @Nullable Object instance,
      BindingGraph.Builder graphBuilder) {
    Reflection.walkHierarchy(moduleClass,
        new ReflectiveModuleParser(moduleClass, instance, graphBuilder));
  }

  private final Class<?> moduleClass;
  private final @Nullable Object instance;
  private final BindingGraph.Builder graphBuilder;

  private ReflectiveModuleParser(Class<?> moduleClass, @Nullable Object instance,
      BindingGraph.Builder graphBuilder) {
    this.moduleClass = moduleClass;
    this.instance = instance;
    this.graphBuilder = graphBuilder;
  }

  @Override public void visit(Class<?> cls) {
    if (cls == Object.class) {
      return;
    }

    for (Method method : cls.getDeclaredMethods()) {
      if ((method.getModifiers() & PRIVATE) != 0) {
        throw new IllegalArgumentException("Private module methods are not allowed: " + method);
      }

      Binding<?> binding;
      if ((method.getModifiers() & ABSTRACT) != 0) {
        if (method.getAnnotation(Binds.class) != null) {
          binding = new Binding.UnlinkedBinds(method);
        } else if (method.getAnnotation(BindsOptionalOf.class) != null) {
          throw notImplemented("@BindsOptionalOf");
        } else {
          continue;
        }
      } else {
        if ((method.getModifiers() & STATIC) == 0 && instance == null) {
          throw new IllegalStateException(moduleClass.getCanonicalName() + " must be set");
        }

        if (method.getAnnotation(Provides.class) != null) {
          binding = new Binding.UnlinkedProvides(instance, method);
        } else {
          continue;
        }
      }

      if (method.getAnnotation(IntoSet.class) != null) {
        throw notImplemented("@IntoSet");
      }
      if (method.getAnnotation(ElementsIntoSet.class) != null) {
        throw notImplemented("@ElementsIntoSet");
      }
      if (method.getAnnotation(IntoMap.class) != null) {
        throw notImplemented("@IntoMap");
      }

      Annotation[] annotations = method.getAnnotations();
      Type returnType = method.getGenericReturnType();
      Key key = Key.of(findQualifier(annotations), returnType);

      Annotation scope = findScope(annotations);
      if (scope != null) {
        // TODO check correct scope against BindingGraph.
        throw notImplemented("Scoped bindings");
      }

      graphBuilder.add(key, binding);
    }
  }
}
