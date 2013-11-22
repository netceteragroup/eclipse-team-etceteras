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
package ch.netcetera.eclipse.workspaceconfig.ui.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.FrameworkUtil;

import ch.netcetera.eclipse.workspaceconfig.ui.preferences.WorkspaceConfigurationConstants;


/**
 * Utility class to work with the workspace configuration status stored in the
 * plug-in's configuration.
 */
public final class WorkspaceConfigurationStatusUtil {

  private static final String DATEFORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
  private static final String NO_CONFIG = "NO_CONFIG";
  private static final String ERROR = "ERROR ";

  /**
   * Private default constructor to avoid instantiation.
   */
  private WorkspaceConfigurationStatusUtil() {
    throw new AssertionError("Instantiation not allowed.");
  }

  /**
   * Finds out whether the currently staring workspace was ever configured using
   * this plug-in.
   *
   * @return <code>true</code> if it's a new unconfigured workspace and
   * <code>false</code> otherwise
   */
  public static boolean isNewWorkspace() {
    return getConfigurationAttribute().length() == 0;
  }

  /**
   * Writes the appropriate configuration status of the workspace according to the status passed.
   *
   * @param status the status to check
   */
  public static void writeConfiguredFlag(IStatus status) {
    if (status.isOK()) {
      writeConfiguredAttribute(getFormattedTimestamp());
    } else {
      writeErrorFlag();
    }
  }

  /**
   * Tags the workspace as configured.
   */
  public static void writeConfiguredFlag() {
    writeConfiguredAttribute(getFormattedTimestamp());
  }

  /**
   * Tags the workspace as not configured, which means it won't be configured
   * next time.
   */
  public static void writeNoConfigFlag() {
    writeConfiguredAttribute(NO_CONFIG);
  }

  /**
   * Tags the workspace as not configured, which means it won't be configured
   * next time.
   */
  public static void writeErrorFlag() {
    writeConfiguredAttribute(ERROR + getFormattedTimestamp());
  }

  /**
   * Writes the value of the CONFIGURED attribute.
   *
   * @param value the value to set
   */
  private static void writeConfiguredAttribute(String value) {
    IEclipsePreferences rootNode = InstanceScope.INSTANCE.getNode(
        FrameworkUtil.getBundle(WorkspaceConfigurationStatusUtil.class).getSymbolicName());
    if (rootNode != null) {
      rootNode.put(WorkspaceConfigurationConstants.CONFIG_CONFIGURED, value);
    }
  }

  /**
   * Finds out whether the workspace is unconfigured by user choice.
   *
   * @return <code>true</code> if the workspace is unconfigured by user choice
   * and <code>false</code> otherwise
   */
  public static boolean isUnconfiguredWorkspace() {
    String value = getConfigurationAttribute();
    return value != null && value.equals(NO_CONFIG);
  }

  /**
   * Gets the current value of the CONFIGURATION configuration attribute.
   *
   * @return the current value for the configuration attribute
   */
  private static String getConfigurationAttribute() {
    return Platform.getPreferencesService().getString(
        FrameworkUtil.getBundle(WorkspaceConfigurationStatusUtil.class).getSymbolicName(),
        WorkspaceConfigurationConstants.CONFIG_CONFIGURED, "", null);
  }

  /**
   * Finds out whether the workspace is configured by user choice.
   *
   * @return <code>true</code> if the workspace is configured by user choice and
   * <code>false</code> otherwise
   */
  public static boolean isConfiguredWorkspace() {
    String value = getConfigurationAttribute();
    return value != null
        && !isErrorDuringConfiguration()
        && !value.equals(NO_CONFIG);
  }

  /**
   * Finds out whether an error occurred during the configuration process.
   *
   * @return <code>true</code> if an error occurred during the configuration
   * process and <code>false</code> otherwise
   */
  public static boolean isErrorDuringConfiguration() {
    String value = getConfigurationAttribute();
    return value != null && value.startsWith(ERROR);
  }

  /**
   * Gets the configuration date of the workspace.
   *
   * @return the configuration date of the workspace
   */
  public static String getConfigDate() {
    return getConfigurationAttribute();
  }

  /**
   * Gets the configuration date of the workspace.
   *
   * @return the configuration date of the workspace
   */
  public static String getErrorDate() {
    return getConfigurationAttribute().substring(ERROR.length());
  }

  /**
   * Gets a properly formatted timestamp.
   *
   * @return a formatted timestamp
   */
  private static String getFormattedTimestamp() {
    return new SimpleDateFormat(DATEFORMAT_PATTERN, Locale.US).format(new Date());
  }
}
