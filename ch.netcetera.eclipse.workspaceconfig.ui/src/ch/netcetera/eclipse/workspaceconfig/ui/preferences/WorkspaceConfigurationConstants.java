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
package ch.netcetera.eclipse.workspaceconfig.ui.preferences;

/**
 * Constants used for the preferences.
 */
public final class WorkspaceConfigurationConstants {
  
  /** ID used to store the preference entry of the startup check flag. */
  public static final String CONFIG_STARTUP_CHECK = "STARTUP_CHECK_V3";
  
  /**
   * The configuration key of the list of environment variables to replace
   * during the import.
   */
  public static final String CONFIG_ENV_REPLACEMENT_LIST = "ENV_REPLACEMENT_LIST_V3";

  /** The configuration key for the remote configuration file URL. */
  public static final String CONFIG_URL = "URL_V3";
  
  /** The configuration key to store current configuration state of the workspace. */
  public static final String CONFIG_CONFIGURED = "CONFIGURED_V3";

  
  /**
   * Private default constructor to avoid instantiation.
   */
  private WorkspaceConfigurationConstants() {
    throw new AssertionError("Not instantiable.");
  }
}
