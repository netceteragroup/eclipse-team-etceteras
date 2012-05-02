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
package ch.netcetera.eclipse.projectconfig.ui.preferences;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import ch.netcetera.eclipse.common.preference.AbstractResourceBundlePreferenceInitializer;
import ch.netcetera.eclipse.projectconfig.ui.ProjectConfigurationUIPlugin;

/**
 * Initializer for the project configuration plug-in's preferences.
 */
public class ProjectConfigPreferenceInitializer extends AbstractResourceBundlePreferenceInitializer {

  private static final String DEFAULT = "";
  private static final String RESOURCE_BUNDLE_NAME = "ch.netcetera.eclipse.projectconfig.defaults";

  // keys used in the property file
  private static final String PROP_KEY_CMDFILE_URL = "project.config.url";


  /**
   * Constructor.
   */
  public ProjectConfigPreferenceInitializer() {
    ResourceBundle resourceBundle = null;
    try {
      resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
    } catch (MissingResourceException e) {
      ProjectConfigurationUIPlugin.getDefault().getLog().log(
          new Status(IStatus.WARNING, ProjectConfigurationUIPlugin.PLUGIN_ID,
              "could not locate the resource bundle containing the default configuration", e));
    }
    setResourceBundle(resourceBundle);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initializeDefaultPreferences() {
    IEclipsePreferences preferences = new DefaultScope().getNode(ProjectConfigurationUIPlugin.PLUGIN_ID);

    preferences.put(ProjectConfigurationUIPlugin.CONFIG_CMDFILE_URL, getConfigValue(PROP_KEY_CMDFILE_URL, DEFAULT));
  }
}