/**
 * Copyright (C) 2009 Google Inc.
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

package com.google.inject;

import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.FailableCache;
import com.google.inject.internal.ImmutableList;
import com.google.inject.internal.Stopwatch;

import static com.google.inject.internal.Iterables.concat;
import com.google.inject.spi.InjectionPoint;

import android.util.Log;

/**
 * Constructor injectors by type.
 *
 * @author jessewilson@google.com (Jesse Wilson)
 */
class ConstructorInjectorStore {
  private final InjectorImpl injector;
  private final Stopwatch stopwatch = new Stopwatch();

  private final FailableCache<TypeLiteral<?>, ConstructorInjector<?>>  cache
      = new FailableCache<TypeLiteral<?>, ConstructorInjector<?>> () {
    @SuppressWarnings("unchecked")
    protected ConstructorInjector<?> create(TypeLiteral<?> type, Errors errors)
        throws ErrorsException {
      return createConstructor(type, errors);
    }
  };

  ConstructorInjectorStore(InjectorImpl injector) {
    this.injector = injector;
  }

  /**
   * Returns a new complete constructor injector with injection listeners registered.
   */
  @SuppressWarnings("unchecked") // the ConstructorInjector type always agrees with the passed type
  public <T> ConstructorInjector<T> get(TypeLiteral<T> key, Errors errors) throws ErrorsException {
    return (ConstructorInjector<T>) cache.get(key, errors);
  }

  private <T> ConstructorInjector<T> createConstructor(TypeLiteral<T> type, Errors errors)
      throws ErrorsException {
//      Log.d("Guice", "CREATECONSTRUCTOR: " + type.toString());
      stopwatch.reset();
      
    if (type.toString().equals("com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget"))
        stopwatch.resetAndLog("GSW: 1");
    int numErrorsBefore = errors.size();

    InjectionPoint injectionPoint;
    try {
      injectionPoint = InjectionPoint.forConstructorOf(type);
    } catch (ConfigurationException e) {
      errors.merge(e.getErrorMessages());
      throw errors.toException();
    }
    if (type.toString().equals("com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget"))
        stopwatch.resetAndLog("GSW: 2");

    SingleParameterInjector<?>[] constructorParameterInjectors
        = injector.getParametersInjectors(injectionPoint.getDependencies(), errors);
    if (type.toString().equals("com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget"))
        stopwatch.resetAndLog("GSW: 2.5");
    MembersInjectorImpl<T> membersInjector = injector.membersInjectorStore.get(type, errors);

    if (type.toString().equals("com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget"))
        stopwatch.resetAndLog("GSW: 3");
    






    
    ConstructionProxyFactory<T> factory = new DefaultConstructionProxyFactory<T>(injectionPoint);
    if (type.toString().equals("com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget"))
        stopwatch.resetAndLog("GSW: 4");


    errors.throwIfNewErrors(numErrorsBefore);

    if (type.toString().equals("com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget"))
        stopwatch.resetAndLog("GSW: 5");
    final ConstructorInjector<T> constructorInjector = new ConstructorInjector<T>(membersInjector.getInjectionPoints(), factory.create(),
        constructorParameterInjectors, membersInjector);
    if (type.toString().equals("com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget"))
        stopwatch.resetAndLog("GSW: 6");

    stopwatch.resetAndLog(type.toString());
    return constructorInjector;
  }
}
