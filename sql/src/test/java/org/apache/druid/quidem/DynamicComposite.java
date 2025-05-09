/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.quidem;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Dynamically creates a composite from two classes.
 */
public class DynamicComposite<T, E> implements InvocationHandler
{
  @SuppressWarnings("unchecked")
  public static <T, E> T make(T base, Class<T> baseClass, E ext, Class<E> extClass)
  {
    return (T) Proxy.newProxyInstance(
        base.getClass().getClassLoader(),
        new Class[] {baseClass, extClass},
        new DynamicComposite<>(base, ext, extClass)
    );
  }

  private final T base;
  private final E ext;
  private final Class<E> extClass;

  private DynamicComposite(T base, E ext, Class<E> extClass)
  {
    this.base = base;
    this.ext = ext;
    this.extClass = extClass;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
  {
    if (method.getDeclaringClass() == extClass) {
      return method.invoke(ext, args);
    } else {
      return method.invoke(base, args);
    }
  }
}
