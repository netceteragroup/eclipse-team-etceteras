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
package ch.netcetera.eclipse.workspaceconfig.net.internal;

import ch.netcetera.eclipse.workspaceconfig.net.IPreferenceFileData;

/**
 * byte array backed {@link IPreferenceFileData} implementation.
 */
public final class PreferenceFileData implements IPreferenceFileData {

  private final byte[] data;
  
  /**
   * Constructor.
   * 
   * @param data the preference file data 
   */
  public PreferenceFileData(byte[] data) {
    this.data = data;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getData() {
    return this.data;
  }

}
