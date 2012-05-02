/*
 * Copyright (c) 2010 Netcetera AG and others.
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
package ch.netcetera.eclipse.workspaceconfig.net;

/**
 * Interface to entities holding preference file data.
 */
public interface IPreferenceFileData {

  /**
   * Gets the preference file data.
   * 
   * @return the preference file date
   */
  byte[] getData();
}
