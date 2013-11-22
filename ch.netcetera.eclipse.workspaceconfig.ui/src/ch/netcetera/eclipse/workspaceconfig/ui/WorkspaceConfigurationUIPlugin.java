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
package ch.netcetera.eclipse.workspaceconfig.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import ch.netcetera.eclipse.common.plugin.AbstractTextAccessorUIPlugin;
import ch.netcetera.eclipse.workspaceconfig.core.IPreferencesImportService;

/**
 * The activator class controls the plug-in life cycle.
 */
public class WorkspaceConfigurationUIPlugin extends AbstractTextAccessorUIPlugin {

  /** File extension of the messages property file. */
  private static final String PROPERTY_FILEEXT = ".messages";

  private static WorkspaceConfigurationUIPlugin plugin;

  private ServiceTracker<IPreferencesImportService, Object> tracker;
  private IPreferencesImportService service;

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;

    this.tracker = new ServiceTracker<>(context, IPreferencesImportService.class.getName(), null);
    this.tracker.open();
    this.service = (IPreferencesImportService) this.tracker.getService();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop(BundleContext context) throws Exception {
    this.tracker.close();
    plugin = null;
    super.stop(context);
  }

  /**
   * Gets the shared instance.
   *
   * @return the shared instance
   */
  public static WorkspaceConfigurationUIPlugin getDefault() {
    return plugin;
  }

  /**
   * Gets the image descriptor for the image file given at the plug-in's
   * relative path.
   *
   * @param path the image path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path) {
    return imageDescriptorFromPlugin(plugin.getBundle().getSymbolicName(), path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void initializeImageRegistry(ImageRegistry registry) {
    super.initializeImageRegistry(registry);
    PluginImages.initializeImageRegistry(registry);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResourceBundle getResourceBundle() {
    return ResourceBundle.getBundle(plugin.getBundle().getSymbolicName() + PROPERTY_FILEEXT, Locale.ENGLISH);
  }

  /**
   * Gets the {@link IPreferencesImportService} instance.
   *
   * @return the {@link IPreferencesImportService} instance
   */
  public IPreferencesImportService getPreferencesImportService() {
    return this.service;
  }
}
