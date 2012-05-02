/*
 * Copyright (c) 2009 Netcetera AG and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * - Netcetera AG: initial implementation
 */
package ch.netcetera.eclipse.common.text;


/**
 * Interface to access text resources.
 */
public interface ITextAccessor {

  /**
   * Gets the text resource.
   *
   * @param key the lookup key
   * @return the text resource
   */
  String getText(String key);

  /**
   * Gets the text resource. The replacement of the arguments is done with {@link java.text.MessageFormat}.
   *
   * @param key the lookup key
   * @param arguments the arguments to fill into the text resource
   * @return the text resource
   */
  String getText(String key, Object[] arguments);
}
