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
package ch.netcetera.eclipse.workspaceconfig.core.internal;


/**
 * Interface to classes doing String replacements. This interface is used by the
 * {@link BufferedReplacementInputStream} to handle it's callback instance.
 */
public interface IReplacer {

  /**
   * Does replacements on the String passed and returns the changed string. 
   * 
   * @param line the original string
   * @return the string after the replacements
   */
  String replace(String line); 
}
