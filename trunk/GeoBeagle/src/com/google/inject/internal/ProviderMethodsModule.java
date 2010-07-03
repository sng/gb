/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.internal;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import static com.google.inject.internal.Preconditions.checkNotNull;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Message;
import com.google.inject.util.Modules;

import roboguice.config.AbstractAndroidModule;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * Creates bindings to methods annotated with {@literal @}{@link Provides}. Use the scope and
 * binding annotations on the provider method to configure the binding.
 *
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 */
public final class ProviderMethodsModule implements Module {
  private final Object delegate;
  private final TypeLiteral<?> typeLiteral;
private final Stopwatch stopwatch;
  private static HashMap<Class<?>, Integer> hasNoProviderMethods = initializeHasNoProviderMethods();
  private final static HashMap<Class<?>, Integer> initializeHasNoProviderMethods(){
      HashMap<Class<?>, Integer> hm = new HashMap<Class<?>, Integer>();
      hm.put(PrivateModule.class, 0);
      hm.put(AbstractModule.class, 0);
      hm.put(AbstractAndroidModule.class, 0);
       
    return hm;
      }
  private ProviderMethodsModule(Object delegate) {
    this.delegate = checkNotNull(delegate, "delegate");
    this.typeLiteral = TypeLiteral.get(this.delegate.getClass());
    this.stopwatch = new Stopwatch();
  }

  /**
   * Returns a module which creates bindings for provider methods from the given module.
   */
  public static Module forModule(Module module) {
    return forObject(module);
  }

  /**
   * Returns a module which creates bindings for provider methods from the given object.
   * This is useful notably for <a href="http://code.google.com/p/google-gin/">GIN</a>
   */
  public static Module forObject(Object object) {
    // avoid infinite recursion, since installing a module always installs itself
    if (object instanceof ProviderMethodsModule) {
      return Modules.EMPTY_MODULE;
    }

    return new ProviderMethodsModule(object);
  }
  public synchronized void configure(Binder binder) {
    for (ProviderMethod<?> providerMethod : getProviderMethods(binder)) {
      providerMethod.configure(binder);
    }
  }

  public List<ProviderMethod<?>> getProviderMethods(Binder binder) {
    List<ProviderMethod<?>> result = Lists.newArrayList();
    for (Class<?> c = delegate.getClass(); 
        c != Object.class; 
        c = c.getSuperclass()) {
      if (hasNoProviderMethods.containsKey(c))
          continue;
      int annotationCount = 0;
      for (Method method : c.getDeclaredMethods()) {
        if (method.isAnnotationPresent(Provides.class)) {
          result.add(createProviderMethod(binder, method));
          annotationCount++;
        }
//        stopwatch.resetAndLog("getProviderMethods: " + method.getName());
      }
      if (annotationCount == 0) {
        hasNoProviderMethods.put(c, 0);
      }
    }
    return result;
  }

  <T> ProviderMethod<T> createProviderMethod(Binder binder, final Method method) {
    binder = binder.withSource(method);
    Errors errors = new Errors(method);

    // prepare the parameter providers
    List<Dependency<?>> dependencies = Lists.newArrayList();
    List<Provider<?>> parameterProviders = Lists.newArrayList();
    List<TypeLiteral<?>> parameterTypes = typeLiteral.getParameterTypes(method);
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    for (int i = 0; i < parameterTypes.size(); i++) {
      Key<?> key = getKey(errors, parameterTypes.get(i), method, parameterAnnotations[i]);
      dependencies.add(Dependency.get(key));
      parameterProviders.add(binder.getProvider(key));
    }

    @SuppressWarnings("unchecked") // Define T as the method's return type.
    TypeLiteral<T> returnType = (TypeLiteral<T>) typeLiteral.getReturnType(method);

    Key<T> key = getKey(errors, returnType, method, method.getAnnotations());
    Class<? extends Annotation> scopeAnnotation
        = Annotations.findScopeAnnotation(errors, method.getAnnotations());

    for (Message message : errors.getMessages()) {
      binder.addError(message);
    }

    return new ProviderMethod<T>(key, method, delegate, ImmutableSet.copyOf(dependencies),
        parameterProviders, scopeAnnotation);
  }

  <T> Key<T> getKey(Errors errors, TypeLiteral<T> type, Member member, Annotation[] annotations) {
    Annotation bindingAnnotation = Annotations.findBindingAnnotation(errors, member, annotations);
    return bindingAnnotation == null ? Key.get(type) : Key.get(type, bindingAnnotation);
  }

  @Override public boolean equals(Object o) {
    return o instanceof ProviderMethodsModule
        && ((ProviderMethodsModule) o).delegate == delegate;
  }

  @Override public int hashCode() {
    return delegate.hashCode();
  }
}
