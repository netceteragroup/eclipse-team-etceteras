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
package ch.netcetera.eclipse.projectconfig.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import ch.netcetera.eclipse.common.plugin.AbstractTextAccessorUIPlugin;
import ch.netcetera.eclipse.projectconfig.core.IProjectConfigurationService;

/**
 * The activator class controls the plug-in life cycle.
 */
public class ProjectConfigurationUIPlugin extends AbstractTextAccessorUIPlugin {

  /** The plug-in ID. */
  public static final String PLUGIN_ID = "ch.netcetera.eclipse.projectconfig.ui";


  /** The configuration id of the configuration command file URL. */
  public static final String CONFIG_CMDFILE_URL = "CONFIG_CMDFILE_URL";

  private static final String RESOURCE_BUNDLE_NAME = PLUGIN_ID + ".messages";

  private static ProjectConfigurationUIPlugin plugin;

  private ServiceTracker<?, IProjectConfigurationService> tracker;



  /** {@inheritDoc} */
  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;

    this.tracker = new ServiceTracker<IProjectConfigurationService, IProjectConfigurationService>(context,
        IProjectConfigurationService.class.getName(), null);
    this.tracker.open();
  }

  /** {@inheritDoc} */
  @Override
  public void stop(BundleContext context) throws Exception {
    this.tracker.close();
    plugin = null;
    super.stop(context);
  }

  /**
   * Gets the shared instance of the {@link ProjectConfigurationUIPlugin}.
   *
   * @return the shared instance of the {@link ProjectConfigurationUIPlugin}
   */
  public static ProjectConfigurationUIPlugin getDefault() {
    return plugin;
  }

  /** {@inheritDoc} */
  @Override
  public ResourceBundle getResourceBundle() {
    return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, Locale.ENGLISH);
  }

  /**
   * Gets the {@link IProjectConfigurationService} instance.
   *
   * @return the {@link IProjectConfigurationService} instance
   */
  public IProjectConfigurationService getProjectConfigurationService() {
    return this.tracker.getService();
  }
}