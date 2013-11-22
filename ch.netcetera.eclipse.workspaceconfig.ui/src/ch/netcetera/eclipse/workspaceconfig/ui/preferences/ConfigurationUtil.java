/*
 * Copyright (c) 2011 Netcetera AG and others.
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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.FrameworkUtil;



/**
 * Service to access the configuration of this plugin.
 * <p>
 * The term configuration is used here to denote the preferences used to store the configuration
 * of this plugin. In contrast, the term preferences is used for the handling of the import
 * operations performed by this plugin.
 * </p>
 */
public final class ConfigurationUtil {

  private static String bundleSymbolicName = FrameworkUtil.getBundle(ConfigurationUtil.class).getSymbolicName();

  private static ConfigurationXMLMarshaler marshaler = new ConfigurationXMLMarshaler();
  private static ConfigurationXmlParser parser = new ConfigurationXmlParser();

  /**
   * Not instantiable.
   */
  private ConfigurationUtil() {
    throw new AssertionError("Not instantiable.");
  }

  /**
   * Saves the EPF URLs passed in the preference store.
   *
   * @param epfUrls the EPF URLs to store
   */
  static void saveEpfUrls(Collection<String> epfUrls) {
    String xml = "";
    try {
      xml = marshaler.marshalEpfUrlList(epfUrls);
    } catch (IOException e) {
      // don't do anything...
    }

    getPreferences().put(WorkspaceConfigurationConstants.CONFIG_URL, xml);
  }


  /**
   * Gets the EPF URLs from the preference store.
   *
   * @return the EPF URLs from the preference store
   */
  public static List<String> getEpfUrls() {
    String xml = Platform.getPreferencesService().getString(bundleSymbolicName,
        WorkspaceConfigurationConstants.CONFIG_URL, "", null);
    try {
      return parser.parseEpfUrlList(xml);
    } catch (IOException e) {
      return Collections.emptyList();
    }
  }

  /**
   * Saves the JVM environment variables to substitute in the preference store.
   *
   * @param envReplacements the replacements to save
   */
  static void saveEnvReplacements(Collection<String> envReplacements) {
    String xml = "";
    try {
      xml = marshaler.marshalEnvVarList(envReplacements);
    } catch (IOException e) {
      // don't do anything...
    }

    getPreferences().put(WorkspaceConfigurationConstants.CONFIG_ENV_REPLACEMENT_LIST, xml);
  }

  /**
   * Gets JVM environment variables to substitute from the preference store.
   *
   * @return the JVM environment variables to substitute
   */
  public static List<String> getEnvReplacements() {
    String xml = Platform.getPreferencesService().getString(bundleSymbolicName,
        WorkspaceConfigurationConstants.CONFIG_ENV_REPLACEMENT_LIST, "", null);
    try {
      return parser.parseEnvVarList(xml);
    } catch (IOException e) {
      return Collections.emptyList();
    }
  }

  /**
   * Saves the new workspace detection flag in the preference store.
   *
   * @param newWorkspaceDetection the new workspace detection flag to save
   */
  static void saveNewWorkspaceDetection(boolean newWorkspaceDetection) {
    getPreferences().putBoolean(WorkspaceConfigurationConstants.CONFIG_STARTUP_CHECK, newWorkspaceDetection);
  }

  /**
   * Finds out whether at least one EPF URL is configured.
   *
   * @return {@code true} if at least in eEPF URL is configured and {@code false} otherwise
   */
  public static boolean isEpfUrlConfigured() {
    return !getEpfUrls().isEmpty();
  }


  private static IEclipsePreferences getPreferences() {
    return DefaultScope.INSTANCE.getNode(FrameworkUtil.getBundle(ConfigurationUtil.class).getSymbolicName());
  }
}
