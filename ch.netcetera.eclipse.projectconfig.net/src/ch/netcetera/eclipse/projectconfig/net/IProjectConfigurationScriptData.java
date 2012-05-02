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
package ch.netcetera.eclipse.projectconfig.net;

/**
 * Interface to entities holding project configuration script file data.
 */
public interface IProjectConfigurationScriptData {

  /**
   * Gets the project configuration script file data.
   * 
   * @return the project configuration script file data
   */
  byte[] getData();
}
